package thread.stage1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPolicyTest {

    public static void main(final String[] args) throws InterruptedException {
        // 테스트를 위한 매개변수 설정
        // 스레드 2개를 상시 활성화
        final int corePoolSize = 2;
        // 최대 늘어날 수 있는 스레드 개수
        final int maximumPoolSize = 4;
        // 대기할 수 있는 Queue 의 개수
        final int queueCapacity = 2;

        final long taskExecutionTime = 5000; // 각 작업의 실행 시간 (밀리초)
        final int numberOfTasks = 10; // 총 작업의 수
        // 큐와 스레드 풀 설정
        final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueCapacity);
        final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                corePoolSize, maximumPoolSize, 0L,
                // CallerRunsPolicy : Queue,Thread 가 전부 작업 상태일 시, Main 스레드도 작업을 수행한다. ( 10 개 전부 수행 )
                // AbortPolicy : 전부 작업 상태 시, RejectedExecutionException 발생시킴 ( 4 + 2 개만 수행 )
                // DiscardPolicy : 전부 작업 상태 시, 조용히 작업 거부 ( 4 + 2 개만 수행 )
                // DiscardOldPolicy : 전부 작업 상태 시, 예전에 대기하고 있는 작업을 제거 후 교체한다 - current Queue 내 작업이 계속 달라짐 ( 4 +2 개만 수행 )
                TimeUnit.MILLISECONDS, queue, new ThreadPoolExecutor.DiscardPolicy());

        // 작업 추가
        final long memory = Runtime.getRuntime()
                .freeMemory();
        for (int i = 0; i < numberOfTasks; i++) {
            final int taskNumber = i;
            executorService.submit(() -> execute(taskNumber, executorService, taskExecutionTime));
            printQueueStatus(queue, taskNumber);
        }

        // 더 이상 새로운 작업을 받지 않고, 현재 큐에 있는 작업 + 실행 중인 작업 완료를 대기 한 후 스레드 풀 종료
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        System.out.println(memory);
        System.out.println(min);
        System.out.println("Total task execution time: " + executorService.getCompletedTaskCount());
    }

    private static long min = Long.MAX_VALUE;

    private static void execute(final int taskNumber, final ThreadPoolExecutor executorService, final long taskExecutionTime) {
        System.out.println("Task " + taskNumber + " is running on thread " + Thread.currentThread()
                .getName());
        System.out.println("Current Queue Size : " + executorService.getQueue()
                .size() + "\t" + "Curren Pool Size : " + executorService.getPoolSize() + "\t" + "Active Thread Count : " + executorService.getActiveCount());
        try {
            min = Math.min(min, Runtime.getRuntime()
                    .freeMemory());
            Thread.sleep(taskExecutionTime); // 작업을 일정 시간 동안 실행
        } catch (final InterruptedException e) {
            Thread.currentThread()
                    .interrupt();
        }
        System.out.println("Task " + taskNumber + " is completed");
    }

    private static void printQueueStatus(final BlockingQueue<Runnable> queue, final int taskNumber) {
        System.out.println("After submitting Task " + taskNumber + ", current queue:");
        queue.forEach(task -> System.out.println(task.toString()));
        System.out.println("------------------------------------------------");
    }
}
