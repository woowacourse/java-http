### 처음에 구현한 방식

- newCachedThreadPool()를 활용
    - 오랫동안 사용하지 않는 스레드를 자동으로 제거함으로써 상황에 따라 현재 유지하고 있는 스레드수를 적절하게 조정
    - MAX_THREADS 상수값을 활용한 검증로직을 통해 생성 가능한 최대 스레드수 제한
- 대기큐(QUEUE) 활용
    - 최대 스레드수보다 많은 요청이 들어오면 요청을 대기큐에 저장
    - 만약 대기큐도 모두 차면 요청을 무시
- ExecutorService의 동작과정 정리
    - ExecutorService 생성시 내부에서 동작하는 메인스레드를 생성
    - 외부에서는 executorService.registerTask()를 통해 요청을 대기큐에 저장
    - ExecutorService의 메인스레드에서는 대기큐에 요청이 있는지 확인하고 대기큐의 요청을 꺼내 스레드풀에 넣는 과정 반복

```java
package org.apache.util.threads;

public class ExecutorService implements Runnable {

    private final int MAX_THREADS;
    private final int ACCEPT_COUNT;
    private final ThreadPoolExecutor THREAD_POOL;
    private final Queue<Runnable> QUEUE;
    private final Thread THREADPOOL_MAIN_THREAD;

    private boolean isStop;

    private ExecutorService(int acceptCount, int maxThreads) {
        this.MAX_THREADS = maxThreads;
        this.ACCEPT_COUNT = acceptCount;
        this.THREAD_POOL = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.QUEUE = new ArrayDeque<>();
        this.isStop = false;
        this.THREADPOOL_MAIN_THREAD = new Thread(this);
        this.THREADPOOL_MAIN_THREAD.start();
    }

    @Override
    public void run() {
        while (!isStop) {
            executeTask();
        }
    }

    public synchronized void registerTask(Runnable task) {
        if (QUEUE.size() <= ACCEPT_COUNT) {
            QUEUE.add(task);
        }
    }

    public void stopService() {
        this.isStop = true;
    }

    private void executeTask() {
        if (QUEUE.isEmpty()) {
            return;
        }
        if (THREAD_POOL.getPoolSize() <= MAX_THREADS) {
            THREAD_POOL.submit(QUEUE.remove());
        }
    }
}

```

### 현재 구현을 선택한 이유

- 위 방식보다 더욱 나은 방법이 있는지 조사하던 중에 ExecutorService를 직접 구현하는 것이 아님을 알게됨
- 또한 ThreadPoolExecutor를 통해 위에서 직접 구현한 동작을 이미 제공하고 있다는 것을 알게됨

```java
ExecutorService executor = new ThreadPoolExecutor(
        0,                              // 최소한 유지할 스레드수
        10,                              // 최대 생성가능한 스레드수
        60L, TimeUnit.SECONDS,          // 스레드 생존시간 60초 제한
        new ArrayBlockingQueue<Runnable>(100) // 대기열 사이즈 제한
);
```
