package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            HttpRequestData httpRequestData = createHttpRequestData(inputStream);
            String httpResponse = createHttpResponseMessage(responseResource(httpRequestData));

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequestData createHttpRequestData(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = bufferedReader.readLine();
        List<String> headerTokens = new ArrayList<>();

        String nowLine = bufferedReader.readLine();
        while (!nowLine.isBlank()) {
            headerTokens.add(nowLine);
            nowLine = bufferedReader.readLine();
        }

        return new HttpRequestData(requestLine, new Header(headerTokens));
    }

    private HttpResponseData responseResource(HttpRequestData httpRequestData) throws IOException {
        final var uri = URI.create(httpRequestData.startLine.split(" ")[1]);

        if ("/".equals(uri.getPath())) {
            return new HttpResponseData("Hello world!".getBytes(), "text/html;charset=utf-8");
        }

        var contentType = "";
        String path = uri.getPath();
        // TODO: acceptHeader를 읽어 적절한 contentType을 생성하는 부분
        if (path.endsWith(".html")) {
            contentType = "text/html;charset=utf-8";
        }

        if (path.endsWith(".css")) {
            contentType = "text/css;charset=utf-8";
        }

        if (path.endsWith(".js")) {
            contentType = "text/javascript;charset=utf-8";
        }

        var resourceUrl = getClass().getClassLoader().getResource("static/" + path);
        var resourcePath = "";

        // TODO: 존재하지 않는 정적 리소스 요청이라면 동적으로 생성하는 부분
        if (resourceUrl == null) {
            if (path.contains("login")) {
                processLogin(httpRequestData);
                resourcePath = getClass().getClassLoader().getResource("static/login.html").getPath();
            }
        } else {
            resourcePath = resourceUrl.getPath();
        }

        final var bufferedInputStream = new BufferedInputStream(new FileInputStream(resourcePath));
        final var responseBody = bufferedInputStream.readAllBytes();
        bufferedInputStream.close();

        return new HttpResponseData(responseBody, contentType);
    }

    private void processLogin(HttpRequestData httpRequestData) {
        final var startLine = httpRequestData.startLine;
        final var split = startLine.split(" ");
        final var resourcePath = split[1];

        URI uri = URI.create(resourcePath);
        String query = uri.getQuery();

        checkUser(new QueryParameter(query));
    }

    private void checkUser(QueryParameter queryParameter) {
        String password = queryParameter.get("password").orElse("");
        Optional<User> user = queryParameter.get("account").flatMap(InMemoryUserRepository::findByAccount);

        if (user.isPresent()) {
            boolean isSame = user.get().checkPassword(password);
            if (isSame) {
                log.info("{}", user.get());
            }
        }
    }

    private String createHttpResponseMessage(HttpResponseData httpResponseData) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + httpResponseData.contentType + " ",
                "Content-Length: " + httpResponseData.responseBody.length + " ",
                "",
                new String(httpResponseData.responseBody, StandardCharsets.UTF_8));
    }

    private record HttpRequestData(String startLine, Header header) {
    }

    private record HttpResponseData(byte[] responseBody, String contentType) {
    }
}
