package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.service.UserService;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";

    private final Socket connection;
    private final HttpRequestReader httpRequestReader;
    private final HttpResponseBuilder httpResponseBuilder;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.httpRequestReader = new HttpRequestReader();
        this.httpResponseBuilder = new HttpResponseBuilder();
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

            HttpRequest httpRequest = httpRequestReader.read(inputStream);

            String path = httpRequest.path();
            if (path.startsWith("/login")) {
                Map<String, String> queries = httpRequest.queries();
                String account = queries.get("account");
                String password = queries.get("password");
                UserService.login(account, password);
            }

            HttpResponse response = httpResponseBuilder.build(path);
            outputStream.write(response.asString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
