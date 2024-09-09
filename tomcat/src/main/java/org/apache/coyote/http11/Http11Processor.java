package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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

            HttpRequest request = HttpRequestExtractor.extract(inputStream);
            HttpResponse response = new HttpResponse();
            execute(request, response);

            outputStream.write(response.parseResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void execute(HttpRequest request, HttpResponse response) throws IOException, URISyntaxException {
        String httpMethod = request.getMethod();
        String path = request.getPath();
        log.info("request = {} {}", httpMethod, path);

        if (path.equals("/login") && httpMethod.equals("GET")) {
            checkLogin(request, response);
        }
        if (path.equals("/login") && httpMethod.equals("POST")) {
            doLogin(request, response);
            return;
        }
        if (path.equals("/register") && httpMethod.equals("POST")) {
            doRegister(request, response);
            return;
        }

        String responseBody = decideResponseBody(request);
        response.setBody(responseBody);

        response.addHeader("Content-Type", decideContentTypeHeader(request));
        response.addHeader("Content-Length", decideContentLengthHeader(response));
    }

    private void checkLogin(HttpRequest request, HttpResponse response) {
        HttpCookie cookies = request.extractCookie();
        if (!cookies.containsCookie("JSESSIONID")) {
            return;
        }
        String jSessionId = cookies.getCookie("JSESSIONID");
        boolean isLogin = SessionManager.containsSession(jSessionId);
        if (isLogin) {
            response.sendRedirection("/index.html");
        }
    }

    private void doLogin(HttpRequest request, HttpResponse response) throws IOException, URISyntaxException {
        String requestBody = request.getBody();
        Map<String, String> fields = new HashMap<>();
        String[] rawFields = requestBody.split("&");
        for (String rawField : rawFields) {
            String key = rawField.split("=")[0];
            String value = rawField.split("=")[1];
            fields.put(key, value);
        }

        String account = fields.get("account");
        String password = fields.get("password");

        Optional<User> rawUser = InMemoryUserRepository.findByAccount(account);
        if (rawUser.isEmpty()) {
            response.setStatusUnauthorized();
            response.setBody(FileReader.readResourceFile("/401.html"));
            return;
        }
        User user = rawUser.get();
        if (!user.checkPassword(password)) {
            response.setStatusUnauthorized();
            response.setBody(FileReader.readResourceFile("/401.html"));
            return;
        }
        log.info("user: {}", user);
        response.sendRedirection("/index.html");

        String jSessionId = UUID.randomUUID().toString();
        Session session = new Session(jSessionId);
        session.setAttribute("user", user);
        SessionManager.addSession(session.getId(), session);
        response.addHeader("Set-Cookie", "JSESSIONID=" + jSessionId);
    }

    private void doRegister(HttpRequest request, HttpResponse response) {
        String requestBody = request.getBody();

        Map<String, String> fields = new HashMap<>();
        String[] rawFields = requestBody.split("&");
        for (String rawField : rawFields) {
            String key = rawField.split("=")[0];
            String value = rawField.split("=")[1];
            fields.put(key, value);
        }

        String account = fields.get("account");
        String email = fields.get("email");
        String password = fields.get("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.sendRedirection("/index.html");
    }

    // 아래는 응답 생성 관련

    private String decideResponseBody(HttpRequest request) throws IOException, URISyntaxException {
        String requestPath = request.getPath();
        if (requestPath.equals("/")) {
            return FileReader.readResourceFile("/default.html");
        }
        String filePath = chooseFilePath(requestPath);
        try {
            return FileReader.readResourceFile(filePath);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    private String chooseFilePath(String requestPath) {
        if (requestPath.contains(".")) {
            return "/" + requestPath;
        }
        return "/" + requestPath + ".html";
    }

    private String decideContentTypeHeader(HttpRequest request) {
        Map<String, String> headers = request.getHeaders();
        String accepts = headers.getOrDefault("Accept", "");
        String mediaType = Arrays.stream(accepts.split(","))
                .filter(accept -> accept.startsWith("text/"))
                .findFirst()
                .orElse("text/html")
                .split("/")[1];
        return String.format("text/%s;charset=utf-8", mediaType);
    }

    private String decideContentLengthHeader(HttpResponse response) {
        String responseBody = response.getBody();
        return String.valueOf(responseBody.getBytes().length);
    }
}
