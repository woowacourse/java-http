package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.FrontController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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
        try (
                InputStream inputStream = connection.getInputStream();
                OutputStream outputStream = connection.getOutputStream()
        ) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            HttpRequest httpRequest = readHttpRequest(bufferedReader);
            if (httpRequest == null) {
                return;
            }
            HttpResponse httpResponse = new HttpResponse(outputStream);

            FrontController frontController = new FrontController();
            frontController.handleRequest(httpRequest, httpResponse);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            return null;
        }
        List<String> headers = reader.lines()
                .takeWhile(s -> !s.isBlank())
                .toList();

        HttpRequest httpRequest = new HttpRequest(requestLine, headers);
        setRequestBodyIfPresent(reader, httpRequest);
        return httpRequest;
    }

    private void setRequestBodyIfPresent(BufferedReader reader, HttpRequest httpRequest) throws IOException {
        int contentLength = httpRequest.getContentLength();
        if (contentLength == 0) {
            return;
        }

        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        httpRequest.setRequestBody(requestBody);
    }
}
