package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.ProcessBroker;
import org.apache.coyote.Processor;
import org.apache.coyote.util.HttpParser;
import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ProcessBroker processBroker;

    public Http11Processor(final Socket connection, final ProcessBroker processBroker) {
        this.connection = connection;
        this.processBroker = processBroker;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = HttpParser.parseToRequest(inputStream);
            log.info("HTTP 요청객체 생성 완료");

            final HttpResponse httpResponse = new HttpResponse();
            processBroker.brokeRequest(httpRequest, httpResponse);

            outputStream.write(httpResponse.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
