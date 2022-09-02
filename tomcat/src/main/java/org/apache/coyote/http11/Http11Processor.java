package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
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
             final var outputStream = connection.getOutputStream()) {

            String httpRequest = readHttpRequest(inputStream);
            HttpRequest requestMessage = new HttpRequest(httpRequest);

            if (requestMessage.getRequestUri().hasExtension()) { // 정적 파일 서빙
                HttpResponse httpResponse = new HttpResponse.Builder()
                        .contentType(requestMessage.getRequestUri().getExtension())
                        .body(StaticFileUtil.readFile(requestMessage.getRequestUri().getPath()))
                        .build();

                writeHttpResponse(outputStream, httpResponse);
            }

            if (requestMessage.getRequestUri().getPath().equals("/")) {
                HttpResponse httpResponse = new HttpResponse.Builder()
                        .contentType(ContentType.HTML)
                        .body("Hello world!")
                        .build();

                writeHttpResponse(outputStream, httpResponse);
            }

            if (requestMessage.getRequestUri().getPath().equals("/login")) {
                HttpResponse httpResponse = new HttpResponse.Builder()
                        .contentType(ContentType.HTML)
                        .body(StaticFileUtil.readFile("/login.html"))
                        .build();

                writeHttpResponse(outputStream, httpResponse);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readHttpRequest(final InputStream inputStream) throws IOException {
        Reader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);

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
