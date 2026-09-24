package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String INDEX_PATH = "index.html";

    private final Socket connection;

    // 요청
    private String method;
    private String path;
    private String requestBody;
    private final Map<String, String> queryParameters = new HashMap<>();
    private final Map<String, String> formParameters = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();

    // 응답
    private byte[] httpResponse;
    private final StringBuilder bodyBuilder = new StringBuilder();

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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8));
             final var outputStream = connection.getOutputStream()) {

            parseRequestLine(reader);
            parseHeaders(reader);

            if (method.equals("GET")) {
                handleGetRequest();
                writeResponse(outputStream);
                return;
            }
            if (method.equals("POST")) {
                readRequestBody(reader);
                formParameters.putAll(parseParameters(requestBody));
                handlePostRequest();
                writeResponse(outputStream);
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void parseRequestLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("EOF: No request line received");
        }

        String[] parts = line.split(" ");
        if (parts.length != 3) {
            throw new IOException("Invalid line received: " + line);
        }

        method = parts[0];
        String[] pathParts = parts[1].split("\\?", 2);
        path = pathParts[0];

        if (pathParts.length > 1) {
            queryParameters.putAll(parseParameters(pathParts[1]));
        }
    }

    private void parseHeaders(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(" ");
            headers.put(headerParts[0].trim(), headerParts[1].trim());
        }
    }

    private Map<String, String> parseParameters(String queryString) {
        Map<String, String> parameters = new HashMap<>();
        for (String param : queryString.split("&")) {
            if (param.isEmpty()) {
                continue;
            }

            String[] keyValue = param.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], UTF_8);
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], UTF_8) : "";
            parameters.put(key, value);
        }

        return parameters;
    }

    private void readRequestBody(BufferedReader reader) throws IOException {
        String value = headers.get("Content-Length:");
        if (value == null) {
            throw new IOException("Content-Length header is missing");
        }

        int length = Integer.parseInt(value);
        char[] body = new char[length];
        int offset = 0;

        while (offset < length) {
            int count = reader.read(body, offset, length - offset);
            if (count == -1) {
                throw new IOException("요청 바디가 Content-Length보다 짧습니다.");
            }
            offset += count;
        }

        requestBody = new String(body);
    }

    private void handleGetRequest() throws IOException {
        if (path.equals("/login") || path.equals("/register")) {
            serveStaticFile(path + ".html");
            return;
        }
        if (path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js")) {
            serveStaticFile(path);
            return;
        }
        if (path.equals("/")) {
            serverHomePage();
            return;
        }
        notFound();
    }

    private void notFound() {
        httpResponse = createNotFoundResponse("404.html").getBytes(UTF_8);
    }

    private void handlePostRequest() {
        if (path.equals("/login")) {
            loginResult();
        }
        if (path.equals("/register")) {
            registerResult();
        }
    }

    private void loginResult() {
        String account = formParameters.get("account");
        String password = formParameters.get("password");

        boolean loginSucceeded = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();

        String location = "/401.html";
        if (loginSucceeded) {
            location = INDEX_PATH;
        }

        httpResponse = createRedirectResponse(location).getBytes(UTF_8);
    }

    private void registerResult() {
        String account = formParameters.get("account");
        String password = formParameters.get("password");
        String email = formParameters.get("email");

        InMemoryUserRepository.save(new User(account, password, email));

        httpResponse = createRedirectResponse(INDEX_PATH).getBytes(UTF_8);
    }

    private void serveStaticFile(String requestUri) throws IOException {
        String contentType = findContentType(requestUri);

        URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource("static" + requestUri));
        String body = Files.readString(Path.of(resource.getPath()), UTF_8);
        writeBody(body);

        httpResponse = createOkHttpResponse(contentType).getBytes(UTF_8);
    }

    private void serverHomePage() {
        writeBody("Hello world!");
        httpResponse = createOkHttpResponse(findContentType("/")).getBytes(UTF_8);
    }

    private String findContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html;charset=utf-8";
    }

    private void writeBody(String body) {
        bodyBuilder.append(body);
    }

    @Nonnull
    private String createOkHttpResponse(String contentType) {
        int contentLength = bodyBuilder.toString().getBytes(UTF_8).length;
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + contentLength + " ",
                "",
                bodyBuilder.toString()
        );
    }

    private String createRedirectResponse(String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "",
                ""
        );
    }

    private String createNotFoundResponse(String location) {
        return String.join("\r\n",
                "HTTP/1.1 404 NOT FOUND ",
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "",
                ""
        );
    }

    private void writeResponse(OutputStream outputStream) throws IOException {
        outputStream.write(httpResponse);
        outputStream.flush();
    }
}
