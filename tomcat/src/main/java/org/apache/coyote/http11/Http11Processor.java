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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
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

            RequestLine requestLine = new RequestLine(request);
            RequestHeader requestHeader = new RequestHeader(readHeaderLines(reader));
            RequestBody requestBody = new RequestBody(readBody(reader, requestHeader.get("Content-Length")));

            String method = requestLine.getMethod();
            String requestUri = requestLine.getPath();

            Cookie cookie = new Cookie(requestHeader.get("Cookie"));

            String statusLine = "HTTP/1.1 200 OK ";
            String location = null;
            String setCookie = null;

            if (requestUri.equals("/login") && method.equals("GET")) {
                requestUri = "/login.html";

                //이미 로그인한 상태면 로그인 페이지를 보여줄 필요가 없음
                if (isLoggedIn(cookie)) {
                    statusLine = "HTTP/1.1 302 Found ";
                    location = "/index.html";
                }
            }

            if (requestUri.equals("/login") && method.equals("POST")) {
                Map<String, String> params = requestBody.parseParams();

                Optional<User> user = InMemoryUserRepository.findByAccount(params.getOrDefault("account", ""))
                        .filter(it -> it.checkPassword(params.get("password")));

                statusLine = "HTTP/1.1 401 Unauthorized ";
                requestUri = "/401.html";

                if (user.isPresent()) {
                    log.info("user : {}", user.get());
                    statusLine = "HTTP/1.1 302 Found ";
                    location = "/index.html";

                    //로그인 정보는 서버(세션)에 두고, 클라이언트에는 세션 아이디만 내려보냄
                    Session session = new Session(UUID.randomUUID().toString());
                    session.setAttribute("user", user.get());
                    SessionManager.getInstance().add(session);

                    setCookie = Cookie.ofJSessionId(session.getId());
                }
            }

            if (requestUri.equals("/register") && method.equals("GET")) {
                requestUri = "/register.html";
            }

            if (requestUri.equals("/register") && method.equals("POST")) {
                Map<String, String> params = requestBody.parseParams();

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

    private boolean isLoggedIn(final Cookie cookie) throws IOException {
        if (!cookie.hasJSessionId()) {
            return false;
        }

        Session session = SessionManager.getInstance().findSession(cookie.getJSessionId());
        if (session == null) {
            return false;
        }

        return getUser(session) != null;
    }

    private User getUser(final Session session) {
        return (User) session.getAttribute("user");
    }

    private List<String> readHeaderLines(final BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            headerLines.add(line);
            line = reader.readLine();
        }
        return headerLines;
    }

    private String readBody(final BufferedReader reader, final String contentLengthHeader) throws IOException {
        if (contentLengthHeader == null) {
            return "";
        }

        int contentLength = Integer.parseInt(contentLengthHeader.trim());
        char[] buffer = new char[contentLength];

        int totalRead = 0;
        while (totalRead < contentLength) {
            int read = reader.read(buffer, totalRead, contentLength - totalRead);
            if (read == -1) {
                break;
            }
            totalRead += read;
        }

        return new String(buffer, 0, totalRead);
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
