package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.requests.HttpCookie;
import org.apache.coyote.requests.RequestBody;
import org.apache.coyote.requests.RequestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;

import static org.apache.coyote.http11.ContentType.TEXT_HTML;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager sessionManager = new SessionManager();
    private static final String HTTP_11 = "HTTP/1.1";
    private static final String INDEX_URI = "/index";
    private static final String REGISTER_URI = "/register";
    private static final String LOGIN_URI = "/login";
    private static final String UNAUTHORIZED_URI = "/401.html";

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
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }

            String httpMethod = Arrays.stream(line.split(" "))
                    .findFirst()
                    .orElseGet(() -> "");

            String uri = Arrays.stream(line.split(" "))
                    .filter(it -> it.startsWith("/"))
                    .findAny()
                    .orElseGet(() -> "");

            RequestHeader requestHeader = readHeader(bufferedReader);
            RequestBody requestBody = readBody(bufferedReader, requestHeader);

            HttpResponse httpResponse = routeByHttpMethod(uri, httpMethod, requestHeader, requestBody);
            String header = createHeader(httpResponse);

            bufferedWriter.write(header + httpResponse.getBody());
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse routeByHttpMethod(String uri, String httpMethod, RequestHeader requestHeader, RequestBody requestBody) throws IOException {
        if (httpMethod.equals("POST")) {
            return routePost(uri, requestHeader, requestBody);
        }

        return createHttpResponse(uri, requestHeader);
    }

    private HttpResponse routePost(String uri, RequestHeader requestHeader, RequestBody requestBody) throws IOException {
        if (REGISTER_URI.equals(uri)) {
            return doRegister(requestBody);
        }

        if (LOGIN_URI.equals(uri)) {
            return doLogin(requestHeader, requestBody);
        }
        return createHttpResponse(uri, requestHeader);
    }

    private RequestHeader readHeader(BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        for (String line = bufferedReader.readLine();
             !"".equals(line); line = bufferedReader.readLine()) {
            stringBuilder.append(line).append("\r\n");
        }
        return RequestHeader.from(stringBuilder.toString());
    }

    private RequestBody readBody(BufferedReader bufferedReader, RequestHeader requestHeader) throws IOException {
        final String contentLength = requestHeader.get("Content-Length");
        if (contentLength == null) {
            return new RequestBody();
        }
        final int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return new RequestBody(new String(buffer));
    }

    private HttpResponse createHttpResponse(String uri, RequestHeader requestHeader) throws IOException {
        String path = uri;
        int index = uri.indexOf("?");
        if (index != -1) {
            path = uri.substring(0, index);
        }

        HttpCookie cookie = HttpCookie.from(requestHeader.get("Cookie"));
        if (LOGIN_URI.equals(path)
                && cookie != null
                && sessionManager.findSession(cookie.getJSessionId(false)) != null) {
            return ViewResolver.resolveView(INDEX_URI);
        }
        return ViewResolver.routePath(path);
    }

    private String createHeader(HttpResponse response) {
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        HttpStatus httpStatus = response.getHttpStatus();

        stringJoiner.add(String.format("%s %d %s ", HTTP_11, httpStatus.getCode(), httpStatus.getMessage()));

        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            stringJoiner.add(String.format("%s: %s ", entry.getKey(), entry.getValue()));
        }

        if (httpStatus.equals(HttpStatus.FOUND)) {
            stringJoiner.add(toHeaderFormat("Location", response.getBody()));
            stringJoiner.add("\r\n");
            return stringJoiner.toString();
        }
        stringJoiner.add(toHeaderFormat("Content-Type", response.getContentType()));
        stringJoiner.add(String.format("%s %s ", "Content-Length:", response.getBody().getBytes().length));
        stringJoiner.add("\r\n");
        return stringJoiner.toString();
    }

    private static String toHeaderFormat(String name, String value) {
        return String.format("%s: %s ", name, value);
    }

    private HttpResponse doLogin(RequestHeader requestHeader, RequestBody requestBody) throws IOException {
        String query = requestBody.getItem();
        if (query.isBlank()) {
            return ViewResolver.resolveView(LOGIN_URI);
        }

        String account = "";
        String password = "";

        for (String parameter : query.split("&")) {
            int idx = parameter.indexOf("=");
            String key = parameter.substring(0, idx);
            String value = parameter.substring(idx + 1);
            if ("account".equals(key)) {
                account = value;
            }
            if ("password".equals(key)) {
                password = value;
            }
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElse(null);

        if (user != null && user.checkPassword(password)) {
            HttpCookie cookie = HttpCookie.from(requestHeader.get("Cookie"));
            HttpResponse httpResponse = new HttpResponse(INDEX_URI, HttpStatus.FOUND, TEXT_HTML);

            if (cookie.getJSessionId(false) == null) {
                String jSessionId = cookie.getJSessionId(true);
                httpResponse.addHeader("Set-Cookie", "JSESSIONID=" + jSessionId);
                Session session = new Session(jSessionId);
                session.setAttribute("user", user);
                sessionManager.add(session);
            }

            if (log.isInfoEnabled()) {
                log.info(String.format("%s %s", "로그인 성공!", user));
            }
            return httpResponse;
        }

        return ViewResolver.resolveView(UNAUTHORIZED_URI);
    }

    private HttpResponse doRegister(RequestBody requestBody) throws IOException {
        String query = requestBody.getItem();
        if (query.isBlank()) {
            return ViewResolver.resolveView(REGISTER_URI);
        }

        String account = "";
        String password = "";
        String email = "";

        for (String parameter : query.split("&")) {
            int idx = parameter.indexOf("=");
            String key = parameter.substring(0, idx);
            String value = parameter.substring(idx + 1);

            if ("account".equals(key)) {
                account = value;
            }
            if ("password".equals(key)) {
                password = value;
            }
            if ("email".equals(key)) {
                email = value;
            }
        }

        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            return ViewResolver.resolveView(REGISTER_URI);
        }

        User registUser = new User(account, password, email);
        InMemoryUserRepository.save(registUser);

        if (log.isInfoEnabled()) {
            log.info(String.format("%s %s", "회원가입 성공!", registUser));
        }

        return ViewResolver.resolveView(LOGIN_URI);
    }
}
