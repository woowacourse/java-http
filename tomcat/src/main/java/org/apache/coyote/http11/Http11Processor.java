package org.apache.coyote.http11;

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

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.Session;
import nextstep.jwp.model.SessionManager;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

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

            HttpRequest request = createHttpRequestFrom(inputStream);

            HttpResponse httpResponse = createHttpResponseFrom(request);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequestFrom(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String requestLine = bufferedReader.readLine();
        StringTokenizer requestTokenizer = new StringTokenizer(requestLine);
        HttpMethod httpMethod = HttpMethod.of(requestTokenizer.nextToken());
        String uri = requestTokenizer.nextToken();

        Map<String, String> httpRequestHeaders = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            StringTokenizer headerTokenizer =
                new StringTokenizer(line.replaceAll(" ", "").replaceAll(":", " "));
            httpRequestHeaders.put(headerTokenizer.nextToken(), headerTokenizer.nextToken());
            line = bufferedReader.readLine();
        }

        Map<String, String> cookies = new HashMap<>();
        String cookie = httpRequestHeaders.getOrDefault("Cookie", "")
            .replaceAll(" ", "")
            .replaceAll(";", " ");
        StringTokenizer cookieTokenizer = new StringTokenizer(cookie);
        while (cookieTokenizer.hasMoreTokens()) {
            String attribute = cookieTokenizer.nextToken().replaceAll("=", " ");
            StringTokenizer attributeTokenizer = new StringTokenizer(attribute);
            cookies.put(attributeTokenizer.nextToken(), attributeTokenizer.nextToken());
        }

        Map<String, String> bodyParams = new HashMap<>();
        int contentLength = Integer.parseInt(httpRequestHeaders.getOrDefault("Content-Length", "0"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        StringTokenizer bodyTokenizer = new StringTokenizer(new String(buffer).replaceAll("&", " "));
        while (bodyTokenizer.hasMoreTokens()) {
            String attribute = bodyTokenizer.nextToken().replaceAll("=", " ");
            StringTokenizer attributeTokenizer = new StringTokenizer(attribute);
            bodyParams.put(attributeTokenizer.nextToken(), attributeTokenizer.nextToken());
        }

        return HttpRequestBuilder.of(httpMethod, uri, HttpStatusCode.HTTP_STATUS_OK)
            .setRequestHeaders(httpRequestHeaders)
            .setHttpCookie(cookies)
            .setBodyParams(bodyParams)
            .toHttpRequest();
    }

    private HttpResponse createHttpResponseFrom(HttpRequest request) throws IOException {
        if (request.isLoginRequestWithAuthorization()) {
            return loginWithAuthorization(request);
        }

        if (request.isValidRegisterRequest()) {
            return registerUser(request);
        }

        if (request.isValidLoginRequest()) {
            return loginUser(request);
        }

        final URL resource = getClass().getClassLoader().getResource(request.getStaticPath());
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        return HttpResponse.of(request.getHttpStatusCode(), responseBody)
            .setContentType(request.getContentType() + ";charset=utf-8")
            .setLocation(request.getPath())
            .setCookie(request.getCookie().toString());
    }

    private HttpResponse loginWithAuthorization(HttpRequest request) throws IOException {
        Session session = request.getSession();

        if (SessionManager.getSessionManager().containsSession(session)) {
            Map<String, String> cookies = Map.of("JSESSIONID", session.getId());
            HttpCookie httpCookie = HttpCookie.of(cookies);

            return redirectTo("/index", HttpStatusCode.HTTP_STATUS_FOUND)
                .setCookie(httpCookie.toString());
        }

        final URL resource = getClass().getClassLoader().getResource(request.getStaticPath());
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        return HttpResponse.of(request.getHttpStatusCode(), responseBody)
            .setContentType(request.getContentType() + ";charset=utf-8")
            .setLocation(request.getPath());
    }

    private HttpResponse loginUser(HttpRequest request) throws IOException {
        Optional<User> user = InMemoryUserRepository.findByAccount(request.getBodyParam("account"));

        if (user.isPresent() && user.get().checkPassword(request.getBodyParam("password"))) {
            log.info("user : " + user.get());
            Session session = request.getSession();
            session.setAttribute("user", user.get());
            SessionManager.getSessionManager().add(session);
            Map<String, String> cookies = Map.of("JSESSIONID", session.getId());
            HttpCookie httpCookie = HttpCookie.of(cookies);

            return redirectTo("/index", HttpStatusCode.HTTP_STATUS_FOUND)
                .setCookie(httpCookie.toString());
        }

        return redirectTo("/401", HttpStatusCode.HTTP_STATUS_UNAUTHORIZED);
    }

    private HttpResponse registerUser(HttpRequest request) throws IOException {
        InMemoryUserRepository.save(new User(request.getBodyParam("account"),
            request.getBodyParam("password"), request.getBodyParam("email")));

        return redirectTo("/index", HttpStatusCode.HTTP_STATUS_FOUND);
    }

    private HttpResponse redirectTo(String location, HttpStatusCode httpStatusCode) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + location + ".html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        return HttpResponse.of(httpStatusCode, responseBody)
            .setContentType("text/html;charset=utf-8")
            .setLocation(location);
    }
}
