package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
        /*
         * 1. inputStream -> Request resource 읽어오기
         *      첫 번째 줄 파싱 (method 뒤에 명시된 파일 명 기반으로 찾기)
         * 2. resource 찾기 (with ClassLoader)
         * 3. 찾은 파일 내용을 읽어서 outputStream 에 전달 (바이트 코드로)
         */
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            try {
                final var requestUri = getRequestUri(inputStream);
                final var responseBody = getResponseBody(requestUri);
                final var response = getHttpResponse(requestUri, 200, responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
            } catch (FileNotFoundException | IllegalArgumentException e) {
                final var responseBody = readNotFoundFile();
                final var response = getHttpResponse(400, responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String getRequestUri(InputStream inputStream) throws IOException {
        final var reader = new BufferedReader(new InputStreamReader(inputStream));
        final var request = reader.readLine();

        // 1-2. request url 에서 리소스명 추출
        final var split = Arrays.stream(request.split("\\s+")).toList(); // 공백 문자를 기준으로 파싱
        if (split.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return split.get(1);
    }

    private Map<String, String> getHeaders(InputStream inputStream) throws IOException {
        final var reader = new BufferedReader(new InputStreamReader(inputStream));

        final Map<String, String> result = new HashMap<>();
        while (!reader.readLine().isBlank()) {
            final var line = reader.readLine();
            final var colonIndex = line.indexOf(":");
            final var key = line.substring(0, colonIndex).trim();
            final var value = line.substring(colonIndex + 1).trim();
            result.put(key, value);
        }

        return result;
    }

    /**
     * handle request and get response body
     * @param requestUri HTTP request uri
     * @return response body text
     * @throws FileNotFoundException occurs when couldn't find target file
     * @throws IOException occurs when there are invalid bytes in file
     */
    private String getResponseBody(final String requestUri) throws FileNotFoundException, IOException {
        // 1. 정적 파일에 대한 요청인 경우
        if (isStaticFileUri(requestUri)) {
            return readStaticFileByName(requestUri);
        }
        // 2. API 요청인 경우
        final var queryIndex = requestUri.indexOf("?");
        final var endpoint = getAPIEndpoint(requestUri, queryIndex);

        // 3. endpoint 에 따른 처리 로직 분기 (= handler mapping)
        if (endpoint.equals("/login")) { // handler logic (Controller -> Service -> Repo)
            final var queryString = requestUri.substring(queryIndex + 1);
            final var queryParams = getQueryParams(queryString);

            final var account = queryParams.get("account");
            final var password = queryParams.get("password");

            InMemoryUserRepository.findByAccount(account)
                    .ifPresent((user) -> {
                        if (user.checkPassword(password)) {
                            log.info("user : {}", user);
                        }
                    });

            return readStaticFileByName("/login.html");
        }

        // 4. 존재하지 않는 리소스/endpoint 에 대한 요청인 경우
        throw new IllegalArgumentException();
    }

    /**
     * parse API endpoint from request uri
     * @param requestUri request uri (ex. '/home?name=hello')
     * @param queryIndex index of '?' in query string
     * @return
     */
    private String getAPIEndpoint(final String requestUri, final int queryIndex) {
        if (queryIndex == -1) {
            return requestUri;
        }
        return requestUri.substring(0, queryIndex);
    }

    /**
     *
     * @param queryString ex) 'name=123&age=1'
     * @return query params map
     */
    private Map<String, String> getQueryParams(final String queryString) {
        final Map<String, String> result = new HashMap<>();

        final var split = Arrays.stream(queryString.split("&")).toList();
        for (String param : split) {
            final var pair = Arrays.stream(param.split("=")).toList();
            // TODO: pair size != 2 예외
            final var key = pair.getFirst().trim();
            final var value = pair.get(1).trim();
            result.put(key, value);
        }
        return result;
    }

    /**
     * check if uri is for static file or not
     * @param uri request uri text
     * @return whether uri is for static file or not
     * @throws FileNotFoundException occurs when couldn't find target file
     */
    private boolean isStaticFileUri(final String uri) throws FileNotFoundException {
        final var dotIndex = uri.indexOf(".");
        if (uri.equals("/") || dotIndex > 0 && dotIndex < uri.length() - 1) {
            return true;
        }
        if (dotIndex == -1) {
            return false;
        }
        throw new FileNotFoundException("요청 리소스가 유효하지 않습니다.");
    }

    /**
     * get content by static file name
     * @param target target file name
     * @return target html file's text content
     * @throws FileNotFoundException occurs when couldn't find target file
     * @throws IOException occurs when there are invalid bytes in file
     */
    private String readStaticFileByName(final String target) throws FileNotFoundException, IOException {
        if (target.equals("/")) {
            return "Hello world!";
        }
        final var resource = getStaticResource(target);
        if (resource == null) {
            throw new FileNotFoundException();
        }
        return readContent(resource);
    }

    /**
     * get '404 not found' html content
     * @return not found html file's text content
     * @throws IOException occurs when there are invalid bytes in file
     */
    private String readNotFoundFile() {
        try {
            final var notfoundResource = getStaticResource("/404.html");
            return readContent(notfoundResource);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     * @param url resource's URL
     * @return resource's text content
     * @throws IOException occurs when there are invalid bytes in file
     */
    private String readContent(final URL url) throws IOException {
        final var result = new StringBuilder();
        final var readLines = Files.readAllLines(Path.of(url.getPath()));
        for (String line : readLines) {
            result.append(line);
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * find static resource path url
     * @param target target file name
     * @return target resource path url
     */
    private URL getStaticResource(final String target) {
        final var loader = ClassLoader.getSystemClassLoader();
        return loader.getResource("static" + target);
    }

    /**
     * get HTTP response
     * @param content request content
     * @param status status code
     * @param responseBody response body
     * @return response body text
     */
    private String getHttpResponse(final String content, final int status, final String responseBody) {
        // TODO: status, status code enum
        String statusCode = "";
        if (status == 200) statusCode = "OK";
        if (status == 400) statusCode = "NOT FOUND";
        final var responseInfoHeader = String.format("HTTP/1.1 %d %s ", status, statusCode);

        final var fileExtension = getFileExtension(content);
        final var contentTypeHeader = String.format("Content-Type: text/%s;charset=utf-8 ", fileExtension);
        return String.join("\r\n",
                responseInfoHeader,
                contentTypeHeader,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    /**
     * get HTTP response
     * @param status status code
     * @param responseBody response body
     * @return response body text
     */
    private String getHttpResponse(final int status, final String responseBody) {
        // TODO: status, status code enum
        String statusCode = "";
        if (status == 200) statusCode = "OK";
        if (status == 400) statusCode = "NOT FOUND";
        final var responseInfoHeader = String.format("HTTP/1.1 %d %s ", status, statusCode);

        return String.join("\r\n",
                responseInfoHeader,
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    /**
     * get file extension from url / path / name
     * @param target target resource path or name
     * @return file extension
     */
    private String getFileExtension(String target) {
        // TODO: 확장자별 content type 매핑
        String fileExtension = "html";

        final var slashIndex = target.lastIndexOf("/");
        String fileName = target;
        if (slashIndex >= 0) {
            fileName = target.substring(slashIndex + 1);
        }

        final var dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            fileExtension = fileName.substring(dotIndex + 1).toLowerCase();
        }
        return fileExtension;
    }
}
