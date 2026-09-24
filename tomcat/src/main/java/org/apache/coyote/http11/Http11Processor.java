package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.web.controller.Controller;
import com.techcourse.web.routing.RequestMapping;
import org.apache.coyote.Processor;
import org.apache.session.Session;
import org.apache.session.SessionManager;
import org.apache.session.SessionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final RequestMapping requestMapping;
    private final Socket connection;

    public Http11Processor(RequestMapping requestMapping, Socket connection) {
        this.requestMapping = requestMapping;
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
             final var outputStream = connection.getOutputStream();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)
        ) {
            HttpRequest request = HttpRequest.from(bufferedInputStream);
            HttpResponse response = new HttpResponse();

            Controller controller = requestMapping.getController(request);
            controller.service(request, response);
            writeAndFlush(outputStream, response);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void writeAndFlush(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }
}
