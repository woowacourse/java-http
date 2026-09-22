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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            final var response = buildResponseWith(inputStream);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String buildResponseWith(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String[] startLine = reader.readLine().split(" ");
        String httpMethod = startLine[0];
        String requestUri = startLine[1];

        Map<String, String> headers = readHeaders(reader);
        String requestBody = readBody(reader, headers);

        String path = parsePathFrom(requestUri);

        if (path.startsWith("/login") && httpMethod.equals("POST")) {
            return loginResponse(parseFormData(requestBody));
        }

        if (path.startsWith("/register") && httpMethod.equals("POST")) {
            return registerResponse(parseFormData(requestBody));
        }

        return staticResponse(path);
    }

    private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] keyValue = line.split(": ", 2);
            headers.put(keyValue[0], keyValue[1]);
        }
        return headers;
    }

    private String readBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return null;
        }
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        reader.read(buffer, 0, length);
        return new String(buffer);
    }

    private Map<String, String> parseFormData(String data) {
        Map<String, String> params = new HashMap<>();
        if (data == null || data.isEmpty()) {
            return params;
        }
        for (String pair : data.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    private String loginResponse(Map<String, String> params) {
        Optional<User> account = findAccount(params.get("account"), params.get("password"));
        if (account.isPresent()) {
            return redirect("/index.html");
        }
        return redirect("/401.html");
    }

    private String registerResponse(Map<String, String> params) {
        User user = new User(params.get("account"), params.get("password"), params.get("email"));
        InMemoryUserRepository.save(user);
        return redirect("/index.html");
    }

    private Optional<User> findAccount(String account, String password) {
        if (account == null || password == null) {
            return Optional.empty();
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : {}", user.get());
            return user;
        }

        return Optional.empty();
    }

    private String redirect(String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: " + location + " "
        );
    }

    private String staticResponse(String path) throws IOException {
        String contentType = contentTypeOf(path);
        String responseBody = resolveContentOf(path);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String parsePathFrom(String requestUri) {
        String path = requestUri;
        if (path.contains("?")) {
            int queryFileStrEndIndex = requestUri.indexOf("?");
            if (queryFileStrEndIndex != -1) {
                path = path.substring(0, queryFileStrEndIndex);
            }
        }

        if (!path.contains(".")) {
            path = path.concat(".html");
        }

        return path;
    }

    private String resolveContentOf(String filePath) throws IOException {
        URL resource = getResource(filePath);
        if (!filePath.equals("/") && resource != null) {
            return Files.readString(new File(resource.getFile()).toPath());
        }
        return "Hello world!";
    }

    private URL getResource(String filePath) {
        String path = "static" + filePath;
        return getClass().getClassLoader().getResource(path);
    }

    private String contentTypeOf(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }
}
