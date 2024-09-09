package org.apache.catalina.connector;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.executor.Executor;
import org.apache.coyote.http11.executor.RequestExecutors;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.TestHttpUtils;

import java.net.ServerSocket;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectorTest {
    @Test
    @DisplayName("500개 요청 중 250개를 수행하고, 100개만 대기가 된다.")
    void some() throws InterruptedException {
        final ServerSocket serverSocket = ServerSocketBuilder.createServerSocket(8080, 100);

        final Tomcat tomcat = new Tomcat(
                new Connector(
                        serverSocket,
                        executorService,
                        requestExecutors,
                        sessionManager
                )
        );

        serverStart(tomcat);

        final ExecutorService httpExecutorService = Executors.newFixedThreadPool(500);

        IntStream.range(0, 500)
                .forEach(count -> httpExecutorService.submit(() -> TestHttpUtils.send("/hello")));

        httpExecutorService.awaitTermination(2, TimeUnit.SECONDS);
        assertThat(executorService.getActiveCount()).isEqualTo(250);
        assertThat(executorService.getQueue()).hasSize(100);
    }

    private void serverStart(final Tomcat tomcat) {
        final Runnable serverTask = () -> tomcat.start();
        final Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    private final RequestExecutors requestExecutors = new RequestExecutors(
            Collections.singletonList(new Executor() {

                @Override
                public HttpResponse execute(final HttpRequest req) {
                    try {
                        Thread.sleep(8000);
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new HttpResponse(
                            HttpStatusCode.OK,
                            new Headers(),
                            "HTTP/1.1",
                            new byte[]{}
                    );
                }

                @Override
                public boolean isMatch(final HttpRequest req) {
                    return true;
                }
            })
    );
    private final SessionManager sessionManager = new SessionManager();
    BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);
    ThreadPoolExecutor executorService = new ThreadPoolExecutor(
            0, 250, 0L, TimeUnit.MILLISECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy());

}
