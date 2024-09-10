package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.domain.User;
import com.techcourse.model.dto.UserInfo;
import org.apache.coyote.Processor;
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

public class Http11Processor implements Runnable, Processor {

    public static final String QUERY_PARAMETER = "?";
    public static final String STATIC = "/static";
    public static final String HTML = ".html";
    public static final String CSS = ".css";
    public static final String JS = ".js";
    public static final String SVG = ".svg";

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
                doGet(requestURL, outputStream);
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

        while ((line = bufferedReader.readLine()) != null && !"".equals(line)) {
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
            String path = "/login" + "?" + requestBody;
            doGet(path, outputStream);
        }

        if (requestURL.equals("/register")) {
            register(requestBody);
            String path = "/success";
            doGet(path, outputStream);
        }
    }

    private String readRequestBody(final Map<String, String> httpRequestHeaders, final BufferedReader bufferedReader) throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private void register(String requestBody) {
        Map<String, String> userInfo = parseUserInfo(requestBody);

        String account = userInfo.get("account");
        String password = userInfo.get("password");
        String email = userInfo.get("email");

        User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
        log.info("user: {}의 회원가입이 완료되었습니다.", newUser.getAccount());
                                UserInfo userRegisterInfo = new UserInfo(account, password, email);
                                User newUser = InMemoryUserRepository.save(userRegisterInfo);
                                log.info("user: {}의 회원가입이 완료되었습니다.", newUser.getAccount());
    }

    private void doGet(final String requestURL, final OutputStream outputStream) throws IOException {
        String[] searchResourcePath = determineResourcePath(requestURL);
        String httpStatus = searchResourcePath[0];
        String resourcePath = searchResourcePath[1];

        String contentType = determineContentType(resourcePath);
        URL resource = getClass().getResource(resourcePath);

        if (resource == null) {
            resource = getClass().getResource("/static/404.html");
            httpStatus = "404 NOT FOUND";
        }

        byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        createHttpResponse(outputStream, httpStatus, contentType, responseBody);
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

        if (requestURL.endsWith(SVG)) {
            result[1] = STATIC + "/assets/img/error-404-monochrome.svg";
            return result;
        }

        if (requestURL.contains(QUERY_PARAMETER)) {
            String[] statusAndPath = parseLoginQueryString(requestURL);

            statusAndPath[1] = STATIC + statusAndPath[1] + HTML;
            return statusAndPath;
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

    private String[] parseLoginQueryString(String requestURL) {
        String[] result = new String[2];
        result[0] = "302 FOUND";

        int index = requestURL.indexOf(QUERY_PARAMETER);
        boolean isMember = isValidUser(parseUserInfo(requestURL.substring(index + 1)));

        if (!isMember) {
            result[0] = "401 UNAUTHORIZED";
            result[1] = "/401";
            return result;
        }
        result[1] = "/index";

        return result;
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

    private boolean isValidUser(Map<String, String> userInfo) {
        String account = userInfo.get("account");
        String password = userInfo.get("password");

        Optional<User> loginUser = InMemoryUserRepository.findByAccount(account);

        if (loginUser.isEmpty()) {
            log.info(account + "는(은) 등록되지 않은 계정입니다.");
            return false;
        }

        return loginUser.filter(user -> user.checkPassword(password))
                .map(
                        user -> {
                            log.info("user : {}", user);
                            return true;
                        }
                )
                .orElseGet(() -> {
                    log.info(account + "의 비밀번호가 잘못 입력되었습니다.");
                    return false;
                });
    }

    private void createHttpResponse(final OutputStream outputStream, final String httpStatus,
                                    final String contentType, final byte[] responseBody) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 " + httpStatus + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.length + " ",
                "",
                "");

        outputStream.write(response.getBytes());
        outputStream.write(responseBody);
        outputStream.flush();
    }
}
