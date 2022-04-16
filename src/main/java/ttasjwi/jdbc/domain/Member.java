package ttasjwi.jdbc.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class Member {

    private String memberId;
    private int money;

    @Builder
    public Member(String memberId, int money) {
        this.memberId = memberId;
        this.money = money;
    }
}
