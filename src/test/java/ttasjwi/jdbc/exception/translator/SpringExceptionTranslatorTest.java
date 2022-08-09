package ttasjwi.jdbc.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static ttasjwi.jdbc.connection.ConnectionConst.*;

@Slf4j
public class SpringExceptionTranslatorTest {

    DataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    /**
     * 예외코드를 일일이 개발자가 확인해서, 예외를 변환해야함 (기존)
     */
    @Test
    public void sqlExceptionErrorCode() {
        String sql = "SELECT bad grammar";
        try {
            Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.executeQuery();
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            assertThat(errorCode).isEqualTo(42122);
            log.info("errorCode = {}",errorCode);

            //org.h2.jdbc.JdbcSQLSyntaxErrorException
            log.info("error!", e);
        }
    }

    /**
     * 각각의 DB 에러코드가 다르지만 스프링이 지원하는 변환기를 통해 일관성 있게 스프링 예외로 변환함.
     */
    @Test
    public void exceptionTranslator() {
        String sql = "SELECT bad grammar";
        try {
            Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.executeQuery();
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            assertThat(errorCode).isEqualTo(42122); // 기존 방식대로면 개발자가 에러코드 일일이 확인하고 그에 맞춰 수동으로 예외를 만들었음
            SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);

            DataAccessException resultEx = exTranslator.translate("select", sql, e); // 스프링 예외로 변환
            log.info("resultEx", resultEx);
            assertThat(resultEx).isInstanceOf(BadSqlGrammarException.class);
        }
    }
}
