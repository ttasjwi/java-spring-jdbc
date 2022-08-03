package ttasjwi.jdbc.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import ttasjwi.jdbc.domain.Member;
import ttasjwi.jdbc.repository.MemberRepositoryV3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ttasjwi.jdbc.connection.ConnectionConst.*;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
class MemberServiceV3_1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_1 memberService;

    @BeforeEach
    void before() {
        DriverManagerDataSource datasource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV3(datasource);

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(datasource);
        memberService = new MemberServiceV3_1(memberRepository, transactionManager);
    }

    @AfterEach
    void after() throws Exception {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    public void accountTransfer() throws Exception {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        //then
        Member afterMemberA = memberRepository.findById(memberA.getMemberId());
        Member afterMemberB = memberRepository.findById(memberB.getMemberId());
        assertThat(afterMemberA.getMoney()).isEqualTo(8000);
        assertThat(afterMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    public void accountTransferEx() throws Exception {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        //when
        assertThatThrownBy(() ->
                memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        //then
        Member afterMemberA = memberRepository.findById(memberA.getMemberId());
        Member afterMemberEx = memberRepository.findById(memberEx.getMemberId());
        assertThat(afterMemberA.getMoney()).isEqualTo(10000);
        assertThat(afterMemberEx.getMoney()).isEqualTo(10000);
    }
}
