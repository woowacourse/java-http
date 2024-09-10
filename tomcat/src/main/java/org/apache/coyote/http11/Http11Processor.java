package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.servlet.RequestMapper;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.body.HttpResponseBody;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.startline.HttpResponseLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapper requestMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapper = new RequestMapper();
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
            HttpRequest request = HttpRequest.parse(inputStream);
            HttpResponse response = new HttpResponse(
                    new HttpResponseLine(request.getHttpVersion()),
                    new HttpHeaders(),
                    new HttpResponseBody()
            );

            Controller controller = requestMapper.getController(request);
            boolean isResponseValid = controller.service(request, response);

            if (!isResponseValid) {
                throw new IllegalArgumentException("response not valid: \r\n" + response);
            }
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
