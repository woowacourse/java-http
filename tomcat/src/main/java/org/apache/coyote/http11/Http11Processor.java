package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.vo.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            try {
                final var httpRequest = getHttpRequest(inputStream);
                final var responseBody = getResponseBody(httpRequest);
                final var response = getHttpResponse(httpRequest.uri(), 200, responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
            } catch (FileNotFoundException | IllegalArgumentException e) {
                final var responseBody = readNotFoundFile();
                final var response = getHttpResponse(404, responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest getHttpRequest(InputStream inputStream) throws IOException {
        final var reader = new BufferedReader(new InputStreamReader(inputStream));
        final var request = reader.readLine();

        final var firstLine = Arrays.stream(request.split("\\s+")).toList();
        if (firstLine.size() != 3 || !firstLine.getLast().equals("HTTP/1.1")) {
            throw new IllegalArgumentException();
        }

        final var headerLines = new ArrayList<String>();
        while (true) {
            final var read = reader.readLine();
            if (read == null || read.isBlank()) break;
            headerLines.add(read);
        }

        return new HttpRequest(
                firstLine.getFirst(),
                firstLine.get(1),
                getHeaders(headerLines)
        );
    }

    private Map<String, String> getHeaders(final List<String> headerLines) {
        final var result = new HashMap<String, String>();
        for (String line : headerLines) {
            final var colonIndex = line.indexOf(":");
            final var key = line.substring(0, colonIndex).trim();
            final var value = line.substring(colonIndex + 1).trim();
            result.put(key, value);
        }
        return result;
    }

    /**
     * handle request and get response body
     * @param request HTTP request
     * @return response body text
     * @throws FileNotFoundException occurs when couldn't find target file
     * @throws IOException occurs when there are invalid bytes in file
     */
    private String getResponseBody(final HttpRequest request) throws FileNotFoundException, IOException {
        final var method = request.method();
        final var uri = request.uri();

        // handler mapping
        // 1. / 요청인 경우
        if (method.equalsIgnoreCase("GET") && uri.equals("/")) {
            return "Hello world!";
        }
        // 2. static file 요청인 경우
        if (method.equalsIgnoreCase("GET") && isStaticFileUri(uri)) {
            return readStaticFileByName(uri);
        }
        // 3. login 화면 요청인 경우
        if (method.equalsIgnoreCase("GET") && uri.equals("/login")) {
            return readStaticFileByName("login.html");
        }
        // 4. login API 요청인 경우
        if (method.equalsIgnoreCase("GET") && uri.startsWith("/login")) {
            final var queryIndex = uri.indexOf("?");
            if (queryIndex == -1) {
                throw new IllegalArgumentException();
            }

            final var queryString = uri.substring(queryIndex + 1);
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
        throw new IllegalArgumentException();
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
        throw new FileNotFoundException();
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
        if (target.startsWith("/")) {
            return readContent("static" + target);
        }
        return readContent("static/" + target);
    }

    /**
     * get '404 not found' html content
     * @return not found html file's text content
     * @throws IOException occurs when there are invalid bytes in file
     */
    private String readNotFoundFile() {
        try {
            return readContent("static/404.html");
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     * @param target resource's URL
     * @return resource's text content
     * @throws FileNotFoundException occurs when couldn't find target file
     * @throws IOException occurs when there are invalid bytes in file
     */
    private String readContent(final String target) throws FileNotFoundException, IOException {
        try (final var stream = getClass().getClassLoader()
                .getResourceAsStream(target)) {
            if (stream == null) {
                throw new FileNotFoundException();
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
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
        if (status == 404) statusCode = "NOT FOUND";
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
