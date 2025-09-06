package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            HttpRequest httpRequest = parseHttpRequest(bufferedReader);
            HttpResponse httpResponse = handleHttpRequest(httpRequest);
            writeResponseToOutputStream(httpResponse, outputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private record HttpRequest(String method, String path, Map<String, String> queryStrings) {}

    private record HttpResponse(String status, String contentType, String responseBody) {
        public String toString() {
            return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length,
                "",
                responseBody);
        }
    }

    private HttpRequest parseHttpRequest(BufferedReader bufferedReader) {
        try {
            String[] methodAndUriAndProtocol = bufferedReader.readLine().split(" ");
            String method = methodAndUriAndProtocol[0];

            String uri = methodAndUriAndProtocol[1];
            String path = uri;
            int index = uri.indexOf("?");
            if (index == -1) {
                return new HttpRequest(method, path, null);
            }
            path = uri.substring(0, index);
            Map<String, String> queryStrings = Arrays.stream(uri.substring(index + 1).split("&"))
                .map(queryString -> queryString.split("="))
                .collect(Collectors.toUnmodifiableMap(
                    strings -> strings[0], // key
                    strings -> strings[1] // value
                ));
            return new HttpRequest(method, path, queryStrings);
        } catch (IOException | ArrayIndexOutOfBoundsException exception) {
            log.error(exception.getMessage(), exception);
            return null;
        }
    }

    private HttpResponse handleHttpRequest(HttpRequest httpRequest) {
        if (httpRequest == null) {
            return new HttpResponse("500 Internal Server Error", "text/html;charset=utf-8", null);
        }

        if (httpRequest.path.startsWith("/login")) {
            handleLogin(httpRequest);
        }

        if (httpRequest.path.equals("/")) {
            return new HttpResponse("200 OK", "text/html;charset=utf-8", "Hello world!");
        }

        try {
            URL resourceUrl = getClass().getClassLoader().getResource("static" + httpRequest.path);
            if (httpRequest.path.lastIndexOf(".") == -1) {
                resourceUrl = getClass().getClassLoader().getResource("static" + httpRequest.path + ".html");
            }
            String responseBody = Files.readString(Path.of(resourceUrl.toURI()));
            if (httpRequest.path.endsWith(".css")) {
                return new HttpResponse("200 OK", "text/css;charset=utf-8", responseBody);
            }
            return new HttpResponse("200 OK", "text/html;charset=utf-8", responseBody);
        } catch (IOException | URISyntaxException exception) {
            log.error(exception.getMessage(), exception);
            return new HttpResponse("404 Not Found", "text/html;charset=utf-8", null);
        }
    }

    private void handleLogin(HttpRequest httpRequest) {
        String account = httpRequest.queryStrings.get("account");
        String password = httpRequest.queryStrings.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 혹은 비밀번호입니다." + " " + account));
        if (user.isPasswordValid(password)) {
            log.info(user.toString());
            return;
        }
        throw new IllegalArgumentException("존재하지 않는 아이디 혹은 비밀번호입니다." + " " + account);
    }

    private void writeResponseToOutputStream(HttpResponse httpResponse, OutputStream outputStream) throws IOException {
        outputStream.write(httpResponse.toString().getBytes());
        outputStream.flush();
    }
}
