package ttasjwi.jdbc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ttasjwi.jdbc.domain.Member;
import ttasjwi.jdbc.repository.MemberRepositoryV3;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    private final MemberRepositoryV3 memberRepository;
    private final PlatformTransactionManager transactionManager;


    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            businessLogic(fromId, toId, money); // 비즈니스 로직
            transactionManager.commit(status); // 성공 시 커밋
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw  new IllegalStateException(e); // 일단 예외를 감싸서 덤지도록 처리함
        } // relelase 등 커넥션 관리를 트랜잭션 매니저 - 트랜잭션 동기화 매니저를 통해 위임
    }

    private void businessLogic(String fromId, String toId, int money) throws SQLException {
        // 비즈니스 로직 수행
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney()- money);

        validation(toMember);

        memberRepository.update(toId, toMember.getMoney() + money);
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
