package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.vo.HttpCookie;
import org.apache.coyote.http11.vo.HttpRequest;
import org.apache.coyote.http11.vo.HttpResponse;
import org.apache.coyote.http11.vo.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
                final var response = getResponse(httpRequest);
                final var httpResponse = getHttpResponse(response);

                outputStream.write(httpResponse.getBytes());
                outputStream.flush();
            } catch (FileNotFoundException | IllegalArgumentException e) {
                final var responseBody = readNotFoundFile();
                final var httpResponse = getHttpResponse(responseBody);
                outputStream.write(httpResponse.getBytes());
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
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        final var headers = getHeaders(headerLines);

        if (headers.containsKey("Content-Length")) {
            final int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            if (reader.read(buffer, 0, contentLength) == -1) {
                throw new EOFException();
            }
            final String body = new String(buffer);
            return new HttpRequest(
                    firstLine.getFirst(),
                    firstLine.get(1),
                    getHeaders(headerLines),
                    body
            );
        }

        return new HttpRequest(
                firstLine.getFirst(),
                firstLine.get(1),
                getHeaders(headerLines),
                ""
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
     * @return response
     * @throws FileNotFoundException occurs when couldn't find target file
     * @throws IOException occurs when there are invalid bytes in file
     */
    private HttpResponse getResponse(final HttpRequest request) throws FileNotFoundException, IOException {
        final var method = request.method();
        final var uri = request.uri();

        // handler mapping
        // / 요청인 경우
        if (method.equalsIgnoreCase("GET") && uri.equals("/")) {
            return new HttpResponse("text/html", HttpStatus.OK, "Hello world!");
        }
        // static file 요청인 경우
        if (method.equalsIgnoreCase("GET") && isStaticFileUri(uri)) {
            // TODO:
            final var extension = getFileExtension(uri);
            if (extension.equals("css") || extension.equals("html")) {
                return new HttpResponse("text/" + extension, HttpStatus.OK, readStaticFileByName(uri));
            }
            if (extension.equals("js")) {
                return new HttpResponse("text/javascript", HttpStatus.OK, readStaticFileByName(uri));
            }
            return new HttpResponse("text/html", HttpStatus.OK, readStaticFileByName(uri));
        }
        // login 화면 요청인 경우
        if (method.equalsIgnoreCase("GET") && uri.equals("/login")) {
            final var session = request.getSession(false);
            if (session == null) {
                return new HttpResponse("text/html", HttpStatus.OK, readStaticFileByName("login.html"));
            }
            return new HttpResponse("text/html", HttpStatus.OK, readStaticFileByName("index.html"));
        }
        // register 화면 요청인 경우
        if (method.equalsIgnoreCase("GET") && uri.equals("/register")) {
            return new HttpResponse("text/html", HttpStatus.OK, readStaticFileByName("register.html"));
        }
        // register API 요청인 경우
        if (method.equalsIgnoreCase("POST") && uri.equals("/register")) {
            final String queryString = request.body();
            final var params = getQueryParams(queryString);

            final String account = params.get("account");
            final String password = params.get("password");
            final String email = params.get("email");

            if (account == null || password == null || email == null
                    || account.isBlank() || password.isBlank() || email.isBlank()
            ) {
                return new HttpResponse("application/json", HttpStatus.BAD_REQUEST, "값이 모두 입력되지 않았습니다.");
            }

            final var user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            return new HttpResponse("text/html", HttpStatus.OK, readStaticFileByName("index.html"));
        }
        // login API 요청인 경우
        if (method.equalsIgnoreCase("POST") && uri.startsWith("/login")) {
            final String queryString = request.body();
            final var params = getQueryParams(queryString);

            final String account = params.get("account");
            final String password = params.get("password");

            final var user = InMemoryUserRepository.findByAccount(account);
            if (user.isEmpty()) {
                return new HttpResponse("text/html", HttpStatus.UNAUTHORIZED, readStaticFileByName("401.html"));
            }

            final var savedUser = user.get();
            if (savedUser.checkPassword(password)) {
                log.info("user : {}", savedUser);
                final var session = request.getSession(true);
                session.setAttribute("user", savedUser);

                final var cookie = new HttpCookie();
                cookie.add("JSESSIONID", session.getId());

                final var httpResponse = new HttpResponse("text/html", HttpStatus.OK, readStaticFileByName("index.html"));
                httpResponse.setCookie(cookie);
                return httpResponse;
            }
            return new HttpResponse("text/html", HttpStatus.UNAUTHORIZED, readStaticFileByName("401.html"));
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

        if (queryString.isBlank()) {
            return Map.of(); // nullable
        }

        final var split = Arrays.stream(queryString.split("&")).toList();
        for (String param : split) {
            if (!param.contains("=")) {
                throw new IllegalArgumentException();
            }

            final var pair = Arrays.stream(param.split("=")).toList();
            if (pair.size() >= 3 || pair.isEmpty()) {
                throw new IllegalArgumentException();
            }

            final var key = pair.getFirst().trim();
            var value = pair.getLast().trim();
            if (pair.size() == 1 && param.indexOf("=") == param.length() - 1) {
                value = "";
            }
            result.put(key, value);
        }
        return Collections.unmodifiableMap(result);
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
     */
    private HttpResponse readNotFoundFile() {
        try {
            return new HttpResponse("text/html", HttpStatus.NOT_FOUND, readStaticFileByName("404.html"));
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
     * @param response Http response
     * @return response body text
     */
    private String getHttpResponse(final HttpResponse response) {
        return String.join("\r\n",
                getHeaderString(response),
                "",
                response.body()
        );
    }

    private String getHeaderString(final HttpResponse response) {
        final var responseInfoHeader = String.format("HTTP/1.1 %d %s ", response.getStatusCode(), response.getStatusReason());
        final var contentTypeHeader = String.format("Content-Type: %s;charset=utf-8 ", response.mediaType());
        final var contentLengthHeader = String.format("Content-Length: %d ", response.body().getBytes().length);
        if (response.headers().isEmpty()) {
            return String.join("\r\n",
                    responseInfoHeader,
                    contentTypeHeader,
                    contentLengthHeader
            );
        }
        final var customHeaders = response.getHeaderString();
        return String.join("\r\n",
                responseInfoHeader,
                contentTypeHeader,
                contentLengthHeader,
                customHeaders
        );
    }

    /**
     * get file extension from url / path / name
     * @param target target resource path or name
     * @return file extension
     */
    private String getFileExtension(String target) {
        // TODO: 확장자별 content type 매핑
        String fileExtension = "application/json";

        final var dotIndex = target.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < target.length() - 1) {
            fileExtension = target.substring(dotIndex + 1).toLowerCase();
        }
        return fileExtension;
    }
}
