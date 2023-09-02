package org.apache.coyote.http11;

import static org.apache.coyote.http11.common.Constants.CRLF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponseGenerator;
import org.apache.coyote.http11.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOG = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpResponseGenerator httpResponseGenerator = new HttpResponseGenerator();
    private final SessionManager sessionManager = new SessionManager();

    public Http11Processor(final Socket connection) {
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
            final RequestHeader requestHeader = readHeader(bufferedReader);
            final RequestBody requestBody = readBody(bufferedReader, requestHeader);

            final ResponseEntity responseEntity = handleRequest(requestLine, requestHeader, requestBody);

            final String response = httpResponseGenerator.generate(responseEntity);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private RequestHeader readHeader(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        for (String line = bufferedReader.readLine(); !"".equals(line); line = bufferedReader.readLine()) {
            stringBuilder.append(line).append(CRLF);
        }
        return RequestHeader.from(stringBuilder.toString());
    }

    private RequestBody readBody(final BufferedReader bufferedReader, final RequestHeader requestHeader)
            throws IOException {
        final String contentLength = requestHeader.get("Content-Length");
        if (contentLength == null) {
            return RequestBody.empty();
        }
        final int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return RequestBody.from(new String(buffer));
    }

    private ResponseEntity handleRequest(
            final RequestLine requestLine,
            final RequestHeader requestHeader,
            final RequestBody requestBody
    ) {
        LOG.info("request uri: {}", requestLine.getUri());
        final String path = requestLine.parseUriWithOutQueryString();
        if (path.equals("/login")) {
            return login(requestLine, requestBody);
        }
        if (path.equals("/register")) {
            return register(requestLine, requestBody);
        }
        return new ResponseEntity(HttpStatus.OK, path);
    }

    private ResponseEntity login(final RequestLine requestLine, final RequestBody requestBody) {
        if (requestLine.getHttpMethod() == HttpMethod.GET) {
            return new ResponseEntity(HttpStatus.OK, "/login.html");
        }
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> {
                    final ResponseEntity responseEntity = new ResponseEntity(HttpStatus.FOUND, "/index.html");
                    responseEntity.setCookie("JSESSIONID", UUID.randomUUID().toString());
                    return responseEntity;
                })
                .orElseGet(() -> new ResponseEntity(HttpStatus.UNAUTHORIZED, "/401.html"));
    }

    private ResponseEntity register(final RequestLine requestLine, final RequestBody requestBody) {
        if (requestLine.getHttpMethod() == HttpMethod.GET) {
            return new ResponseEntity(HttpStatus.OK, "/register.html");
        }
        final String account = requestBody.get("account");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return new ResponseEntity(HttpStatus.CONFLICT, "/409.html");
        }

        final String password = requestBody.get("password");
        final String email = requestBody.get("email");
        InMemoryUserRepository.save(new User(account, password, email));
        return new ResponseEntity(HttpStatus.FOUND, "/index.html");
    }
}
