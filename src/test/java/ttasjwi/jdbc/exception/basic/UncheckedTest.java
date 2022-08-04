package ttasjwi.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UncheckedTest {

    private Service service;

    @BeforeEach
    void setUp() {
        this.service = new Service();
    }
    @Test
    @DisplayName("예외 catch!")
    public void unchecked_catch() {
        service.callCatch();
    }

    @Test
    @DisplayName("예외 throw!")
    public void unchecked_throw() {
        assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyUncheckedException.class);
    }


    /**
     * RuntimeException 을 상속받은 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException {

        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * 언체크 예외는
     * 예외를 잡아서 처리하거나 예외를 호출한 쪽으로 떠넘길 필요가 없다.
     * 예외를 잡지 않으면 자동으로 밖으로 던진다.
     */
    static class Service {

        private final Repository repository = new Repository();

        /**
         * 필요한 경우 예외를 잡아서 처리하면 된다.
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                // 예외 처리 로직
                log.error("예외 처리, message = {}", e.getMessage(), e);
            }
        }

        /**
         * 예외를 잡지 않아도 된다. 자연스럽게 상위로 넘어간다.
         * 체크 예외와 다르게 throws 예외 선언을 하지 않아도 된다.
         */
        public void callThrow() {
            repository.call();
        }
    }

    static class Repository {

        public void call() {
            throw new MyUncheckedException("ex");
        }
    }

}
