package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String headers = readHeaders(bufferedReader);
            int contentLength = getContentLength(headers);
            String payload = readPayload(bufferedReader, contentLength);

            HttpMethod httpMethod = RequestExtractor.extractHttpMethod(headers);
            String response = http11MethodHandlerAdaptor.handle(httpMethod, headers, payload);

            outputStream.write(response.getBytes());
            outputStream.flush();
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
//        return bufferedReader.lines()
//                .collect(Collectors.joining("\r\n", "", "\r\n")); // 이 방식 테스트에서 똑같이 했는데 통과함. 실제 실행하면 안됨
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
