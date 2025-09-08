package org.apache.coyote.http11;

import com.techcourse.service.UserService;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestReader httpRequestReader;
    private final HttpResourceHandler httpResourceHandler;
    private final HttpResponseWriter httpResponseWriter;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.httpRequestReader = new HttpRequestReader();
        this.httpResourceHandler = new HttpResourceHandler();
        this.httpResponseWriter = new HttpResponseWriter();
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
                try {
                    UserService.login(account, password);
                } catch (RuntimeException e) {
                    HttpResponse response = httpResourceHandler.handle("401.html");
                    httpResponseWriter.write(outputStream, response);
                }
                HttpResponse response = redirect("/index.html");
                httpResponseWriter.write(outputStream, response);
                return;
            }

            HttpResponse response = httpResourceHandler.handle(httpRequest);
            httpResponseWriter.write(outputStream, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse redirect(final String redirectUri) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", redirectUri);

        return new HttpResponse(HttpStatus.FOUND, headers, new byte[0]);
    }
}
