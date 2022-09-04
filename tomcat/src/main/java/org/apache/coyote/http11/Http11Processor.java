package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestUri;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.apache.coyote.http11.util.StaticFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String NEW_LINE = "\r\n";

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
             final var bufferReader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest httpRequest = HttpRequest.parse(readHttpRequest(bufferReader));
            RequestUri requestUri = httpRequest.getRequestUri();

            if (requestUri.matches("/")) {
                responseHelloWorld(outputStream);
                return;
            }

            if (requestUri.matches("/login")) {
                responseLogin(outputStream, requestUri);
                return;
            }

            responseStaticFiles(outputStream, requestUri);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void responseHelloWorld(final OutputStream outputStream) throws IOException {
        HttpResponse httpResponse = new HttpResponse.Builder()
                .contentType(ContentType.HTML)
                .body("Hello world!")
                .build();

        writeHttpResponse(outputStream, httpResponse);
    }

    private void responseLogin(final OutputStream outputStream, final RequestUri requestUri) throws IOException {
        if (!requestUri.hasQuery()) {
            HttpResponse httpResponse = new HttpResponse.Builder()
                    .contentType(ContentType.HTML)
                    .body(StaticFileUtil.readFile("/login.html"))
                    .build();

            writeHttpResponse(outputStream, httpResponse);
            return;
        }

        String account = requestUri.getQuery("account").orElse("");
        String password = requestUri.getQuery("password").orElse("");

        boolean loginSuccess = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();

        if (!loginSuccess) {
            HttpResponse httpResponse = new HttpResponse.Builder()
                    .status(HttpStatus.FOUND)
                    .header("Location", "/401.html")
                    .build();

            writeHttpResponse(outputStream, httpResponse);
            return;
        }

        HttpResponse httpResponse = new HttpResponse.Builder()
                .status(HttpStatus.FOUND)
                .header("Location", "/index.html")
                .build();

        writeHttpResponse(outputStream, httpResponse);
    }

    private void responseStaticFiles(final OutputStream outputStream, final RequestUri requestUri) throws IOException {
        HttpResponse httpResponse = new HttpResponse.Builder()
                .contentType(requestUri.getExtension())
                .body(StaticFileUtil.readFile(requestUri.getPath()))
                .build();

        writeHttpResponse(outputStream, httpResponse);
    }

    private String readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        StringBuilder httpRequest = new StringBuilder();
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            httpRequest.append(line).append(NEW_LINE);
        }

        return httpRequest.toString();
    }

    private void writeHttpResponse(final OutputStream outputStream, final HttpResponse httpResponse)
            throws IOException {
        outputStream.write(httpResponse.generateMessage().getBytes());
        outputStream.flush();
    }
}
