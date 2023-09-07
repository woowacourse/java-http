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
import org.apache.coyote.http11.session.HttpSession;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager sessionManager = SessionManager.create();
    private static final String STATIC = "static";
    private static final String INDEX_HTML = "/index.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";
    private static final String LOGIN_HTML = "/login.html";

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
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final HttpCookie httpCookie = makeHttpCookie(httpRequest);

            final HttpResponseEntity httpResponseEntity = makeResponseEntity(httpRequest, httpCookie);
            final String response = makeResponse(httpResponseEntity);

            bufferedWriter.write(response);
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpCookie makeHttpCookie(final HttpRequest httpRequest) {
        if (httpRequest.hasCookie()) {
            return HttpCookie.from(httpRequest.getCookie());
        }
        return HttpCookie.empty();
    }

    private HttpResponseEntity makeResponseEntity(final HttpRequest httpRequest, final HttpCookie cookie) {
        final HttpRequestStartLine startLine = httpRequest.getStartLine();

        if (startLine.getHttpMethod().equals(HttpMethod.POST)) {
            if (startLine.getPath().startsWith("/login")) {
                return login(httpRequest);
            }
            if (startLine.getPath().startsWith("/register")) {
                return register(httpRequest);
            }
        }
        if (startLine.getPath().startsWith("/login") && cookie.hasJSESSIONID()) {
            final String jsessionid = cookie.getJSESSIONID();
            HttpSession httpSession = sessionManager.findSession(jsessionid);
            if (Objects.isNull(httpSession)) {
                return HttpResponseEntity.ok(LOGIN_HTML);
            }
            return HttpResponseEntity.found(INDEX_HTML);
        }
        return HttpResponseEntity.ok(startLine.getPath());
    }

    private HttpResponseEntity register(final HttpRequest httpRequest) {
        final String body = httpRequest.getBody();
        final Map<String, String> registerData = Arrays.stream(body.split("&"))
                .map(data -> data.split("="))
                .collect(Collectors.toMap(
                        data -> data[0],
                        data -> data[1])
                );
        InMemoryUserRepository.save(new User(registerData.get("account"), registerData.get("password"), registerData.get("email")));
        return HttpResponseEntity.ok(INDEX_HTML);
    }

    private HttpResponseEntity login(final HttpRequest httpRequest) {
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
            final HttpCookie newCookie = HttpCookie.create();
            final HttpSession httpSession = new HttpSession(newCookie.getJSESSIONID());
            httpSession.setAttribute("user", user);
            sessionManager.add(httpSession);
            final HttpResponseEntity httpResponseEntity = HttpResponseEntity.found(INDEX_HTML);
            httpResponseEntity.addHeader("Set-Cookie: JSESSIONID=", newCookie.getJSESSIONID());
            return httpResponseEntity;
        }
        return HttpResponseEntity.ok(UNAUTHORIZED_HTML);
    }

    private String makeResponse(final HttpResponseEntity httpResponseEntity) throws IOException {
        final String path = httpResponseEntity.getPath();
        final String responseBody = makeResponseBody(path);
        final String contentType = makeContentType(path);
        final HttpStatusCode httpStatusCode = httpResponseEntity.getHttpStatusCode();

        StringJoiner stringJoiner = new StringJoiner("\r\n");
        stringJoiner.add("HTTP/1.1 " + HttpStatusCode.message(httpStatusCode));

        for (final Entry<String, String> entry : httpResponseEntity.getAdditionalHeader().entrySet()) {
            stringJoiner.add(entry.getKey() + entry.getValue());
        }

        if (httpStatusCode != HttpStatusCode.FOUND) {
            stringJoiner.add("Content-Type: " + contentType + ";charset=utf-8 ");
            stringJoiner.add("Content-Length: " + responseBody.getBytes().length + " ");
            stringJoiner.add("");
            stringJoiner.add(responseBody);
        }

        return stringJoiner.toString();
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
