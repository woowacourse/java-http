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
                responseHome(outputStream);
                return;
            }

            if (httpMethod == HttpMethod.POST && path.contains("/login")) {
                final boolean isValidLogin = isValidLogin(httpRequest);
                if (isValidLogin) {
                    responseRedirectHome(outputStream);
                    return;
                }
                responseErrorPage(outputStream, "/401.html");
                return;
            }

            if (httpMethod == HttpMethod.GET && path.contains("/login")) {
                printMemberLog(httpHeader);
                responseLoginHtml(outputStream);
            }

            if (httpMethod == HttpMethod.GET && path.endsWith(".html")) {
                responseHtml(outputStream, httpHeader);
                return;
            }

            if (httpMethod == HttpMethod.GET && path.endsWith(".css")) {
                responseCss(outputStream, httpHeader);
                return;
            }

            if (httpMethod == HttpMethod.GET && path.endsWith(".js")) {
                responseJs(outputStream, httpHeader);
            }

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
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
            log.info("user : {}", user);
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

    private void responseHtml(
            final OutputStream outputStream,
            final HttpHeader httpHeader
    ) throws URISyntaxException, IOException {
        final String body = getStaticResponseBody("static" + httpHeader.getPurePath());

        final HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1",
                StatusCode.OK,
                body
        );

        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        final String response = httpResponse.getResponse();

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseRedirectHome(final OutputStream outputStream) throws IOException {
        final HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1",
                StatusCode.FOUND,
                null
        );
        httpResponse.addHeader("Content-Length", "0");
        httpResponse.addHeader("Location", "/index.html");

        final String response = httpResponse.getResponse();

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseLoginHtml(final OutputStream outputStream) throws URISyntaxException, IOException {
        final String body = getStaticResponseBody("static/login.html");

        final HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1",
                StatusCode.OK,
                body
        );
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        final String response = httpResponse.getResponse();

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseCss(
            final OutputStream outputStream,
            final HttpHeader httpHeader
    ) throws URISyntaxException, IOException {
        final String body = getStaticResponseBody("static" + httpHeader.getPurePath());
        final HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1",
                StatusCode.OK,
                body
        );

        httpResponse.addHeader("Content-Type", "text/css;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        final String response = httpResponse.getResponse();

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseJs(
            final OutputStream outputStream,
            final HttpHeader httpHeader
    ) throws URISyntaxException, IOException {
        final String body = getStaticResponseBody("static" + httpHeader.getPurePath());

        final HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1",
                StatusCode.OK,
                body
        );

        httpResponse.addHeader("Content-Type", "application/javascript;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        final String response = httpResponse.getResponse();

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseHome(final OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";
        final HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1",
                StatusCode.OK,
                responseBody
        );
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        final String response = httpResponse.getResponse();

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseErrorPage(
            final OutputStream outputStream,
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
        final String response = httpResponse.getResponse();

        outputStream.write(response.getBytes());
        outputStream.flush();
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
