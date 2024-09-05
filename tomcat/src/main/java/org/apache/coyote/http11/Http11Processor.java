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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

            String httpMethod = null;
            String statusCode = "200 OK";
            String responseBody = "Hello world!";
            String contentType = "text/html;charset=utf-8";
            String uri = "/";
            String redirectUrl = null;
            String startLine = bufferedReader.readLine();
            if (startLine != null) {
                String[] requestParts = startLine.split(" ");
                if (requestParts.length >= 2) {
                    httpMethod = requestParts[0].trim();
                    uri = requestParts[1].trim();
                }
            }
            Map<String, String> httpHeaders = new HashMap<>();
            String requestLine = null;
            while ((requestLine = bufferedReader.readLine()) != null && !requestLine.isEmpty()) {
                String[] requestParts = requestLine.split(":");
                if (requestParts.length >= 2) {
                    httpHeaders.put(requestParts[0].trim(), requestParts[1].trim());
                }
            }
            StringBuilder requestBodyBuilder = new StringBuilder();
            if (httpHeaders.get("Content-Length") != null) {
                int contentLength = Integer.parseInt(httpHeaders.get("Content-Length"));
                char[] bodyChars = new char[contentLength];
                bufferedReader.read(bodyChars, 0, contentLength);
                requestBodyBuilder.append(bodyChars);
            }
            String requestBody = requestBodyBuilder.toString();

            if (uri.endsWith(".html")) {
                String fileName = "static" + uri;
                responseBody = getHtmlResponseBody(fileName);
            } else if (uri.endsWith(".css") || uri.endsWith(".js")) {
                contentType = "text/css";
                if (uri.endsWith(".js")) {
                    contentType = "application/javascript";
                }
                String fileName = "static" + uri;
                responseBody = getHtmlResponseBody(fileName);
            } else if (uri.startsWith("/login")) {
                if (httpMethod.equals("POST")) {
                    Map<String, String> paramMap = Arrays.stream(requestBody.split("&"))
                            .map(param -> param.split("=", 2))
                            .collect(Collectors.toMap(
                                    keyValue -> keyValue[0],
                                    keyValue -> keyValue[1]
                            ));
                    String account = paramMap.get("account");
                    String password = paramMap.get("password");
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
                    statusCode = "302 Found";
                } else if (httpMethod.equals("GET")) {
                    String fileName = "static/login.html";
                    responseBody = getHtmlResponseBody(fileName);
                }
            } else if (uri.equals("/register")) {
                if (httpMethod.equals("POST")) {
                    Map<String, String> paramMap = Arrays.stream(requestBody.split("&"))
                            .map(param -> param.split("=", 2))
                            .collect(Collectors.toMap(
                                    keyValue -> keyValue[0],
                                    keyValue -> keyValue[1]
                            ));
                    statusCode = "302 Found";
                    try {
                        User user = new User(paramMap.get("account"), paramMap.get("password"), paramMap.get("email"));
                        InMemoryUserRepository.save(user);
                        redirectUrl = "/index.html";
                    } catch (IllegalArgumentException e) {
                        redirectUrl = "/400.html";
                    }
                } else if (httpMethod.equals("GET")) {
                    String fileName = "static/register.html";
                    requestBody = getHtmlResponseBody(fileName);
                }
            }

            List<String> headers = new ArrayList<>();
            headers.add("HTTP/1.1 " + statusCode + " ");
            headers.add("Content-Type: " + contentType + " ");
            headers.add("Content-Length: " + responseBody.getBytes().length + " ");
            if (redirectUrl != null) {
                headers.add("Location: " + redirectUrl);
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
