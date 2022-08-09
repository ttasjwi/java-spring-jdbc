package ttasjwi.jdbc.exception.translator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import ttasjwi.jdbc.connection.ConnectionConst;
import ttasjwi.jdbc.domain.Member;
import ttasjwi.jdbc.repository.db.MyDbException;
import ttasjwi.jdbc.repository.db.MyDuplicateKeyException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

@Slf4j
public class ExTranslatorV1Test {

    Repository repository;
    Service service;

    /**
     *     public static final String URL = "jdbc:h2:tcp://localhost/~/java-spring-jdbc";
     *     public static final String USERNAME = "sa";
     *     public static final String PASSWORD = "";
     */

    @BeforeEach
    void init() {
        DataSource datasource = new DriverManagerDataSource(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD);
        repository = new Repository(datasource);
        service = new Service(repository);
    }
    
    @Test
    public void duplicateKeySave() {
        service.create("myId");
        service.create("myId"); // 같은 id 저장 시도
    }

    @Slf4j
    @RequiredArgsConstructor
    static class Service {
        private final Repository repository;

        public void create(String memberId) {
            try {
                repository.save(new Member(memberId, 0));
                log.info("saveId = {}", memberId);
            } catch (MyDuplicateKeyException e) { // 중복 예외가 catch됐을 시
                log.info("키 중복, 복구 시도");
                String retryId = generateNewId(memberId);
                log.info("retryId = {}",retryId);
                repository.save(new Member(retryId, 0));
            } catch (MyDbException e) { // 처리할 수 없으므로 외부로 떠넘긴다.
                log.info("데이터 접근 계층 예외", e);
                throw e;
            }

        }

        private String generateNewId(String memberId) { // 랜덤 회원 식별자 생성
            return new StringBuilder(memberId)
                    .append(new Random().nextInt(10000))
                    .toString();
        }
    }

    @RequiredArgsConstructor
    static class Repository {

        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "INSERT INTO member(member_id, money) VALUES (?,?)";
            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());
                pstmt.executeUpdate();
                return member;
            } catch (SQLException e) {
                // h2 db 중복 예외
                if (e.getErrorCode() == 23505) { // db 중복 예외 -> 서비스 중복 예외 변환
                    throw new MyDuplicateKeyException(e);
                }
                throw new MyDbException(e); // 중복 예외가 아닐 경우 db 예외로 퉁친다.
            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }
        }
    }
}
