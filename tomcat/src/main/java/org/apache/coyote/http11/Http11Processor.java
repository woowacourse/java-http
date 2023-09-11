package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.handlermapping.HandlerMapper;
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
        log.info(
                "connect host: {}, port: {}",
                connection.getInetAddress(),
                connection.getPort()
        );
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequestHeader httpRequestHeader = getHttpRequestHeader(bufferedReader);
            HttpRequestBody httpRequestBody = getHttpRequestBody(httpRequestHeader, bufferedReader);
            HttpRequest httpRequest = new HttpRequest(httpRequestHeader, httpRequestBody);
            Handler handler = HandlerMapper.getHandle(httpRequest);
            HttpResponse httpResponse = new HttpResponse();
            handler.service(httpRequest, httpResponse);
            outputStream.write(httpResponse.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequestHeader getHttpRequestHeader(BufferedReader bufferedReader) throws IOException {
        StringBuilder header = new StringBuilder();
        String line;

        do {
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            header.append(line).append("\n");
        } while (!"".equals(line));

        return extractHttpRequest(header);
    }

    private HttpRequestHeader extractHttpRequest(StringBuilder content) {
        String[] lines = content.toString()
                .split("\n");
        String[] methodAndRequestUrl = lines[0].split(" ");

        return HttpRequestHeader.of(
                methodAndRequestUrl[0],
                methodAndRequestUrl[1],
                Arrays.copyOfRange(lines, 1, lines.length)
        );
    }

    private HttpRequestBody getHttpRequestBody(HttpRequestHeader httpRequestHeader, BufferedReader bufferedReader) throws IOException {
        String header = httpRequestHeader.get(CONTENT_LENGTH.getValue());
        if (header.isBlank()) {
            return HttpRequestBody.from("");
        }

        int contentLength = Integer.parseInt(header);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        return HttpRequestBody.from(requestBody);
    }

}
