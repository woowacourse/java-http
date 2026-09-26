package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.apache.coyote.login.LoginParser;
import org.apache.coyote.login.LoginResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Map<String, String> httpInfo = new HashMap<>();
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

            // HTTP 요청 파싱
            parseHttpRequest(inputStream);

            // Request Line
            // Request Header
            // Content-Type을 보고 Body가 어떤 타입인지 확인
            // Content-Type이 없다면 body도 업는걸까?
            // Accept를 보고 어떤 타입을 반환할지 결정
            // Request Body

            // 반환 타입 확정
            final var responseType = resolveContentType(httpInfo.getOrDefault("Accept", "*/*"));

            // Path에 따른 비지니스 로직
            if (httpInfo.get("Path").contains("/login") && httpInfo.get("Method").equals("GET")) {
                Map<String, String> queryString = parseQueryString();
                LoginResult loginResult = authenticateUser(queryString.get("account"), queryString.get("password"));

                // 실패 응답 반환
                if (loginResult == LoginResult.FAIL) {
                    final var responseBody = readStaticResource("/401", responseType);
                    final var response = buildRedirectResponse("/401", responseBody, responseType);
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                    return;
                }

                // 리다이렉트 응답 반환
                final var responseBody = readStaticResource("/index", responseType);
                final var response = buildRedirectResponse("/index.html", responseBody, responseType);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (httpInfo.get("Method").equals("POST") && httpInfo.get("Path").contains("/register")) {
                // TODO
            }

            // 200 OK 응답 반환
            final var responseBody = readStaticResource(null, responseType);

            var response = buildOKHttpResponse(responseBody, responseType);
            if (parseCookies().get("JSESSIONID") == null) {
                response = buildOKHttpResponseWithCookie(responseBody, responseType);
            }
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void parseHttpRequest(InputStream httpRequest) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(httpRequest));

        final String[] top = reader.readLine().split(" ");
        httpInfo.put("Method", top[0]);
        httpInfo.put("Path", top[1]);
        httpInfo.put("VersionOfTheProtocol", top[2]);

        String value;
        while ((value = reader.readLine()) != null) {
            final String[] header = value.split(": ");
            if (header.length != 2) {
                break;
            }
            httpInfo.put(header[0], header[1]);
        }
    }

    private Map<String, String> parseCookies() {
        final Map<String, String> cookies = new HashMap<>();
        final String cookieHeader = httpInfo.get("Cookie");

        if (cookieHeader == null || cookieHeader.isBlank()) {
            return cookies;
        }

        final String[] cookiePairs = cookieHeader.split(";");
        for (String cookiePair : cookiePairs) {
            final String[] keyValue = cookiePair.trim().split("=", 2);
            if (keyValue.length != 2) {
                log.debug("[parseCookies] cookie의 형식이 올바르지 않습니다. cookie = {}", cookiePair);
                continue;
            }

            cookies.put(keyValue[0], keyValue[1]);
        }

        return cookies;
    }

    private String resolveContentType(String acceptValue) {
        if (acceptValue.contains("css")) {
            return "css";
        }
        if (acceptValue.contains("html") || acceptValue.contains("*/*")) {
            return "html";
        }
        return "";
    }

    private String readStaticResource(String fileCode, String type) {
        final String path = "/static";
        final String NO_CONTENT = "Hello world!";

        final String fileName = resolveResourcePath(fileCode, type);
        final URL resource = getClass().getResource(path + fileName);
        if (resource == null) {
            log.error("[getStaticResource] 파일을 찾을 수 없습니다. path = {}", path + fileName);
            return NO_CONTENT;
        }

        try {
            return Files.readString(Path.of(resource.toURI()));
        } catch (IOException | URISyntaxException e) {
            log.error("[getStaticResource] 파일을 찾을 수 없습니다. path = {}", path + fileName);
            return NO_CONTENT;
        }
    }

    private String resolveResourcePath(String fileName, String type) {
        final String NO_CONTENT = "Hello world!";

        String uri = httpInfo.get("Path");
        if (uri == null) {
            log.error("[getStaticResource] 파일 경로가 null 입니다.");
            return NO_CONTENT;
        }

        uri = uri.split("\\?")[0]; // 순수 URL

        final String extension = "." + type;
        if (!uri.endsWith(extension)) {
            uri += extension;
        }

        if (fileName != null) {
            uri = fileName + extension;
        }

        return uri;
    }

    private Map<String, String> parseQueryString() {
        final String path = httpInfo.getOrDefault("Path", "/");
        URI uri = URI.create(path);

        return LoginParser.parseQueryString(uri.getQuery());
    }

    private LoginResult authenticateUser(String account, String password) {
        try {
            User user = InMemoryUserRepository.findByAccount(account).orElseThrow();

            if (!user.checkPassword(password)) {
                log.info("[authenticateUser] 회원 정보가 일치하지 않습니다.");
                return LoginResult.FAIL;
            }

            log.info("user : {}", user);
            return LoginResult.SUCCESS;
        } catch (Exception e) {
            log.error("[authenticateUser] 회원 정보를 찾을 수 없습니다.");
            return LoginResult.FAIL;
        }
    }


    private String buildOKHttpResponse(final String responseBody, final String type) {
        return String.join("\r\n",
            String.format("HTTP/1.1 %d %s ", 200, "OK"),
            String.format("Content-Type: text/%s;charset=utf-8 ", type),
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    private String buildOKHttpResponseWithCookie(final String responseBody, final String type) {
        return String.join("\r\n",
            String.format("HTTP/1.1 %d %s ", 200, "OK"),
            String.format("Content-Type: text/%s;charset=utf-8 ", type),
            String.format("Set-Cookie: JSESSIONID=%s; ", UUID.randomUUID()),
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    private String buildRedirectResponse(final String url, final String responseBody, final String type) {
        return String.join("\r\n",
            "HTTP/1.1 302 Redirect ",
            String.format("Content-Type: text/%s;charset=utf-8 ", type),
            String.format("Location: %s ", url),
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
