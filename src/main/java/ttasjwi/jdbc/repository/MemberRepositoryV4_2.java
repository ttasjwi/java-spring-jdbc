package ttasjwi.jdbc.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import ttasjwi.jdbc.domain.Member;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;


/**
 * SQLExceptionTranslator 추가
 *
 */

@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository {

    private final DataSource dataSource;
    private final SQLExceptionTranslator exTranslator;

    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        this.exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO MEMBER (member_id, money)\n" +
                "values (?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            throw exTranslator.translate("save", sql, e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public Member findById(String memberId) {
        String sql = "SELECT member_id, money\n" +
                "FROM member\n" +
                "WHERE member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return Member.builder()
                        .memberId(rs.getString("member_id"))
                        .money(rs.getInt("money"))
                        .build();
            } else {
                throw new NoSuchElementException(
                        String.format("Member Not Found : memberId= %s", memberId)
                );
            }
        } catch (SQLException e) {
            throw exTranslator.translate("findById", sql, e);

        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "UPDATE member\n" +
                "SET money = ?\n" +
                "WHERE member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(2, memberId);
            pstmt.setInt(1, money);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            throw exTranslator.translate("update", sql, e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public void delete(String memberId) {
        String sql = "DELETE\n" +
                "FROM member\n" +
                "WHERE member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw exTranslator.translate("delete", sql, e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    private Connection getConnection() {
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get Connection = {}, class={}", con, con.getClass());
        return con;
    }

    private void close(Connection conn, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        // JdbcUtils.closeConnection(conn);

        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(conn, dataSource);
    }
}
