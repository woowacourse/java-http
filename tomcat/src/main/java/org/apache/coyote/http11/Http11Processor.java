package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.HttpStatusException;
import org.apache.coyote.http11.httprequest.HttpMethod;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String RESOURCE_EXTENSION_SEPARATOR = ".";

    private final Socket connection;
    private final UserService userService;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.userService = new UserService();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            handleRequest(reader, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequest(final BufferedReader reader, final OutputStream outputStream) throws IOException {
        try {
            final HttpRequest httpRequest = new HttpRequest(reader);
            httpRequest.parseHttpRequest();
            final String requestPath = httpRequest.getRequestPath();

            // GET /
            if (requestPath.equals("/")) {
                final HttpResponse response = HttpResponse.createWelcomeHttpResponse();
                sendHttpResponse(response, outputStream);
                return;
            }

            // GET /register
            if (requestPath.equals("/register") && httpRequest.getHttpMethod() == HttpMethod.GET) {
                final URL resource = getStaticResource("/register.html");
                final HttpResponse response = getHttpResponse(HttpStatusCode.OK, resource);
                sendHttpResponse(response, outputStream);
                return;
            }

            // POST /register
            if (requestPath.equals("/register") && httpRequest.getHttpMethod() == HttpMethod.POST) {
                Map<String, String> parameters = httpRequest.getRequestBody();
                final String account = parameters.get("account");
                final String password = parameters.get("password");
                final String email = parameters.get("email");

                userService.signup(account, password, email);
                final HttpResponse response = getRedirectHttpResponse("/index.html");
                sendHttpResponse(response, outputStream);
                return;
            }

            // GET /login
            if (requestPath.equals("/login") && httpRequest.getHttpMethod() == HttpMethod.GET) {
                final URL resource = getStaticResource("/login.html");
                final HttpResponse response = getHttpResponse(HttpStatusCode.OK, resource);
                sendHttpResponse(response, outputStream);
                return;
            }

            // POST /login
            if (requestPath.equals("/login") && httpRequest.getHttpMethod() == HttpMethod.POST) {
                final Map<String, String> parameters = httpRequest.getRequestBody();
                final String account = parameters.get("account");
                final String password = parameters.get("password");
                final Optional<User> user = InMemoryUserRepository.findByAccount(account);

                if (user.isEmpty() || !user.get().checkPassword(password)) {
                    final HttpStatusCode statusCode = HttpStatusCode.UNAUTHORIZED;
                    final URL resource = getStaticResource("/" + statusCode.getStatusCode() + ".html");
                    final HttpResponse errorResponse = getHttpResponse(statusCode, resource);
                    sendHttpResponse(errorResponse, outputStream);
                }

                final HttpResponse response = getRedirectHttpResponse("/index.html");
                sendHttpResponse(response, outputStream);
                log.info("user: " + user);
                return;
            }

            // 이 외의 정적 요청
            final URL resource = getStaticResource(httpRequest.getRequestPath());
            final HttpResponse response = getHttpResponse(HttpStatusCode.OK, resource);
            sendHttpResponse(response, outputStream);

        } catch (HttpStatusException e) {
            final HttpStatusCode statusCode = e.getStatusCode();
            final URL resource = getStaticResource("/" + statusCode.getStatusCode() + ".html");
            final HttpResponse errorResponse = getHttpResponse(statusCode, resource);
            sendHttpResponse(errorResponse, outputStream);
        }
    }

    private URL getStaticResource(final String path) {
        final URL resource = getClass().getClassLoader().getResource("static" + path);
        if (resource == null) {
            throw new HttpStatusException(HttpStatusCode.NOT_FOUND);
        }
        return resource;
    }

    private byte[] readFile(final URL resource) throws IOException {
        return Files.readAllBytes(new File(resource.getFile()).toPath());
    }

    private HttpResponse getHttpResponse(final HttpStatusCode statusCode, final URL resource) throws IOException {
        final String responseLine = String.format("HTTP/1.1 %s %s", statusCode.getStatusCode(),
                statusCode.getStatusMessage());
        final byte[] responseBody = readFile(resource);
        final LinkedHashMap<String, String> responseHeaders = new LinkedHashMap<>();
        responseHeaders.put("Content-Type", getContentType(resource));
        responseHeaders.put("Content-Length", String.valueOf(responseBody.length));

        return new HttpResponse(responseLine, responseHeaders, responseBody);
    }

    private HttpResponse getRedirectHttpResponse(final String location) {
        final HttpStatusCode statusCode = HttpStatusCode.FOUND;
        final String responseLine = String.format("HTTP/1.1 %s %s", statusCode.getStatusCode(),
                statusCode.getStatusMessage());
        final LinkedHashMap<String, String> responseHeaders = new LinkedHashMap<>();
        responseHeaders.put("Content-Type", "text/html; charset=UTF-8");
        responseHeaders.put("Content-Length", "0");
        responseHeaders.put("Location", location);

        return new HttpResponse(responseLine, responseHeaders, new byte[0]);
    }

    private void sendHttpResponse(final HttpResponse response, final OutputStream outputStream) throws IOException {
        final String parsedResponse = response.parseHttpResponse();
        outputStream.write(parsedResponse.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String getContentType(final URL resource) {
        final int extensionIndex = resource.getPath().lastIndexOf(RESOURCE_EXTENSION_SEPARATOR);
        final String responseResourceExtension = resource.getPath().substring(extensionIndex + 1);
        return HttpResponse.getContentType(responseResourceExtension);
    }
}
