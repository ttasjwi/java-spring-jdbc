package ttasjwi.jdbc.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ttasjwi.jdbc.domain.Member;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    @DisplayName("crud")
    public void crud() throws Exception {
        //given
        String memberId = "memberV0";
        int money = 10000;

        Member member = new Member(memberId, money);
        repository.save(member);

        Member findMember = repository.findById(member.getMemberId());
        log.info("find Member = {}", findMember);

        assertThat(findMember).isEqualTo(member);
        assertThat(findMember.getMemberId()).isEqualTo(memberId);
        assertThat(findMember.getMoney()).isEqualTo(money);
    }
}
