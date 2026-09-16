package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
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

            // 반환 타입 확정
            final var type = resolveContentType();

            // 반환할 정적 파일 찾기
            final var responseBody = readStaticResource(type);

            // Path에 따른 비지니스 로직
            if (httpInfo.get("Path").contains("/login?")) {
                authenticateUser();
            }

            // HTTP 요청 응답 완성
            final var response = buildHttpResponse(responseBody, type);

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

    private String resolveContentType() {
        final String accept = httpInfo.getOrDefault("Accept", "html");
        if (accept.contains("css")) {
            return "css";
        }
        if (accept.contains("html")) {
            return "html";
        }
        return "";
    }

    private String readStaticResource(String type) {
        final String path = "/static";
        final String NO_CONTENT = "Hello world!";

        final String fileName = resolveResourcePath(type);
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

    private String resolveResourcePath(String type) {
        final String NO_CONTENT = "Hello world!";

        String uri = httpInfo.get("Path");
        if (uri == null) {
            log.error("[getStaticResource] 파일 경로가 null 입니다.");
            return NO_CONTENT;
        }

        uri = uri.split("\\?")[0];

        final String extension = "." + type;
        if (!uri.endsWith(extension)) {
            uri += extension;
        }

        return uri;
    }

    private void authenticateUser() {
        final String[] uri = httpInfo.getOrDefault("Path", "/").split("\\?");

        if (uri.length == 2) {
            Map<String, String> queryString = new HashMap<>();
            final String[] queryStrings = uri[1].split("&");
            for (String query : queryStrings) {
                final String[] keyValue = query.split("=");
                queryString.put(keyValue[0], keyValue[1]);
            }

            User user = InMemoryUserRepository.findByAccount(queryString.get("account")).orElseThrow();
            if (user.checkPassword(queryString.get("password"))) {
                log.info("user : {}", user);
            } else {
                log.info("[printLoginResult] 회원 정보가 일치하지 않습니다.");
            }
        }
    }

    private String buildHttpResponse(final String responseBody, final String type) {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            String.format("Content-Type: text/%s;charset=utf-8 ", type),
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
