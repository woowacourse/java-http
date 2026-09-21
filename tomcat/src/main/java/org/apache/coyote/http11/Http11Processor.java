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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String STATIC_ROOT = "static";
    private static final String ROOT_PATH = "/";
    private static final String MIME_TYPE_DEFAULT = "text/html";
    private static final String MIME_TYPES_WILDCARD = "*/*";
    private static final Map<String, String> MIME_TYPE = Map.ofEntries(
            Map.entry("text/html", ".html"),
            Map.entry("text/css", ".css"),
            Map.entry("text/javascript", ".js")
    );
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

            HttpRequest request = parseRequest(inputStream);
            final String response = handle(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseRequest(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequestHeader header = parseRequestHeader(br);

        if (!header.hasContain("Content-Length")) {
            return new HttpRequest(header, HttpRequestBody.empty());
        }

        int contentLength = Integer.parseInt(header.header().get("Content-Length"));
        HttpRequestBody body = parseRequestBody(br, contentLength);
        return new HttpRequest(header, body);
    }

    private HttpRequestHeader parseRequestHeader(BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line = br.readLine();
        RequestLine firstLine = RequestLine.from(line);

        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            String[] parts = line.split(": ", 2);
            if (parts.length == 2) {
                headers.put(parts[0], parts[1]);
            }
        }
        return new HttpRequestHeader(firstLine, headers);
    }

    private HttpRequestBody parseRequestBody(BufferedReader br, int contentLength) throws IOException {
        String requestBody;

        char[] buffer = new char[contentLength];
        int offset = 0;
        while (offset < contentLength) {
            int result = br.read(buffer, offset, contentLength - offset);
            if (result == -1) {
                break;
            }
            offset += result;
        }
        requestBody = new String(buffer);

        return new HttpRequestBody(requestBody);
    }

    private String handle(HttpRequest request) throws IOException {
        HttpRequestHeader header = request.requestHeader();
        HttpRequestBody body = request.requestBody();

        String responseBody;

        String contentType = resolveContentType(header);
        URL url = findStaticResource(header.path(), contentType);

        if (url == null || url.getPath().endsWith(ROOT_PATH)) {
            responseBody = "Hello world!";
            return buildResponse(HttpStatus.OK, contentType, responseBody);
        }

        if (header.path().contains("login") && header.hasContain("Content-Length")) {
            String location = loginUser(body);
            return redirectResponse(HttpStatus.FOUND, location);
        }

        if (header.path().contains("register") && header.hasContain("Content-Length")) {
            String location = registerUser(body);
            return redirectResponse(HttpStatus.FOUND, location);
        }

        Path path = new File(url.getFile()).toPath();
        responseBody = Files.readString(path);

        return buildResponse(HttpStatus.OK, contentType, responseBody);
    }

    private String resolveContentType(HttpRequestHeader header) {
        String accept = header.header().get("Accept");

        if (accept == null || accept.isEmpty()) {
            return MIME_TYPE_DEFAULT;
        }

        String preferred = accept.split(",")[0].split(";")[0].trim();

        if (MIME_TYPES_WILDCARD.equals(preferred)) {
            return MIME_TYPE_DEFAULT;
        }

        return preferred;
    }

    private URL findStaticResource(String path, String contentType) {
        if (path.contains(".")) {
            return getClass().getClassLoader().getResource(STATIC_ROOT + path);
        }

        return getClass().getClassLoader().getResource(STATIC_ROOT + path + MIME_TYPE.get(contentType));
    }

    private String loginUser(HttpRequestBody body) {
        String[] formData = body.requestBody().split("&");

        List<String> data = Arrays.asList(formData);

        String account = data.get(0).split("=")[1];
        String password = data.get(1).split("=")[1];

        User user = InMemoryUserRepository.findByAccount(account)
                .orElse(null);

        if (user == null) {
            return "/401.html";
        }

        if (!user.checkPassword(password)) {
            return "/401.html";
        }

        log.info("user : {}", user);

        return "/index.html";
    }

    private String registerUser(HttpRequestBody body) {
        String[] formData = body.requestBody().split("&");

        List<String> data = Arrays.asList(formData);

        String account = data.get(0).split("=")[1];
        String email = data.get(1).split("=")[1];
        String password = data.get(2).split("=")[1];

        User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);

        log.info("new user : {}", newUser);

        return "/index.html";
    }

    private static String buildResponse(HttpStatus status, String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + status.status() + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }

    private String redirectResponse(HttpStatus status, String location) {
        return String.join("\r\n",
                "HTTP/1.1 " + status.status() + " ",
                "Location: " + location + " ",
                "",
                ""
        );
    }
}
