package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
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

            final String response = getResponse(inputStream);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(InputStream requestStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(requestStream));

        String line = bufferedReader.readLine();
        String method = "";
        String path = "";
        if (line != null) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            method = tokenizer.nextToken();
            path = tokenizer.nextToken();
        }

        Map<String, String> requestHeader = new HashMap<>();
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] header = line.split(": ");
            if (header.length != 2) {
                throw new IllegalArgumentException("잘못된 header 형식입니다.");
            }
            requestHeader.put(header[0].strip(), header[1].strip());
        }

        String contentLength = requestHeader.getOrDefault("Content-Length", "0");
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        String requestBody = new String(buffer);

        if (method.equals("GET")) {
            return doGet(path, requestHeader);
        }
        if (method.equals("POST")) {
            return doPost(path, requestHeader, requestBody);
        }
        return null;
    }

    private String doGet(String path, Map<String, String> requestHeader) throws IOException {
        if (path.endsWith(".css")) {
            URL resource = getClass().getClassLoader().getResource("static" + path);
            String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/css;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (path.endsWith(".js")) {
            URL resource = getClass().getClassLoader().getResource("static" + path);
            String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/javascript;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (path.endsWith(".svg")) {
            URL resource = getClass().getClassLoader().getResource("static" + path);
            String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: image/svg+xml ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (path.equals("/login")) {
            String cookies = requestHeader.getOrDefault("Cookie", "");
            HttpCookie httpCookie = new HttpCookie(cookies);
            if (httpCookie.hasKey("JSESSIONID")) {
                return String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: http://localhost:8080/index.html ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: 0 ",
                        "");
            }
        }

        if (path.equals("/")) {
            String responseBody = "Hello world!";
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (!path.endsWith(".html")) {
            path += ".html";
        }
        URL resource = getClass().getClassLoader().getResource("static" + path);
        if (resource != null) {
            String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }
        return null;
    }

    private String doPost(String path, Map<String, String> requestHeader, String requestBody) {
        if (path.equals("/register")) {
            StringTokenizer tokenizer = new StringTokenizer(requestBody, "=|&");
            String account = "";
            String password = "";
            String email = "";
            while (tokenizer.hasMoreTokens()) {
                String key = tokenizer.nextToken();
                if (key.equals("account") && tokenizer.hasMoreTokens()) {
                    account = tokenizer.nextToken();
                } else if (key.equals("password") && tokenizer.hasMoreTokens()) {
                    password = tokenizer.nextToken();
                } else if (key.equals("email") && tokenizer.hasMoreTokens()) {
                    email = tokenizer.nextToken();
                }
            }
            if (account.isBlank() || password.isBlank() || email.isBlank()) { // TODO: 예외처리 개선
                throw new IllegalArgumentException("올바르지 않은 request body 형식입니다.");
            }
            InMemoryUserRepository.save(new User(account, password, email));
            return String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: http://localhost:8080/index.html ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 0 ",
                    "");
        }

        if (path.equals("/login")) {
            StringTokenizer tokenizer = new StringTokenizer(requestBody, "=|&");
            String account = "";
            String password = "";
            while (tokenizer.hasMoreTokens()) {
                String key = tokenizer.nextToken();
                if (key.equals("account") && tokenizer.hasMoreTokens()) {
                    account = tokenizer.nextToken();
                } else if (key.equals("password") && tokenizer.hasMoreTokens()) {
                    password = tokenizer.nextToken();
                }
            }

            Optional<User> loginUser = InMemoryUserRepository.findByAccount(account);
            if (loginUser.isPresent()) {
                final User user = loginUser.get();
                if (user.checkPassword(password)) {
                    log.info("로그인 성공! 아이디: {}", user.getAccount());

                    String cookies = requestHeader.getOrDefault("Cookie", "");
                    HttpCookie httpCookie = new HttpCookie(cookies);
                    if (httpCookie.hasKey("JSESSIONID")) {
                        return String.join("\r\n",
                                "HTTP/1.1 302 Found ",
                                "Location: http://localhost:8080/index.html ",
                                "Content-Type: text/html;charset=utf-8 ",
                                "Content-Length: 0 ",
                                "");
                    }
                    Cookie cookie = CookieManager.setCookie();
                    Session session = new Session(cookie.getValue());
                    session.setAttribute("user", user);
                    SessionManager.getInstance().add(session);

                    return String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Location: http://localhost:8080/index.html ",
                            "Set-Cookie: " + cookie.getKeyValue(),
                            "Content-Type: text/html;charset=utf-8 ",
                            "Content-Length: 0 ",
                            "");
                }
            }
            return String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: http://localhost:8080/401.html ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 0 ",
                    "");
        }
        return null;
    }
}
