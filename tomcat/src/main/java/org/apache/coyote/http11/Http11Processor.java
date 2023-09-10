package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Set;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.header.HttpMethod;
import org.apache.coyote.http11.adaptor.Http11MethodHandlerAdaptor;
import org.apache.coyote.http11.handler.GetHttp11MethodHandler;
import org.apache.coyote.http11.handler.PostHttp11MethodHandler;
import org.apache.coyote.util.RequestExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Http11MethodHandlerAdaptor http11MethodHandlerAdaptor;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.http11MethodHandlerAdaptor = new Http11MethodHandlerAdaptor(Set.of(
                new GetHttp11MethodHandler(), new PostHttp11MethodHandler())
        );
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            String headers = readHeaders(reader);
            int contentLength = getContentLength(headers);
            String payload = readPayload(reader, contentLength);

            HttpMethod httpMethod = RequestExtractor.extractHttpMethod(headers);
            String response = http11MethodHandlerAdaptor.handle(httpMethod, headers, payload);

            writer.write(response);
            writer.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readHeaders(final BufferedReader bufferedReader) throws IOException {
        StringBuilder result = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            result.append(line).append("\r\n");
        }
        return result.toString();
    }

    private int getContentLength(String headers) {
        String[] lines = headers.split("\r\n");
        for (String line : lines) {
            if (line.startsWith("Content-Length")) {
                String[] split = line.split(": ");
                return Integer.parseInt(split[1]);
            }
        }
        return 0;
    }

    private String readPayload(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        return requestBody;
    }
}
