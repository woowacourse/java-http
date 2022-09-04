package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.mapping.MappingKey;
import org.apache.coyote.http11.request.mapping.RequestMapper;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            processInAndOut(outputStream, bufferedReader);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processInAndOut(final OutputStream outputStream, final BufferedReader bufferedReader)
            throws IOException {
        final HttpRequest httpRequest = readHttpRequest(bufferedReader);

        final String responseMessage = getResponseMessage(httpRequest);

        outputStream.write(responseMessage.getBytes());
        outputStream.flush();
    }

    private HttpRequest readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();

        final List<String> headers = readHeaders(bufferedReader);

        final String requestBody = readRequestBody(headers, bufferedReader);

        return HttpRequest.from(firstLine, headers, requestBody);
    }

    private List<String> readHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> headers = new ArrayList<>();
        for (String header = bufferedReader.readLine();
             header != null && !header.equals("");
             header = bufferedReader.readLine()) {
            headers.add(header);
        }
        return headers;
    }

    private String readRequestBody(final List<String> headers, final BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        if (headers.contains("Content-Length")) {
            addRequestBodyLine(bufferedReader, stringBuilder);
        }
        return stringBuilder.toString();
    }

    private void addRequestBodyLine(final BufferedReader bufferedReader, final StringBuilder requestBody)
            throws IOException {
        for (String requestBodyLine = bufferedReader.readLine();
             requestBodyLine != null && !requestBodyLine.equals("");
             requestBodyLine = bufferedReader.readLine()) {
            requestBody.append(requestBodyLine);
        }
    }

    private String getResponseMessage(final HttpRequest httpRequest) {
        return RequestMapper.getInstance()
                .getHandler(new MappingKey(httpRequest.getMethod(), httpRequest.getUriPath()))
                .handle(httpRequest)
                .toHttpMessage();
    }
}
