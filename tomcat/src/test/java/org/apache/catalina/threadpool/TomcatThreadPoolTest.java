package org.apache.catalina.threadpool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.concurrent.ThreadPoolExecutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TomcatThreadPoolTest {

    @DisplayName("톰캣 기본 스레드 풀을 생성한다.")
    @Test
    void get() {
        //given
        //when
        final ThreadPoolExecutor threadPoolExecutor = new TomcatThreadPool();

        //then
        assertAll(
            () -> assertThat(threadPoolExecutor.getCorePoolSize()).isEqualTo(25),
            () -> assertThat(threadPoolExecutor.getMaximumPoolSize()).isEqualTo(200),
            () -> assertThat(threadPoolExecutor.getQueue().remainingCapacity())
                .isEqualTo(Integer.MAX_VALUE)
        );
    }

    @DisplayName("maximumPoolSize 이 corePoolSize 보다 작을 경우 corePoolSize 를 maximumPoolSize 로 가진 스레드 풀을 생성한다.")
    @Test
    void get_with_maximumPoolSize() {
        //given
        //when
        final ThreadPoolExecutor threadPoolExecutor = new TomcatThreadPool(30);

        //then
        assertAll(
            () -> assertThat(threadPoolExecutor.getCorePoolSize()).isEqualTo(25),
            () -> assertThat(threadPoolExecutor.getMaximumPoolSize()).isEqualTo(30)
        );
    }

    @DisplayName("maximumPoolSize 이 corePoolSize 보다 작을 경우 corePoolSize 를 maximumPoolSize 로 가진 스레드 풀을 생성한다.")
    @Test
    void get_with_maximumPoolSize_lessThanCorePoolSize() {
        //given
        //when
        final ThreadPoolExecutor threadPoolExecutor = new TomcatThreadPool(20);

        //then
        assertAll(
            () -> assertThat(threadPoolExecutor.getCorePoolSize()).isEqualTo(25),
            () -> assertThat(threadPoolExecutor.getMaximumPoolSize()).isEqualTo(25)
        );
    }

    @DisplayName("인자로 전달한 maximumPoolSize 와 workQueueSize 를 가진 스레드 풀을 생성한다.")
    @Test
    void get_with_maximumPoolSizeAndWorkQueueSize() {
        //given
        //when
        final ThreadPoolExecutor threadPoolExecutor = new TomcatThreadPool(30, 10);

        //then
        assertAll(
            () -> assertThat(threadPoolExecutor.getCorePoolSize()).isEqualTo(25),
            () -> assertThat(threadPoolExecutor.getMaximumPoolSize()).isEqualTo(30),
            () -> assertThat(threadPoolExecutor.getQueue().remainingCapacity()).isEqualTo(10)
        );
    }

    @DisplayName("인자로 전달한 workQueueSize 가 1보다 작을 경우 workQueueSize 를 1로 가진 스레드 풀을 생성한다.")
    @Test
    void get_with_maximumPoolSize_biggerThanCorePoolSize() {
        //given
        //when
        final ThreadPoolExecutor threadPoolExecutor = new TomcatThreadPool(30, 0);

        //then
        assertAll(
            () -> assertThat(threadPoolExecutor.getCorePoolSize()).isEqualTo(25),
            () -> assertThat(threadPoolExecutor.getMaximumPoolSize()).isEqualTo(30),
            () -> assertThat(threadPoolExecutor.getQueue().remainingCapacity()).isEqualTo(1)
        );
    }
}
