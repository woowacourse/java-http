package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, String> CONTENT_TYPE = Map.of("html", "text/html", "css", "text/css", "js",
            "application/javascript");
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String STATIC_ROOT = "static";
    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String DEFAULT_REQUEST = "/";
    private static final String LOGIN_REQUEST = "/login";
    private static final String REGISTER_REQUEST = "/register";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String NOT_FOUND_PAGE = "/404.html";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

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
             final var outputStream = connection.getOutputStream();
             final var inputReader = new InputStreamReader(inputStream);
             final var reader = new BufferedReader(inputReader)) {

            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            String[] requestLineParts = requestLine.split(" ");
            String httpMethod = requestLineParts[0];
            String requestUri = requestLineParts[1];

            Map<String, String> headers = readHeaders(reader);
            String requestBody = readBody(reader, headers);

            String[] uriParts = requestUri.split("\\?");
            String path = uriParts[0];

            if (path.equals(DEFAULT_REQUEST)) {
                path = INDEX_PAGE;
            }

            if (path.equals(LOGIN_REQUEST) && httpMethod.equals(POST_METHOD)) {
                writeResponse(outputStream, handleLogin(requestBody));
                return;
            }
            if (path.equals(LOGIN_REQUEST) && httpMethod.equals(GET_METHOD)) {
                path = LOGIN_PAGE;
            }

            if (path.equals(REGISTER_REQUEST) && httpMethod.equals(POST_METHOD)) {
                writeResponse(outputStream, handleRegister(requestBody));
                return;
            }
            if (path.equals(REGISTER_REQUEST) && httpMethod.equals(GET_METHOD)) {
                path = REGISTER_PAGE;
            }

            URL url = getClass().getClassLoader().getResource(STATIC_ROOT + path);
            if (url == null) {
                writeResponse(outputStream, makeResponse(HttpStatus.NOT_FOUND, NOT_FOUND_PAGE));
                return;
            }
            writeResponse(outputStream, makeResponse(HttpStatus.OK, path));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String handleLogin(String requestBody) {
        Map<String, String> params = parseFormData(requestBody);
        String account = params.get("account");
        String password = params.get("password");

        if (account == null || password == null) {
            return makeRedirectResponse(UNAUTHORIZED_PAGE);
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("회원 조회 결과: user={}", user.get());
            return makeRedirectResponse(INDEX_PAGE);
        }
        return makeRedirectResponse(UNAUTHORIZED_PAGE);
    }

    private String handleRegister(String requestBody) {
        Map<String, String> params = parseFormData(requestBody);
        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");

        if (account == null || password == null || email == null) {
            return makeRedirectResponse(REGISTER_PAGE);
        }

        InMemoryUserRepository.save(new User(account, password, email));
        return makeRedirectResponse(INDEX_PAGE);
    }

    private void writeResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            if (colonIndex == -1) {
                continue;
            }

            String name = line.substring(0, colonIndex).trim();
            String value = line.substring(colonIndex + 1).trim();
            headers.put(name, value);
        }
        return headers;
    }

    private String readBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        String contentLength = headers.get(CONTENT_LENGTH_HEADER);
        if (contentLength == null) {
            return "";
        }

        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        reader.read(buffer, 0, length);

        return new String(buffer);
    }

    private Map<String, String> parseFormData(String formData) {
        Map<String, String> params = new HashMap<>();
        if (formData.isEmpty()) {
            return params;
        }

        for (String param : formData.split("&")) {
            String[] keyAndValue = param.split("=", 2);
            if (keyAndValue.length == 2) {
                params.put(keyAndValue[0], keyAndValue[1]);
            }
        }

        return params;
    }

    private String makeResponse(HttpStatus status, String resourcePath) throws IOException, URISyntaxException {
        URL url = getClass().getClassLoader().getResource(STATIC_ROOT + resourcePath);
        String responseBody = new String(Files.readAllBytes(Paths.get(url.toURI())));
        String extension = getExtension(resourcePath);

        return HTTP_VERSION + " " + status.getCode() + " " + status.getReasonPhrase() + " \r\n" +
                "Content-Type: " + CONTENT_TYPE.getOrDefault(extension, "text/html") + ";charset=utf-8 \r\n" +
                "Content-Length: " + getContentLength(responseBody) + " \r\n\r\n" +
                responseBody;
    }

    private String makeRedirectResponse(String locationAddress) {
        return HTTP_VERSION + " " + HttpStatus.FOUND.getCode() + " " + HttpStatus.FOUND.getReasonPhrase() + " \r\n" +
                "Location: " + locationAddress + " \r\n\r\n";
    }

    private String getExtension(String resourcePath) {
        int lastIndex = resourcePath.lastIndexOf(".");
        return resourcePath.substring(lastIndex + 1);
    }

    private int getContentLength(String responseBody) {
        return responseBody.getBytes().length;
    }
}
