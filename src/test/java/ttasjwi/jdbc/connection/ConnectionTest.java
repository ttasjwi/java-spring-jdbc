package ttasjwi.jdbc.connection;

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

    private void userDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();

        log.info("connection1 = {}, class={}",con1, con1.getClass());
        log.info("connection2 = {}, class={}",con2, con2.getClass());
    }
}
