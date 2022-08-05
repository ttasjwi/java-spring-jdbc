package ttasjwi.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UnCheckedAppTest {

    private Controller controller;

    @BeforeEach
    void setUp() {
        controller = new Controller();
    }

    @Test
    public void unChecked() {
//        controller.request();
        assertThatThrownBy(() -> controller.request())
                .isInstanceOf(RuntimeSQLException.class);
    }

    @Test
    public void printEx() {
        try {
            controller.request();
        } catch (Exception e) {
//            e.printStackTrace(); // sout
            log.info("ex!!!", e);
        }
    }

    static class Controller {
        private final Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    static class Service {
        private final Repository repository = new Repository();
        private final NetworkClient networkClient = new NetworkClient();


        public void logic() {
            repository.call();
            networkClient.call();
        }
    }

    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e); // 예외 전환시 기존 예외를 포함해야한다!
            }
        }

        private void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }

    }

    static class RuntimeSQLException extends RuntimeException {

        public RuntimeSQLException() {
        }

        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }

    }

    static class RuntimeConnectException extends RuntimeException {

        public RuntimeConnectException(String message) {
            super(message);
        }
    }
}
