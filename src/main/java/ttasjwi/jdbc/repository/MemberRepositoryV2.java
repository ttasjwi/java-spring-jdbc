package ttasjwi.jdbc.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import ttasjwi.jdbc.domain.Member;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;


/**
 * JDBC - Connection을 파라미터로 넘기기
 */

@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
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
            log.error("db error : {}", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
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
            log.error("db error : {}", e);
            throw e;
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public Member findById(Connection conn, String memberId) throws SQLException {
        String sql = "SELECT member_id, money\n" +
                "FROM member\n" +
                "WHERE member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
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
            log.error("db error : {}", e);
            throw e;
        } finally {
            // 커넥션(Connection)은 여기서 닫지 않는다!!!

            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            // JdbcUtils.closeConnection(conn); // 커넥션을 유지해야함. 여기서 커넥션을 닫으면 안 된다.
        }
    }

    public void update(String memberId, int money) throws SQLException {
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
            log.error("db error : {}", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }

    public void update(Connection conn, String memberId, int money) throws SQLException {
        String sql = "UPDATE member\n" +
                "SET money = ?\n" +
                "WHERE member_id = ?";

        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(2, memberId);
            pstmt.setInt(1, money);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            log.error("db error : {}", e);
            throw e;
        } finally {
            // 커넥션(Connection)은 여기서 닫지 않는다!!!
            JdbcUtils.closeResultSet(null);
            JdbcUtils.closeStatement(pstmt);
            // JdbcUtils.closeConnection(conn); // 커넥션을 유지해야함. 여기서 커넥션을 닫으면 안 된다.
        }
    }

    public void delete(String memberId) throws SQLException {
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
            log.error("db error : {}", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get Connection = {}, class={}", con, con.getClass());
        return con;
    }

    private void close(Connection conn, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(conn);
    }
}
