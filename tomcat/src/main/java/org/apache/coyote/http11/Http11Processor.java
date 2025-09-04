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
            String line = br.readLine();
            if (line == null || line.isBlank()) {
                writeError(outputStream, 400, "Bad Request", "Request line is empty");
                return;
            }

            String[] tokens = line.trim().split(" ");
            if (tokens.length != 3) {
                writeError(outputStream, 400, "Bad Request", "Invalid request line");
                return;
            }

            String rawUri = tokens[1].trim();
            int index = rawUri.indexOf("?");

            String uri = makeUri(rawUri, index);
            Map<String, List<String>> query = makeQuery(rawUri, index);

            if (uri.equals("/") || uri.isEmpty()) {
                String responseBody = "Hello world!";
                writeResponse(outputStream, "text/html;charset=utf-8", responseBody.getBytes());
                return;
            }

            if (uri.equals("/login")) {
                handleLogin(outputStream, query);
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

    private String makeUri(final String rawUri, final int index) {
        return (index >= 0) ? rawUri.substring(0, index) : rawUri;
    }

    private Map<String, List<String>> makeQuery(final String rawUri, final int index) {
        String rawQuery = (index >= 0) ? rawUri.substring(index + 1) : "";
        return parseQuery(rawQuery);
    }

    private Map<String, List<String>> parseQuery(final String queryString) {
        Map<String, List<String>> query = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return query;
        }
        String[] split = queryString.split("&");
        for (String pair : split) {
            int eq = pair.indexOf("=");

            String key = pair.substring(0, eq);
            String value = pair.substring(eq + 1);

            query.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        return query;
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
            final Map<String, List<String>> query
    )
            throws IOException, URISyntaxException {
        String account = getFirst(query, "account");
        String password = getFirst(query, "password");

        if (account != null && password != null) {
            InMemoryUserRepository.findByAccount(account).ifPresentOrElse(
                    user -> {
                        try {
                            user.checkPassword(password);
                            log.info("Login OK - account {}", account);
                        } catch (UncheckedServletException e) {
                            log.info("Login FAILED - invalid password {}", account);
                        }
                    },
                    () -> log.info("Login FAILED - no sush account {}", account)
            );
        }
        String resourcePath = "static/login.html";
        URL url = getClass().getClassLoader().getResource(resourcePath);
        if (url == null) {
            writeError(outputStream, 400, "Bad Request", "Invalid request line");
            return;
        }
        Path absoluteLogin = Path.of(url.toURI());
        byte[] loginBytes = Files.readAllBytes(absoluteLogin);
        writeResponse(outputStream, "text/html;charset=utf-8", loginBytes);
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
