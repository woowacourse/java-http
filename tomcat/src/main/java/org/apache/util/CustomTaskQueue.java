package org.apache.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Tomcat TaskQueue 유사 구현
 * - 큐보다 스레드 확장을 우선시
 * - idle thread 있으면 큐에 넣음
 * - maxThreads 도달 시에만 큐 사용
 */
public class CustomTaskQueue extends LinkedBlockingQueue<Runnable> {

    private volatile ThreadPoolExecutor parent;

    public CustomTaskQueue(final int capacity) {
        super(capacity);
    }

    public void setParent(final ThreadPoolExecutor parent) {
        this.parent = parent;
    }

    @Override
    public boolean offer(final Runnable runnable) {
        if (parent == null) {
            return super.offer(runnable);
        }

        final var poolSize = parent.getPoolSize();
        final var maxPoolSize = parent.getMaximumPoolSize();

        if (poolSize == maxPoolSize) {
            return super.offer(runnable);
        }

        if (parent.getQueue()
                .isEmpty()
                && parent.getActiveCount() < poolSize) {
            return super.offer(runnable);
        }

        if (poolSize < maxPoolSize) {
            return false;
        }

        return super.offer(runnable);
    }
}
