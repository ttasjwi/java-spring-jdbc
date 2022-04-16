package ttasjwi.jdbc.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ttasjwi.jdbc.domain.Member;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    @DisplayName("crud")
    public void crud() throws Exception {
        Member member = new Member("memberV0", 10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("find Member = {}", findMember);
        assertThat(findMember).isEqualTo(member);
        assertThat(findMember.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(findMember.getMoney()).isEqualTo(member.getMoney());

        //update Money (10000 -> 20000)
        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(()-> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}
