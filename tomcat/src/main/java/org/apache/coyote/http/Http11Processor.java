package org.apache.coyote.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Processor;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

@Slf4j
@RequiredArgsConstructor
public class Http11Processor implements Runnable, Processor {

    private final Socket connection;

    @Override
    public void run() {
        log.info("연결된 호스트: {}, 포트: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest request = HttpRequestParser.getInstance().parse(inputStream);
            final HttpResponse response = HttpRequestDispatcher.getInstance().execute(request);

            outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (final Exception e) {
            log.error("요청 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
