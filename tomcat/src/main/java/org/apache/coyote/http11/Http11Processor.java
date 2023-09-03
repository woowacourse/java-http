package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionRepository;
import org.apache.coyote.http11.common.SessionRepositoryImpl;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.headers.RequestHeaders;
import org.apache.coyote.http11.request.line.HttpMethod;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.HttpResponseGenerator;
import org.apache.coyote.http11.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.coyote.http11.common.HttpStatus.CONFLICT;
import static org.apache.coyote.http11.common.HttpStatus.FOUND;
import static org.apache.coyote.http11.common.HttpStatus.OK;
import static org.apache.coyote.http11.common.HttpStatus.UNAUTHORIZED;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOG = LoggerFactory.getLogger(Http11Processor.class);
    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String LOGIN_PAGE = "/login.html";

    private final Socket connection;
    private final SessionRepository sessionRepository = new SessionRepositoryImpl();
    private final HttpResponseGenerator httpResponseGenerator = new HttpResponseGenerator();

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        LOG.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                return;
            }
            final RequestLine requestLine = RequestLine.from(firstLine);
            final RequestHeaders requestHeader = getHeaders(bufferedReader);
            final RequestBody requestBody = getBody(bufferedReader, requestHeader);

            final ResponseEntity responseEntity = handleRequest(requestLine, requestHeader, requestBody);
            final String response = httpResponseGenerator.generate(responseEntity);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private RequestHeaders getHeaders(final BufferedReader bufferedReader) throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        for (String line = bufferedReader.readLine(); !"".equals(line); line = bufferedReader.readLine()) {
            requestHeaders.add(line);
        }
        return RequestHeaders.from(requestHeaders);
    }

    private RequestBody getBody(final BufferedReader bufferedReader, final RequestHeaders requestHeaders)
            throws IOException {
        List<String> contentLengths = requestHeaders.headers().get("Content-Length");
        if (contentLengths == null) {
            return RequestBody.from(null);
        }
        int contentLength = Integer.parseInt(contentLengths.get(0));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return RequestBody.from(new String(buffer));
    }

    private ResponseEntity handleRequest(
            final RequestLine requestLine,
            final RequestHeaders requestHeaders,
            final RequestBody requestBody
    ) {
        final String path = requestLine.path().defaultPath();
        if (path.equals("/login")) {
            return login(requestLine, requestHeaders, requestBody);
        }
        if (path.equals("/register")) {
            return register(requestLine, requestBody);
        }
        return new ResponseEntity(OK, path);
    }

    private ResponseEntity login(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        if (requestLine.method().isGet()) {
            final Cookie cookie = requestHeaders.getCookie();
            final Session session = sessionRepository.getSession(cookie.get("JSESSIONID"));
            if (session != null) {
                return new ResponseEntity(FOUND, INDEX_PAGE);
            }
            return new ResponseEntity(OK, LOGIN_PAGE);
        }
        final String account = requestBody.getBy("account");
        final String password = requestBody.getBy("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(this::loginSuccess)
                .orElseGet(() -> new ResponseEntity(UNAUTHORIZED, "/401.html"));
    }

    private ResponseEntity loginSuccess(final User user) {
        final String uuid = UUID.randomUUID().toString();
        final ResponseEntity responseEntity = new ResponseEntity(FOUND, INDEX_PAGE);
        responseEntity.setCookie("JSESSIONID", uuid);
        final Session session = new Session(uuid);
        session.setAttribute("user", user);
        sessionRepository.create(session);
        return responseEntity;
    }

    private ResponseEntity register(final RequestLine requestLine, final RequestBody requestBody) {
        if (requestLine.method() == HttpMethod.GET) {
            return new ResponseEntity(OK, REGISTER_PAGE);
        }
        final String account = requestBody.getBy("account");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return new ResponseEntity(CONFLICT, "/409.html");
        }
        final String email = requestBody.getBy("email");
        final String password = requestBody.getBy("password");
        InMemoryUserRepository.save(new User(account, password, email));
        return new ResponseEntity(FOUND, INDEX_PAGE);
    }

}
