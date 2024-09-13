package org.apache.coyote;

import com.techcourse.controller.ResourceController;
import org.apache.catalina.connector.CatalinaConnectionListener;
import org.apache.catalina.connector.ConnectionListener;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.adapter.CoyoteAdapter;
import org.apache.coyote.http11.request.RequestMapper;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.TestController;
import support.TestHttpUtils;
import support.TestServer;

import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadTest {

    @Test
    @DisplayName("250개의 요청을 받고, 100개를 대기한다.")
    void some() throws InterruptedException {
        final var controller = new TestController();
        final var mapper = new RequestMapper(Map.of("/test", controller));
        final ResourceController resourceController = new ResourceController();
        final SessionManager sessionManager = new SessionManager();

        final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);
        final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                0, 250, 0L,
                //DiscardPolicy 일시 500개는 다 받음.
                TimeUnit.MILLISECONDS, queue, new ThreadPoolExecutor.AbortPolicy());

        TestServer.serverStart(createTomcat(mapper, resourceController, sessionManager, executorService));

        final ExecutorService httpExecutorService = Executors.newFixedThreadPool(500);

        IntStream.range(0, 500)
                .forEach(count -> httpExecutorService.submit(() -> TestHttpUtils.sendGet(TestServer.getHost(), "/test")));

        httpExecutorService.awaitTermination(4, TimeUnit.SECONDS);
        assertThat(executorService.getActiveCount()).isEqualTo(250);
        assertThat(executorService.getQueue()).hasSize(100);
    }


    private static Tomcat createTomcat(final RequestMapper requestMapper, final ResourceController resourceController, final SessionManager sessionManager,
                                       final ExecutorService executorService) {

        final ConnectionListener connectionListener = new CatalinaConnectionListener(
                new CoyoteAdapter(requestMapper, resourceController, sessionManager),
                executorService
        );
        final Connector connector = new Connector(11240, 100, connectionListener);

        return new Tomcat(connector);
    }
}
