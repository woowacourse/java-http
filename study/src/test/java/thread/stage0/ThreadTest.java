package thread.stage0;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 자바로 동시에 여러 작업을 처리할 때 스레드를 사용한다.
 * 스레드 객체를 직접 생성하는 방법부터 알아보자.
 * 진행하면서 막히는 부분은 아래 링크를 참고해서 해결한다.
 *
 * Thread Objects
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/threads.html
 *
 * Defining and Starting a Thread
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
 */
class ThreadTest {

    private static final Logger log = LoggerFactory.getLogger(ThreadTest.class);

    /**
     * 자바에서 직접 스레드를 만드는 방법은 2가지가 있다.
     * 먼저 Thread 클래스를 상속해서 스레드로 만드는 방법을 살펴보자.
     * 주석을 참고하여 테스트 코드를 작성하고, 테스트를 실행시켜서 메시지가 잘 나오는지 확인한다.
     */
    @Test
    void testExtendedThread() throws InterruptedException {
        // 하단의 ExtendedThread 클래스를 Thread 클래스로 상속하고 스레드 객체를 생성한다.
        Thread thread = new ExtendedThread("hello thread");

        // 생성한 thread 객체를 시작한다.
         thread.start();

        // thread의 작업이 완료될 때까지 기다린다.
         thread.join();
    }

    /**
     * Runnable 인터페이스를 사용하는 방법도 있다.
     * 주석을 참고하여 테스트 코드를 작성하고, 테스트를 실행시켜서 메시지가 잘 나오는지 확인한다.
     */
    @Test
    void testRunnableThread() throws InterruptedException {
        // 하단의 RunnableThread 클래스를 Runnable 인터페이스의 구현체로 만들고 Thread 클래스를 활용하여 스레드 객체를 생성한다.
        Thread thread = new Thread(new RunnableThread("hello thread")); //

        // 생성한 thread 객체를 시작한다.
         thread.start();

        // thread의 작업이 완료될 때까지 기다린다.
         thread.join();
    }

    /*
    ExtendedThread는 Thread 클래스를 상속해서 만든다.
    Thread 클래스를 상속받아 run() 메서드를 오버라이드하며, Thread 객체 자체를 생성해서 실행한다.
    이 경우 클래스가 Thread를 이미 상속받았기 때문에 다른 클래스를 상속받을 수 없다.
    각각의 ExtendedThread 인스턴스는 고유의 Thread 객체다.
     */
    private static final class ExtendedThread extends Thread {

        private String message;

        public ExtendedThread(final String message) {
            this.message = message;
        }

        @Override
        public void run() {
            log.info(message);
        }
    }

    /*
    RunnableThread는 Runnable 인터페이스를 구현해서 만든다.
    Runnable 인터페이스의 run() 메서드를 구현한 후, Thread 생성자에 해당 Runnable 객체를 넘겨 실행한다.
    이 방법은 클래스 상속 제한이 없고, 여러 Thread가 같은 Runnable 인스턴스를 공유할 수 있어 메모리 효율적이다.
    기능적으로는 Runnable 객체가 실행할 작업을 담고, Thread가 실제로 실행을 담당한다.
     */
    private static final class RunnableThread implements Runnable {

        private String message;

        public RunnableThread(final String message) {
            this.message = message;
        }

        @Override
        public void run() {
            log.info(message);
        }
    }
}
