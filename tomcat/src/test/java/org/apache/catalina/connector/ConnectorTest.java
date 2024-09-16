package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    @Nested
    class 스레드_풀 {

        @Test
        void 스레드_풀을_생성한다() {
            Connector connector = new Connector();
            int poolSize = connector.getMaximumPoolSize();
            assertThat(poolSize).isEqualTo(250);
        }
    }
}
