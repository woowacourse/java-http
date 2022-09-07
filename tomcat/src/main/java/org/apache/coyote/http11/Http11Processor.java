package org.apache.coyote.http11;

import static nextstep.jwp.http.StatusCode.matchStatusCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.Cookie;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.QueryParams;
import nextstep.jwp.http.StatusCode;
import nextstep.jwp.model.User;
import nextstep.jwp.utils.FileUtils;
import org.apache.coyote.Processor;
import org.apache.session.Session;
import org.apache.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    private final Socket connection;
    private final SessionManager sessionManager = new SessionManager();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
            final var outputStream = connection.getOutputStream()) {
            final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            HttpRequest httpRequest = extractRequest(bufferedReader);
            HttpResponse httpResponse = handle(httpRequest);

            outputStream.write(httpResponse.writeResponse());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest extractRequest(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        HttpHeaders httpHeaders = parseHttpHeaders(bufferedReader);
        String requestBody = parseRequestBody(bufferedReader, httpHeaders);

        return HttpRequest.of(requestLine, httpHeaders, requestBody);
    }

    private HttpHeaders parseHttpHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            lines.add(line);
        }
        return HttpHeaders.parse(lines);
    }

    private String parseRequestBody(BufferedReader bufferedReader, HttpHeaders httpHeaders)
        throws IOException {
        char[] body = new char[httpHeaders.getContentLength()];
        bufferedReader.read(body);
        return new String(body);
    }

    private HttpResponse handle(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        if (httpRequest.matches("/", HttpMethod.GET)) {
            return HttpResponse.of(StatusCode.OK, ContentType.TEXT_PLAIN, "Hello world!");
        }
        if (httpRequest.matches("/login", HttpMethod.GET)) {
            return handleLoginPage(httpRequest);
        }
        if (httpRequest.matches("/login", HttpMethod.POST)) {
            return login(httpRequest);
        }
        if (httpRequest.matches(ContentType.TEXT_PLAIN)) {
            String filePath =
                path + FILE_EXTENSION_SEPARATOR + ContentType.TEXT_HTML.getFileExtension();
            return HttpResponse.of(StatusCode.OK, ContentType.TEXT_HTML,
                FileUtils.readFile(getResource(filePath)));
        }
        return readFile(httpRequest);
    }

    private HttpResponse handleLoginPage(HttpRequest httpRequest) {
        if (httpRequest.isLoggedInUser(sessionManager)) {
            return HttpResponse.of(StatusCode.OK, ContentType.TEXT_HTML,
                FileUtils.readFile(getResource("/index.html")));
        }
        return HttpResponse.of(StatusCode.OK, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/login.html")));
    }

    private HttpResponse login(HttpRequest httpRequest) {
        QueryParams queryParams = httpRequest.getFormData();
        if (LoginHandler.canLogin(queryParams)) {
            User user = InMemoryUserRepository.findByAccount(queryParams.get("account"))
                .orElseThrow(UserNotFoundException::new);
            Session session = sessionManager.generateNewSession();
            session.createAttribute("user", user);
            sessionManager.add(session);
            Cookie cookie = Cookie.fromJSessionId(session.getId());
            return HttpResponse.of(StatusCode.FOUND, ContentType.TEXT_HTML,
                FileUtils.readFile(getResource("/index.html")), cookie);
        }
        return HttpResponse.of(StatusCode.UNAUTHORIZED, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/401.html")));
    }

    private HttpResponse readFile(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        return HttpResponse.of(matchStatusCode(path), httpRequest.getFileExtension(),
            FileUtils.readFile(getResource(path)));
    }

    private URL getResource(String uri) {
        URL resource = FileUtils.getResource(uri);
        if (resource == null) {
            log.error("올바르지 않은 경로: " + uri);
            return getResource("/404.html");
        }
        return resource;
    }
}
