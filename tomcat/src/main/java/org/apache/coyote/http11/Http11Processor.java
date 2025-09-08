package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String[] tokens = readStartLine(br, outputStream);
            if (tokens == null) {
                return;
            }

            String rawUri = tokens[1].trim();
            int queryIndex = rawUri.indexOf("?");

            String uri = makeUri(rawUri, queryIndex);
            Map<String, List<String>> queryParameters = makeQueryParameters(rawUri, queryIndex);

            if (uri.equals("/") || uri.isEmpty()) {
                String responseBody = "Hello world!";
                writeResponse(outputStream, "text/html;charset=utf-8", responseBody.getBytes());
                return;
            }

            if (uri.equals("/login")) {
                handleLogin(outputStream, queryParameters);
                return;
            }

            if (uri.endsWith(".html") || uri.endsWith(".css")) {
                handleStatic(uri, outputStream);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String[] readStartLine(final BufferedReader br, final OutputStream outputStream) throws IOException {
        String line = br.readLine();
        if (line == null || line.isBlank()) {
            writeError(outputStream, 400, "Bad Request", "Request line is empty");
            return null;
        }

        String[] tokens = line.trim().split(" ");
        if (tokens.length != 3) {
            writeError(outputStream, 400, "Bad Request", "Invalid request line");
            return null;
        }
        return tokens;
    }

    private String makeUri(final String rawUri, final int queryIndex) {
        return (queryIndex >= 0) ? rawUri.substring(0, queryIndex) : rawUri;
    }

    private Map<String, List<String>> makeQueryParameters(final String rawUri, final int queryIndex) {
        String rawQueryParameters = (queryIndex >= 0) ? rawUri.substring(queryIndex + 1) : "";
        return parseQueryParameters(rawQueryParameters);
    }

    private Map<String, List<String>> parseQueryParameters(final String queryParameters) {
        Map<String, List<String>> parameters = new HashMap<>();
        if (queryParameters == null || queryParameters.isEmpty()) {
            return parameters;
        }
        String[] split = queryParameters.split("&");
        for (String pair : split) {
            int eq = pair.indexOf("=");

            String key = pair.substring(0, eq);
            String value = pair.substring(eq + 1);

            parameters.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        return parameters;
    }

    private void handleStatic(String uri, final OutputStream outputStream) throws IOException, URISyntaxException {
        uri = "static/" + uri.substring(1);
        URL url = getClass().getClassLoader().getResource(uri);
        if (url == null) {
            writeError(outputStream, 404, "Not Found", "Requested resource was not found on the server.");
            return;
        }
        Path absolutePath = Path.of(url.toURI());
        if (!Files.exists(absolutePath)) {
            writeError(outputStream, 500, "Internal Server Error", "File not found");
            return;
        }
        byte[] responseBodyBytes = Files.readAllBytes(absolutePath);
        String contentType = guessContentType(absolutePath.toString());
        writeResponse(outputStream, contentType, responseBodyBytes);
    }

    private void handleLogin(
            final OutputStream outputStream,
            final Map<String, List<String>> queryParameters
    )
            throws IOException, URISyntaxException {
        String account = getFirst(queryParameters, "account");
        String password = getFirst(queryParameters, "password");

        if (account != null && password != null) {
            boolean success = InMemoryUserRepository.findByAccount(account)
                    .map(user -> user.checkPassword(password))
                    .orElse(false);

            if (success) {
                log.info("Login OK - account {}", account);
                writeRedirect(outputStream, "/index.html");
                return;
            }
            log.info("Login FAILED - invalid password {}", account);
            writeRedirect(outputStream, "/401.html");
        }
        writeRedirect(outputStream, "/login.html");
    }

    private String getFirst(final Map<String, List<String>> map, final String key) {
        List<String> list = map.get(key);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.getFirst();
    }

    private void writeError(
            final OutputStream outputStream,
            final int code,
            final String phase,
            final String message
    ) throws IOException {
        String response = "HTTP/1.1 " + code + " " + phase + "\r\n"
                + "Content-Type: text/html;charset=utf-8 " + "\r\n"
                + "Content-Length: " + message.getBytes().length + " " + "\r\n"
                + "Connection: close " + "\r\n"
                + "\r\n";
        outputStream.write(response.getBytes());
        outputStream.write(message.getBytes());
        outputStream.flush();
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String contentType,
            final byte[] bytes
    ) throws IOException {
        String response = "HTTP/1.1 200 OK " + "\r\n"
                + "Content-Type: " + contentType + " " + "\r\n"
                + "Content-Length: " + bytes.length + " " + "\r\n"
                + "\r\n";
        outputStream.write(response.getBytes());
        outputStream.write(bytes);
        outputStream.flush();
    }

    private void writeRedirect(
            final OutputStream outputStream,
            final String location
    ) throws IOException {
        String response = "HTTP/1.1 302 Found " + "\r\n"
                + "Location: " + location + "\r\n"
                + "Content-Type: text/html;charset=utf-8 " + "\r\n"
                + "Content-Length: " + "\r\n"
                + "\r\n";
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String guessContentType(final String target) {
        if (target.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (target.endsWith(".htm")) {
            return "text/html;charset=utf-8";
        }
        if (target.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return null;
    }
}
