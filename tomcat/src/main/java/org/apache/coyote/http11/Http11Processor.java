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

            Http11Request request = RequestExtractor.extract(inputStream);
            Http11Response response = new Http11Response();
            execute(request, response);

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void execute(Http11Request request, Http11Response response) throws IOException, URISyntaxException {
        String httpMethod = request.getHttpMethod();
        String path = request.getPath();
        log.info("request path = {}", path);

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

    private void doLogin(Http11Request request, Http11Response response) throws IOException, URISyntaxException {
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

        if (canLogin(account, password)) {
            response.setStatusCode(302);
            response.addHeader("Location", "/index.html");
            response.addHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
            return;
        }
        response.setStatusCode(401);
        response.setBody(FileReader.readResourceFile("401.html"));
    }

    private boolean canLogin(String account, String password) {
        Optional<User> rawUser = InMemoryUserRepository.findByAccount(account);
        if (rawUser.isEmpty()) {
            return false;
        }
        User user = rawUser.get();
        if (!user.checkPassword(password)) {
            return false;
        }
        log.info("user: {}", user);
        return true;
    }

    private void doRegister(Http11Request request, Http11Response response) {
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

        response.setStatusCode(302);
        response.addHeader("Location", "/index.html");
    }

    private void refresh(Http11Request request, Http11Response response) {
        Map<String, String> headers = request.getHeaders();
        String rawCookies = headers.get("Cookie");
        Http11Cookie cookie = new Http11Cookie(rawCookies);

        if (!cookie.containsKey("JSESSIONID")) {
            response.addHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
        }
    }

    // 아래는 응답 생성 관련

    private String decideResponseBody(Http11Request request) throws IOException, URISyntaxException {
        String requestPath = request.getPath();
        if (requestPath.equals("/")) {
            return FileReader.readResourceFile();
        }
        String fileName = chooseFileName(requestPath);
        return FileReader.readResourceFile(fileName);
    }

    private String chooseFileName(String requestPath) {
        if (requestPath.contains(".")) {
            return requestPath;
        }
        return requestPath + ".html";
    }

    private String decideContentTypeHeader(Http11Request request) {
        Map<String, String> headers = request.getHeaders();
        String accepts = headers.getOrDefault("Accept", "");
        String mediaType = Arrays.stream(accepts.split(","))
                .filter(accept -> accept.startsWith("text/"))
                .findFirst()
                .orElse("text/html")
                .split("/")[1];
        return String.format("text/%s;charset=utf-8", mediaType);
    }

    private String decideContentLengthHeader(Http11Response response) {
        String responseBody = response.getBody();
        return String.valueOf(responseBody.getBytes().length);
    }
}
