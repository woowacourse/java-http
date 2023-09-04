package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.HttpResponseEntity;
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
            HttpCookie cookie = HttpCookie.empty();
            if (httpRequest.hasCookie()) {
                cookie = HttpCookie.from(httpRequest.getCookie());
            }

            final HttpResponseEntity httpResponseEntity = makeResponseEntity(httpRequest, cookie);
            final String response = makeResponse(httpResponseEntity);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponseEntity makeResponseEntity(final HttpRequest httpRequest, final HttpCookie cookie) {
        final HttpRequestStartLine startLine = httpRequest.getStartLine();
        if (startLine.getHttpMethod().equals(HttpMethod.POST)) {
            if (startLine.getPath().startsWith("/login")) {
                return login(httpRequest, cookie);
            }
            if (startLine.getPath().startsWith("/register")) {
                return register(httpRequest, cookie);
            }
        }
        return new HttpResponseEntity(startLine.getPath(), cookie, HttpStatusCode.OK);
    }

    private HttpResponseEntity register(final HttpRequest httpRequest, final HttpCookie cookie) {
        final String body = httpRequest.getBody();
        final Map<String, String> registerData = Arrays.stream(body.split("&"))
                .map(data -> data.split("="))
                .collect(Collectors.toMap(
                        data -> data[0],
                        data -> data[1])
                );
        InMemoryUserRepository.save(new User(registerData.get("account"), registerData.get("password"), registerData.get("email")));
        return new HttpResponseEntity(INDEX_HTML, cookie, HttpStatusCode.OK);
    }

    private HttpResponseEntity login(final HttpRequest httpRequest, final HttpCookie cookie) {
        if (cookie.hasJSESSIONID()) {
            return new HttpResponseEntity(INDEX_HTML, cookie, HttpStatusCode.OK);
        }
        final String body = httpRequest.getBody();
        final Map<String, String> loginData = Arrays.stream(body.split("&"))
                .map(data -> data.split("="))
                .collect(Collectors.toMap(
                        data -> data[0],
                        data -> data[1])
                );
        final User user = InMemoryUserRepository.findByAccount(loginData.get("account"))
                .orElseThrow();
        if (user.checkPassword(loginData.get("password"))) {
            return new HttpResponseEntity(INDEX_HTML, HttpCookie.create(), HttpStatusCode.FOUND);
        }
        return new HttpResponseEntity(UNAUTHORIZED_HTML, HttpCookie.empty(), HttpStatusCode.OK);
    }

    private String makeResponse(final HttpResponseEntity httpResponseEntity) throws IOException {
        final String path = httpResponseEntity.getPath();
        final String responseBody = makeResponseBody(path);
        final String contentType = makeContentType(path);
        final HttpCookie cookie = httpResponseEntity.getCookie();
        final HttpStatusCode httpStatusCode = httpResponseEntity.getHttpStatusCode();

        if (cookie.hasJSESSIONID()) {
            return String.join("\r\n",
                    "HTTP/1.1 " + HttpStatusCode.message(httpStatusCode),
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "Set-Cookie: JSESSIONID=" + cookie.getJSESSIONID() + " ",
                    "",
                    responseBody);
        }
        return String.join("\r\n",
                "HTTP/1.1 " + HttpStatusCode.message(httpStatusCode),
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String makeResponseBody(final String path) throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }
        final ClassLoader classLoader = getClass().getClassLoader();
        final String filePath = classLoader.getResource(STATIC + path).getPath();
        final String fileContent = new String(Files.readAllBytes(Path.of(filePath)));
        return String.join("\r\n", fileContent);
    }

    private String makeContentType(final String path) {
        if (path.endsWith("css")) {
            return "text/css";
        }
        return "text/html";
    }
}
