package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.RequestMapping;
import org.apache.catalina.session.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping REQUEST_MAPPING = new RequestMapping();

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
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
            process(inputStream, outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static byte[] process(final byte[] requestBytes) {
        final var outputStream = new ByteArrayOutputStream();
        try {
            process(new ByteArrayInputStream(requestBytes), outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return outputStream.toByteArray();
    }

    private static void process(final InputStream inputStream, final OutputStream outputStream) throws Exception {
        HttpRequest request = HttpRequest.from(inputStream);
        HttpResponse response = new HttpResponse(outputStream);

        final Optional<String> requestedSessionId = request.getCookies().getValue("JSESSIONID");
        final Session session = request.getSession(true);
        if (requestedSessionId.filter(session.getId()::equals).isEmpty()) {
            response.addCookie("JSESSIONID", session.getId());
        }

        final Controller controller = REQUEST_MAPPING.getController(request);
        controller.service(request, response);
    }
}
