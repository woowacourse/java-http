package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
             final var outputStream = connection.getOutputStream()
        ) {
            // 1. 요청 라인(Request Line) 읽기
            final String requestLine = readRequestLine(inputStream);
            if (requestLine == null) {
                return;
            }

            // 2. 요청 라인(Request Line) URI 부분 파싱
            final String requestUriPath = parseRequestLineUriPath(requestLine);

            // 3. 경로를 추출할 URI 처리
            String uriPath = requestUriPath;
            if (requestUriPath.startsWith("/login")) {
                final int index = requestUriPath.indexOf("?");
                uriPath = requestUriPath.substring(0, index);
                String queryString = requestUriPath.substring(index + 1);
                Map<String, String> queryParams = mapQueryParams(queryString);
                findUserByAccount(queryParams);
            }

            // 3. 요청된 URI 경로 추출 (기본 /index.html)
            final String rawPath = extractRequestUriRawPath(uriPath);

            // 4. 실제 (정적) 리소스 경로 생성 (static 디렉터리 매핑)
            final String resourcePath = createResourcePath(rawPath);

            // 5. classPath에 해당하는 (정적) 리소스 찾기
            final URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
            if (resourceUrl == null) {
                return;
            }

            // 6. 파일 읽기
            final File resourceFile = new File(resourceUrl.getFile());
            final byte[] fileContentBytes = Files.readAllBytes(resourceFile.toPath());

            // 7. HTTP 응답 헤더(Response Header) 생성
            final String contentTypeHeader = getContentTypeHeader(rawPath);
            final int contentLength = fileContentBytes.length;
            final String responseHeaders = createResponseHeaders(contentTypeHeader, contentLength);

            // 8. HTTP 응답 전송하기
            outputStream.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
            outputStream.write(fileContentBytes);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestLine(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader.readLine();
    }

    private String parseRequestLineUriPath(final String requestLine) {
        final String[] requestTokens = requestLine.split(" ");
        return requestTokens[1];
    }

    private String extractRequestUriRawPath(final String requestUriPath) {
        String rawPath = requestUriPath;
        if (rawPath == null || rawPath.isBlank() || "/".equals(rawPath)) {
            rawPath = "/index.html";
        } else if (rawPath.startsWith("/login")) {
            return String.format("%s.html", rawPath);
        }
        return rawPath;
    }

    private String createResourcePath(final String rawPath) {
        return "static" + rawPath;
    }

    private String getContentTypeHeader(final String rawPath) {
        String contentType = "text/html";
        if (rawPath.endsWith(".css")) {
            contentType = "text/css";
        } else if (rawPath.endsWith(".js")) {
            contentType = "text/javascript";
        }
        return String.format("%s;charset=utf-8", contentType);
    }

    private String createResponseHeaders(final String contentTypeHeader, final int contentLength) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK",
                contentTypeHeader,
                "Content-Length: " + contentLength,
                "",
                "");
    }

    private Map<String, String> mapQueryParams(final String queryString) {
        final Map<String, String> queryParams = new HashMap<>();
        final String[] pairs = queryString.split("&");
        for (final String pair : pairs) {
            final String[] keyValue = pair.split("=");
            queryParams.put(keyValue[0], keyValue[1]);
        }
        return queryParams;
    }

    private void findUserByAccount(final Map<String, String> queryParams) {
        final String account = queryParams.get("account");
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        log.info("user : {}", user);
    }
}
