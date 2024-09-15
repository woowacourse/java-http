package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.concurrent.ThreadPoolExecutor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConnectorTest {

    private static final Logger log = LoggerFactory.getLogger(ConnectorTest.class);

    @Test
    @DisplayName("풀 사이즈와 큐 사이즈 확인")
    void poolSizeAndQueueSizeTest() {
        //given
        final var executor = (ThreadPoolExecutor) new Connector().getExecutorService();
        final int expectedPoolSize = 10;
        final int expectedQueueSize = 100;


        //when
        for (int i = 0; i < 110; i++) {
            executor.submit(logWithSleep("hello fixed thread pools " + i));
        }


        //then
        assertAll(
                () -> assertThat(executor.getPoolSize()).isEqualTo(expectedPoolSize),
                () -> assertThat(executor.getQueue()).hasSize(expectedQueueSize)
        );

    }


    private Runnable logWithSleep(final String message) {
        return () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info(message);
        };
    }
}
