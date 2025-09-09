package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.techcourse.exception.ErrorMessage.*;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private Request request;

    private Response response;

    public Http11Processor(final Socket connection) {
        response = new Response();
        response.setProtocolVersion("HTTP/1.1");
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
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            request = new Request(br);

            String httpMethod = request.getHttpMethod();
            String uri = request.getUrl();

            Path path = parsePath(uri);

            if (uri.startsWith("/login")) {
                if (uri.contains("?")) {
                    if (login(parseQueryParameter(uri))) {
                        response.setHttpStatusCode(HttpStatusCode.FOUND);
                        response.addHeader("Location", "/index.html");
                        response.addHeader("Content-Type", getContentType(path));
                        response.addHeader("Content-Length", response.getContentLength());
                        sendResponse(outputStream);
                        return;
                    }
                }
            }

            if (httpMethod.equals("POST") && uri.startsWith("/register")) {
                if(register(parseQueryString(request.getBody()))){
                    response.setHttpStatusCode(HttpStatusCode.FOUND);
                    response.addHeader("Location", "/index.html");
                    response.addHeader("Content-Type", getContentType(path));
                    response.addHeader("Content-Length", response.getContentLength());
                    sendResponse(outputStream);
                    return;
                }
            }

            staticResourceResponse(path);
            sendResponse(outputStream);
        } catch (IOException | UncheckedServletException | URISyntaxException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean register(Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return InMemoryUserRepository.findByAccount(account).isPresent();
    }


    private Path parsePath(String uri) {
        int idx = uri.indexOf('?');
        if (idx == -1) {
            return Paths.get(uri);
        }
        return Paths.get(uri.substring(0, idx));
    }

    private Map<String, String> parseQueryParameter(String uri) {
        if (!uri.contains("?")) {
            throw new IllegalArgumentException(INVALID_QUERY_STRING.getMessage());
        }
        String queryString = uri.split("\\?")[1];
        return parseQueryString(queryString);
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                params.put(parts[0], parts[1]);
            }
        }
        return params;
    }

    private boolean login(Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_NOT_FOUND.getMessage()));
        user.logUserInfo(password, log);
        return user.checkPassword(password);
    }

    private void sendResponse(OutputStream outputStream) throws IOException, URISyntaxException {
        String httpFormatResponse = formatHttpResponse();
        outputStream.write(httpFormatResponse.getBytes());
        outputStream.flush();
    }

    private void staticResourceResponse(Path path) throws IOException, URISyntaxException {
        response.setHttpStatusCode(HttpStatusCode.OK);
        response.addHeader("Content-Type", getContentType(path));
        response.setBody(getStaticResource(path));
        response.addHeader("Content-Length", response.getContentLength());
    }

    private String getContentType(Path path) throws IOException {
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "text/html";
        }
        return contentType + ";charset=utf-8";
    }

    private String getStaticResource(Path path) throws IOException, URISyntaxException {
        if (path.equals(Path.of("\\"))) {
            return "Hello world!";
        }
        Path staticPath = getStaticPath(path);
        return new String(Files.readAllBytes(staticPath));
    }

    private Path getStaticPath(Path path) throws URISyntaxException {
        if (!path.toString().contains(".")) {
            path = Path.of(path + ".html");
        }
        return Paths.get(getClass().getClassLoader().getResource("static" + path).toURI());
    }

    private String formatHttpResponse() {
        return String.join("\r\n",
                response.getProtocolVersion() + " " +
                        response.getStatusCode() + " " +
                        response.getStatusMessage() + " ",
                response.getHeaders()
                        .entrySet()
                        .stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                        .collect(Collectors.joining("\r\n")),
                "\r\n" + response.getBody()
        );
    }
}
