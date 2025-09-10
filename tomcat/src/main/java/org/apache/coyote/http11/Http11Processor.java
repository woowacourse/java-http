package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.ResponseCookie;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.io.Http11InputBuffer;
import org.apache.coyote.http11.io.Http11OutputBuffer;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpRequest;
import org.apache.coyote.http11.message.HttpResponse;
import org.apache.coyote.http11.message.HttpResponseHeader;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.StaticResourceExtension;
import org.apache.coyote.http11.message.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String JAVA_SESSION_ID_KEY = "JSESSIONID";

    private static final Charset DEFAULT_BODY_CHARSET = StandardCharsets.UTF_8;
    private static final Charset DEFAULT_HEADER_CHARSET = StandardCharsets.ISO_8859_1;

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(Socket connection, SessionManager sessionManager) {
        this.connection = connection;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();) {

            Http11InputBuffer http11InputBuffer = new Http11InputBuffer(inputStream, DEFAULT_HEADER_CHARSET,
                    DEFAULT_BODY_CHARSET);
            Http11OutputBuffer http11OutputBuffer = new Http11OutputBuffer(outputStream);

            HttpRequest httpRequest = http11InputBuffer.read();

            String path = httpRequest.getPath();

            HttpResponse response = null;
            if (path.contains("/login") && httpRequest.getHttpMethod().equals(HttpMethod.POST)) {
                response = handleForLogin(httpRequest);
            }

            if (path.contains("/register") && httpRequest.getHttpMethod().equals(HttpMethod.POST)) {
                response = handleForRegister(httpRequest);
            }

            if (path.equals("/")) {
                StatusLine statusLine = new StatusLine(HttpStatus.OK, httpRequest.getPath(),
                        httpRequest.getHttpVersion());

                HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
                httpResponseHeader.add("Content-Type",
                        StaticResourceExtension.findMimeTypeByUrl(httpRequest.getPath()) + ";charset=utf-8");
                httpResponseHeader.add("Content-Length",
                        String.valueOf(DEFAULT_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8).length));

                response = new HttpResponse(statusLine, httpResponseHeader,
                        DEFAULT_RESPONSE_BODY);
            }

            if (response == null) {
                response = handleForStaticResource(httpRequest, path);
            }

            http11OutputBuffer.write(response);
        } catch (IOException |
                 UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleForStaticResource(HttpRequest httpRequest, String uri) throws IOException {
        if (!StaticResourceExtension.anyMatch(uri)) {
            uri = uri + ".html";
        }
        URL resource = getPathOfResource(uri);
        String responseBody = readFile(resource);

        StatusLine statusLine = new StatusLine(HttpStatus.OK, httpRequest.getPath(), httpRequest.getHttpVersion());

        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
        httpResponseHeader.add("Content-Type",
                StaticResourceExtension.findMimeTypeByUrl(httpRequest.getPath()) + ";charset=utf-8");
        httpResponseHeader.add("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
//        httpResponseHeader.add("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        // TODO: charset 관련 처리

        return new HttpResponse(statusLine, httpResponseHeader, responseBody);
    }

    private URL getPathOfResource(String uri) {
        URL resource = getClass().getClassLoader().getResource("static/" + uri);
        if (resource != null) {
            return resource;
        }

        throw new IllegalArgumentException("해당 파일이 존재하지 않습니다.");
    }

    private static String readFile(URL resource) throws IOException {
        File file = new File(resource.getFile());
        return Files.readString(file.toPath());
    }

    private Map<String, String> parseRequestBody(String requestBody) {
        Map<String, String> parsedRequestBody = new HashMap<>();

        String[] pairs = requestBody.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = keyValue[0];
            String value = keyValue[1];
            parsedRequestBody.put(key, value);
        }

        return parsedRequestBody;
    }

    private HttpResponse handleForLogin(HttpRequest httpRequest) throws IOException {
        Map<String, String> parsedRequestBody = parseRequestBody(httpRequest.getRequestBody());

        Optional<User> foundUser = InMemoryUserRepository.findByAccount(parsedRequestBody.get("account"));
        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();

        if (foundUser.isPresent() && foundUser.get().checkPassword(parsedRequestBody.get("password"))) {
            ResponseCookie responseCookie = getCookie(httpRequest, foundUser.get());
            httpResponseHeader.add("Location", "index.html");
            httpResponseHeader.addCookie(responseCookie);

            StatusLine statusLine = new StatusLine(HttpStatus.FOUND, httpRequest.getPath(),
                    httpRequest.getHttpVersion());

            return new HttpResponse(statusLine, httpResponseHeader, null);
        }

        httpResponseHeader.add("Location", "401.html");

        StatusLine statusLine = new StatusLine(HttpStatus.UNAUTHORIZED, httpRequest.getPath(),
                httpRequest.getHttpVersion());
        return new HttpResponse(statusLine, httpResponseHeader, null);
    }

    private HttpResponse handleForRegister(HttpRequest httpRequest) throws IOException {
        String requestBody = httpRequest.getRequestBody();

        Map<String, String> parsedRequestBody = parseRequestBody(requestBody);
        User user = new User(parsedRequestBody.get("account"), parsedRequestBody.get("password"),
                parsedRequestBody.get("email"));
        InMemoryUserRepository.save(user);

        ResponseCookie responseCookie = getCookie(httpRequest, user);
        StatusLine statusLine = new StatusLine(HttpStatus.FOUND, httpRequest.getPath(), httpRequest.getHttpVersion());

        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
        httpResponseHeader.add("Location", "index.html");
        httpResponseHeader.addCookie(responseCookie);

        return new HttpResponse(statusLine, httpResponseHeader, null);
    }

    private ResponseCookie getCookie(HttpRequest httpRequest, User user) {
        HttpSession session = httpRequest.getSession(sessionManager, true);
        session.setAttribute(session.getId(), user);

        ResponseCookie responseCookie = new ResponseCookie();
        responseCookie.add(JAVA_SESSION_ID_KEY, session.getId());
        return responseCookie;
    }
}

