package thread.stage1;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 스레드를 다룰 때 어떤 상황을 조심해야 할까?
 * - 상태를 가진 한 객체를 여러 스레드에서 동시에 접근할 경우
 * - static 변수를 가진 객체를 여러 스레드에서 동시에 접근할 경우
 *
 * 위 경우는 동기화(synchronization)를 적용시키거나 객체가 상태를 갖지 않도록 한다.
 * 객체를 불변 객체로 만드는 방법도 있다.
 *
 * 웹서버는 여러 사용자가 동시에 접속을 시도하기 때문에 동시성 이슈가 생길 수 있다.
 * 어떤 사례가 있는지 아래 테스트 코드를 통해 알아보자.
 */
class ConcurrencyTest {

    @Test
    void test() throws InterruptedException {
        final var userServlet = new UserServlet();

        // 웹서버로 동시에 2명의 유저가 gugu라는 이름으로 가입을 시도했다.
        // UserServlet의 users에 이미 가입된 회원이 있으면 중복 가입할 수 없도록 코드를 작성했다.
        final var firstThread = new Thread(new HttpProcessor(new User("gugu"), userServlet));
        firstThread.setName("T1");
        final var secondThread = new Thread(new HttpProcessor(new User("gugu"), userServlet));
        secondThread.setName("T2");

        // 스레드는 실행 순서가 정해져 있지 않다.
        // firstThread보다 늦게 시작한 secondThread가 먼저 실행될 수도 있다.
        firstThread.start();
        secondThread.start();
        secondThread.join(); // secondThread가 먼저 gugu로 가입했다.
        firstThread.join();

        // 이미 gugu로 가입한 사용자가 있어서 UserServlet.join() 메서드의 if절 조건은 false가 되고 크기는 1이다.
        // 하지만 디버거로 개별 스레드를 일시 중지하면 if절 조건이 true가 되고 크기가 2가 된다. 왜 그럴까?
        /**
         * T1에서 UserServlet의 join() 메서드 호출 시에 contains()를 통과한 상태에서 add() 직전 정지 시켜둔다.
         * T2의 입장에선, contains()가 통과할 수 밖에 없다.
         * 때문에 T2에서 add가 진행되고, break point를 통과 시키는 경우 T1의 User도 add()되기 때문에 2개의 유저가 저장됨
         *
         * [문제 해결]
         * 1. join() 메서드에 synchronized 키워드를 붙여 T2 쓰레드가 T1 쓰레드의 동작이 수행될 때까지 기다리게 한다.
         * 2. User에 HashCode&Equals가 구현되어 있으니 ConcurrentHashMap으로 구현한다.
         */
        assertThat(userServlet.getUsers()).hasSize(1);
    }
}
