package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.domain.User;
import com.techcourse.model.dto.UserInfo;
import org.apache.coyote.Processor;
import org.apache.coyote.http.session.HttpSession;
import org.apache.coyote.http.session.HttpSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final String STATIC = "/static";
    private static final String HTML = ".html";
    private static final String CSS = ".css";
    private static final String JS = ".js";
    private static final String SVG = ".svg";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final HttpSessionManager httpSessionManager = HttpSessionManager.getInstance();
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
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String firstHttpRequestHeaderLine = bufferedReader.readLine();

            if (firstHttpRequestHeaderLine == null) {
                return;
            }

            Map<String, String> httpRequestHeaders = readHttpRequestHeaders(bufferedReader);

            final String[] httpRequestLine = firstHttpRequestHeaderLine.split(" ");
            final String method = httpRequestLine[0];
            final String requestURL = httpRequestLine[1];

            if (method.equals("GET")) {
                doGet(httpRequestHeaders, requestURL, outputStream);
            }

            if (method.equals("POST")) {
                doPost(httpRequestHeaders, bufferedReader, requestURL, outputStream);
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHttpRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> httpRequestHeaders = new HashMap<>();
        String line;

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] headerLine = line.split(": ");

            String key = URLDecoder.decode(headerLine[0]);
            String value = URLDecoder.decode(headerLine[1]);

            httpRequestHeaders.put(key, value);
        }
        return httpRequestHeaders;
    }

    private void doPost(final Map<String, String> httpRequestHeaders, final BufferedReader bufferedReader,
                        final String requestURL, final OutputStream outputStream) throws IOException {
        String requestBody = readRequestBody(httpRequestHeaders, bufferedReader);

        if (requestURL.equals("/login")) {
            login(httpRequestHeaders, outputStream, requestBody);
        }

        if (requestURL.equals("/register")) {
            String path = register(requestBody);
            doGet(httpRequestHeaders, path, outputStream);
        }
    }

    private void login(final Map<String, String> httpRequestHeaders, final OutputStream outputStream, final String requestBody) throws IOException {
        String cookies = httpRequestHeaders.get("Cookie");

        Optional<HttpSession> httpSession = loginPost(requestBody);
        String[] result = loginGet(httpSession.isPresent());
        String path = determineResourcePath(result[1])[1];
        String httpStatus = result[0];
        String contentType = determineContentType(result[1]);
        String cookie = "";

        if (httpSession.isPresent() && !isLogined(cookies)) {
            cookie = setCookie(httpSession.get().getId());
        }

        createHttpResponse(path, outputStream, httpStatus, contentType, cookie);
    }

    private String setCookie(UUID uuid) {
        return "Set-Cookie: JSESSIONID=" + uuid + " " + "\r\n"
                + "";
    }

    private String readRequestBody(final Map<String, String> httpRequestHeaders, final BufferedReader bufferedReader)
            throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private String register(String requestBody) {
        Map<String, String> userInfo = parseUserInfo(requestBody);

        String account = userInfo.get("account");
        String password = userInfo.get("password");
        String email = userInfo.get("email");

        return validateRegistration(account, password, email);
    }

    private String validateRegistration(final String account, final String password, final String email) {
        try {
            InMemoryUserRepository.findByAccount(account)
                    .ifPresentOrElse(
                            user -> {
                                throw new IllegalArgumentException("이미 존재하는 계정명입니다.");
                            },
                            () -> {
                                UserInfo userRegisterInfo = new UserInfo(account, password, email);
                                User newUser = InMemoryUserRepository.save(userRegisterInfo);
                                log.info("user: {}의 회원가입이 완료되었습니다.", newUser.getAccount());
                            }
                    );
            return "/success";
        } catch (IllegalArgumentException e) {
            log.info("오류 발생: {}", e.getMessage());
            return "/fail";
        }
    }

    private boolean isLogined(String cookies) {
        return cookies != null && cookies.contains("JSESSIONID");
    }

    private void doGet(final Map<String, String> httpRequestHeaders, final String requestURL,
                       final OutputStream outputStream) throws IOException {
        String cookies = httpRequestHeaders.get("Cookie");
        String[] searchResourcePath = determineResourcePath(requestURL);

        if (isLogined(cookies) && requestURL.equals("/login")) {
            searchResourcePath = determineResourcePath("/");
        }

        String httpStatus = searchResourcePath[0];
        String resourcePath = searchResourcePath[1];

        String contentType = determineContentType(resourcePath);

        createHttpResponse(resourcePath, outputStream, httpStatus, contentType, "");
    }

    private String[] determineResourcePath(String requestURL) {
        String[] result = new String[2];
        result[0] = "200 OK";

        if (requestURL.equals("/") || requestURL.equals("/index.html")) {
            result[1] = "/static/index.html";
            return result;
        }

        if (requestURL.equals("/success")) {
            result[0] = "201 CREATED";
            result[1] = "/static/index.html";
            return result;
        }

        if (requestURL.equals("/fail")) {
            result[0] = "400 BAD REQUEST";
            result[1] = "/static/register.html";
            return result;
        }

        if (requestURL.endsWith(SVG)) {
            result[1] = STATIC + "/assets/img/error-404-monochrome.svg";
            return result;
        }

        if (requestURL.endsWith(HTML) || requestURL.endsWith(CSS) || requestURL.endsWith(JS)) {
            result[1] = STATIC + requestURL;
            return result;
        }

        result[1] = STATIC + requestURL + HTML;
        return result;
    }

    private String determineContentType(String resourcePath) {
        if (resourcePath.endsWith(CSS)) {
            return "text/css";
        }

        if (resourcePath.endsWith(JS)) {
            return "application/javascript";
        }

        if (resourcePath.endsWith(SVG)) {
            return "image/svg+xml";
        }

        return "text/html;charset=utf-8";
    }

    private Optional<HttpSession> loginPost(String requestBody) {
        Map<String, String> userInfo = parseUserInfo(requestBody);
        UUID uuid = null;

        try {
            uuid = validateUser(userInfo);
        } catch (IllegalArgumentException e) {
            log.info("오류 발생: {}", e.getMessage());
        }

        if (uuid != null) {
            return Optional.ofNullable(HttpSessionManager.getInstance().findSession(uuid.toString()));
        }

        return Optional.empty();
    }

    private String[] loginGet(boolean isLogined) {
        if (!isLogined) {
            return new String[]{"401 UNAUTHORIZED", "/401"};
        }
        return new String[]{"302 FOUND", "/index"};
    }

    private Map<String, String> parseUserInfo(String requestBody) {
        Map<String, String> userInfo = new HashMap<>();

        String[] info = requestBody.split("&");

        for (String metadata : info) {
            String[] temp = metadata.split("=");
            if (temp.length < 2 || temp[1].isEmpty()) {
                throw new IllegalArgumentException("필수 입력값이 비어 있습니다.");
            }
            String key = URLDecoder.decode(temp[0]);
            String value = URLDecoder.decode(temp[1]);

            userInfo.put(key, value);
        }
        return userInfo;
    }

    private UUID validateUser(Map<String, String> userInfo) {
        String account = userInfo.get("account");
        String password = userInfo.get("password");

        Optional<User> loginUser = InMemoryUserRepository.findByAccount(account);

        if (loginUser.isEmpty()) {
            log.info(account + "는(은) 등록되지 않은 계정입니다.");
            return null;
        }

        return loginUser.filter(user -> user.checkPassword(password))
                .map(
                        user -> {
                            UUID uuid = UUID.randomUUID();
                            HttpSession session = new HttpSession(uuid);
                            session.setAttribute("user", user);
                            httpSessionManager.add(session);

                            log.info("로그인 성공. user : {}", user);
                            return uuid;
                        }
                )
                .orElseGet(() -> {
                    log.info(account + "의 비밀번호가 잘못 입력되었습니다.");
                    return null;
                });
    }

    private void createHttpResponse(final String path, final OutputStream outputStream, String httpStatus,
                                    final String contentType, final String cookie) throws IOException {
        URL resource = getClass().getResource(path);

        if (resource == null) {
            resource = getClass().getResource("/static/404.html");
            httpStatus = "404 NOT FOUND";
        }

        byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());
        final var response = String.join("\r\n",
                "HTTP/1.1 " + httpStatus + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.length + " ",
                cookie,
                "");

        outputStream.write(response.getBytes());
        outputStream.write(responseBody);
        outputStream.flush();
    }
}
