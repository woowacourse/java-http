package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.ResponseDto;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String END_OF_LINE = "";

    private final Socket connection;
    private final SessionManager sessionManager = new SessionManager();

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
        try (
                final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream();
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8)
                )
        ) {
            HttpRequestHeader header = readHttpRequestHeader(reader);

            String response = makeResponse(header, reader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequestHeader readHttpRequestHeader(BufferedReader reader) throws IOException {
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null & !Objects.equals(END_OF_LINE, line)) {
            builder.append(line).append(System.lineSeparator());
        }

        return HttpRequestHeader.from(builder.toString());
    }

    private String makeResponse(HttpRequestHeader header, BufferedReader reader) throws IOException {
        String url = header.get("URL");
        Session session = null;
        HttpCookie httpCookie = HttpCookie.from(header.get("Cookie"));
        if (httpCookie != null) {
            String sessionId = httpCookie.get("JESSIONID");
            session = sessionManager.findSession(sessionId);
        }

        if (Objects.equals(header.get("HTTP Method"), "POST")) {
            String bodyContent = readBodyContent(header, reader);
            HttpRequestBody requestBody = HttpRequestBody.from(bodyContent);

            if (url.contains("/login")) {
                ResponseDto responseDto = checkUserCredentials(
                        requestBody.get("account"),
                        requestBody.get("password"),
                        session
                );

                String location = responseDto.location();
                String code = responseDto.code();
                FileManager fileManager = FileManager.from(location);
                String type = fileManager.mimeType();
                String responseBody = fileManager.fileContent();

                if (code.equals("401 Unauthorized ")) {
                    return String.join(
                            System.lineSeparator(),
                            "HTTP/1.1 " + code,
                            "Location:" + location,
                            "Content-Type: " + type + ";charset=utf-8 ",
                            "Content-Length: " + responseBody.getBytes().length + " ",
                            "",
                            responseBody
                    );
                }

                if (responseDto.id() == null) {
                    return String.join(
                            System.lineSeparator(),
                            "HTTP/1.1 " + "200 OK ",
                            "Location:" + location,
                            "Content-Type: " + type + ";charset=utf-8 ",
                            "Content-Length: " + responseBody.getBytes().length + " ",
                            "",
                            responseBody
                    );
                }

                return String.join(
                        System.lineSeparator(),
                        "HTTP/1.1 " + code,
                        "Location:" + location,
                        "Set-Cookie:" + "JESSIONID=" + responseDto.id(),
                        "Content-Type: " + type + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody
                );
            }

            if (url.contains("/register")) {
                ResponseDto responseDto = save(
                        requestBody.get("account"),
                        requestBody.get("password"),
                        requestBody.get("email")
                );

                String location = responseDto.location();
                String code = responseDto.code();
                FileManager fileManager = FileManager.from(location);
                String type = fileManager.mimeType();
                String responseBody = fileManager.fileContent();

                return String.join(
                        System.lineSeparator(),
                        "HTTP/1.1 " + code,
                        "Location:" + location,
                        "Content-Type: " + type + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody
                );
            }
        }

        if (Objects.equals(header.get("HTTP Method"), "GET")) {

            if (url.contains("/login") & session != null) {
                String location = "index.html";
                String code = "200 OK ";
                FileManager fileManager = FileManager.from(location);
                String type = fileManager.mimeType();
                String responseBody = fileManager.fileContent();

                return String.join(
                        System.lineSeparator(),
                        "HTTP/1.1 " + code,
                        "Location:" + location,
                        "Content-Type: " + type + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody
                );
            }

            if (Objects.equals(url, "/")) {
                String responseBody = "Hello world!";
                return String.join(
                        System.lineSeparator(),
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody
                );
            }

            FileManager fileManager = FileManager.from(url);
            String type = fileManager.mimeType();
            String responseBody = fileManager.fileContent();

            return String.join(
                    System.lineSeparator(),
                    "HTTP/1.1 " + "200 OK ",
                    "Content-Type: " + type + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody
            );
        }

        throw new IllegalArgumentException();
    }

    private String readBodyContent(HttpRequestHeader header, BufferedReader reader) throws IOException {
        int contentLength = Integer.parseInt(header.get("Content-Length"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public ResponseDto checkUserCredentials(String account, String password, Session session) {
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if ((user.checkPassword(password)) & session == null) {
                session = new Session(UUID.randomUUID().toString());
                session.setAttribute("user", user);
                sessionManager.add(session);
                String id = session.getId();

                log.info("user : {}", user);
                return new ResponseDto("302 Found ", "index.html", id);
            }

            if ((user.checkPassword(password))) {
                log.info("user : {}", user);
                return new ResponseDto("302 Found ", "index.html", null);
            }
        }
        return new ResponseDto("401 Unauthorized ", "401.html", null);
    }

    public ResponseDto save(String account, String password, String email) {
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return new ResponseDto("200 OK ", "index.html", null);
    }
}
