package ttasjwi.jdbc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ttasjwi.jdbc.domain.Member;
import ttasjwi.jdbc.repository.MemberRepositoryV3;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {

    private final MemberRepositoryV3 memberRepository;
    private final TransactionTemplate txTemplate;

    public MemberServiceV3_2(MemberRepositoryV3 memberRepository, PlatformTransactionManager transactionManager) {
        this.memberRepository = memberRepository;
        this.txTemplate = new TransactionTemplate(transactionManager);
    }

    public void accountTransfer(String fromId, String toId, int money) {
        txTemplate.executeWithoutResult(
                status -> {
                    try {
                        businessLogic(fromId, toId, money);
                    } catch (SQLException e) {
                        throw new IllegalStateException(e);
                    }
                }
        );
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

}
