package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.header.HttpVersion;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpPath;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HEADER_DELIMITER = ":";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequestLine httpRequestLine = HttpRequestLine.from(reader.readLine());
            final HttpHeaders httpHeaders = new HttpHeaders(parseRequestHeaders(reader));
            final HttpRequestBody httpRequestBody = HttpRequestBody.from(
                    parseRequestBody(httpRequestLine.getMethod(), httpHeaders.getContentLength(), reader));
            final HttpRequest httpRequest = new HttpRequest(httpRequestLine, httpHeaders, httpRequestBody);

            final HttpResponse httpResponse = createHttpResponse(httpRequest);

            outputStream.write(httpResponse.getResponseAsBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseRequestHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            final String[] header = line.split(HEADER_DELIMITER);
            final String name = header[0].trim();
            final String value = header[1].trim();
            headers.put(name, value);
        }
        return headers;
    }

    private String parseRequestBody(final HttpMethod method, final int contentLength, final BufferedReader reader)
            throws IOException {
        if (method.isPost() && contentLength > 0) {
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    private HttpResponse createHttpResponse(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isDefaultRequest()) {
            return new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK, Location.empty(), httpRequest.getContentType(),
                    DEFAULT_RESPONSE_BODY);
        }
        if (httpRequest.isLoginRequest()) {
            if (httpRequest.hasQueryParams()) {
                final User user = InMemoryUserRepository.findByAccount(httpRequest.getParam("account"))
                        .orElseThrow(UserNotFoundException::new);
                if (user.checkPassword(httpRequest.getParam("password"))) {
                    log.info(String.format("user : %s", user));
                    return new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.FOUND, new Location("/index.html"),
                            httpRequest.getContentType(), readFile(httpRequest.getHttpPath()));
                }
                return new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.FOUND, new Location("/401.html"),
                        httpRequest.getContentType(), readFile(httpRequest.getHttpPath()));
            }
        }
        return new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK, Location.empty(), httpRequest.getContentType(),
                readFile(httpRequest.getHttpPath()));
    }

    private String readFile(final HttpPath httpPath) throws IOException {
        final String filePath = String.format("static%s", httpPath.getPath());
        final URL resource = this.getClass().getClassLoader().getResource(filePath);
        return Files.readString(Path.of(Objects.requireNonNull(resource).getPath()));
    }
}
