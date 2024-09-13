package org.apache.catalina.connector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ConnectorTest {

    Connector connector;

    @AfterEach
    void tearDown() {
        connector.stop();
    }

    @DisplayName("최대 스레드만큼 요청시 풀 사이즈는 최대 스레드 수이며 대기열은 0이다.")
    @Test
    void run() {
        connector = new Connector(8081, 1, 2);

        connector.start();
        connector.start();

        final int expectedPoolSize = 2;
        final int expectedQueueSize = 0;

        assertAll(
                () -> assertThat(connector.getPoolSize()).isEqualTo(expectedPoolSize),
                () -> assertThat(connector.getQueueSize()).isEqualTo(expectedQueueSize)
        );
    }

    @DisplayName("최대 스레드 이상 요청시 풀 사이즈는 최대 스레드 수이며 나머지는 대기한다.")
    @Test
    void runWithOverRequestWithinAcceptCount() {
        connector = new Connector(8081, 1, 2);

        connector.start();
        connector.start();
        connector.start();

        final int expectedPoolSize = 2;
        final int expectedQueueSize = 1;

        assertAll(
                () -> assertThat(connector.getPoolSize()).isEqualTo(expectedPoolSize),
                () -> assertThat(connector.getQueueSize()).isEqualTo(expectedQueueSize)
        );
    }

    @DisplayName("대기 가능 수보다 요청이 많다면 예외가 발생한다.")
    @Test
    void runWithOverRequest() {
        connector = new Connector(8081, 1, 2);

        assertThatThrownBy(() -> {
            connector.start();
            connector.start();
            connector.start();
            connector.start();
        });
    }
}
