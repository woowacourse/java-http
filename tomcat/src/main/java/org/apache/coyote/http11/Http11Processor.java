package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOG = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpResponseGenerator httpResponseGenerator = new HttpResponseGenerator();

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
            final ResponseEntity responseEntity = handleRequest(requestLine);
            final String response = httpResponseGenerator.generate(responseEntity);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private ResponseEntity handleRequest(final RequestLine requestLine) {
        final String path = requestLine.parseUriWithOutQueryString();
        final QueryString queryString = requestLine.parseQueryString();
        LOG.info("request uri: {}, queryStrings: {}", requestLine.getUri(), requestLine.parseQueryString().getItems());
        if (path.equals("/login")) {
            return login(requestLine, queryString);
        }
        return new ResponseEntity(HttpStatus.OK, path);
    }

    private static ResponseEntity login(final RequestLine requestLine, final QueryString queryString) {
        final HttpMethod httpMethod = requestLine.getHttpMethod();
        if (httpMethod == HttpMethod.GET) {
            return new ResponseEntity(HttpStatus.OK, "/login.html");
        }
        final String account = queryString.get("account");
        final String password = queryString.get("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> new ResponseEntity(HttpStatus.REDIRECT, "/index.html"))
                .orElseGet(() -> new ResponseEntity(HttpStatus.UNAUTHORIZED, "/401.html"));
    }
}
