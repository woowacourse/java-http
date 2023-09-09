package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager = new SessionManager();

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
        try (var inputStream = connection.getInputStream();
             var outputStream = connection.getOutputStream()) {

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            List<String> lines = new ArrayList<>();

            String line = "";
            while (!(line = bufferedReader.readLine()).equals("")) {
                lines.add(line);
            }
            HttpRequest httpRequest = HttpRequest.from(lines);

            HttpResponse httpResponse;
            if (httpRequest.method().equals("POST")) {
                int contentLength = getContentLength(lines);
                String requestBody = readRequestBody(bufferedReader, contentLength);
                httpResponse = handlePostRequest(httpRequest, requestBody);
            } else {
                List<String> cookieHeaderValues = httpRequest.header("Cookie");
                httpResponse = handleGetRequest(httpRequest, cookieHeaderValues);
            }

            httpResponse.addHeader("Content-Length", String.valueOf(httpResponse.getBody().getBytes().length));
            httpResponse.addHeader("Content-Type", getContentTypeHeaderFrom(httpRequest));

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

            bufferedOutputStream.write(httpResponse.format().getBytes());
            bufferedOutputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String getContentTypeHeaderFrom(HttpRequest httpRequest) {
        List<String> acceptHeaderValues = httpRequest.header("Accept");
        if (acceptHeaderValues != null && acceptHeaderValues.contains("text/css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private int getContentLength(List<String> headers) {
        Optional<String> contentLengthHeader = headers.stream()
                                                      .filter(it -> it.startsWith("Content-Length"))
                                                      .findFirst();

        if (contentLengthHeader.isEmpty()) {

            return -1;
        }
        int index = contentLengthHeader.get().indexOf(" ");
        return Integer.parseInt(contentLengthHeader.get().substring(index + 1));
    }

    private static String readRequestBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private HttpResponse handlePostRequest(HttpRequest request, String requestBody) {
        String[] splitRequestBody = requestBody.split("&");
        if (request.path().equals("/login")) {
            return handleLoginRequest(request.httpVersion(), splitRequestBody);
        }
        if (request.path().equals("/register")) {
            return handleRegisterRequest(request.httpVersion(), splitRequestBody);
        }
        return HttpResponse.of(request.httpVersion(), HttpStatus.NOT_FOUND, readFile("/404.html"));
    }

    private HttpResponse handleLoginRequest(String httpVersion, String[] splitQueryString) {
        Optional<String> account = getValueOf("account", splitQueryString);
        Optional<String> password = getValueOf("password", splitQueryString);

        if (account.isEmpty() || password.isEmpty()) {
            return HttpResponse.of(httpVersion, HttpStatus.NOT_FOUND, readFile("/401.html"));
        }

        Optional<User> findUser = InMemoryUserRepository.findByAccount(account.get());
        if (findUser.isPresent() && findUser.get().checkPassword(password.get())) {
            User user = findUser.get();
            log.info(user.toString());
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user);
            sessionManager.add(session);
            HttpResponse response = HttpResponse.of(httpVersion, HttpStatus.FOUND, readFile("/index.html"));
            response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
            return response;
        }
        return HttpResponse.of(httpVersion, HttpStatus.UNAUTHORIZED, readFile("/401.html"));
    }

    private Optional<String> getValueOf(String key, String[] splitQueryString) {
        return Arrays.stream(splitQueryString)
                     .filter(it -> equalsKey(key, it))
                     .map(it -> it.substring(it.indexOf("=") + 1))
                     .findFirst();
    }

    private boolean equalsKey(String expected, String actual) {
        String[] splitActual = actual.split("=");
        return splitActual[0].equals(expected);
    }

    private HttpResponse handleRegisterRequest(String httpVersion, String[] splitQueryString) {
        Optional<String> account = getValueOf("account", splitQueryString);
        Optional<String> email = getValueOf("email", splitQueryString);
        Optional<String> password = getValueOf("password", splitQueryString);

        if (account.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return HttpResponse.of(httpVersion, HttpStatus.BAD_REQUEST, readFile("/register.html"));
        }

        InMemoryUserRepository.save(new User(account.get(), password.get(), email.get()));
        return HttpResponse.of(httpVersion, HttpStatus.FOUND, readFile("/index.html"));
    }

    private HttpResponse handleGetRequest(HttpRequest request, List<String> cookies) {
        if (!request.method().equalsIgnoreCase("GET")) {
            throw new IllegalArgumentException("GET 요청만 처리 가능합니다.");
        }

        if (request.path().equals("/login.html") || request.path().equals("/login")) {
            return handleLoginPageRequest(request.httpVersion(), cookies);
        }
        return HttpResponse.of(request.httpVersion(), HttpStatus.OK, readFile(request.path()));
    }

    private HttpResponse handleLoginPageRequest(String httpVersion, List<String> cookies) {
        Optional<String> sessionId = getSessionFrom(cookies);
        if (sessionId.isEmpty()) {
            return HttpResponse.of(httpVersion, HttpStatus.OK, readFile("/login.html"));
        }
        Session session = sessionManager.findSession(sessionId.get());
        User user = getUser(session);
        if (InMemoryUserRepository.existsByAccount(user.getAccount())) {
            return HttpResponse.of(httpVersion, HttpStatus.NOT_FOUND, readFile("/index.html"));
        }
        return HttpResponse.of(httpVersion, HttpStatus.OK, readFile("/login.html"));
    }

    private Optional<String> getSessionFrom(List<String> cookies) {
        return cookies.stream()
                      .filter(cookie -> cookie.startsWith("JSESSIONID="))
                      .map(jsessionid -> jsessionid.substring(jsessionid.indexOf('=') + 1))
                      .findFirst();
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }

    private String readFile(String fileName) {
        String filePath = this.getClass().getClassLoader().getResource("static" + fileName).getPath();
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.collect(Collectors.joining("\n", "", "\n"));
        } catch (IOException | UncheckedIOException e) {
            return "Hello world!";
        }
    }
}
