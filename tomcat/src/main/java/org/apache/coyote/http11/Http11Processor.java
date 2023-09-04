package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
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
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final char COLON = ':';
    private static final String EMPTY_JSESSION = "";
    private static final String CSS = ".css";
    private static final String JS_ICO_CSS_REGEX = ".*\\.(js|ico|css)$";
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
            final String httpMethod = parseHttpMethod(requestLine);  // HTTP method를 읽음 (예: GET)
            final Map<String, String> headers = parseRequestHeaders(br); // header를 읽음

            if ("POST".equals(httpMethod)) { //post method일 때만 requestBody 읽어오고 user를 등록
                final String requestBody = readRequestBody(br, headers);
                registerUser(requestBody);
            }

            String jsessionId = getJsessionId(headers);

            final String path = parseHttpRequest(requestLine); // 파싱된 HTTP 요청에서 경로 추출
            final String parsedPath = parsePath(path, jsessionId); // 경로를 기반으로 정적 파일을 읽고 응답 생성
            final String responseBody = readStaticFile(parsedPath);
            final String contentType = getContentType(path); // css인 경우 content type을 다르게 준다
            final var response = getResponse(path, contentType, responseBody, headers, parsedPath, jsessionId);  // JSESSIONID가 있는 경우, 없는 경우 다르게 response를 준다

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getJsessionId(final Map<String, String> headers) {
        if (isContainJsessionId(headers)) {
            return parseJsessionId(headers);
        }
        return UUID.randomUUID().toString();
    }

    private String getResponse(final String path, final String contentType, final String responseBody, final Map<String, String> headers, final String parsedPath, final String jsessionId) {
        if (path.contains("/login?") && parsedPath.equals("/index.html") && !isContainJsessionId(headers)) {
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Set-Cookie: " + "JSESSIONID=" + jsessionId + " ",
                    "Content-Type: " + contentType + "charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + "charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

    }

    private boolean isContainJsessionId(final Map<String, String> headers) {
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

    private String parseJsessionId(final Map<String, String> headers) {
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

        return "";
    }

    private String readRequestBody(final BufferedReader br, final Map<String, String> headers) throws IOException {
        final int contentLength = Integer.parseInt(headers.get("Content-Length"));
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    private Map<String, String> parseRequestHeaders(final BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        final List<String> lines = readAllLines(br);

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

    private List<String> readAllLines(final BufferedReader br) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line;
        while (!(line = br.readLine()).equals(EMPTY_JSESSION)) {
            lines.add(line);
        }
        return lines;
    }

    private int getEmptyLineIndex(final List<String> lines) {
        int emptyLineIndex = lines.indexOf(EMPTY_JSESSION);
        if (emptyLineIndex == -1) {
            emptyLineIndex = lines.size();
        }
        return emptyLineIndex;
    }

    private String getContentType(final String path) {
        final String contentType;
        if (path.endsWith(CSS)) {
            contentType = "text/css;";
        } else {
            contentType = "text/html;";
        }
        return contentType;
    }

    private String parseHttpRequest(final String requestLine) throws IOException {
        // 요청 라인을 공백으로 분리하여 경로를 추출
        String[] requestParts = requestLine.split(" ");

        if (requestParts.length >= 2) {
            return requestParts[1]; // 두 번째 요소가 경로 (예: "/index.html")
        } else {
            throw new IOException("Invalid HTTP request"); // 유효하지 않은 요청 처리
        }
    }

    private String parseHttpMethod(final String requestLine) throws IOException {
        // 요청 라인을 공백으로 분리하여 경로를 추출
        String[] requestParts = requestLine.split(" ");
        return requestParts[0];
    }

    private String readStaticFile(final String parsedPath) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resourceInputStream = classLoader.getResourceAsStream("static" + parsedPath);

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

    private String parsePath(final String path, final String jsessionId) {
        if (path.contains("/login")) {
            if (path.contains("?") && !isValidUser(path, jsessionId)) {
                return "/401.html";
            } else if (!SessionManager.validJsession(jsessionId)) {
                return "/login.html";
            }
        } else if (path.equals("/register")) {
            return "/register.html";
        } else if (path.matches(JS_ICO_CSS_REGEX)) {
            return path;
        }

        return "/index.html";
    }

    private boolean isValidUser(final String path, final String jsessionId) {
        String noUrlPath = path.replace("/login?","");
        Map<String, String> loginData = parseLoginData(noUrlPath);
        String parsedAccount = loginData.get("account");
        String parsedPassword = loginData.get("password");

        final Optional<User> maybeUser = InMemoryUserRepository.findByAccount(parsedAccount);
        if (maybeUser.isPresent()) {
            final User foundUser = maybeUser.get();
            if (foundUser.checkPassword(parsedPassword) && !SessionManager.validUser(foundUser)) {
                log.info("로그인 성공! 아이디 : " + parsedAccount);
                SessionManager.add(foundUser, jsessionId);
                return true;
            }
        }

        return false;
    }

    private Map<String, String> parseLoginData(final String path) {
        Map<String, String> loginData = new HashMap<>();
        final StringTokenizer st = new StringTokenizer(path, "&");

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int equalsIndex = token.indexOf('=');

            if (equalsIndex != -1) {
                String key = token.substring(0, equalsIndex);
                String value = token.substring(equalsIndex + 1);
                loginData.put(key, value);
            }
        }

        return loginData;
    }

    private void registerUser(final String requestBody) throws IOException {
        Map<String, String> userData = parseRequestBody(requestBody);
        String parsedAccount = userData.get("account");
        String parsedEmail = userData.get("email");
        String parsedPassword = userData.get("password");

        final User user = new User(parsedAccount, parsedPassword, parsedEmail);
        InMemoryUserRepository.save(user);
        log.info("유저 저장 성공!");
    }

    private Map<String, String> parseRequestBody(final String requestBody) {
        Map<String, String> userData = new HashMap<>();
        StringTokenizer st = new StringTokenizer(requestBody, "&");

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int equalsIndex = token.indexOf('=');

            if (equalsIndex != -1) {
                String key = token.substring(0, equalsIndex);
                String value = token.substring(equalsIndex + 1);
                userData.put(key, value);
            }
        }

        return userData;
    }
}
