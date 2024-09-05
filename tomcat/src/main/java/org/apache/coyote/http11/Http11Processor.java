package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;

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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> headers = new ArrayList<>();
            String line;
            int contentLength = 0;

            // 헤더 읽기
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                headers.add(line);

                // Content-Length 헤더 확인
                if (line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }


            StringTokenizer tokenizer = new StringTokenizer(headers.get(0));
            String method = tokenizer.nextToken();
            String pattern = tokenizer.nextToken();
            String httpVersion = tokenizer.nextToken();

            if (!httpVersion.equals("HTTP/1.1")) {
                throw new IOException("not http1.1 request");
            }

            var responseBody = "";

            if (method.equals("GET")) {
                responseBody = processGetRequest(pattern);
            }
            if (method.equals("POST")) {
                char[] body = new char[contentLength];
                if (contentLength > 0) {
                    reader.read(body, 0, contentLength);
                }

                String requestBody = new String(body);
                String decodedBody = URLDecoder.decode(requestBody, StandardCharsets.UTF_8);

                log.info("Decoded Request Body: {}", decodedBody);
                responseBody = processPostRequest(pattern, decodedBody);
            }

            var response = "HTTP/1.1 " + getStatusCode(pattern, method) + " OK \r\n";
            response += String.join("\r\n", getHeaders(pattern, responseBody, method));
            response += "\r\n" + "\r\n" + responseBody;


            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String processPostRequest(String pattern, String body) {

        if (pattern.startsWith("/register")) {
            Map<String, String> params = parseQueryString(body);
            User user = new User(
                (InMemoryUserRepository.getLastId() + 1),
                params.get("account"),
                params.get("password"),
                params.get("email"));
            InMemoryUserRepository.save(user);

            log.info(user.toString());
            return "/index.html";
        }

        return "";
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> parameters = new HashMap<>();

        // '&'로 각 키-값 쌍 분리
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            // '='로 키와 값을 분리
            String[] keyValue = pair.split("=", 2);

            // 키와 값 저장
            String key = keyValue[0];
            String value = keyValue.length > 1 ? keyValue[1] : "";

            // Map에 저장
            parameters.put(key, value);
        }

        return parameters;
    }


    private List<String> getHeaders(String pattern, String responseBody, String method) {
        List<String> headers = new ArrayList<>();
        headers.add("Content-Type: " + getContentType(pattern) + ";charset=utf-8 ");
        headers.add("Content-Length: " + calculateContentLength(responseBody) + " ");

        if (getStatusCode(pattern, method).equals("302")) {
            headers.add("Location: " + responseBody);
        }

        return headers;
    }

    private int calculateContentLength (String content){
        return content.replaceAll("\r\n", "\n").getBytes(StandardCharsets.UTF_8).length;
    }

    private String processGetRequest(String pattern) throws IOException {
        if (pattern.equals("/")) {
            return "Hello world!";
        }

        if (pattern.equals("/register")) {
            URL resource = getClass().getClassLoader().getResource("static" + pattern+ ".html");
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()), StandardCharsets.UTF_8);
        }

        if (pattern.startsWith("/login") && !pattern.endsWith(".html")) {
            String uri = pattern;
            int index = uri.indexOf("?");
            // String path = uri.substring(0, index);
            String queryString = uri.substring(index + 1);
            int separatorIndex = queryString.indexOf("&");
            String accountQuery = queryString.substring(0, separatorIndex);
            String passwordQuery = queryString.substring(separatorIndex + 1);
            String account = accountQuery.substring(8);

            Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);

            if (byAccount.isPresent()) {
                log.info(byAccount.get().getAccount());
                return "/index.html";
            }
            return "/401.html";
		}

        URL resource = getClass().getClassLoader().getResource("static" + pattern);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()), StandardCharsets.UTF_8);
    }

    private String getContentType(String pattern) {
        if (pattern.startsWith("/css")) {
            return "text/css";
        }
        return "text/html";
    }

    private String getStatusCode(String pattern, String method) {
        if (pattern.startsWith("/login") && !pattern.endsWith(".html")) {
            return "302";
        }
        if (pattern.startsWith("/register") && method.equals("POST")) {
            return "302";
        }
        return "200";
    }
}
