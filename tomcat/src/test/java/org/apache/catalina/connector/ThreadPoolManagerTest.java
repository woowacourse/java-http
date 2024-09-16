package org.apache.catalina.connector;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ThreadPoolManagerTest {

    private ThreadPoolManager threadPoolManager;

    @DisplayName("최대 스레드만큼 요청시 풀 사이즈는 최대 스레드 수이며 대기열은 0이다.")
    @Test
    void run() {
        threadPoolManager = new ThreadPoolManager(2, 1);

        StubSocket socket = new StubSocket();
        threadPoolManager.run(new Http11Processor(socket));
        threadPoolManager.run(new Http11Processor(socket));

        assertAll(
                () -> assertThat(threadPoolManager.getPoolSize()).isEqualTo(2),
                () -> assertThat(threadPoolManager.getQueueSize()).isEqualTo(0)
        );
    }

    @DisplayName("최대 스레드 이상 요청시 풀 사이즈는 최대 스레드 수이며 나머지는 대기한다.")
    @Test
    void runWithOverRequestWithinAcceptCount() {
        threadPoolManager = new ThreadPoolManager(2, 1);

        StubSocket socket = new StubSocket();
        threadPoolManager.run(new Http11Processor(socket));
        threadPoolManager.run(new Http11Processor(socket));
        threadPoolManager.run(new Http11Processor(socket));

        assertAll(
                () -> assertThat(threadPoolManager.getPoolSize()).isEqualTo(2),
                () -> assertThat(threadPoolManager.getQueueSize()).isEqualTo(1)
        );
    }

    @DisplayName("대기 가능 수보다 요청이 많다면 예외가 발생한다.")
    @Test
    void runWithOverRequest() {
        threadPoolManager = new ThreadPoolManager(2, 1);

        StubSocket socket = new StubSocket();

        assertThatThrownBy(() -> {
            threadPoolManager.run(new Http11Processor(socket));
            threadPoolManager.run(new Http11Processor(socket));
            threadPoolManager.run(new Http11Processor(socket));
            threadPoolManager.run(new Http11Processor(socket));
        });
    }
}
