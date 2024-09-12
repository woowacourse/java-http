package org.apache.coyote.http11;

import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.apache.coyote.http11.request.HttpMethod.POST;
import static org.apache.coyote.http11.response.HttpStatusCode.FOUND;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.domain.Cookie;
import org.apache.coyote.http11.io.HttpResponseWriter;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.ContentTypes;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE = "Cookie";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String CHARSET = ";charset=utf-8";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String ASSIGN_OPERATOR = "=";
    private static final String HEADER_DELIMITER = ": ";

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
        try (final var inputStreamReader = new InputStreamReader(connection.getInputStream());
             final var outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
             final BufferedReader reader = new BufferedReader(inputStreamReader)) {
            HttpRequest httpRequest = readHttpRequest(reader);
            HttpResponse httpResponse = HttpResponse.defaultResponse();
            HttpResponseWriter httpResponseWriter = new HttpResponseWriter(outputStreamWriter);

            if (httpRequest.getMethod().equals(GET)) {
                doGet(httpRequest, httpResponse, httpResponseWriter);
            }
            if (httpRequest.getMethod().equals(POST)) {
                doPost(httpRequest, httpResponse, httpResponseWriter);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(BufferedReader reader) throws IOException {
        HttpRequestLine httpRequestLine = new HttpRequestLine(reader.readLine());
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(readRequestHeaders(reader));
        HttpRequestBody httpRequestBody = new HttpRequestBody(readRequestBody(httpRequestHeader, reader));
        return new HttpRequest(httpRequestLine, httpRequestHeader, httpRequestBody);
    }

    private List<String> readRequestHeaders(BufferedReader reader) throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            requestHeaders.add(line);
        }
        return requestHeaders;
    }

    private String readRequestBody(HttpRequestHeader requestHeader, BufferedReader reader) throws IOException {
        String contentLengthHeader = requestHeader.getContentLength();
        if (contentLengthHeader == null) {
            return "";
        }
        int contentLength = Integer.parseInt(contentLengthHeader);
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private void doGet(
            HttpRequest httpRequest,
            HttpResponse httpResponse,
            HttpResponseWriter responseWriter
    ) throws IOException {
        String path = httpRequest.getPath();
        String version = httpRequest.getVersion();
        if (path.equals("/")) {
            httpResponse.addHeader("Content-Length", Integer.toString("Hello world!".length()));
            httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
            httpResponse.addBody("Hello world!");
            writeHttpResponse(httpResponse, responseWriter);
            return;
        }
        determineGetResponse(httpRequest, httpResponse, httpRequest.getRequestHeader());

        writeHttpResponse(httpResponse, responseWriter);
    }

    private void determineGetResponse(HttpRequest request, HttpResponse response, Map<String, String> requestHeaders)
            throws IOException {
        String path = "static" + request.getPath();

        if (path.equals("/login") && isAlreadyLogin(requestHeaders)) {
            redirect(request, response, "index.html");
            return;
        }
        if (!path.contains(".")) {
            path = path + ".html";
        }
        URL resource = getClass().getClassLoader().getResource(path);
        File file = new File(resource.getPath());
        var responseBody = Files.readString(file.toPath());
        String contentType = Files.probeContentType(file.toPath());
        response.addHeader("Content-Length", Long.toString(file.length()));
        response.addHeader("Content-Type", ContentTypes.getContentType(contentType));
        response.addBody(responseBody);
    }

    private void redirect(HttpRequest request, HttpResponse response, String path) {
        response.setStatusLine(request.getVersion(), FOUND);
        response.addHeader("Location", path);
    }

    private void doPost(
            HttpRequest httpRequest,
            HttpResponse httpResponse,
            HttpResponseWriter responseWriter
    ) throws IOException {
        determinePostResponse(httpRequest, httpResponse);
        writeHttpResponse(httpResponse, responseWriter);
    }

    private void determinePostResponse(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getPath().equals("/login")) {
            doLogin(request, response);
        }
        if (request.getPath().equals("/register")) {
            doRegister(request, response);
        }
    }

    private void doLogin(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> requestBody = request.getRequestBody();

        String account = requestBody.get("account");
        String password = requestBody.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        if (!user.checkPassword(password)) {
            response.setStatusLine(request.getVersion(), FOUND);
            response.addHeader("Location", "/401.html");
            return;
        }
        log.info(user.toString());

        response.setStatusLine(request.getVersion(), FOUND);
        response.addHeader("Location", "/index.html");

        String sessionId = UUID.randomUUID().toString();
        createSession(user, createCookie(JSESSIONID, sessionId));
        response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId);
    }

    private boolean isAlreadyLogin(Map<String, String> requestHeaders) {
        if (requestHeaders.containsKey(COOKIE)) {
            String sessionId = requestHeaders.get(COOKIE).split(ASSIGN_OPERATOR)[1];
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.findSession(sessionId);
            return session != null;
        }
        return false;
    }

    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie();
        cookie.setCookie(name, value);
        return cookie;
    }

    private void createSession(User user, Cookie cookie) {
        Map<String, String> cookies = cookie.getCookies();
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = new Session(cookies.get(JSESSIONID));
        sessionManager.add(session);
        session.setAttribute("user", user);
    }

    private void doRegister(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusLine(request.getVersion(), FOUND);
        response.addHeader("Location", "/index.html");

        User user = createUser(request.getRequestBody());
        InMemoryUserRepository.save(user);
    }

    private User createUser(Map<String, String> requestBody) {
        return new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
    }

    private void writeHttpResponse(HttpResponse httpResponse, HttpResponseWriter responseWriter) throws IOException {
        responseWriter.writeResponse(httpResponse);
    }
}
