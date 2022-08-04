package ttasjwi.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CheckedTest {

    private Service service;

    @BeforeEach
    void setUp() {
        this.service = new Service();
    }
    @Test
    @DisplayName("예외 catch!")
    public void checked_catch() {
        service.callCatch();
    }

    @Test
    @DisplayName("예외 throw!")
    public void checked_throws() {
        assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }
    
    /**
     * Exception을 상속받은 예외는 체크 예외가 된다.
     */
    static class MyCheckedException extends Exception {

        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * 체크 예외는
     * 예외를 잡아서 처리하거나,
     * 예외를 호출한 쪽으로 떠넘기거나
     * 둘 중 하나를 필수로 처리해야한다.
     */
    static class Service {

        private final Repository repository = new Repository();

        /**
         * 체크 예외를 잡아서 처리하는 코드
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                // 예외 처리 로직
                log.error("예외 처리, message = {}", e.getMessage(), e);
            }
        }

        /**
         * 체크 예외를 밖으로 떠넘기는 코드
         * 체크 예외를 밖으로 떠넘기려면 "throws 체크예외"를 메서드에 필수로 선언해야한다.
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository {
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }
}
