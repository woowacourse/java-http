package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String STATIC_RESOURCE_DIRECTORY = "static";
    private static final String HTML_EXTENSION = ".html";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String HTML_CONTENT_TYPE = "text/html";
    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String ACCEPT_HEADER_PREFIX = "Accept:";
    private static final String CRLF = "\r\n";
    private static final String OK_STATUS_LINE = "HTTP/1.1 200 OK ";
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
            while(true) {
                String line = bufferedReader.readLine();
                if(line == null || line.isEmpty()) break;
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

            if(isQuery) {
                String queryString = devidedUrlQuery[1];
                if(queryString != null && !queryString.isBlank()) {
                    String[] query = queryString.split("&");
                    for(int i = 0; i < query.length; i++) {
                        String[] value = query[i].split("=");
                        queryMap.put(value[0], value[1]);
                    }
                }
            }


            // 경로 없음 -> Hello world!
            // 경로 존재하면 파일 읽기
            String responseBody;
            if(url.equals(ROOT_PATH)) {
                responseBody = DEFAULT_RESPONSE_BODY;
            } else if (urlPath.equals(LOGIN_PATH)) {
                URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_DIRECTORY + urlPath + HTML_EXTENSION);
                responseBody = Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
            } else {
                URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_DIRECTORY + urlPath);
                responseBody = Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
            }

            // 로그
            if(urlPath.equals(LOGIN_PATH) && isQuery && queryMap.containsKey(ACCOUNT_PARAMETER) && queryMap.containsKey(PASSWORD_PARAMETER)) {
                InMemoryUserRepository.findByAccount(queryMap.get(ACCOUNT_PARAMETER))
                        .filter(user -> user.checkPassword(queryMap.get(PASSWORD_PARAMETER)))
                        .ifPresent(user -> log.info("회원 조회 성공: {}", user));
            }

            // 헤더를 하나씩 읽으면서 Accept 존재하면 확장자 설정
            // Accept 존재하지 않으면 기본값 text/html로 설정
            String contentType = HTML_CONTENT_TYPE;
            for(String line : header) {
                if(line.startsWith(ACCEPT_HEADER_PREFIX)) {
                    if(line.contains(CSS_CONTENT_TYPE)) {
                        contentType = CSS_CONTENT_TYPE;
                    }
                    break;
                }
            }

            final var response = String.join(CRLF,
                    OK_STATUS_LINE,
                    CONTENT_TYPE_HEADER_PREFIX + contentType + UTF_8_CHARSET_PARAMETER,
                    CONTENT_LENGTH_HEADER_PREFIX + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
