package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.util.UriParser;
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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            HttpRequest httpRequest = parseHttpRequest(bufferedReader);
            HttpResponse httpResponse = handleHttpRequest(httpRequest);

            outputStream.write(httpResponse.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseHttpRequest(BufferedReader bufferedReader) {
        try {
            String line = readLine(bufferedReader);
            String[] methodAndUriAndProtocol = parseMethodAndUriAndProtocol(line);
            if (methodAndUriAndProtocol == null) {
                return null;
            }
            return buildHttpRequest(methodAndUriAndProtocol[0], methodAndUriAndProtocol[1]);
        } catch (IOException | ArrayIndexOutOfBoundsException exception) {
            log.error(exception.getMessage(), exception);
            return null;
        }
    }

    private String readLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null || line.isBlank()) {
            return "";
        }
        return line;
    }

    private String[] parseMethodAndUriAndProtocol(String rawLine) {
        String[] tokens = rawLine.split(" ");
        if (tokens.length < 3) {
            return null;
        }
        return tokens;
    }

    private HttpRequest buildHttpRequest(String method, String uri) {
        String path = UriParser.parsePath(uri);
        Map<String, String> queryStrings = UriParser.parseQueryStrings(uri);
        return new HttpRequest(method, path, queryStrings);
    }

    private HttpResponse handleHttpRequest(HttpRequest httpRequest) {
        if (httpRequest == null) {
            return new HttpResponse("500 Internal Server Error", "text/html;charset=utf-8", null);
        }

        if (httpRequest.pathEquals("/")) {
            return new HttpResponse("200 OK", "text/html;charset=utf-8", "Hello world!");
        }

        URL resourceUrl = getClass().getClassLoader().getResource("static" + httpRequest.getPath());
        if (resourceUrl == null) {
            return handleApi(httpRequest);
        }
        return handleStaticFile(httpRequest, resourceUrl);
    }

    private HttpResponse handleApi(HttpRequest httpRequest) {
        if (httpRequest.pathEquals("/login")) {
            return handleLogin(httpRequest);
        }

        return new HttpResponse("404 Not Found", "text/html;charset=utf-8", null);
    }

    private HttpResponse handleStaticFile(HttpRequest httpRequest, URL resourceUrl) {
        try {
            String responseBody = Files.readString(Path.of(resourceUrl.toURI()));
            if (httpRequest.getPath().endsWith(".css")) {
                return new HttpResponse("200 OK", "text/css;charset=utf-8", responseBody);
            }
            return new HttpResponse("200 OK", "text/html;charset=utf-8", responseBody);
        } catch (IOException | URISyntaxException exception) {
            log.error(exception.getMessage(), exception);
            return new HttpResponse("404 Not Found", "text/html;charset=utf-8", null);
        }
    }

    private HttpResponse handleLogin(HttpRequest httpRequest) {
        String account = httpRequest.getQueryStringOf("account");
        String password = httpRequest.getQueryStringOf("password");
        if (account == null || password == null) {
            return new HttpResponse("400 Bad Request", "text/html;charset=utf-8", "잘못된 접근입니다.");
        }

        User user = findUserWithAccountAndPassword(account, password);
        log.info(user.toString());
        try {
            URL resourceUrl = getClass().getClassLoader().getResource("static/login.html");
            String responseBody = Files.readString(Path.of(resourceUrl.toURI()));
            return new HttpResponse("200 OK", "text/html;charset=utf-8", responseBody);
        } catch (IOException | URISyntaxException exception) {
            log.error(exception.getMessage(), exception);
            return new HttpResponse("404 Not Found", "text/html;charset=utf-8", null);
        }
    }

    private User findUserWithAccountAndPassword(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
            .orElse(null);
        validateAccountAndPassword(user, password);
        return user;
    }

    private void validateAccountAndPassword(User user, String password) {
        if (user == null) {
            throw new IllegalArgumentException("잘못된 아이디 혹은 비밀번호입니다.");
        }
        if (!user.isPasswordValid(password)) {
            throw new IllegalArgumentException("잘못된 아이디 혹은 비밀번호입니다.");
        }
    }
}
