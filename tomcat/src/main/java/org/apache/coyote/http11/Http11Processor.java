package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String WELCOME_MESSAGE = "Hello world!";

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (var inputStream = connection.getInputStream();
             var outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = new HttpRequest(getRequestLine(inputStream));
            HttpResponse httpResponse = getResponse(httpRequest);
            String response = httpResponse.parseToString();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getRequestLine(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())));
        return reader.readLine();
    }

    public HttpResponse getResponse(HttpRequest httpRequest) throws IOException {
        try {
            String path = httpRequest.getUri().getPath();
            if (path.equals("/")) {
                return new HttpResponse(httpRequest.getProtocol(), HttpStatus.OK, ContentType.HTML, WELCOME_MESSAGE);
            }
            if (path.endsWith(".html")) {
                return new HttpResponse(httpRequest.getProtocol(), HttpStatus.OK, ContentType.HTML,
                        getStaticResponse(path));
            }
            if (path.endsWith(".css")) {
                return new HttpResponse(httpRequest.getProtocol(), HttpStatus.OK, ContentType.CSS,
                        getStaticResponse(path));
            }
            if (path.endsWith(".js")) {
                return new HttpResponse(httpRequest.getProtocol(), HttpStatus.OK, ContentType.JAVASCRIPT,
                        getStaticResponse(path));
            }

            Optional<User> user = InMemoryUserRepository.findByAccount(
                    httpRequest.getUri().getQueryParams().get("account"));
            user.ifPresent(value -> log.debug(value.toString()));
            String responseBody = getStaticResponse(path + ".html");
            return new HttpResponse(httpRequest.getProtocol(), HttpStatus.OK, ContentType.HTML, responseBody);
        } catch (RuntimeException e) {
            return new HttpResponse(httpRequest.getProtocol(), HttpStatus.NOT_FOUND, ContentType.HTML, null);
        }
    }

    private String getStaticResponse(String resourcePath) throws IOException {
        URL resourceURL = getClass().getClassLoader().getResource("static" + resourcePath);
        try {
            File file = new File(Objects.requireNonNull(resourceURL).toURI());
            Path path = file.getAbsoluteFile().toPath();
            return new String(Files.readAllBytes(path));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
