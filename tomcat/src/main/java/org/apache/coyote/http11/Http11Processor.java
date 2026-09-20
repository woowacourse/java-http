package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COLON = ":";
    private static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    private static final String CONTENT_TYPE_TEXT_CSS = "text/css";
    private static final String CONTENT_TYPE_TEXT_JAVASCRIPT = "text/javascript";
    private static final String CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String PATH_INDEX_HTML = "/index.html";
    private static final String PATH_LOGIN_HTML = "/login.html";
    private static final String PATH_401_HTML = "401.html";
    private static final String PATH_404_HTML = "static/404.html";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String HTTP_STATUS_OK = "200 OK";
    private static final String HTTP_STATUS_FOUND = "302 Found";
    private static final String HTTP_STATUS_NOT_FOUND = "404 Not Found";
    private static final String CHARSET_UTF_8 = "charset=utf-8 ";
    private static final String SEMI_COLON = ";";
    private static final String HTTP_VERSION_1_1 = "HTTP/1.1";
    private static final String LOCATION = "Location";
    private static final String STATIC = "static";

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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String requestLine = bufferedReader.readLine();
            if (requestLine == null) {
                return;
            }
            Map<String, String> headers = readHeaders(bufferedReader);
            String requestBody = readBody(bufferedReader, headers);
            String[] requestLines = requestLine.split(" ");
            handle(outputStream, requestLines[0], requestLines[1], requestBody, headers.get(CONTENT_TYPE));
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHeaders(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) {
                return headers;
            }
            String[] headerLine = line.split(COLON);
            headers.put(headerLine[0], headerLine[1].trim());
        }
    }

    private String readBody(final BufferedReader bufferedReader, final Map<String, String> headers) throws IOException {
        String contentLength = headers.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return "";
        }
        char[] buffer = new char[Integer.parseInt(contentLength)];
        int count = bufferedReader.read(buffer, 0, buffer.length);
        return new String(buffer, 0, count);
    }

    private void handle(final OutputStream outputStream, final String method, String path, String body, String contentType) throws IOException, URISyntaxException {
        if ("/".equals(path)) {
            writeResponse(outputStream, HTTP_STATUS_OK, CONTENT_TYPE_TEXT_HTML, "Hello world!");
        } else if ("/login".equals(path)) {
            login(outputStream, method, body, contentType);
        } else {
            writeStaticFile(outputStream, path);
        }
    }

    private void login(final OutputStream outputStream, final String method, String body, String contentType) throws IOException, URISyntaxException {
        if (METHOD_GET.equals(method)) {
            writeStaticFile(outputStream, PATH_LOGIN_HTML);
        } else if (METHOD_POST.equals(method)) {
            try {
                Map<String, String> parameters;
                if (contentType.startsWith(CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED)) {
                    parameters = parseFormData(body);
                } else {
                    throw new IllegalArgumentException("지원하지 않는 Content-Type입니다: " + contentType);
                }
                Optional<User> optionalUser = InMemoryUserRepository.findByAccount(parameters.get("account"));
                if (optionalUser.isEmpty()) {
                    throw new IllegalArgumentException("아이디와 비밀번호를 다시 확인하고 입력해주세요.");
                }
                User user = optionalUser.get();
                if (!user.checkPassword(parameters.get("password"))) {
                    throw new IllegalArgumentException("아이디와 비밀번호를 다시 확인하고 입력해주세요.");
                }
                log.info(user.toString());
                redirectResponse(outputStream, HTTP_STATUS_FOUND, contentTypeOf(CONTENT_TYPE_TEXT_HTML), "", PATH_INDEX_HTML);
            } catch (IllegalArgumentException exception) {
                redirectResponse(outputStream, HTTP_STATUS_FOUND, contentTypeOf(CONTENT_TYPE_TEXT_HTML), "", PATH_401_HTML);
            }
        }
    }

    private Map<String, String> parseFormData(final String body) {
        Map<String, String> formData = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyAndMap = pair.split("=", 2);
            String key = keyAndMap[0];
            String value = keyAndMap[1];
            formData.put(URLDecoder.decode(key, StandardCharsets.UTF_8), URLDecoder.decode(value, StandardCharsets.UTF_8));
        }
        return formData;
    }

    private Map<String, String> parseQueryString(final String requestTarget) {
        String queryString = requestTarget.substring(requestTarget.indexOf("?") + 1);
        Map<String, String> queryStringMap = new HashMap<>();
        for (String queryStringWithAndSplit : queryString.split("&")) {
            String[] split = queryStringWithAndSplit.split("=");
            queryStringMap.put(split[0], split[1]);
        }
        return queryStringMap;
    }

    private void writeStaticFile(final OutputStream outputStream, final String target) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(STATIC + target);
        if (resource == null) {
            URL notFound = getClass().getClassLoader().getResource(PATH_404_HTML);
            writeResponse(outputStream, HTTP_STATUS_NOT_FOUND, CONTENT_TYPE_TEXT_HTML, Files.readString(Path.of(notFound.toURI())));
            return;
        }
        writeResponse(outputStream, HTTP_STATUS_OK, contentTypeOf(target), Files.readString(Path.of(resource.toURI())));
    }

    private String contentTypeOf(final String target) {
        if (target.endsWith(".css")) {
            return CONTENT_TYPE_TEXT_CSS;
        }
        if (target.endsWith(".js")) {
            return CONTENT_TYPE_TEXT_JAVASCRIPT;
        }
        return CONTENT_TYPE_TEXT_HTML;
    }

    private void redirectResponse(final OutputStream outputStream, final String status, final String contentType, final String responseBody, final String locationUrl) throws IOException {
        final var response = String.join("\r\n",
                HTTP_VERSION_1_1 + " " + status + " ",
                CONTENT_TYPE + ": " + contentType + SEMI_COLON + " " + CHARSET_UTF_8,
                CONTENT_LENGTH + ": " + responseBody.getBytes().length + " ",
                LOCATION + ": " + locationUrl + " ",
                "",
                responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void writeResponse(final OutputStream outputStream, final String status, final String contentType, final String responseBody) throws IOException {
        final var response = String.join("\r\n",
                HTTP_VERSION_1_1 + " " + status + " ",
                CONTENT_TYPE + ": " + contentType + SEMI_COLON + " " + CHARSET_UTF_8,
                CONTENT_LENGTH + ": " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
