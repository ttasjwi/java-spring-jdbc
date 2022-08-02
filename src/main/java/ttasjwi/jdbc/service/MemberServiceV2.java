package ttasjwi.jdbc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ttasjwi.jdbc.domain.Member;
import ttasjwi.jdbc.repository.MemberRepositoryV1;
import ttasjwi.jdbc.repository.MemberRepositoryV2;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection conn = dataSource.getConnection();
        try {
            conn.setAutoCommit(false); // 트랜잭션 시작
            // 비즈니스 로직
            businessLogic(conn, fromId, toId, money);

            // 커밋, 롤백
            conn.commit(); // 성공 시 커밋
        } catch (Exception e) {
            conn.rollback(); // 실패 시 롤백
            throw  new IllegalStateException(e); // 일단 예외를 감싸서 덤지도록 처리함
        } finally {
            release(conn);
        }
    }

    private void businessLogic(Connection conn, String fromId, String toId, int money) throws SQLException {
        // 비즈니스 로직 수행
        Member fromMember = memberRepository.findById(conn, fromId);
        Member toMember = memberRepository.findById(conn, toId);

        memberRepository.update(conn, fromId, fromMember.getMoney()- money);

        validation(toMember);

        memberRepository.update(conn, toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

    /**
     * 커넥션 반환
     */
    private void release(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true); // 자동커밋은 기본적으로 활성화되어 있기 때문에 다시 true로 바꾸고 반환해야한다.
                conn.close();
            } catch (Exception e) {
                log.info("error message = {}", e);
            }
        }
    }
}
