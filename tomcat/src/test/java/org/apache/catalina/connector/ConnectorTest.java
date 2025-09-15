package org.apache.catalina.connector;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.TestConnector;

class ConnectorTest {

    private static final int TEST_PORT = 8081;
    private static final String LOCALHOST = "127.0.0.1";

    private static final Logger log = LoggerFactory.getLogger(ConnectorTest.class);


    @DisplayName("acceptCount가 작을 때 클라이언트가 연결에 실패할 수 있다.")
    @Test
    void a() throws InterruptedException {
        int acceptCount = 3;
        int maxThreads = 1;
        int requestCount = 10;

        TestConnector testConnector = new TestConnector(TEST_PORT, acceptCount, maxThreads);
        testConnector.start();

        Thread[] requestThreads = new Thread[requestCount];

        for (int i = 0; i < requestCount; i++) {
            final int num = i + 1;
            requestThreads[i] = new Thread(
                    () -> {
                        log.info("{} 요청 시작", num);
                        try (Socket socket = new Socket(LOCALHOST, TEST_PORT)) {
                            log.info("[{}] ✅ 연결 성공", num);
                        } catch (ConnectException e) {
                            log.info("[{}] ❌ 연결 거부: {}", num, e.getMessage());
                        } catch (IOException e) {
                            log.info("[{}] ❌ 기타 오류", num);
                        }
                    }
            );
        }

        for (int i = 0; i < requestCount; i++) {
            requestThreads[i].start();
        }

        for (int i = 0; i < requestCount; i++) {
            requestThreads[i].join();
        }
    }
}
