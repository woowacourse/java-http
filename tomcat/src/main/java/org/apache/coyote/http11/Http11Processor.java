package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String[] tokens = readStartLine(br, outputStream);
            if (tokens == null) {
                return;
            }

            String method = tokens[0].trim();
            String rawUri = tokens[1].trim();
            int queryIndex = rawUri.indexOf("?");

            // 1. 헤더, 바디 읽기
            Map<String, String> headers = readHeaders(br);
            String body = readBody(headers, br);

            // 2. uri/쿼리 파싱
            String uri = makeUri(rawUri, queryIndex);
            Map<String, List<String>> queryParameters = makeQueryParameters(rawUri, queryIndex);

            // 3. POST form body를 queryParameters에 합치기
            String contentType = headers.get("Content-Type");
            if ("POST".equals(method) && contentType.startsWith("application/x-www-form-urlencoded")) {
                Map<String, List<String>> form = parseQueryParameters(body);
                form.forEach((key, value) -> {
                    queryParameters.computeIfAbsent(key, k -> new ArrayList<>()).addAll(value);
                });
            }

            // 4. 라우팅
            if ((uri.equals("/") || uri.isEmpty()) && method.equals("GET")) {
                String responseBody = "Hello world!";
                writeResponse(outputStream, "text/html;charset=utf-8", responseBody.getBytes());
                return;
            }
            if (uri.equals("/login")) {
                handleLogin(outputStream, queryParameters, method);
                return;
            }
            if (uri.equals("/register")) {
                handleRegister(outputStream, queryParameters, method);
                return;
            }
            if ((uri.endsWith(".html") || uri.endsWith(".css") || uri.endsWith(".js")) && method.equals("GET")) {
                handleStatic(uri, outputStream);
            }

            // 5. 매칭 안 된것들 라우트
            writeError(outputStream, 404, "Not Found", "No Route");
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> readHeaders(final BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            int colon = line.indexOf(':');
            if (colon > 0) {
                String name = line.substring(0, colon).trim();
                String value = line.substring(colon + 1).trim();
                headers.put(name, value);
            }
        }
        return headers;
    }

    private String readBody(Map<String, String> headers, BufferedReader bufferedReader) throws IOException {
        String contentLengthHeader = headers.get("Content-Length");
        if (contentLengthHeader == null) {
            return "";
        }

        final int contentLength = Integer.parseInt(contentLengthHeader);
        final char[] body = new char[contentLength];
        bufferedReader.read(body, 0, contentLength);

        return new String(body);
    }

    private String[] readStartLine(final BufferedReader br, final OutputStream outputStream) throws IOException {
        String line = br.readLine();
        if (line == null) {
            return null;
        }
        if (line.isBlank()) {
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
            final Map<String, List<String>> queryParameters,
            final String method
    ) throws IOException, URISyntaxException {
        if (method.equals("GET")) {
            writeRedirect(outputStream, "/login.html");
            return;
        }
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
    }

    private void handleRegister(
            final OutputStream outputStream,
            final Map<String, List<String>> queryParameters,
            final String method
    ) throws IOException {
        if (method.equals("GET")) {
            writeRedirect(outputStream, "/register.html");
            return;
        }
        String account = getFirst(queryParameters, "account");
        String email = getFirst(queryParameters, "email");
        String password = getFirst(queryParameters, "password");

        if (account != null && email != null && password != null) {
            InMemoryUserRepository.save(new User(account, password, email));
            log.info("Register OK - account {}", account);
            writeRedirect(outputStream, "/index.html");
        }
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
                + "Content-Length: 0" + "\r\n"
                + "Connection: close " + "\r\n"
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
        if (target.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return null;
    }
}
