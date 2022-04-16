package ttasjwi.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.DriverDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static ttasjwi.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {
    
    @Test
    @DisplayName("DriveManager")
    public void driveManagerTest() throws Exception {
        //given
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        log.info("connection1 = {}, class={}",con1, con1.getClass());
        log.info("connection2 = {}, class={}",con2, con2.getClass());
    }

    @Test
    @DisplayName("DriveManagerDataSource")
    public void driveManagerDataSourceTest() throws Exception {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        userDataSource(dataSource);
    }

    @Test
    @DisplayName("커넥션 풀링")
    public void dataSourceConnectionPool() throws Exception {
        //given
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        userDataSource(dataSource);
        Thread.sleep(1000); // 1초 지연시간을 둬서, 별도 스레드에서 커넥션이 생성되는 로그가 찍히는걸 확인할 수 있음.
    }

    private void userDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();

        log.info("connection1 = {}, class={}",con1, con1.getClass());
        log.info("connection2 = {}, class={}",con2, con2.getClass());
    }
}
