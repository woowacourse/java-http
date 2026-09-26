package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.HomeController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.exception.HttpException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.requestline.HttpMethod;
import org.apache.coyote.http11.request.requestline.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_DIRECTORY = "static";
    private static final String USER = "user";

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_PATH = "; Path=/";

    private static final int END_OF_STREAM = -1;
    private static final int LINE_FEED = '\n';
    private static final String CARRIAGE_RETURN = "\r";

    private static final RequestMapping REQUEST_MAPPING = new RequestMapping(
            Map.of(
                    "/", new HomeController(),
                    "/login", new LoginController(),
                    "/register", new RegisterController()
            ),
            new StaticResourceController()
    );

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection, final SessionManager sessionManager) {
        this.connection = connection;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = new BufferedInputStream(connection.getInputStream());
             final var outputStream = connection.getOutputStream()) {

            handle(inputStream, outputStream);
        } catch (IOException | UncheckedServletException |URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handle(final InputStream inputStream, final OutputStream outputStream)
            throws IOException, URISyntaxException {
        final HttpResponse response;
        try {
            final Optional<HttpRequest> request = readRequest(inputStream);
            if (request.isEmpty()) {
                return;   // 요청 없이 연결이 닫힘
            }
            response = service(request.get());
        } catch (HttpException e) {
            log.info("invalid request [{}]: {}", e.getStatus().getCode(), e.getMessage());
            HttpResponse.error(e.getStatus()).writeTo(outputStream);
            return;
        } catch (RuntimeException e) {
            log.error("unexpected error while reading request", e);
            HttpResponse.error(HttpStatus.INTERNAL_SERVER_ERROR).writeTo(outputStream);
            return;
        }
        response.writeTo(outputStream);
    }

    private Optional<HttpRequest> readRequest(final InputStream inputStream) throws IOException {
        final String rawRequestLine = readLine(inputStream);
        if (rawRequestLine == null) {
            return Optional.empty();
        }
        final RequestLine requestLine = RequestLine.from(rawRequestLine);
        final RequestHeaders headers = RequestHeaders.from(readHeaders(inputStream));
        final RequestBody body = RequestBody.of(
                readBody(inputStream, headers.getContentLength()),
                headers.get(HttpHeaderName.CONTENT_TYPE)
        );
        log.info("request: {}", requestLine);
        return Optional.of(HttpRequest.of(requestLine, headers, body, sessionManager));
    }

    private HttpResponse service(final HttpRequest request) {
        try {
            final HttpResponse response = new HttpResponse();
            REQUEST_MAPPING.getController(request).service(request, response);
            addSessionCookie(request, response);
            return response;
        } catch (HttpException e) {
            log.info("request rejected [{}]: {}", e.getStatus().getCode(), e.getMessage());
            return HttpResponse.error(e.getStatus());
        } catch (Exception e) {
            log.error("unexpected error in controller", e);
            return HttpResponse.error(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void addSessionCookie(final HttpRequest request, final HttpResponse response) {
        request.getNewSession().ifPresent(session -> {
            log.debug("issue JSESSIONID: {}", session.getId());
            response.addCookie(JSESSIONID + "=" + session.getId() + COOKIE_PATH);
        });
    }

    private boolean isLoggedIn(final HttpRequest request) {
        return request.findSession()
                .map(session -> session.getAttribute(USER))
                .isPresent();
    }

    private List<String> readHeaders(final InputStream inputStream) throws IOException {
        final List<String> headers = new ArrayList<>();
        String line = readLine(inputStream);
        while (line != null && !line.isEmpty()) {
            headers.add(line);
            line = readLine(inputStream);
        }

        return headers;
    }

    private byte[] readBody(
            final InputStream inputStream,
            final int contentLength
            ) throws IOException {
        if (contentLength == 0) {
            return new byte[0];
        }
        final byte[] body = inputStream.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new BadRequestException("요청 본문이 Content-Length보다 짧습니다");
        }
        return body;
    }

    private String readLine(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream line = new ByteArrayOutputStream();
        int read = inputStream.read();
        while (read != END_OF_STREAM && read != LINE_FEED) {
            line.write(read);
            read = inputStream.read();
        }
        if (read == END_OF_STREAM && line.size() == 0) {
            return null;
        }
        final String value = line.toString(UTF_8);
        if (value.endsWith(CARRIAGE_RETURN)) {
            return value.substring(0, value.length() - CARRIAGE_RETURN.length());
        }
        return value;
    }
}
