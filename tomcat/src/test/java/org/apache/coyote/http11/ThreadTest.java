package org.apache.coyote.http11;

import org.apache.catalina.connector.Connector;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ThreadTest {
    private Connector connector;
    @BeforeEach
    void before() {
         connector = new Connector(8081, 0, 2);
    }
    @AfterEach
    void after(){
        connector.close();
    }

    @Test
    @DisplayName("스레드의 수만큼 스레드풀의이 생성된다.")
    void threadPoolSize() {
        int poolSize = connector.getPoolSize();

        Assertions.assertThat(poolSize).isSameAs(2);
    }

    @Test
    @DisplayName("스레드가 모두 바쁘면 처리되지 못하고 queue에서 대기한다.")
    void go() {
        connector.submit(() -> sleep(3000));
        connector.submit(() -> sleep(3000));

        connector.submit(() -> sleep(3000));
        connector.submit(() -> sleep(3000));

        Assertions.assertThat(connector.getQueueSize()).isEqualTo(2);
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
