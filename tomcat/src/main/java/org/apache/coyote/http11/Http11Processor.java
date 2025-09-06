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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String DEFAULT_RESPONSE_BODY = "Hello world!";

    private final Socket connection;

    public Http11Processor(Socket connection) {
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
             final var outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);) {

            HttpRequest httpRequest = Http11InputBuffer.parseToRequest(bufferedReader);
            String uri = httpRequest.uri();
            String path = uri;

            HttpResponse response = null;
            if (uri.contains("/login") && httpRequest.httpMethod().equals("POST")) {
                response = handleForLogin(httpRequest);
            }

            if (uri.contains("/register") && httpRequest.httpMethod().equals("POST")) {
                response = handleForRegister(httpRequest);
            }

            if (uri.equals("/")) {
                response = HttpResponse.createOKResponse(httpRequest, DEFAULT_RESPONSE_BODY, uri);
            }

            if (response == null) {
                response = handleForStaticResource(httpRequest, path);
            }

            outputStream.write(Http11OutputBuffer.parseToString(response).getBytes());
            outputStream.flush();
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
        return HttpResponse.createOKResponse(httpRequest, responseBody, uri);
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

    private HttpResponse handleForLogin(HttpRequest httpRequest) {
        Map<String, String> parsedRequestBody = parseRequestBody(httpRequest.requestBody());

        Optional<User> foundUser = InMemoryUserRepository.findByAccount(parsedRequestBody.get("account"));

        if (foundUser.isPresent() && foundUser.get().checkPassword(parsedRequestBody.get("password"))) {
            Cookie cookie = getCookie(httpRequest);
            return HttpResponse.createRedirectionResponseWithCookie(httpRequest, "index.html", cookie);
        }

        return HttpResponse.createRedirectionResponse(httpRequest, "401.html");
    }

    private HttpResponse handleForRegister(HttpRequest httpRequest) {
        String requestBody = httpRequest.requestBody();

        Map<String, String> parsedRequestBody = parseRequestBody(requestBody);
        User user = new User(parsedRequestBody.get("account"), parsedRequestBody.get("password"),
                parsedRequestBody.get("email"));
        InMemoryUserRepository.save(user);

        Cookie cookie = getCookie(httpRequest);
        return HttpResponse.createRedirectionResponseWithCookie(httpRequest, "index.html", cookie);
    }

    private Cookie getCookie(HttpRequest httpRequest) {
        if (httpRequest.cookie() != null && httpRequest.cookie().hasSession()) {
            System.out.println("======");
            System.out.println("쿠키 이미 존재!!");
            System.out.println("======");
            return httpRequest.cookie();
        }

        Cookie cookie = new Cookie();
        cookie.add("JSESSIONID", UUID.randomUUID().toString());
        return cookie;
    }
}
