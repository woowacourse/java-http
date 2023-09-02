package org.apache.coyote.http11;

import static org.apache.coyote.http11.Constants.CRLF;

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
        final String path = requestLine.parseUriWithOutQueryString();
        LOG.info("request uri: {}", requestLine.getUri());
        if (path.equals("/login")) {
            return login(requestLine, requestBody);
        }
        return new ResponseEntity(HttpStatus.OK, path);
    }

    private ResponseEntity login(
            final RequestLine requestLine,
            final RequestBody requestBody
    ) {
        final HttpMethod httpMethod = requestLine.getHttpMethod();
        if (httpMethod == HttpMethod.GET) {
            return new ResponseEntity(HttpStatus.OK, "/login.html");
        }
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> new ResponseEntity(HttpStatus.FOUND, "/index.html"))
                .orElseGet(() -> new ResponseEntity(HttpStatus.UNAUTHORIZED, "/401.html"));
    }
}
