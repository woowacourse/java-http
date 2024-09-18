package thread;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ExecutorServiceTest {

    @DisplayName("execute()를 사용할 때 예외가 발생하면 스레드를 재사용하지 않는다")
    @Test
    public void testExecuteThreadNotReuse() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        final String[] threadNameTask1 = new String[1];
        final String[] threadNameTask2 = new String[1];

        Runnable task1 = () -> { // 첫 번째 작업에서 예외를 발생시키는 Runnable
            threadNameTask1[0] = Thread.currentThread().getName();
            System.out.println(threadNameTask1[0] + " - Task 1 started");
            throw new RuntimeException("Exception in Task 1");
        };
        executor.execute(task1); // 첫 번째 작업 실행
        Thread.sleep(2000); // 작업이 완료될 시간을 줌

        Runnable task2 = () -> { // 두 번째 작업
            threadNameTask2[0] = Thread.currentThread().getName();
            System.out.println(threadNameTask2[0] + " - Task 2 started");
        };
        executor.execute(task2); // 두 번째 작업 실행

        executor.shutdown(); // 스레드 풀 종료
        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
        assertTrue(terminated, "스레드 풀이 종료되지 않았습니다."); // 스레드 풀이 정상적으로 종료되었는지 확인

        assertNotEquals(threadNameTask1[0], threadNameTask2[0]); // 첫 번째 작업과 두 번째 작업의 스레드 이름이 다른지 확인 (재사용하지 않음)
    }

    @DisplayName("submit()을 사용할 때 예외가 발생하면 스레드를 재사용한다")
    @Test
    public void testSubmitThreadReuse() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        final String[] threadNameTask1 = new String[1];
        final String[] threadNameTask2 = new String[1];

        Callable<String> task1 = () -> { // 첫 번째 작업에서 예외를 발생시키는 Callable
            threadNameTask1[0] = Thread.currentThread().getName();
            System.out.println(threadNameTask1[0] + " - Task 3 started");
            throw new RuntimeException("Exception in Task 3");
        };
        Future<String> future = executor.submit(task1); // 첫 번째 작업 실행
        Thread.sleep(2000); // 작업이 완료될 시간을 줌
        try {
            future.get(); // 예외를 발생시키고 ExecutionException을 던짐
        } catch (ExecutionException e) {
            System.out.println("Exception caught: " + e.getMessage());
        }

        Runnable task2 = () -> { // 두 번째 작업
            threadNameTask2[0] = Thread.currentThread().getName();
            System.out.println(threadNameTask2[0] + " - Task 4 started");
        };
        executor.submit(task2); // 두 번째 작업 실행

        executor.shutdown(); // 스레드 풀 종료
        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
        assertTrue(terminated, "스레드 풀이 종료되지 않았습니다."); // 스레드 풀이 정상적으로 종료되었는지 확인

        assertEquals(threadNameTask1[0], threadNameTask2[0]); // 첫 번째 작업과 두 번째 작업의 스레드 이름이 같은지 확인 (재사용됨)
    }
}
