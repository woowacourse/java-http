package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.http.request.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.RequestToResponse;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            RequestToResponse requestToResponse = new RequestToResponse();

            HttpRequest request = getRequestHeader(bufferedReader);
            if (request.getRequestLine().getMethod().equals(HttpMethod.POST)) {
                getRequestBody(bufferedReader, request);
            }

            final var response = requestToResponse.build(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest getRequestHeader(final BufferedReader bufferedReader) throws IOException {
        List<String> requestHeader = new ArrayList<>();

        while (true) {
            String line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            requestHeader.add(line);
        }

        return HttpRequest.of(requestHeader);
    }

    private void getRequestBody(final BufferedReader bufferedReader, final HttpRequest request) throws IOException {
        int contentLength = Integer.parseInt(request.getHeaders().getValue("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        request.setBody(requestBody);
    }
}
