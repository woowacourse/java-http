package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String STATIC_RESOURCE_DIRECTORY = "static";
    private static final String HTML_EXTENSION = ".html";
    private static final String CSS_EXTENSION = ".css";
    private static final String JS_EXTENSION = ".js";
    private static final String HTML_CONTENT_TYPE = "text/html";
    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String JS_CONTENT_TYPE = "text/javascript";
    private static final String TEXT_CONTENT_TYPE = "text/plain";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String CRLF = "\r\n";
    private static final String OK_STATUS_LINE = "HTTP/1.1 200 OK ";
    private static final String NOT_FOUND_STATUS_LINE = "HTTP/1.1 404 Not Found ";
    private static final String CONTENT_TYPE_HEADER_PREFIX = "Content-Type: ";
    private static final String UTF_8_CHARSET_PARAMETER = ";charset=utf-8 ";
    private static final String CONTENT_LENGTH_HEADER_PREFIX = "Content-Length: ";

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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            // 요청 라인 분리, 헤더 리스트 생성
            String[] requestLine = bufferedReader.readLine().split(" ");
            List<String> header = new ArrayList<>();
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                header.add(line);
            }

            // GET /css/styles.css HTTP/1.1 각각 분리
            final String httpMethod = requestLine[0];
            final String url = requestLine[1];
            final String httpVersion = requestLine[2];

            // URL 쿼리 분리
            String[] devidedUrlQuery = url.split("\\?");
            boolean isQuery = devidedUrlQuery.length > 1;
            String urlPath = devidedUrlQuery[0];
            Map<String, String> queryMap = new LinkedHashMap<>();

            if (isQuery) {
                String queryString = devidedUrlQuery[1];
                if (queryString != null && !queryString.isBlank()) {
                    String[] query = queryString.split("&");
                    for (int i = 0; i < query.length; i++) {
                        String[] value = query[i].split("=");
                        queryMap.put(value[0], value[1]);
                    }
                }
            }

            // 경로 없음 -> Hello world!
            // 경로 존재하면 파일 읽기
            String responseBody;
            boolean isResourceNull = false;
            if (urlPath.equals(ROOT_PATH)) {
                responseBody = DEFAULT_RESPONSE_BODY;
            } else if (urlPath.equals(LOGIN_PATH)) {
                URL resource = getClass().getClassLoader()
                        .getResource(STATIC_RESOURCE_DIRECTORY + urlPath + HTML_EXTENSION);
                if (resource == null) {
                    isResourceNull = true;
                    responseBody = "요청한 파일을 찾을 수 없습니다.";
                } else {
                    responseBody = Files.readString(
                            Paths.get(resource.toURI()), StandardCharsets.UTF_8);
                }
            } else {
                URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_DIRECTORY + urlPath);
                if (resource == null) {
                    isResourceNull = true;
                    responseBody = "요청한 파일을 찾을 수 없습니다.";
                } else {
                    responseBody = Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
                }
            }

            // 로그
            if (urlPath.equals(LOGIN_PATH) && isQuery && queryMap.containsKey(ACCOUNT_PARAMETER)
                    && queryMap.containsKey(PASSWORD_PARAMETER)) {
                InMemoryUserRepository.findByAccount(queryMap.get(ACCOUNT_PARAMETER))
                        .filter(user -> user.checkPassword(queryMap.get(PASSWORD_PARAMETER)))
                        .ifPresent(user -> log.info("회원 조회 성공: {}", user));
            }

            // 요청 경로 확장자로 Content-Type 결정
            String contentType = HTML_CONTENT_TYPE;

            if (isResourceNull) {
                contentType = TEXT_CONTENT_TYPE;
            } else if (urlPath.endsWith(CSS_EXTENSION)) {
                contentType = CSS_CONTENT_TYPE;
            } else if (urlPath.endsWith(JS_EXTENSION)) {
                contentType = JS_CONTENT_TYPE;
            }

            String response;

            if (isResourceNull) {
                response = String.join(CRLF,
                        NOT_FOUND_STATUS_LINE,
                        CONTENT_TYPE_HEADER_PREFIX + contentType + UTF_8_CHARSET_PARAMETER,
                        CONTENT_LENGTH_HEADER_PREFIX + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                        "",
                        responseBody);
            } else {
                response = String.join(CRLF,
                        OK_STATUS_LINE,
                        CONTENT_TYPE_HEADER_PREFIX + contentType + UTF_8_CHARSET_PARAMETER,
                        CONTENT_LENGTH_HEADER_PREFIX + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                        "",
                        responseBody);
            }
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
