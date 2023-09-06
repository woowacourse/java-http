package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.cookie.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

import static org.apache.coyote.http11.enums.ContentType.getContentType;
import static org.apache.coyote.http11.enums.Path.INDEX_URL;
import static org.apache.coyote.http11.enums.Path.LOGIN_HTML;
import static org.apache.coyote.http11.enums.Path.LOGIN_URL;
import static org.apache.coyote.http11.enums.Path.LOGIN_WITH_PARAM_URL;
import static org.apache.coyote.http11.enums.Path.REGISTER_HTML;
import static org.apache.coyote.http11.enums.Path.REGISTER_URL;
import static org.apache.coyote.http11.enums.Path.STATIC;
import static org.apache.coyote.http11.enums.Path.UNAUTHORIZED_HTML;
import static org.apache.coyote.http11.enums.Path.isContainParam;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String EMPTY = "";
    private static final String JS_ICO_CSS_REGEX = ".*\\.(js|ico|css)$";
    private static final HttpCookie httpCookie = new HttpCookie();
    private static final String POST_HTTP_METHOD = "POST";
    private static final String USER_SAVE_SUCCESS_MESSAGE = "유저 저장 성공!";
    private static final String LOGIN_SUCCESS_MESSAGE = "로그인 성공! 아이디 : ";
    private static final String INVALID_HTTP_REQUEST_MESSAGE = "Invalid HTTP request";
    private static final String PARAMETER_DELIM = "&";
    private static final char EQUALS = '=';
    private static final char COLON = ':';
    private static Map<String, String> headers;
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

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = br.readLine(); // HTTP 요청 라인을 읽음 (예: "GET /index.html HTTP/1.1")
            System.out.println("requestLine : "+requestLine);
            final String httpMethod = parseHttpMethod(requestLine);// HTTP method를 읽음 (예: GET)
            headers = parseRequestHeaders(br); // header를 읽음
            storeJsessionId();

            if (POST_HTTP_METHOD.equals(httpMethod)) { //post method일 때만 requestBody 읽어오고 user를 등록
                final String requestBody = readRequestBody(br);
                registerUser(requestBody);
            }

            final String path = parseHttpRequest(requestLine); // 파싱된 HTTP 요청에서 경로 추출
            final String parsedPath = parsePath(path, httpMethod); // 경로를 기반으로 정적 파일을 읽고 응답 생성

            System.out.println("path: "+ path);
            System.out.println("parsedPath: "+ parsedPath);

            final String responseBody = readStaticFile(parsedPath);
            final String contentType = getContentType(parsedPath); //content type을 다르게 준다
            final String response = createResponse(contentType, responseBody, parsedPath);  // JSESSIONID가 있는 경우, 없는 경우 다르게 response를 준다

            System.out.println(response);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseHttpMethod(final String requestLine) throws IOException {
        // 요청 라인을 공백으로 분리하여 경로를 추출
        String[] requestParts = requestLine.split(" ");
        return requestParts[0];
    }

    private Map<String, String> parseRequestHeaders(final BufferedReader br) throws IOException {
        final List<String> lines = readAllLinesBeforeBody(br);
        final Map<String, String> headers = parseHeaderLines(lines);

        return headers;
    }

    private List<String> readAllLinesBeforeBody(final BufferedReader br) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line;
        while (!(line = br.readLine()).equals(EMPTY)) {
            lines.add(line);
            System.out.println("line : " + line);
        }
        return lines;
    }

    private Map<String, String> parseHeaderLines(final List<String> lines) {
        Map<String, String> headers = new HashMap<>();

        String lineForParse;
        for (int i = 0; i < getEmptyLineIndex(lines); i++) {
            lineForParse = lines.get(i);
            int colonIndex = lineForParse.indexOf(COLON);
            if (colonIndex != -1) {
                String key = lineForParse.substring(0, colonIndex).trim();
                String value = lineForParse.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }

        return headers;
    }

    private int getEmptyLineIndex(final List<String> lines) {
        int emptyLineIndex = lines.indexOf(EMPTY);
        if (emptyLineIndex == -1) {
            emptyLineIndex = lines.size();
        }
        return emptyLineIndex;
    }

    private String storeJsessionId() {
        if (isContainJsessionId()) {
            return httpCookie.changeJSessionId(parseJsessionId());
        }
        return httpCookie.createJSession();

    }

    private String parseJsessionId() {
        if (headers.containsKey("Cookie")) {
            final String cookieValue = headers.get("Cookie");
            String[] cookiePairs = cookieValue.split("; ");
            for (String cookiePair : cookiePairs) {
                String[] parts = cookiePair.split("=");
                if (parts[0].equals("JSESSIONID")) {
                    return parts[1];
                }
            }
        }

        return EMPTY;
    }

    private String readRequestBody(final BufferedReader br) throws IOException {
        final int contentLength = Integer.parseInt(headers.get("Content-Length"));
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    private void registerUser(final String requestBody) throws IOException {
        Map<String, String> userData = parseUserDataFromRequestBody(requestBody);
        String parsedAccount = userData.get("account");
        String parsedEmail = userData.get("email");
        String parsedPassword = userData.get("password");

        final User user = new User(parsedAccount, parsedPassword, parsedEmail);
        InMemoryUserRepository.save(user);
        log.info(USER_SAVE_SUCCESS_MESSAGE);
    }

    private Map<String, String> parseUserDataFromRequestBody(final String requestBody) {
        Map<String, String> userData = new HashMap<>();
        StringTokenizer st = new StringTokenizer(requestBody, PARAMETER_DELIM);

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int equalsIndex = token.indexOf(EQUALS);

            if (equalsIndex != -1) {
                String key = token.substring(0, equalsIndex);
                String value = token.substring(equalsIndex + 1);
                userData.put(key, value);
            }
        }

        return userData;
    }

    private String parseHttpRequest(final String requestLine) throws IOException {
        // 요청 라인을 공백으로 분리하여 경로를 추출
        String[] requestParts = requestLine.split(" ");

        if (requestParts.length >= 2) {
            return requestParts[1]; // 두 번째 요소가 경로 (예: "/index.html")
        }
        throw new IOException(INVALID_HTTP_REQUEST_MESSAGE); // 유효하지 않은 요청 처리
    }

    private String parsePath(final String path, final String httpMethod) {
        String url = INDEX_URL.getValue();

        if (path.contains(LOGIN_URL.getValue())) {
            url = getPathForLogin(path);
        }

        if (path.equals(REGISTER_URL.getValue())) {
            url = getPathForRegister(httpMethod);
        }

        if (path.matches(JS_ICO_CSS_REGEX)) {
            url = STATIC.getValue() + path;
        }

        return url;
    }

    private String readStaticFile(final String parsedPath) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resourceInputStream = classLoader.getResourceAsStream(parsedPath);

        StringBuilder content = new StringBuilder();
        if (resourceInputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceInputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
        }

        return content.toString();
    }

    private String createResponse(final String contentType, final String responseBody, final String path) {
        if(path.equals(UNAUTHORIZED_HTML.getValue())) {
            return Response.of(contentType, responseBody, "401 Unauthorized");
        }

        if (path.equals(LOGIN_WITH_PARAM_URL.getValue()) && !httpCookie.isJsessionEmpty()) {
            return Response.of(contentType, responseBody, httpCookie);
        }

        return Response.of(contentType, responseBody);
    }

    private boolean isContainJsessionId() {
        if (headers.containsKey("Cookie")) {
            final String cookieValue = headers.get("Cookie");
            String[] cookiePairs = cookieValue.split("; ");
            for (String cookiePair : cookiePairs) {
                String[] parts = cookiePair.split("=");
                if (parts[0].equals("JSESSIONID")) {
                    return true;
                }
            }
        }

        return false;
    }

    private String getPathForLogin(final String path) {
        if (isContainParam(path) && !isValidUser(path)) {
            return UNAUTHORIZED_HTML.getValue();
        }

        System.out.println("is contain? : " + SessionManager.isValidHttpCookie(httpCookie));
        if (!SessionManager.isValidHttpCookie(httpCookie)) {
            return LOGIN_HTML.getValue();
        }

        return INDEX_URL.getValue();
    }

    private boolean isValidUser(final String path) {
        String noUrlPath = path.replace(LOGIN_WITH_PARAM_URL.getValue(), EMPTY);
        Map<String, String> loginData = parseLoginData(noUrlPath);
        String parsedAccount = loginData.get("account");
        String parsedPassword = loginData.get("password");

        final Optional<User> maybeUser = InMemoryUserRepository.findByAccount(parsedAccount);
        if (maybeUser.isPresent()) {
            final User foundUser = maybeUser.get();
            if (foundUser.checkPassword(parsedPassword) && !SessionManager.isValidUser(foundUser)) {
                SessionManager.add(foundUser, httpCookie);
                log.info(LOGIN_SUCCESS_MESSAGE + parsedAccount);

                return true;
            }
        }

        return false;
    }

    private Map<String, String> parseLoginData(final String path) {
        Map<String, String> loginData = new HashMap<>();
        final StringTokenizer st = new StringTokenizer(path, PARAMETER_DELIM);

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int equalsIndex = token.indexOf(EQUALS);

            if (equalsIndex != -1) {
                String key = token.substring(0, equalsIndex);
                String value = token.substring(equalsIndex + 1);
                loginData.put(key, value);
            }
        }

        return loginData;
    }

    private String getPathForRegister(final String httpMethod) {
        if (httpMethod.equals(POST_HTTP_METHOD)) {
            return INDEX_URL.getValue();
        }
        return REGISTER_HTML.getValue();
    }
}
