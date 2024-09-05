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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private String requestPath;
    private int statusCode = 200;
    private String statusMessage = "OK";
    private StringBuilder setCookie;
    private String  responseBody;
    private SessionManager sessionManager = SessionManager.getInstance();

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
            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }
            String[] headerFirstLine = line.split(" ");
            String requestUri = headerFirstLine[1];
            log.info("requestUri = {}", requestUri);

            Map<String, String> httpRequestHeaders = new HashMap<>();
            while (true) {
                line = bufferedReader.readLine();
                if ("".equals(line)) {
                    break;
                }
                String[] split = line.split(": ");
                String key = split[0];
                String value = split[1];
                httpRequestHeaders.put(key, value);
            }

            Map<String, String> cookies = new HashMap<>();
            if (!httpRequestHeaders.containsKey("Cookie") || !httpRequestHeaders.get("Cookie").contains("JSESSIONID")) {
                UUID jSessionId = UUID.randomUUID();
                cookies.put("JSESSIONID", jSessionId.toString());
            }

            requestPath = requestUri;
            if (requestUri.equals("/login") && headerFirstLine[0].equals("POST")) {
                int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);

                Map<String, String> userInfo = new HashMap<>();
                for (String request : requestBody.split("&")) {
                    String[] input = request.split("=");
                    userInfo.put(input[0], input[1]);
                }

                UUID sessionId = UUID.randomUUID();
                Session session = new Session(sessionId.toString());
                User user = InMemoryUserRepository.findByAccount(userInfo.get("account"))
                        .orElseThrow(IllegalArgumentException::new);
                if (!user.checkPassword(userInfo.get("password"))) {
                    requestPath = "/401";
                    statusCode = 401;
                    statusMessage = "Unauthorized";
                    return;
                }

                for (String info : userInfo.keySet()) {
                    session.setAttribute(info, userInfo.get(info));
                }
                sessionManager.add(session);

                requestPath = "/index.html";
                statusCode = 302;
                statusMessage = "Found";
                setCookie = new StringBuilder();
                for (String cookie : cookies.keySet()) {
                    setCookie.append(cookie).append("=").append(cookies.get(cookie));
                }
                log.info("user : {}", user);
            }


            if (requestUri.equals("/register") && headerFirstLine[0].equals("POST")) {
                int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);

                Map<String, String> userInfo = new HashMap<>();
                for (String request : requestBody.split("&")) {
                    String[] input = request.split("=");
                    userInfo.put(input[0], input[1]);
                }

                InMemoryUserRepository.save(
                        new User(userInfo.get("account"), userInfo.get("password"), userInfo.get("email")));
                log.info("savedUser = {}", InMemoryUserRepository.findByAccount(userInfo.get("account")));

                requestPath = "/index.html";
                statusCode = 302;
                statusMessage = "Found";
            }

            String contentType = "text/html;";
            if (requestUri.endsWith(".css")) {
                contentType = "text/css;";
            }
            if (requestUri.endsWith(".js")) {
                contentType = "application/javascript;";
            }

            if (requestPath.equals("/")) {
                requestPath = "/index";
            }
            String resourceName = "static" + requestPath;
            if (contentType.equals("text/html;") && !resourceName.endsWith(".html")) {
                resourceName += ".html";
            }
            URL resource = getClass().getClassLoader().getResource(resourceName);
            Path path = new File(resource.getPath()).toPath();
            byte[] bytes = Files.readAllBytes(path);

            responseBody = new String(bytes);
            String response = String.join("\r\n",
                    "HTTP/1.1" + " " + statusCode + " " + statusMessage + " ",
                    "Set-Cookie: " + setCookie + " ",
                    "Content-Type: " + contentType + "charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
