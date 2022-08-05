package ttasjwi.jdbc.exception.basic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CheckedAppTest {

    private Controller controller;

    @BeforeEach
    void setUp() {
        controller = new Controller();
    }

    @Test
    public void checked() {
        assertThatThrownBy(() -> controller.request())
                .isInstanceOf(SQLException.class);
    }


    static class Controller {
        private final Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    static class Service {
        private final Repository repository = new Repository();
        private final NetworkClient networkClient = new NetworkClient();


        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("연결 실패");
        }
    }
}
