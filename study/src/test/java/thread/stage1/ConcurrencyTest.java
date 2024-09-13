package thread.stage1;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * 스레드를 다룰 때 어떤 상황을 조심해야 할까? - 상태를 가진 한 객체를 여러 스레드에서 동시에 접근할 경우 - static 변수를 가진 객체를 여러 스레드에서 동시에 접근할 경우
 * <p>
 * 위 경우는 동기화(synchronization)를 적용시키거나 객체가 상태를 갖지 않도록 한다. 객체를 불변 객체로 만드는 방법도 있다.
 * <p>
 * 웹서버는 여러 사용자가 동시에 접속을 시도하기 때문에 동시성 이슈가 생길 수 있다. 어떤 사례가 있는지 아래 테스트 코드를 통해 알아보자.
 */
class ConcurrencyTest {

    @Test
    void test() throws InterruptedException {
        final var userServlet = new UserServlet();

        // 500명의 사용자가 동시에 'gugu'라는 이름으로 가입을 시도한다.
        int threadNum = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        for (int i = 0; i < threadNum; i++) {
            executorService.submit(() -> createUser(userServlet));
        }

        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);

        // 'gugu'라는 사용자는 한 번만 가입이 가능해야 한다.
        assertThat(userServlet.getUsers()).hasSize(1);
    }

    private static void createUser(UserServlet userServlet) {
        new HttpProcessor(new User("gugu"), userServlet).run();
    }

}
