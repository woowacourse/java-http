package org.apache.coyote.http11;

import static org.apache.catalina.vo.Mime.HTML;
import static org.apache.catalina.vo.Mime.JSON;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.SessionManager;
import org.apache.catalina.vo.HttpRequest;
import org.apache.catalina.vo.HttpResponse;
import org.apache.catalina.vo.HttpStatus;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.util.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
            try {
                final var httpRequest = HttpRequestParser.parse(inputStream);
                final var response = getResponse(httpRequest);
                final var httpResponse = response.toString();

                outputStream.write(httpResponse.getBytes());
                outputStream.flush();
            } catch (FileNotFoundException | IllegalArgumentException e) {
                final var responseBody = readNotFoundFile();
                final var httpResponse = responseBody.toString();
                outputStream.write(httpResponse.getBytes());
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * handle request and get response body
     * @param request HTTP request
     * @return response
     * @throws FileNotFoundException occurs when couldn't find target file
     * @throws IOException occurs when there are invalid bytes in file
     */
    private HttpResponse getResponse(final HttpRequest request) throws FileNotFoundException, IOException {
        final var method = request.getMethod();
        final var path = request.getPath();

        // handler mapping
        // / 요청인 경우
        if (method.equalsIgnoreCase("GET") && path.equals("/")) {
            final var response = HttpResponse.of(HttpStatus.OK, "Hello world!");
            response.setContentType(HTML.getType());
            return response;
        }
        // static file 요청인 경우
        if (method.equalsIgnoreCase("GET") && isStaticFileUri(request.getURI())) {
            // TODO:
            final var response = HttpResponse.of(HttpStatus.OK, readStaticFileByName(path));
            response.setContentType(request.extractMimeType());
            return response;
        }
        // login 화면 요청인 경우
        if (method.equalsIgnoreCase("GET") && path.equals("/login")) {
            final var session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                final var response = HttpResponse.of(HttpStatus.OK, readStaticFileByName("login.html"));
                response.setContentType(HTML.getType());
                return response;
            }
            final var response = HttpResponse.of(HttpStatus.OK, readStaticFileByName("index.html"));
            response.setContentType(HTML.getType());
            return response;
        }
        // register 화면 요청인 경우
        if (method.equalsIgnoreCase("GET") && path.equals("/register")) {
            final var response = HttpResponse.of(HttpStatus.OK, readStaticFileByName("register.html"));
            response.setContentType(HTML.getType());
            return response;
        }
        // register API 요청인 경우
        if (method.equalsIgnoreCase("POST") && path.equals("/register")) {
            final var params = HttpRequestParser.parseQueryString(request.getBody());

            final String account = params.get("account");
            final String password = params.get("password");
            final String email = params.get("email");

            if (account == null || password == null || email == null
                    || account.isBlank() || password.isBlank() || email.isBlank()
            ) {
                final var response = HttpResponse.of(HttpStatus.BAD_REQUEST, "값이 모두 입력되지 않았습니다.");
                response.setContentType(JSON.getType());
                return response;
            }

            final var user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            final var response = HttpResponse.of(HttpStatus.OK, readStaticFileByName("index.html"));
            response.setContentType(HTML.getType());
            return response;
        }
        // login API 요청인 경우
        if (method.equalsIgnoreCase("POST") && path.startsWith("/login")) {
            final var params = HttpRequestParser.parseQueryString(request.getBody());

            final String account = params.get("account");
            final String password = params.get("password");

            final var user = InMemoryUserRepository.findByAccount(account);
            if (user.isEmpty()) {
                final var response = HttpResponse.of(HttpStatus.UNAUTHORIZED, readStaticFileByName("401.html"));
                response.setContentType(HTML.getType());
                return response;
            }

            final var savedUser = user.get();
            if (savedUser.checkPassword(password)) {
                log.info("user : {}", savedUser);

                final var session = request.getSession(true);
                session.setAttribute("user", savedUser);

                final var response = HttpResponse.of(HttpStatus.OK, readStaticFileByName("index.html"));
                response.setContentType(HTML.getType());

                final var sessionManager = SessionManager.getInstance();
                final var sessionCookie = sessionManager.generateSessionCookie(session);
                response.addCookie(sessionCookie);

                return response;
            }
            final var response = HttpResponse.of(HttpStatus.UNAUTHORIZED, readStaticFileByName("401.html"));
            response.setContentType(HTML.getType());
            return response;
        }
        throw new IllegalArgumentException();
    }

    /**
     * check if uri is for static file or not
     * @param uri request uri text
     * @return whether uri is for static file or not
     * @throws FileNotFoundException occurs when couldn't find target file
     */
    private boolean isStaticFileUri(final String uri) throws FileNotFoundException {
        final var dotIndex = uri.indexOf(".");
        if (uri.equals("/") || dotIndex > 0 && dotIndex < uri.length() - 1) {
            return true;
        }
        if (dotIndex == -1) {
            return false;
        }
        throw new FileNotFoundException();
    }

    /**
     * get content by static file name
     * @param target target file name
     * @return target html file's text content
     * @throws FileNotFoundException occurs when couldn't find target file
     * @throws IOException occurs when there are invalid bytes in file
     */
    private String readStaticFileByName(final String target) throws FileNotFoundException, IOException {
        if (target.equals("/")) {
            return "Hello world!";
        }
        if (target.startsWith("/")) {
            return readContent("static" + target);
        }
        return readContent("static/" + target);
    }

    /**
     * get '404 not found' html content
     * @return not found html file's text content
     */
    private HttpResponse readNotFoundFile() {
        try {
            final var response = HttpResponse.of(HttpStatus.NOT_FOUND, readStaticFileByName("404.html"));
            response.setContentType(HTML.getType());
            return response;
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     * @param target resource's URL
     * @return resource's text content
     * @throws FileNotFoundException occurs when couldn't find target file
     * @throws IOException occurs when there are invalid bytes in file
     */
    private String readContent(final String target) throws FileNotFoundException, IOException {
        try (final var stream = getClass().getClassLoader()
                .getResourceAsStream(target)) {
            if (stream == null) {
                throw new FileNotFoundException();
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
