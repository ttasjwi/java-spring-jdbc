package ttasjwi.jdbc.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ttasjwi.jdbc.domain.Member;

import javax.sql.DataSource;

/**
 * JdbcTemplate 사용
 */
public class MemberRepositoryV5 implements MemberRepository {

    private final JdbcTemplate template;

    public MemberRepositoryV5(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }


    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO MEMBER (member_id, money)\n" +
                "values (?, ?)";
        template.update(sql, member.getMemberId(), member.getMoney());
        return member;
    }

    @Override
    public Member findById(String memberId) {
        String sql = "SELECT member_id, money\n" +
                "FROM member\n" +
                "WHERE member_id = ?";
        return template.queryForObject(sql, memberRowMapper(), memberId);
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "UPDATE member\n" +
                "SET money = ?\n" +
                "WHERE member_id = ?";
        template.update(sql, money, memberId);
    }

    @Override
    public void delete(String memberId) {
        String sql = "DELETE\n" +
                "FROM member\n" +
                "WHERE member_id = ?";
        template.update(sql, memberId);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) ->
                Member.builder()
                        .memberId(rs.getString("member_id"))
                        .money(rs.getInt("money"))
                        .build();
    }
}
