package org.apache.coyote.http11;

import com.techcourse.model.LoginService;
import com.techcourse.model.RegisterService;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.session.SimpleHttpSession;
import org.apache.catalina.session.SimpleManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final SimpleManager sessionManager = new SimpleManager();

    private final Socket connection;
    private final LoginService loginService = new LoginService();
    private final RegisterService registerService = new RegisterService();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    // TODO. Controller 인터페이스 추가하기
    @Override
    public void process(final Socket connection) {
        try (var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            
            final var request = HttpRequest.from(reader);
            final var cookie = RequestCookie.from(request.getHeader("Cookie"));
            final var extraHeaders = new LinkedHashMap<String, List<String>>();
            final var session = resolveSession(cookie, extraHeaders);

            final var method = request.getMethod();
            final var path = request.getPath();
            final var params = request.getParameters();

            if ("/login".equals(path)) {
                handleLoginRequest(method, session, params, outputStream, extraHeaders);
                return;
            }

            if ("/register".equals(path)) {
                handleRegisterRequest(method, params, outputStream, extraHeaders);
                return;
            }

            handleStaticResource(path, outputStream, extraHeaders);
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            sendInternalServerErrorResponse(connection);
        }
    }

    private HttpSession resolveSession(
            final RequestCookie cookie,
            final LinkedHashMap<String, List<String>> extraHeaders
    ) {
        if (cookie.contains("JSESSIONID")) {
            final var sessionId = cookie.get("JSESSIONID");
            final var found = sessionManager.findSession(sessionId);
            if (found != null) {
                return found;
            }
        }

        final var newSession = SimpleHttpSession.ofGeneratedId();
        sessionManager.add(newSession);
        extraHeaders.put("Set-Cookie", List.of("JSESSIONID=" + newSession.getId()));

        return newSession;
    }

    private void handleLoginRequest(
            final String method,
            final HttpSession session,
            final Map<String, String> params,
            final OutputStream outputStream,
            final Map<String, List<String>> extraHeaders
    ) throws IOException, URISyntaxException {
        if ("GET".equalsIgnoreCase(method)) {
            handleLoginGet(session, outputStream, extraHeaders);
            return;
        }

        if ("POST".equalsIgnoreCase(method)) {
            handleLoginPost(params, session, outputStream, extraHeaders);
        }
    }

    private void handleLoginGet(
            final HttpSession session,
            final OutputStream outputStream,
            final Map<String, List<String>> extraHeaders
    ) throws IOException, URISyntaxException {
        if (session.getAttribute("user") != null) {
            redirect(outputStream, extraHeaders, "/index.html");
            return;
        }

        final var resourceUrl = Objects.requireNonNull(
                getClass().getClassLoader()
                        .getResource("static/login.html")
        );
        final var body = readResourceFile(resourceUrl);
        writeResponse(outputStream, 200, "OK", extraHeaders, body, "text/html;charset=utf-8");
    }

    private void handleLoginPost(
            final Map<String, String> params,
            final HttpSession session,
            final OutputStream outputStream,
            final Map<String, List<String>> extraHeaders
    ) throws IOException {
        final var account = params.get("account");
        final var password = params.get("password");

        if (loginService.login(account, password, session)) {
            redirect(outputStream, extraHeaders, "/index.html");
            return;
        }

        redirect(outputStream, extraHeaders, "/401.html");
    }

    private void handleRegisterRequest(
            final String method,
            final Map<String, String> params,
            final OutputStream outputStream,
            final Map<String, List<String>> extraHeaders
    ) throws IOException, URISyntaxException {
        if ("GET".equalsIgnoreCase(method)) {
            handleRegisterGet(outputStream, extraHeaders);
            return;
        }

        if ("POST".equalsIgnoreCase(method)) {
            handleRegisterPost(params, outputStream, extraHeaders);
        }
    }

    private void handleRegisterGet(
            final OutputStream outputStream,
            final Map<String, List<String>> extraHeaders
    ) throws IOException, URISyntaxException {
        final var resourceUrl = Objects.requireNonNull(
                getClass().getClassLoader()
                        .getResource("static/register.html")
        );
        final var body = readResourceFile(resourceUrl);

        writeResponse(outputStream, 200, "OK", extraHeaders, body, "text/html;charset=utf-8");
    }

    private void handleRegisterPost(
            final Map<String, String> params,
            final OutputStream outputStream,
            final Map<String, List<String>> extraHeaders
    ) throws IOException {
        final var account = params.get("account");
        final var password = params.get("password");
        final var email = params.get("email");

        registerService.register(account, password, email);
        redirect(outputStream, extraHeaders, "/index.html");
    }

    private void redirect(
            final OutputStream outputStream,
            final Map<String, List<String>> extraHeaders,
            final String location
    ) throws IOException {
        extraHeaders.put("Location", List.of(location));
        writeResponse(outputStream, 302, "Found", extraHeaders, null, "text/html;charset=utf-8");
    }

    private void handleStaticResource(
            final String path,
            final OutputStream outputStream,
            final Map<String, List<String>> extraHeaders
    ) throws IOException, URISyntaxException {
        final var staticResourcePath = "static" + path;
        final var staticResourceUrl = getClass().getClassLoader()
                .getResource(staticResourcePath);

        if (isValidStaticResource(staticResourceUrl)) {
            final var body = readResourceFile(staticResourceUrl);
            final var contentType = detectContentType(staticResourceUrl);

            writeResponse(outputStream, 200, "OK", extraHeaders, body, contentType);
            return;
        }

        final var body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        writeResponse(outputStream, 200, "OK", extraHeaders, body, "text/html;charset=utf-8");
    }

    private boolean isValidStaticResource(final URL resourceUrl) throws URISyntaxException {
        return resourceUrl != null && !Files.isDirectory(Path.of(resourceUrl.toURI()));
    }

    private byte[] readResourceFile(final URL resourceUrl) throws IOException, URISyntaxException {
        final var path = Path.of(resourceUrl.toURI());

        return Files.readAllBytes(path);
    }

    private String detectContentType(final URL resourceUrl) throws IOException, URISyntaxException {
        final var path = Path.of(resourceUrl.toURI());
        final var contentType = Files.probeContentType(path);

        return contentType != null ? contentType : "text/plain;charset=utf-8";
    }

    // TODO. HttpResponse 클래스 구현하기
    private void writeResponse(
            final OutputStream outputStream,
            final int statusCode,
            final String statusMessage,
            final Map<String, List<String>> extraHeaders,
            final byte[] body,
            final String contentType
    ) throws IOException {
        final var headers = new StringBuilder()
                .append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(statusMessage)
                .append("\r\n")
                .append("Content-Type: ")
                .append(contentType)
                .append("\r\n");

        if (body != null) {
            headers.append("Content-Length: ")
                    .append(body.length)
                    .append("\r\n");
        }

        if (extraHeaders != null) {
            extraHeaders.forEach((key, values) -> values.forEach(value ->
                    headers.append(key)
                            .append(": ")
                            .append(value)
                            .append("\r\n")));
        }

        headers.append("\r\n");
        outputStream.write(headers.toString()
                .getBytes(StandardCharsets.UTF_8));

        if (body != null) {
            outputStream.write(body);
        }

        outputStream.flush();
    }

    private void sendInternalServerErrorResponse(final Socket connection) {
        try {
            final var resourceUrl = getClass().getClassLoader()
                    .getResource("static/500.html");
            final var body = (resourceUrl != null)
                    ? readResourceFile(resourceUrl)
                    : "Internal Server Error".getBytes(StandardCharsets.UTF_8);

            writeResponse(
                    connection.getOutputStream(),
                    500,
                    "Internal Server Error",
                    Map.of(),
                    body,
                    "text/html;charset=utf-8"
            );
        } catch (Exception ex) {
            log.error("Failed to send {} response: {}", 500, ex.getMessage(), ex);
        }
    }
}
