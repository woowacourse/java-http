package org.apache.coyote.http11;

import static org.apache.coyote.http11.message.common.ContentType.HTML;
import static org.apache.coyote.http11.message.common.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.common.HttpHeader.COOKIE;
import static org.apache.coyote.http11.message.common.HttpHeader.LOCATION;
import static org.apache.coyote.http11.message.common.HttpMethod.GET;
import static org.apache.coyote.http11.message.common.HttpMethod.POST;
import static org.apache.coyote.http11.message.response.HttpStatus.FOUND;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.InvalidRequestException;
import org.apache.coyote.http11.message.common.HttpCookie;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.QueryString;
import org.apache.coyote.http11.message.request.RequestLine;
import org.apache.coyote.http11.message.request.RequestUri;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.util.StaticFileUtil;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var is = connection.getInputStream();
             final var os = connection.getOutputStream();
             final var bs = new BufferedReader(new InputStreamReader(is))) {

            HttpRequest request = generateHttpRequest(bs);

            HttpResponse response = null;
            if (request.matches(GET, "/")) {
                response = helloWorldResponse();
            }

            if (request.matches(GET, "/login")) {
                response = loginHtmlResponse(request);
            }

            if (request.matches(POST, "/login")) {
                response = loginUserResponse(request);
            }

            if (request.matches(GET, "/register")) {
                response = registerHtmlResponse();
            }

            if (request.matches(POST, "/register")) {
                registerUser(request);
                response = redirectionResponse("/index.html");
            }

            if (Objects.isNull(response)) {
                response = staticFileResponse(request);
            }

            writeHttpResponse(os, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse helloWorldResponse() {
        return new HttpResponse.Builder()
                .contentType(HTML)
                .body("Hello world!")
                .build();
    }

    private HttpResponse loginHtmlResponse(final HttpRequest request) {
        Optional<String> cookie = request.getHttpHeaders().getHeader(COOKIE);

        if (cookie.isPresent()) {
            return new HttpResponse.Builder()
                    .contentType(HTML)
                    .body(StaticFileUtil.readFile("/index.html"))
                    .build();
        }

        return new HttpResponse.Builder()
                .contentType(HTML)
                .body(StaticFileUtil.readFile("/login.html"))
                .build();
    }

    private HttpResponse loginUserResponse(final HttpRequest request) {
        QueryString queryString = QueryString.parse(request.getRequestBody());
        Optional<User> user = getUser(queryString);

        if (user.isEmpty()) {
            return redirectionResponse("/401.html");
        }

        Session session = SessionManager.create();
        HttpResponse.Builder builder = new HttpResponse.Builder()
                .status(FOUND)
                .header(LOCATION, "/index.html");

        if (!request.getHttpHeaders().hasHeader(COOKIE)) {
            builder.setCookie(HttpCookie.sessionId(session.getId()));
        }

        return builder.build();
    }

    private Optional<User> getUser(final QueryString queryString) {
        String account = queryString.getValues("account").orElseThrow(InvalidRequestException::new);
        String password = queryString.getValues("password").orElseThrow(InvalidRequestException::new);

        return InMemoryUserRepository.findByAccount(account)
                .filter(it -> it.checkPassword(password));
    }

    private HttpResponse registerHtmlResponse() {
        return new HttpResponse.Builder()
                .contentType(HTML)
                .body(StaticFileUtil.readFile("/register.html"))
                .build();
    }

    private void registerUser(final HttpRequest request) {
        QueryString queryString = QueryString.parse(request.getRequestBody());
        String account = queryString.getValues("account").orElseThrow(InvalidRequestException::new);
        String password = queryString.getValues("password").orElseThrow(InvalidRequestException::new);
        String email = queryString.getValues("email").orElseThrow(InvalidRequestException::new);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private HttpResponse redirectionResponse(final String path) {
        return new HttpResponse.Builder()
                .status(FOUND)
                .header(LOCATION, path)
                .build();
    }

    private HttpResponse staticFileResponse(final HttpRequest httpRequest) {
        RequestUri requestUri = httpRequest.getRequestUri();
        return new HttpResponse.Builder()
                .contentType(requestUri.getExtension())
                .body(StaticFileUtil.readFile(requestUri.getPath()))
                .build();
    }

    private HttpRequest generateHttpRequest(final BufferedReader bs) throws IOException {
        RequestLine requestLine = RequestLine.parse(bs.readLine());
        HttpHeaders httpHeaders = generateHttpHeaders(bs);
        String body = generateHttpBody(bs, httpHeaders);

        return new HttpRequest(requestLine, httpHeaders, body);
    }

    private HttpHeaders generateHttpHeaders(final BufferedReader bs) throws IOException {
        StringBuilder requestHeaders = new StringBuilder();

        String line;
        while (!(line = bs.readLine()).isEmpty()) {
            requestHeaders.append(line).append("\r\n");
        }

        return HttpHeaders.parse(requestHeaders.toString());
    }

    private String generateHttpBody(final BufferedReader bs, final HttpHeaders httpHeaders) throws IOException {
        String rawContentLength = httpHeaders.getHeader(CONTENT_LENGTH).orElse("0");
        int contentLength = Integer.parseInt(rawContentLength);

        char[] body = new char[contentLength];
        bs.read(body);

        return new String(body);
    }

    private void writeHttpResponse(final OutputStream os, final HttpResponse httpResponse) throws IOException {
        os.write(httpResponse.generateMessage().getBytes());
        os.flush();
    }
}
