package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String OK = "200 OK";
    private static final String FOUND = "302 Found";
    private static final String UNAUTHORIZED = "401 Unauthorized";
    private static final String NOT_FOUND = "404 Not Found";
    private static final String INTERNAL_SERVER_ERROR = "500 Internal Server Error";

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
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String requestLine = br.readLine();
            if (requestLine == null) {
                return;
            }

            String[] requestHeaderInfo = requestLine.split(" ");
            if (requestHeaderInfo.length < 2) {
                return;
            }

            String url = requestHeaderInfo[1].substring(1);
            String staticUrl = "static/" + url;
            String response;

            if (url.isEmpty()) {
                response = sendDefaultResource();
                sendResponse(outputStream, response);
                return;
            }

            if (!url.contains(".") && !url.contains("?")) {
                staticUrl = "static/" + url + ".html";
                response = getResponse(staticUrl, OK);
                sendResponse(outputStream, response);
                return;
            }

            if (url.contains("?") && url.contains("login")) {
                int index = url.indexOf("?");
                response = authenticateUserResponse(url, index);
                sendResponse(outputStream, response);
                return;
            }

            response = getResponse(staticUrl, OK);
            sendResponse(outputStream, response);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String sendDefaultResource() throws IOException {
        final var responseBody = "Hello world!";
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }

    private void sendResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String authenticateUserResponse(String uri, int index) throws URISyntaxException, IOException {
        String queryString = uri.substring(index + 1);
        String accountQuery = queryString.split("&")[0];
        String passwordQuery = queryString.split("&")[1];
        String account = accountQuery.split("=")[1];
        String password = passwordQuery.split("=")[1];

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty()) {
            return getResponse("static/404.html", NOT_FOUND);
        }
        if (user.get().checkPassword(password)) {
            log.info(user.toString());
            return getResponse("static/index.html", FOUND);
        }
        return getResponse("static/401.html", UNAUTHORIZED);
    }

    private String getResponse(String uri, String statusCode) throws IOException, URISyntaxException {
        final var path = Paths.get(findUri(uri));
        final var contentType = Files.probeContentType(path);
        final byte[] responseBodyBytes = Files.readAllBytes(path);
        final String responseBody = Files.readString(path);

        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBodyBytes.length + " ",
                "",
                responseBody);
    }

    private URI findUri(String staticUrl) throws URISyntaxException {
        final var resource = getClass().getClassLoader().getResource(staticUrl);

        if (resource == null) {
            return Objects.requireNonNull(getClass().getClassLoader().getResource("static/404.html")).toURI();
        }
        return resource.toURI();
    }
}
