package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpQueryParser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.types.HttpMethod;
import org.apache.coyote.http11.request.HttpCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.http11.ViewResolver.resolveView;
import static org.apache.coyote.http11.types.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.types.HeaderType.LOCATION;
import static org.apache.coyote.http11.types.HeaderType.SET_COOKIE;
import static org.apache.coyote.http11.types.HttpProtocol.HTTP_1_1;
import static org.apache.coyote.http11.types.HttpStatus.FOUND;
import static org.apache.coyote.http11.types.HttpStatus.OK;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager sessionManager = new SessionManager();
    private static final String CONTENT_LENGTH = "Content-Length";
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

            HttpRequest request = readHttpRequest(bufferedReader, line);
            HttpResponse httpResponse = route(request);

            bufferedWriter.write(httpResponse.toResponseFormat());
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(BufferedReader bufferedReader, String line) throws IOException {
        Map<String, String> headers = readHeader(bufferedReader);
        String requestBody = readBody(bufferedReader, headers);
        return HttpRequest.of(line, headers, requestBody);
    }

    private Map<String, String> readHeader(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        for (String line = bufferedReader.readLine();
             !"".equals(line); line = bufferedReader.readLine()) {
            List<String> header = Arrays.stream(line.split(": ")).collect(Collectors.toList());
            headers.put(header.get(0), header.get(1));
        }
        return headers;
    }

    private String readBody(BufferedReader bufferedReader, Map<String, String> request) throws IOException {
        final String contentLength = request.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return null;
        }
        final int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return new String(buffer);
    }

    private HttpResponse route(HttpRequest request) throws IOException {
        if ("/".equals(request.getPath())) {
            return HttpResponse.of(HTTP_1_1, OK, "Hello world!", TEXT_HTML);
        }
        if (LOGIN_URI.equals(request.getPath()) && request.getMethod() == HttpMethod.POST) {
            return doLogin(request);
        }
        if (REGISTER_URI.equals(request.getPath()) && request.getMethod() == HttpMethod.POST) {
            return doRegister(request);
        }

        return createHttpResponse(request);
    }

    private HttpResponse createHttpResponse(HttpRequest request) throws IOException {
        HttpCookie cookie = HttpCookie.from(request.getHeader("Cookie"));
        if (LOGIN_URI.equals(request.getPath())
                && cookie != null
                && sessionManager.findSession(cookie.getJSessionId(false)) != null) {
            return redirectTo(INDEX_URI);
        }

        return resolveView(request.getPath());
    }

    private HttpResponse doLogin(HttpRequest request) {
        Map<String, String> queries = HttpQueryParser.parse(request.getPath());

        if (queries.isEmpty()) {
            return redirectTo(LOGIN_URI);
        }

        String account = queries.get("account");
        String password = queries.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElse(null);

        if (user != null && user.checkPassword(password)) {
            HttpCookie cookie = HttpCookie.from(request.getHeader("Cookie"));
            HttpResponse httpResponse = redirectTo(INDEX_URI);

            if (cookie.getJSessionId(false) == null) {
                String jSessionId = cookie.getJSessionId(true);
                httpResponse.addHeader(SET_COOKIE, String.format("JSESSIONID=%s", jSessionId));
                Session session = new Session(jSessionId);
                session.setAttribute("user", user);
                sessionManager.add(session);
            }

            if (log.isInfoEnabled()) {
                log.info(String.format("%s %s", "로그인 성공!", user));
            }
            return httpResponse;
        }

        return redirectTo(UNAUTHORIZED_URI);
    }

    private HttpResponse doRegister(HttpRequest request) {
        Map<String, String> queries = HttpQueryParser.parse(request.getPath());

        if (queries.isEmpty()) {
            return redirectTo(REGISTER_URI);
        }

        String account = queries.get("account");
        String password = queries.get("password");
        String email = queries.get("email");

        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            return redirectTo(REGISTER_URI);
        }

        User registUser = new User(account, password, email);
        InMemoryUserRepository.save(registUser);

        if (log.isInfoEnabled()) {
            log.info(String.format("%s %s", "회원가입 성공!", registUser));
        }

        return redirectTo(INDEX_URI);
    }

    private HttpResponse redirectTo(String path) {
        HttpResponse response = HttpResponse.of(HTTP_1_1, FOUND);
        response.addHeader(LOCATION, path);
        return response;
    }
}
