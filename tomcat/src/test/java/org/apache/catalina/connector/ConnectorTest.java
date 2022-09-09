package org.apache.catalina.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectorTest {

    private static final Logger log = LoggerFactory.getLogger(ConnectorTest.class);
    private static final AtomicInteger count = new AtomicInteger(0);

//    @Test
    void 최대_요청을_실행한다() throws InterruptedException {
        // given
        final Connector connector = new Connector();
        connector.start();

        final var NUMBER_OF_THREAD = 250;
        var threads = new Thread[NUMBER_OF_THREAD];

        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            threads[i] = new Thread(() -> incrementIfOk(TestHttpUtils.send("/")));
        }

        for (final var thread : threads) {
            thread.start();
            Thread.sleep(110);
        }

        for (final var thread : threads) {
            thread.join();
        }

        assertThat(count.intValue()).isEqualTo(NUMBER_OF_THREAD);
    }

    private static void incrementIfOk(final HttpResponse<String> response) {
        if(response == null) {
            log.info("Response is Null!!");
            return;
        }
        if (response.statusCode() == 200) {
            count.incrementAndGet();
        }
    }
}
