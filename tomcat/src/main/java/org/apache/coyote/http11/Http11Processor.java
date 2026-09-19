package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private static final String POST_METHOD = "POST";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String STATIC_RESOURCE_DIRECTORY = "static";
    private static final String INDEX_HTML_PATH = "/index.html";
    private static final String UNAUTHORIZED_PAGE_PATH = "/401.html";
    private static final String HTML_EXTENSION = ".html";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String HTML_CONTENT_TYPE = "text/html";
    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String ACCEPT_HEADER_PREFIX = "Accept:";
    private static final String CRLF = "\r\n";
    private static final String OK_STATUS_LINE = "HTTP/1.1 200 OK ";
    private static final String FOUND_STATUS_LINE = "HTTP/1.1 302 FOUND ";
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
            Map<String, String> headerMap = new LinkedHashMap<>();
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                String[] parts = line.split(":", 2);
                headerMap.put(parts[0], parts[1].trim());
            }

            // GET /css/styles.css HTTP/1.1 각각 분리
            final String httpMethod = requestLine[0];
            final String url = requestLine[1];
            final String httpVersion = requestLine[2];
            final int contentLength = Integer.parseInt(headerMap.getOrDefault(CONTENT_LENGTH_HEADER, "0"));

            String requestBody = "";

            if (POST_METHOD.equals(httpMethod) && contentLength > 0) {
                char[] buffer = new char[contentLength];
                int totalRead = 0;

                while (totalRead < contentLength) {
                    int count = bufferedReader.read(buffer, totalRead, contentLength - totalRead);

                    if(count == -1) {
                        throw new IOException("예상보다 짧음");
                    }
                    totalRead += count;
                }
                requestBody = new String(buffer);
            }

            Map<String, String> requestBodyMap = new LinkedHashMap<>();

            if (httpMethod.equals(POST_METHOD) && url.equals(REGISTER_PATH) && contentLength > 0) {
                String[] body = requestBody.split("&");
                for (int i = 0; i < body.length; i++) {
                    String[] value = body[i].split("=");
                    requestBodyMap.put(value[0], value[1]);
                }

                User user = new User(requestBodyMap.get(ACCOUNT_PARAMETER), requestBodyMap.get(PASSWORD_PARAMETER), requestBodyMap.get("email"));
                InMemoryUserRepository.save(user);
            }



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
            if (url.equals(ROOT_PATH)) {
                responseBody = DEFAULT_RESPONSE_BODY;
            } else if (urlPath.equals(LOGIN_PATH) || urlPath.equals(REGISTER_PATH)) {
                URL resource = getClass().getClassLoader()
                        .getResource(STATIC_RESOURCE_DIRECTORY + urlPath + HTML_EXTENSION);
                responseBody = Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
            } else {
                URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_DIRECTORY + urlPath);
                responseBody = Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
            }

            // 로그
            boolean isLoginSuccess = false;
            if (urlPath.equals(LOGIN_PATH) && isQuery && queryMap.containsKey(ACCOUNT_PARAMETER)
                    && queryMap.containsKey(PASSWORD_PARAMETER)) {
                Optional<User> matchedUser = InMemoryUserRepository
                        .findByAccount(queryMap.get(ACCOUNT_PARAMETER))
                        .filter(user -> user.checkPassword(queryMap.get(PASSWORD_PARAMETER)));
                isLoginSuccess = matchedUser.isPresent();

                if (isLoginSuccess) {
                    log.info("회원 조회 성공: {}", matchedUser);
                }
            }

            // 헤더를 하나씩 읽으면서 Accept 존재하면 확장자 설정
            // Accept 존재하지 않으면 기본값 text/html로 설정
            String contentType = HTML_CONTENT_TYPE;
            String accept = headerMap.getOrDefault("Accept", "");

            if (accept.contains(CSS_CONTENT_TYPE)) {
                contentType = CSS_CONTENT_TYPE;
            }

            String response;
            if (urlPath.equals(LOGIN_PATH) && isQuery) {
                String location = "";
                if (isLoginSuccess) {
                    location += INDEX_HTML_PATH;
                } else {
                    location += UNAUTHORIZED_PAGE_PATH;
                }

                response = String.join(CRLF,
                        FOUND_STATUS_LINE,
                        "Location: " + location,
                        "Content-Length: 0",
                        "",
                        "");
            } else if(httpMethod.equals(POST_METHOD) && urlPath.equals(REGISTER_PATH)) {
                response = String.join(CRLF,
                        FOUND_STATUS_LINE,
                        "Location: " + INDEX_HTML_PATH,
                        "Content-Length: 0",
                        "",
                        "");
            } else {
                response = String.join(CRLF,
                        OK_STATUS_LINE,
                        CONTENT_TYPE_HEADER_PREFIX + contentType + UTF_8_CHARSET_PARAMETER,
                        CONTENT_LENGTH_HEADER_PREFIX + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
