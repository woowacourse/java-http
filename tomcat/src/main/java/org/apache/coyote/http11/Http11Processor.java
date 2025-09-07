package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
             final var outputStream = connection.getOutputStream();
             var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final var requestLine = RequestLine.from(reader.readLine());
            var requestPath = requestLine.getPath();
            final Map<String, String> queryParameters = requestLine.getQueryParameters();

            if (requestPath.isBlank() || "/".equals(requestPath)) {
                requestPath = "index.html";
            }
            if (requestPath.equals("login")) {
                printMemberInfo(queryParameters.get("account"), queryParameters.get("password"));
            }

            String statusCode = "200 OK";
            String responseBody = readStaticFileContent(requestPath);
            if (responseBody == null) {
                statusCode = "404 Not Found";
                responseBody = readStaticFileContent("404.html");

                if (responseBody == null) {
                    responseBody = "<h1>404 Not Found</h1>";
                }
            }

            final var contentType = ContentType.from(requestPath);
            final var response = buildHttpResponse(statusCode, contentType.getMimeType(), responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try (final var outputStream = connection.getOutputStream()) {
                String responseBody;
                URL errorResource = getResourceFrom("500.html");
                if (errorResource != null) {
                    responseBody = Files.readString(Paths.get(errorResource.toURI()));
                } else {
                    responseBody = "<h1>500 Internal Server Error</h1>";
                }

                final var response = buildHttpResponse("500 Internal Server Error", "text/html", responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
            } catch (IOException | URISyntaxException ex) {
                log.error("500 에러 전송 실패: " + ex.getMessage(), ex);
            }
        }
    }

    public void printMemberInfo(String account, String password) {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        if (user.checkPassword(password)) {
            System.out.println("user: " + user);
        }
    }

    private URL getResourceFrom(String requestPath) {
        var resource = getClass().getClassLoader().getResource("static/" + requestPath);
        if (resource == null) {
            resource = getClass().getClassLoader().getResource("static/" + requestPath + ".html");
        }
        return resource;
    }

    private String readStaticFileContent(String requestPath) throws URISyntaxException, IOException {
        URL resource = getResourceFrom(requestPath);
        if (resource == null) {
            return null;
        }
        return Files.readString(Paths.get(resource.toURI()));
    }

    private String buildHttpResponse(String statusCode, String mimeType, String responseBody) throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + mimeType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
