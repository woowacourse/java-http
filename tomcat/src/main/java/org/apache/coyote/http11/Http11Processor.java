package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8 ";
    private static final String TEXT_CSS_CHARSET_UTF_8 = "text/css;charset=utf-8 ";

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader br = new BufferedReader(inputStreamReader);

            final String line = br.readLine();
            final String[] requestHeader = line.split(" ");
            final String httpMethod = requestHeader[0];
            final String endPoint = requestHeader[1];

            if (httpMethod.equals("GET") && endPoint.equals("/")) {
                final String response = createResponse("Hello world!", TEXT_HTML_CHARSET_UTF_8);
                writeAndFlush(outputStream, response);
                return;
            }

            if (httpMethod.equals("GET") && endPoint.equals("/css/styles.css")) {
                final URL resource = getClass().getClassLoader().getResource("static" + endPoint);
                validateNullResource(resource);
                final String responseBody = Files.readString(Paths.get(resource.toURI()));
                final String response = createResponse(responseBody, TEXT_CSS_CHARSET_UTF_8);
                writeAndFlush(outputStream, response);
                return;
            }

            if (httpMethod.equals("GET") && endPoint.equals("/login")) {
                final URL resource = getClass().getClassLoader().getResource("static" + endPoint + ".html");
                validateNullResource(resource);
                final String responseBody = Files.readString(Paths.get(resource.toURI()));
                final String response = createResponse(responseBody, TEXT_HTML_CHARSET_UTF_8);
                writeAndFlush(outputStream, response);
                return;
            }

            if (httpMethod.equals("GET") && endPoint.startsWith("/login")) {
                final int index = endPoint.indexOf("?");
                final String path = endPoint.substring(0, index);

                final String queryString = endPoint.substring(index + 1);
                final String[] splitQueryString = queryString.split("&");
                final String account = splitQueryString[0].split("=")[1];
                final String password = splitQueryString[1].split("=")[1];

                final User user = getUserByAccount(account);
                if (isLoginFailed(user, password)) {
                    unAuthenticationResponse(outputStream);
                    return;
                }

                log.info("user: {}", user);
                final URL resource = getClass().getClassLoader().getResource("static" + path + ".html");
                validateNullResource(resource);
                final String responseBody = Files.readString(Paths.get(resource.toURI()));
                final String response = createRedirectionResponse(responseBody, "/index.html");
                writeAndFlush(outputStream, response);
                return;
            }

            final URL resource = getClass().getClassLoader().getResource("static" + endPoint);
            validateNullResource(resource);
            final String responseBody = Files.readString(Paths.get(resource.toURI()));
            final String response = createResponse(responseBody, TEXT_HTML_CHARSET_UTF_8);
            writeAndFlush(outputStream, response);

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private User getUserByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElse(null);
    }

    private boolean isLoginFailed(final User user, final String password) {
        return user == null || isNotMatchPassword(user, password);
    }

    private boolean isNotMatchPassword(final User user, final String password) {
        return !user.checkPassword(password);
    }

    private void unAuthenticationResponse(final OutputStream outputStream) throws IOException, URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource("static" + "/401" + ".html");
        validateNullResource(resource);
        final String responseBody = Files.readString(Paths.get(resource.toURI()));
        final String response = createResponse(responseBody, TEXT_HTML_CHARSET_UTF_8);
        writeAndFlush(outputStream, response);
    }

    private void validateNullResource(final URL resource) {
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 resource 입니다.");
        }
    }

    private String createResponse(final String responseBody, final String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createRedirectionResponse(final String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location,
                "Content-Type: " + TEXT_HTML_CHARSET_UTF_8);
    }

    private void writeAndFlush(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
