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
import java.nio.file.Path;
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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            HttpRequest httpRequest = parseHttpRequest(bufferedReader);
            HttpResponse httpResponse = handleHttpRequest(httpRequest);
            outputStream.write(httpResponse.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private record HttpRequest(String method, String endpoint, String[] queryStrings) {}

    private record HttpResponse(String contentType, String responseBody) {
        public String toString() {
            return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        }
    }

    private HttpRequest parseHttpRequest(BufferedReader bufferedReader) {
        try {
            String[] methodAndUriAndProtocol = bufferedReader.readLine().split(" ");
            String method = methodAndUriAndProtocol[0];

            String uri = methodAndUriAndProtocol[1];
            String endpoint = uri;
            String[] queryStrings = null;
            int index = uri.indexOf("?");
            if (index != -1) {
                endpoint = uri.substring(0, index);
                queryStrings = uri.substring(index + 1).split("&");
            }
            return new HttpRequest(method, endpoint, queryStrings);
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
            return null;
        }
    }

    private HttpResponse handleHttpRequest(HttpRequest httpRequest) {
        if (httpRequest.endpoint.startsWith("/login")) {
            handleLogin(httpRequest);
        }

        if (httpRequest.endpoint.equals("/")) {
            return new HttpResponse("text/html;charset=utf-8 ", "Hello world!");
        } else {
            try {
                URL resourceUrl = getClass().getClassLoader().getResource("static" + httpRequest.endpoint);
                if (httpRequest.endpoint.lastIndexOf(".") == -1) {
                    resourceUrl = getClass().getClassLoader().getResource("static" + httpRequest.endpoint + ".html");
                }
                Path path = Path.of(resourceUrl.toURI());
                String responseBody = Files.readString(path);
                if (httpRequest.endpoint.endsWith(".css")) {
                    return new HttpResponse("text/css;charset=utf-8 ", responseBody);
                }
                return new HttpResponse("text/html;charset=utf-8 ", responseBody);
            } catch (IOException | URISyntaxException exception) {
                log.error(exception.getMessage(), exception);
                return null;
            }
        }
    }

    private void handleLogin(HttpRequest httpRequest) {
        String account = httpRequest.queryStrings[0].split("=")[1];
        String password = httpRequest.queryStrings[1].split("=")[1];
        User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 혹은 비밀번호입니다." + " " + account));
        if (user.isPasswordSameWith(password)) {
            log.info(user.toString());
            return;
        }
        throw new IllegalArgumentException("존재하지 않는 아이디 혹은 비밀번호입니다." + " " + account);
    }
}
