package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
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
            final Http11Request http11Request = new Http11Request(extractRequestHeaders(inputStream));
            final StartLine startLine = http11Request.getStartLine();

            final Http11Response http11Response;
            if (http11Request.isStatic()) {
                String staticPath = startLine.extractStaticPath();
                http11Response = handleStaticRequest(staticPath);
            } else {
                Map<String, String> params = startLine.extractQuerystring();
                http11Response = handleDynamicRequest(params);
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

    private Http11Response handleDynamicRequest(final Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");

        boolean loginSuccess = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();

        if (loginSuccess) {
            log.info("로그인 성공 - account: {}", account);
        } else {
            log.warn("로그인 실패 - account: {}", account);
        }

        String responseBody = String.format("{\"account\":\"%s\",\"success\":%b}", account, loginSuccess);
        String contentType = "application/json;charset=utf-8";

        return Http11Response.ok(contentType, responseBody);
    }

    private String readFile(final String fileName) throws IOException, URISyntaxException {
        return Files.readString(Paths.get(getClass().getClassLoader()
                .getResource(fileName)
                .toURI())
        );
    }

    private List<String> extractRequestHeaders(final InputStream inputStream) throws IOException{
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        List<String> requestHeaders = new ArrayList<>();
        String requestLine;
        while ((requestLine = bufferedReader.readLine()) != null && !requestLine.isEmpty()) {
            requestHeaders.add(requestLine);
        }

        return requestHeaders;
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
