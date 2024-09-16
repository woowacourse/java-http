package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    @Test
    @DisplayName("Tomcat 서버가 시작되면 지정된 크기의 Thread Pool이 생성된다.")
    void creatThreadsWhenTomcatStarted() {
        Connector connector = new Connector();
        connector.start();

        final int threadPoolSize = connector.getMaxThreads();
        connector.stop();

        assertThat(threadPoolSize).isEqualTo(250);
    }
}
