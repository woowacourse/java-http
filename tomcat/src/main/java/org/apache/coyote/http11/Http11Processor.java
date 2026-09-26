package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    private static final String PATH_INDEX_HTML = "/index.html";
    private static final String PATH_LOGIN_HTML = "/login.html";
    private static final String PATH_REGISTER_HTML = "/register.html";
    private static final String PATH_401_HTML = "401.html";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String HTTP_STATUS_OK = "200 OK";

    private static final String USER = "user";

    private static final SessionManager SESSION_MANAGER = new SessionManager();

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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            HttpRequest httpRequest = HttpRequestParser.parse(bufferedReader);

            MyHttpCookie httpCookie = new MyHttpCookie(httpRequest.getCookie());
            Session session = SESSION_MANAGER.findSession(httpCookie.getJSessionId());

            HttpResponse httpResponse = new HttpResponse();

            handle(httpRequest, httpResponse, session);

            write(outputStream, httpResponse);
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }


    private void handle(HttpRequest httpRequest, HttpResponse httpResponse, Session session) throws IOException, URISyntaxException {
        final String method = httpRequest.getMethod();
        final String path = httpRequest.getTarget();
        final String body = httpRequest.getBody();
        final String contentType = httpRequest.getContentType();

        if ("/".equals(path)) {
            httpResponse.send(HTTP_STATUS_OK, CONTENT_TYPE_TEXT_HTML, "Hello world!");
        } else if ("/login".equals(path)) {
            login(httpResponse, method, body, contentType, session);
        } else if ("/register".equals(path)) {
            register(httpResponse, method, body);
        } else {
            httpResponse.sendStaticHtml(path);
        }
    }

    private void login(final HttpResponse httpResponse, final String method, String body, String contentType, Session session) throws IOException, URISyntaxException {
        if (METHOD_GET.equals(method)) {
            if (session != null && session.getAttribute(USER) != null) {
                httpResponse.sendRedirect(PATH_INDEX_HTML);
                return;
            }
            httpResponse.sendStaticHtml(PATH_LOGIN_HTML);
        } else if (METHOD_POST.equals(method)) {
            try {
                Map<String, String> parameters;
                if (contentType.startsWith(CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED)) {
                    parameters = parseFormData(body);
                } else {
                    throw new IllegalArgumentException("지원하지 않는 Content-Type입니다: " + contentType);
                }
                Optional<User> optionalUser = InMemoryUserRepository.findByAccount(parameters.get("account"));
                if (optionalUser.isEmpty()) {
                    throw new IllegalArgumentException("아이디와 비밀번호를 다시 확인하고 입력해주세요.");
                }
                User user = optionalUser.get();
                if (!user.checkPassword(parameters.get("password"))) {
                    throw new IllegalArgumentException("아이디와 비밀번호를 다시 확인하고 입력해주세요.");
                }
                log.info(user.toString());
                if (session == null) {
                    Session newSession = new Session(UUID.randomUUID().toString());
                    newSession.setAttribute(USER, user);
                    SESSION_MANAGER.add(newSession);
                    httpResponse.sendRedirect(PATH_INDEX_HTML);
                    httpResponse.setCookie(newSession.getId());
                    return;
                }
                session.setAttribute(USER, user);
                httpResponse.sendRedirect(PATH_INDEX_HTML);
            } catch (IllegalArgumentException exception) {
                httpResponse.sendRedirect(PATH_401_HTML);
            }
        }
    }

    private void register(final HttpResponse httpResponse, final String method, String body) throws IOException, URISyntaxException {
        if (METHOD_GET.equals(method)) {
            httpResponse.sendStaticHtml(PATH_REGISTER_HTML);
        } else if (METHOD_POST.equals(method)) {
            Map<String, String> parameters = parseFormData(body);
            User user = new User(parameters.get("account"), parameters.get("password"), parameters.get("email"));
            InMemoryUserRepository.save(user);
            log.info(user.toString());
            httpResponse.sendRedirect(PATH_INDEX_HTML);
        }
    }

    private Map<String, String> parseFormData(final String body) {
        Map<String, String> formData = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyAndMap = pair.split("=", 2);
            String key = keyAndMap[0];
            String value = keyAndMap[1];
            formData.put(URLDecoder.decode(key, StandardCharsets.UTF_8), URLDecoder.decode(value, StandardCharsets.UTF_8));
        }
        return formData;
    }

    private void write(final OutputStream outputStream, HttpResponse httpResponse) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(httpResponse.getVersion() + " " + httpResponse.getStatus() + " ");
        for (Map.Entry<String, String> header : httpResponse.getHeaders().entrySet()) {
            lines.add(header.getKey() + ": " + header.getValue() + " ");
        }
        lines.add("");
        lines.add(httpResponse.getBody());
        final var response = String.join("\r\n", lines);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
