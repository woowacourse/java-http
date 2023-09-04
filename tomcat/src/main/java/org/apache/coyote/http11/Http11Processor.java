package org.apache.coyote.http11;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
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
        try (final var inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {
            final HttpRequestParser httpRequestParser = new HttpRequestParser();
            final HttpRequest httpRequest = httpRequestParser.parse(inputStream);

            String requestUri = httpRequest.getRequestUri();
            if (requestUri.equals("/")) {
                requestUri = "/index.html";
            }
            if (requestUri.equals("/login")) {
                requestUri = "/login.html";
                login(httpRequest);
            }

            final FileReader fileReader = new FileReader();
            final String responseBody = fileReader.read(requestUri);

            final ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody).ok();

            final HttpResponseMaker httpResponseMaker = new HttpResponseMaker();
            final HttpResponse httpResponse = httpResponseMaker.make(responseEntity);
            httpResponse.setHeader("Content-Type", httpRequest.getContentType());

            final var responseString = makeResponseString(httpResponse);
            outputStream.write(responseString.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void login(final HttpRequest httpRequest) {
        final String account = httpRequest.getParameter("account");
        final String password = httpRequest.getParameter("password");
        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info(user.toString()));
    }

    private String makeResponseString(final HttpResponse httpResponse) {
        return String.join("\r\n",
                makeResponseCode(httpResponse),
                makeResponseHeaders(httpResponse),
                "",
                httpResponse.getResponseBody());
    }

    private String makeResponseCode(final HttpResponse httpResponse) {
        final int code = httpResponse.getStatusCode().getCode();
        final String message = httpResponse.getStatusCode().getMessage();
        return "HTTP/1.1 " + code + " " + message + " ";
    }

    private String makeResponseHeaders(final HttpResponse httpResponse) {
        final Map<String, String> headers = httpResponse.getHeaders();
        return headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(joining("\r\n"));
    }
}
