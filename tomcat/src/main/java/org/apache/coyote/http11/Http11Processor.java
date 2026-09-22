package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String request = reader.readLine();
            if (request == null) {
                return;
            }

            String[] requestLine = request.split(" ");

            String method = requestLine[0];
            String requestUri = requestLine[1];

            Map<String, String> headers = new HashMap<>();

            String line = reader.readLine();

            while (line != null && !line.isEmpty()) {
                String[] keyValue = line.split(": ", 2);
                if (keyValue.length == 2) {
                    headers.put(keyValue[0], keyValue[1]);
                }

                line = reader.readLine();
            }

            String requestBody = "";
            if (headers.containsKey("Content-Length")) {
                int contentLength = Integer.parseInt(headers.get("Content-Length").trim());
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                requestBody = new String(buffer);
            }

            String queryString = "";
            int index = requestUri.indexOf("?");
            if (index != -1) {
                queryString = requestUri.substring(index + 1);
                requestUri = requestUri.substring(0, index);
            }

            Cookie cookie = new Cookie(headers.get("Cookie"));

            String statusLine = "HTTP/1.1 200 OK ";
            String location = null;
            String setCookie = null;

            if (requestUri.equals("/login") && method.equals("GET")) {
                requestUri = "/login.html";
            }

            if (requestUri.equals("/login") && method.equals("POST")) {
                Map<String, String> params = parseParam(requestBody);

                Optional<User> user = InMemoryUserRepository.findByAccount(params.getOrDefault("account", ""))
                        .filter(it -> it.checkPassword(params.get("password")));

                statusLine = "HTTP/1.1 401 Unauthorized ";
                requestUri = "/401.html";

                if (user.isPresent()) {
                    log.info("user : {}", user.get());
                    statusLine = "HTTP/1.1 302 Found ";
                    location = "/index.html";

                    //이미 세션 아이디를 들고 있으면 새로 발급하지 않음
                    if (!cookie.hasJSessionId()) {
                        setCookie = Cookie.createJSessionId();
                    }
                }
            }

            if (requestUri.equals("/register") && method.equals("GET")) {
                requestUri = "/register.html";
            }

            if (requestUri.equals("/register") && method.equals("POST")) {
                Map<String, String> params = parseParam(requestBody);

                InMemoryUserRepository.save(
                        new User(params.get("account"), params.get("password"), params.get("email")));

                statusLine = "HTTP/1.1 302 Found ";
                location = "/index.html";
            }


            var responseBody = "";

            var contentType = "text/html";

            //302는 본문 없이 Location 헤더로 브라우저를 재요청시킴
            if (location == null) {
                contentType = resolveContentType(requestUri);
                responseBody = readStaticResource(requestUri);
            }

            List<String> lines = new ArrayList<>();
            lines.add(statusLine);
            if (location != null) {
                lines.add("Location: " + location + " ");
            }
            if (setCookie != null) {
                lines.add("Set-Cookie: " + setCookie + " ");
            }
            lines.add("Content-Type: " + contentType + ";charset=utf-8 ");
            lines.add("Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ");
            lines.add("");
            lines.add(responseBody);

            final var response = String.join("\r\n", lines);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseParam(String queryString) {
        Map<String, String> params = new HashMap<>();
        for (String param : queryString.split("&")) {
            String[] keyValue = param.split("=", 2);
            if (keyValue.length != 2 || keyValue[1].isBlank()) {
                continue;
            }
            params.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
        }
        return params;
    }

    private String resolveContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    private String readStaticResource(final String requestUri) throws IOException {
        if (requestUri.equals("/")) {
            return "Hello world!";
        }

        //클래스는 클래스로더에 대한 정보를 가짐
        //클래스로더는 파일의 위치에 대한 정보를 가짐
        //getResource는 파일을 찾지 못하면 null을 반환함
        URL resource = getClass().getClassLoader().getResource("static" + requestUri);

        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()), StandardCharsets.UTF_8);
    }
}
