package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        try (var inputStream = connection.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             var outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequestParser.parse(bufferedReader);
            String path = httpRequest.getPath();
            HttpMethod httpMethod = httpRequest.getHttpMethod();

            String responseBody = "Hello world!";
            String contentType = "text/html;charset=utf-8";
            String redirectUrl = null;
            HttpStatusCode statusCode = HttpStatusCode.OK;
            Map<String, String> responseCookies = new HashMap<>();

            if (path.endsWith(".html")) {
                String fileName = "static" + path;
                responseBody = getHtmlResponseBody(fileName);
            } else if (path.endsWith(".css") || path.endsWith(".js")) {
                contentType = "text/css";
                if (path.endsWith(".js")) {
                    contentType = "application/javascript";
                }
                String fileName = "static" + path;
                responseBody = getHtmlResponseBody(fileName);
            } else if (path.startsWith("/login")) {
                if (httpMethod == HttpMethod.POST) {
                    HttpRequestParameter requestParameter = httpRequest.getHttpRequestParameter();
                    String account = requestParameter.getValue("account");
                    String password = requestParameter.getValue("password");
                    redirectUrl = "/index.html";
                    try {
                        User user = InMemoryUserRepository.findByAccount(account)
                                .orElseThrow(() -> new IllegalArgumentException("account에 해당하는 사용자가 없습니다."));
                        if (!user.checkPassword(password)) {
                            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
                        }
                        log.info("user: " + user);
                    } catch (IllegalArgumentException e) {
                        redirectUrl = "/401.html";
                    }
                    statusCode = HttpStatusCode.FOUND;
                    UUID uuid = UUID.randomUUID();
                    responseCookies.put("JSESSIONID", uuid.toString());
                } else if (httpMethod == HttpMethod.GET) {
                    String fileName = "static/login.html";
                    responseBody = getHtmlResponseBody(fileName);
                }
            } else if (path.equals("/register")) {
                if (httpMethod == HttpMethod.POST) {
                    statusCode = HttpStatusCode.FOUND;
                    HttpRequestParameter requestParameter = httpRequest.getHttpRequestParameter();
                    String account = requestParameter.getValue("account");
                    String password = requestParameter.getValue("password");
                    String email = requestParameter.getValue("email");
                    try {
                        User user = new User(account, password, email);
                        InMemoryUserRepository.save(user);
                        redirectUrl = "/index.html";
                    } catch (IllegalArgumentException e) {
                        redirectUrl = "/400.html";
                    }
                } else if (httpMethod == HttpMethod.GET) {
                    String fileName = "static/register.html";
                    responseBody = getHtmlResponseBody(fileName);
                }
            }

            List<String> headers = new ArrayList<>();
            headers.add("HTTP/1.1 " + statusCode.getString() + " ");
            headers.add("Content-Type: " + contentType + " ");
            headers.add("Content-Length: " + responseBody.getBytes().length + " ");
            String cookies = responseCookies.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("; "));
            if (redirectUrl != null) {
                headers.add("Location: " + redirectUrl);
            }
            if (!cookies.isEmpty()) {
                headers.add("Set-Cookie: " + cookies);
            }

            var response = String.join("\r\n", headers) + "\r\n\r\n" + responseBody;

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getHtmlResponseBody(String fileName) throws IOException {
        URL url = getClass().getClassLoader().getResource(fileName);
        if (url != null) {
            File file = new File(url.getFile());
            Path path = file.toPath();

            StringBuilder htmlContent = new StringBuilder();
            try (BufferedReader htmlBufferedReader = new BufferedReader(new FileReader(path.toString()))) {
                String line;
                while ((line = htmlBufferedReader.readLine()) != null) {
                    htmlContent.append(line).append("\n");
                }
            }
            return htmlContent.toString();
        }
        return "Hello world!";
    }
}
