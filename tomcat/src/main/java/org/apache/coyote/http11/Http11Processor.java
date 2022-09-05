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
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.InvalidRequestException;
import org.apache.coyote.http11.message.common.Cookie;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.QueryString;
import org.apache.coyote.http11.message.request.RequestLine;
import org.apache.coyote.http11.message.request.RequestUri;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.session.SessionStorage;
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
            RequestUri requestUri = request.getRequestUri();

            if (request.matches(GET, "/")) {
                responseHelloWorld(os);
                return;
            }

            if (request.matches(GET, "/login")) {
                responseLoginHtml(os, request);
                return;
            }

            if (request.matches(POST, "/login")) {
                loginUser(os, request);
                return;
            }

            if (request.matches(GET, "/register")) {
                responseRegisterHtml(os);
                return;
            }

            if (request.matches(POST, "/register")) {
                registerUser(os, request);
                return;
            }

            responseStaticFiles(os, requestUri);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void responseHelloWorld(final OutputStream os) throws IOException {
        HttpResponse httpResponse = new HttpResponse.Builder()
                .contentType(HTML)
                .body("Hello world!")
                .build();

        writeHttpResponse(os, httpResponse);
    }

    private void responseLoginHtml(final OutputStream os, final HttpRequest request) throws IOException {
        Optional<String> cookie = request.getHttpHeaders().getHeader(COOKIE);

        if (cookie.isPresent()) {
            HttpResponse httpResponse = new HttpResponse.Builder()
                    .contentType(HTML)
                    .body(StaticFileUtil.readFile("/index.html"))
                    .build();

            writeHttpResponse(os, httpResponse);
            return;
        }

        HttpResponse httpResponse = new HttpResponse.Builder()
                .contentType(HTML)
                .body(StaticFileUtil.readFile("/login.html"))
                .build();

        writeHttpResponse(os, httpResponse);
    }

    private void loginUser(final OutputStream os, final HttpRequest request) throws IOException {
        QueryString queryString = new QueryString(request.getRequestBody());

        String account = queryString.getQuery("account").orElseThrow(InvalidRequestException::new);
        String password = queryString.getQuery("password").orElseThrow(InvalidRequestException::new);

        Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(it -> it.checkPassword(password));

        if (user.isEmpty()) {
            HttpResponse httpResponse = new HttpResponse.Builder()
                    .status(FOUND)
                    .header(LOCATION, "/401.html")
                    .build();

            writeHttpResponse(os, httpResponse);
            return;
        }

        String sessionId = SessionStorage.add(user.get());
        HttpResponse httpResponse = new HttpResponse.Builder()
                .status(FOUND)
                .header(LOCATION, "/index.html")
                .setCookie(Cookie.sessionId(sessionId))
                .build();

        writeHttpResponse(os, httpResponse);
    }

    private void responseRegisterHtml(final OutputStream os) throws IOException {
        HttpResponse response = new HttpResponse.Builder()
                .contentType(HTML)
                .body(StaticFileUtil.readFile("/register.html"))
                .build();

        writeHttpResponse(os, response);
    }

    private void registerUser(final OutputStream os, final HttpRequest request) throws IOException {
        QueryString queryString = new QueryString(request.getRequestBody());
        String account = queryString.getQuery("account").orElseThrow(InvalidRequestException::new);
        String password = queryString.getQuery("password").orElseThrow(InvalidRequestException::new);
        String email = queryString.getQuery("email").orElseThrow(InvalidRequestException::new);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        HttpResponse response = new HttpResponse.Builder()
                .status(FOUND)
                .header(LOCATION, "/index.html")
                .build();

        writeHttpResponse(os, response);
    }

    private void responseStaticFiles(final OutputStream os, final RequestUri requestUri) throws IOException {
        HttpResponse httpResponse = new HttpResponse.Builder()
                .contentType(requestUri.getExtension())
                .body(StaticFileUtil.readFile(requestUri.getPath()))
                .build();

        writeHttpResponse(os, httpResponse);
    }

    private HttpRequest generateHttpRequest(final BufferedReader bs) throws IOException {
        RequestLine requestLine = new RequestLine(bs.readLine());
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

        return new HttpHeaders(requestHeaders.toString());
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
