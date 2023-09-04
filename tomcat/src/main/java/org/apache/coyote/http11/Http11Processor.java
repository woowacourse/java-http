package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC = "static";
    private static final String INDEX_HTML = "/index.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";

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

            final HttpRequest httpRequest = HttpRequest.from(inputStream);
            final HttpRequestStartLine startLine = httpRequest.getStartLine();

            String path = startLine.getPath();
            HttpStatusCode httpStatusCode = HttpStatusCode.OK;

            if (startLine.getHttpMethod().equals(HttpMethod.POST)) {
                if (startLine.getPath().startsWith("/login")) {
                    final String body = httpRequest.getBody();
                    Map<String, String> loginData = Arrays.stream(body.split("&"))
                            .map(data -> data.split("="))
                            .collect(Collectors.toMap(
                                    data -> data[0],
                                    data -> data[1])
                            );
                    final User user = InMemoryUserRepository.findByAccount(loginData.get("account"))
                            .orElseThrow();
                    if (user.checkPassword(loginData.get("password"))) {
                        path = INDEX_HTML;
                        httpStatusCode = HttpStatusCode.FOUND;
                    }
                    if (!user.checkPassword(loginData.get("password"))) {
                        path = UNAUTHORIZED_HTML;
                    }
                }
                if (startLine.getPath().startsWith("/register")) {
                    final String body = httpRequest.getBody();
                    Map<String, String> registerData = Arrays.stream(body.split("&"))
                            .map(data -> data.split("="))
                            .collect(Collectors.toMap(
                                    data -> data[0],
                                    data -> data[1])
                            );
                    InMemoryUserRepository.save(new User(registerData.get("account"), registerData.get("password"), registerData.get("email")));
                    path = INDEX_HTML;
                }
            }

            final String responseBody = makeResponseBody(path);
            final String contentType = makeContentType(path);
            final String response = makeResponse(responseBody, contentType, HttpStatusCode.message(httpStatusCode));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponseBody(final String url) throws IOException {
        if (url.equals("/")) {
            return "Hello world!";
        }
        final ClassLoader classLoader = getClass().getClassLoader();
        final String filePath = classLoader.getResource(STATIC + url).getPath();
        final String fileContent = new String(Files.readAllBytes(Path.of(filePath)));
        return String.join("\r\n", fileContent);
    }

    private String makeContentType(final String url) {
        if (url.endsWith("css")) {
            return "text/css";
        }
        return "text/html";
    }

    private String makeResponse(final String responseBody, final String contentType, final String httpStatusCode) {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode,
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
