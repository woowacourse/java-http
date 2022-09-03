package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.apache.servlet.Servlet;
import org.apache.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final ServletContainer SERVLET_CONTAINER = new ServletContainer();

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
        try (InputStream inputStream = connection.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = generateHttpRequest(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();

            if (httpRequest.getPath().contains(".")) {
                httpResponse.addView(httpRequest.getPath());
                write(outputStream, httpResponse);
                return;
            }

            Servlet servlet = SERVLET_CONTAINER.findByPath(httpRequest.getPath());
            servlet.service(httpRequest, httpResponse);

            write(outputStream, httpResponse);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest generateHttpRequest(final BufferedReader bufferedReader) throws IOException {
        HttpRequest request = new HttpRequest(Objects.requireNonNull(bufferedReader.readLine()));
        log.info(request.getRequestLine());

        String line;
        while (!(line = Objects.requireNonNull(bufferedReader.readLine())).isBlank()) {
            String[] splitHeader = line.split(": ");
            request.addHeader(splitHeader[0], splitHeader[1]);
        }

        return request;
    }

    private void write(final OutputStream outputStream, final HttpResponse httpResponse) throws IOException {
        outputStream.write(httpResponse.parseResponse().getBytes());
        outputStream.flush();
    }
}
