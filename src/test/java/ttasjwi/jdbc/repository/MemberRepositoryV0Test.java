package ttasjwi.jdbc.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ttasjwi.jdbc.domain.Member;

class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    @DisplayName("crud")
    public void crud() throws Exception {
        //given
        Member member = new Member("memverV0", 10000);
        repository.save(member);
    }
}
