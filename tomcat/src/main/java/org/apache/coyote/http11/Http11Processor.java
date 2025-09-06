package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
             final var outputStream = connection.getOutputStream())
        {
            final Http11Request http11Request = extractRequest(inputStream);

            final Http11Response http11Response;
            if (http11Request.isStatic()) {
                String staticPath = http11Request.extractStaticPath();
                http11Response = handleStaticRequest(staticPath);
            } else {
                Map<String, String> params = http11Request.extractRequestBody();
                http11Response = handleDynamicRequest(http11Request.getUri(),params);
            }

            outputStream.write(http11Response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Http11Response handleStaticRequest(final String path) throws IOException, URISyntaxException {
        final String contentType = extractContentType(path);
        if (path.equals("/")) {
            return Http11Response.ok(contentType, "Hello world!");
        }

        final URL url = getURL(path);
        final String responseBody;
        if (url == null) {
            responseBody = readFile("static/404.html");
            return Http11Response.notFound(contentType, responseBody);
        }

        responseBody = readFile(path);
        return Http11Response.ok(contentType, responseBody);
    }

    private Http11Response handleDynamicRequest(final String uri, final Map<String, String> params) {
        switch (uri) {
            case "/login": {
                String account = params.get("account");
                String password = params.get("password");

                boolean loginSuccess = InMemoryUserRepository.findByAccount(account)
                        .filter(user -> user.checkPassword(password))
                        .isPresent();

                if (loginSuccess) {
                    log.info("로그인 성공 - account: {}", account);
                    return Http11Response.redirect("/index.html");
                }
                log.warn("로그인 실패 - account: {}", account);
                return Http11Response.redirect("/401.html");
            }
            case "/register": {
                String account = params.get("account");
                String password = params.get("password");
                String email = params.get("email");
                User newUser = new User(account, password, email);
                InMemoryUserRepository.save(newUser);

                boolean registerSuccess = InMemoryUserRepository.findByAccount(account)
                        .filter(user -> user.getAccount().equals(account) && user.checkPassword(password))
                        .isPresent();

                if (registerSuccess) {
                    log.info("회원가입 성공 - account: {}, email: {}", account, email);
                    return Http11Response.redirect("/index.html");
                }
                log.warn("회원가입 실패 - account: {}, email: {}", account, email);
                return Http11Response.redirect("/401.html");
            }
            default:
                return Http11Response.notFound("text/html;charset=utf-8", "지원하지 않는 URI입니다.");
        }
    }

    private String readFile(final String fileName) throws IOException, URISyntaxException {
        return Files.readString(Paths.get(getClass().getClassLoader()
                .getResource(fileName)
                .toURI())
        );
    }

    private Http11Request extractRequest(final InputStream inputStream) throws IOException{
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> startLineWithHeaders = extractRequestHeadersWithStartLine(bufferedReader);
        final String body = extractRequestBody(bufferedReader, startLineWithHeaders);

        return new Http11Request(startLineWithHeaders, body);
    }

    private List<String> extractRequestHeadersWithStartLine(final BufferedReader bufferedReader) throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        String requestLine;
        while ((requestLine = bufferedReader.readLine()) != null && !requestLine.isEmpty()) {
            requestHeaders.add(requestLine);
        }

        return requestHeaders;
    }

    private String extractRequestBody(final BufferedReader bufferedReader, final List<String> headers) throws IOException {
        //Content-Length 헤더가 있는지 봐야함.
        final int contentLength = headers.stream()
                .filter(header -> header.startsWith("Content-Length:"))
                .map(header -> header.split(":", 2)[1].trim())
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(0);

        if (contentLength == 0) {
            return "";
        }

        char[] bodyChars = new char[contentLength];
        int readCount = bufferedReader.read(bodyChars);

        if (readCount == -1) {
            return "";
        }

        return new String(bodyChars);
    }

    private URL getURL(final String path) {
        return getClass().getClassLoader().getResource(path);
    }

    private String extractContentType(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }
}
