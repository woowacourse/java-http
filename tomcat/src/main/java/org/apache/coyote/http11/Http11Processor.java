package org.apache.coyote.http11;

import org.apache.coyote.Adapter;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Adapter adapter;
    private final ScheduledExecutorService timeoutExecutor;

    public Http11Processor(final Socket connection, final Adapter adapter) {
        this(connection, adapter, null);
    }

    public Http11Processor(final Socket connection, final Adapter adapter,
                           final ScheduledExecutorService timeoutExecutor) {
        this.timeoutExecutor = timeoutExecutor;
        this.connection = Objects.requireNonNull(connection);
        this.adapter = Objects.requireNonNull(adapter);
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = new BufferedInputStream(connection.getInputStream());
             final var outputStream = connection.getOutputStream()) {

            HttpResponse response = handle(connection, inputStream);
            response.writeTo(outputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handle(Socket connection, InputStream inputStream) {
        try {
            HttpRequest request = readRequest(connection, inputStream);
            HttpResponse response = new HttpResponse();

            try {
                adapter.service(request, response);
            } catch (Exception e) {
                log.error("요청 처리 중 오류가 발생했습니다.", e);
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR.getMessage());
            }
            return response;
        } catch (SocketTimeoutException e) {
            log.warn("Request timed out while reading from the connection.");
            return errorResponse(HttpStatus.REQUEST_TIMEOUT);
        } catch (UnsupportedHttpMethodException e) {
            log.warn(e.getMessage());
            return errorResponse(HttpStatus.NOT_IMPLEMENTED);
        } catch (HttpRequestParseException e) {
            log.warn(e.getMessage());
            return errorResponse(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("요청 처리 중 오류가 발생했습니다.", e);
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpRequest readRequest(Socket connection, InputStream inputStream) throws IOException {
        int timeoutMillis = connection.getSoTimeout();
        if (timeoutExecutor == null || timeoutMillis == 0) {
            return HttpRequest.parse(inputStream);
        }

        // 읽기 타임아웃과 같은 값을 전체 수신에도 적용하며, 데이터가 와도 타이머를 갱신하지 않는다.
        AtomicBoolean receiving = new AtomicBoolean(true);
        long startedAt = System.nanoTime();
        var timeout = timeoutExecutor.schedule(() -> {
            synchronized (connection) {
                if (receiving.getAndSet(false)) {
                    try {
                        // 입력만 종료하여 읽기를 깨우고, 출력은 408 응답을 위해 유지한다.
                        connection.shutdownInput();
                    } catch (IOException e) {
                        log.warn("Failed to stop reading a timed out request.", e);
                    }
                }
            }
        }, timeoutMillis, TimeUnit.MILLISECONDS);

        try {
            return HttpRequest.parse(inputStream);
        } finally {
            // 타이머가 이미 실행 중이어도 요청 처리 단계로 넘어간 뒤 입력을 닫지 않도록 한다.
            synchronized (connection) {
                boolean timedOut = !receiving.getAndSet(false);
                timeout.cancel(false);
                if (timedOut || System.nanoTime() - startedAt >= TimeUnit.MILLISECONDS.toNanos(timeoutMillis)) {
                    throw new SocketTimeoutException("Request reception timed out");
                }
            }
        }
    }

    private HttpResponse errorResponse(HttpStatus status) {
        HttpResponse response = new HttpResponse();
        response.sendError(status, status.getMessage());
        return response;
    }
}
