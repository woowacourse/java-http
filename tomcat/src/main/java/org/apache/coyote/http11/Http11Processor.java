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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.Cookie;
import org.apache.coyote.Processor;
import org.apache.coyote.httpRequest.HttpRequest;
import org.apache.coyote.httpRequest.httpBody.HttpBody;
import org.apache.coyote.httpRequest.httpHeader.ContentType;
import org.apache.coyote.httpRequest.httpHeader.HttpHeader;
import org.apache.coyote.httpRequest.httpHeader.HttpMethod;
import org.apache.coyote.httpResponse.HttpResponse;
import org.apache.coyote.httpResponse.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            final HttpRequest httpRequest = readHttpRequest(inputStream);
            final HttpHeader httpHeader = httpRequest.getHttpHeader();
            final HttpMethod httpMethod = httpHeader.getHttpMethod();
            final String path = httpHeader.getPurePath();

            if (httpMethod == HttpMethod.GET && path.equals("/")) {
                HttpResponse httpResponse = responseHome();
                writeResponse(outputStream, httpResponse.getResponse());
                return;
            }

            if (httpMethod == HttpMethod.POST && path.contains("/login")) {
                if (isValidLogin(httpRequest)) {
                    HttpResponse httpResponse = responseRedirectPage("/index.html");
                    addJSSESSIONIDIfNotExist(httpHeader,httpResponse);
                    writeResponse(outputStream, httpResponse.getResponse());
                    return;
                }
                HttpResponse httpResponse = responseErrorPage( "/401.html");
                writeResponse(outputStream, httpResponse.getResponse());
                return;
            }

            if (httpMethod == HttpMethod.POST && path.contains("/register")) {
                final boolean isRegistered = registerMember(httpRequest);
                if (isRegistered) {
                    HttpResponse httpResponse = responseRedirectPage("/index.html");

                    writeResponse(outputStream, httpResponse.getResponse());
                    return;
                }
                HttpResponse httpResponse = responseErrorPage("/register.html");
                writeResponse(outputStream, httpResponse.getResponse());
                return;
            }

            if (httpMethod == HttpMethod.GET && path.contains("/register")) {
                HttpResponse httpResponse = responseHtml("register");
                writeResponse(outputStream, httpResponse.getResponse());
                return;
            }

            if (httpMethod == HttpMethod.GET && path.contains("/login")) {
                printMemberLog(httpHeader);
                HttpResponse httpResponse = responseHtml("login");
                writeResponse(outputStream,httpResponse.getResponse());
                return;
            }

            if (httpMethod == HttpMethod.GET && path.endsWith(".html")) {
                HttpResponse httpResponse = responseHtml(httpHeader);
                writeResponse(outputStream, httpResponse.getResponse());
                return;
            }

            if (httpMethod == HttpMethod.GET && path.endsWith(".css")) {
                HttpResponse httpResponse = responseCss(httpHeader);
                writeResponse(outputStream, httpResponse.getResponse());
                return;
            }

            if (httpMethod == HttpMethod.GET && path.endsWith(".js")) {
                HttpResponse httpResponse = responseJs(httpHeader);
                writeResponse(outputStream, httpResponse.getResponse());
                return;
            }

            HttpResponse httpResponse = responseErrorPage("/404.html");
            writeResponse(outputStream, httpResponse.getResponse());
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String response
    ) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private boolean registerMember(final HttpRequest httpRequest) {
        HttpBody httpBody = httpRequest.getHttpBody();
        String account = httpBody.getData("account");
        String email = httpBody.getData("email");
        String password = httpBody.getData("password");
        if (account == null || email == null || password == null) {
            return false;
        }
        boolean isAlreadyRegister = InMemoryUserRepository.findByAccount(account)
                .isPresent();
        if (isAlreadyRegister) {
            return false;
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return true;
    }

    private boolean isValidLogin(final HttpRequest httpRequest) {
        HttpBody httpBody = httpRequest.getHttpBody();
        String account = httpBody.getData("account");
        String password = httpBody.getData("password");
        if (account == null || password == null) {
            return false;
        }
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElse(null);
        if (user != null && user.checkPassword(password)) {
            log.info("로그인 성공 user : {}", user);
            return true;
        }
        return false;
    }

    private void printMemberLog(final HttpHeader httpHeader) {
        final Map<String, String> queries = httpHeader.getQueries();
        final String account = queries.get("account");
        if (account == null) {
            return;
        }
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElse(null);
        if (user != null && user.checkPassword(queries.get("password"))) {
            log.info("user : {}", user);
        }
    }

    private HttpResponse responseHtml(final HttpHeader httpHeader) throws URISyntaxException, IOException {
        final String body = getStaticResponseBody("static" + httpHeader.getPurePath());

        final HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1",
                StatusCode.OK,
                body
        );

        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        return httpResponse;
    }

    private void addJSSESSIONIDIfNotExist(final HttpHeader httpHeader, final HttpResponse httpResponse) {
        if (!httpHeader.hasCookie("JSESSIONID")) {
            httpResponse.addCookie(new Cookie("JSESSIONID", UUID.randomUUID().toString()));
        }
    }

    private HttpResponse responseRedirectPage(
            final String redirectPage
    ) throws IOException {
        final HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1",
                StatusCode.FOUND,
                null
        );
        httpResponse.addHeader("Content-Length", "0");
        httpResponse.addHeader("Location", redirectPage);
        return httpResponse;
    }

    private HttpResponse responseHtml(
            final String path
    ) throws URISyntaxException, IOException {
        final String body = getStaticResponseBody("static/" + path + ".html");

        final HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1",
                StatusCode.OK,
                body
        );
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        return httpResponse;
    }

    private HttpResponse responseCss(final HttpHeader httpHeader) throws URISyntaxException, IOException {
        final String body = getStaticResponseBody("static" + httpHeader.getPurePath());
        final HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1",
                StatusCode.OK,
                body
        );

        httpResponse.addHeader("Content-Type", "text/css;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        return httpResponse;
    }

    private HttpResponse responseJs(final HttpHeader httpHeader) throws URISyntaxException, IOException {
        final String body = getStaticResponseBody("static" + httpHeader.getPurePath());

        final HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1",
                StatusCode.OK,
                body
        );

        httpResponse.addHeader("Content-Type", "application/javascript;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        return httpResponse;
    }

    private HttpResponse responseHome() throws IOException {
        final var responseBody = "Hello world!";
        final HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1",
                StatusCode.OK,
                responseBody
        );
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        return httpResponse;
    }

    private HttpResponse responseErrorPage(
            final String errorPagePath
    ) throws URISyntaxException, IOException {
        final String body = getStaticResponseBody("static" + errorPagePath);

        final HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1",
                StatusCode.UNAUTHORIZED,
                body
        );
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        return httpResponse;
    }

    private HttpRequest readHttpRequest(
            final InputStream inputStream
    ) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final HttpHeader httpHeader = readHttpHeader(bufferedReader);
        final HttpBody httpBody = readHttpBody(bufferedReader, httpHeader);

        return new HttpRequest(httpHeader, httpBody);
    }

    private HttpHeader readHttpHeader(final BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        final List<String> headers = new ArrayList<>();
        String headerLine;
        while ((headerLine = bufferedReader.readLine()) != null && !headerLine.isEmpty()) {
            headers.add(headerLine);
        }

        return new HttpHeader(requestLine, headers);
    }

    private HttpBody readHttpBody(
            final BufferedReader bufferedReader,
            final HttpHeader httpHeader
    ) throws IOException {
        final String contentLengthValue = httpHeader.getHeader("Content-Length");
        if (contentLengthValue == null) {
            return null;
        }
        final String contentType = httpHeader.getHeader("Content-Type");
        final int contentLength = Integer.parseInt(contentLengthValue);
        final char[] chars = new char[contentLength];
        bufferedReader.read(chars, 0, contentLength);

        return new HttpBody(new String(chars), ContentType.findContentType(contentType));
    }

    private String getStaticResponseBody(final String httpHeader) throws URISyntaxException, IOException {
        final URI uri = getClass().getClassLoader()
                .getResource(httpHeader)
                .toURI();
        final Path htmlPath = Path.of(uri);
        final byte[] read = Files.readAllBytes(htmlPath);
        final String body = new String(read, StandardCharsets.UTF_8);
        return body;
    }
}
