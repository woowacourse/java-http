package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestDispatcher requestDispatcher;
    private final SessionManager sessionManager;
    private final HttpRequestParser requestParser;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestDispatcher = new RequestDispatcher();
        this.sessionManager = SessionManager.getInstance();
        this.requestParser = HttpRequestParser.getInstance();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest request = requestParser.parseInput(inputStream);
            HttpResponse response = new HttpResponse();

            requestDispatcher.requestMapping(request, response);
            String resourceName = response.getResourceName();
            if (resourceName == null) {
                resourceName = request.getPath();
                response.setResourceName(resourceName);
            }
            String responseBody = getResource(resourceName);
            String resourceExtension = getExtension(resourceName);

            if (!request.hasCookieFrom("JSESSIONID")) {
                String sessionId = UUID.randomUUID().toString();
                response.setHttpCookie(HttpCookie.ofJSessionId(sessionId));
            }
            response.setResponseBody(responseBody);
            response.setContentType(resourceExtension);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getExtension(String resourceName) {
        if (resourceName == null) {
            return "html";
        }
        int extensionIndex = resourceName.indexOf('.') + 1;
        if (extensionIndex == 0) {
            return "html";
        }
        return resourceName.substring(extensionIndex);
    }

    private String getResource(String resourceName) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader()
                .getResource("static" + resourceName);

        if (resource == null) {
            return "Hello world!";
        }
        try {
            return Files.readString(Paths.get(resource.toURI()));
        } catch (IOException e) {
            return "Hello world!";
        }
    }
}
