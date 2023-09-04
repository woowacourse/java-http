package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Set;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.header.HttpMethod;
import org.apache.coyote.http11.adaptor.Http11MethodHandlerAdaptor;
import org.apache.coyote.http11.handler.GetHttp11MethodHandler;
import org.apache.coyote.util.RequestExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Http11MethodHandlerAdaptor http11MethodHandlerAdaptor;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.http11MethodHandlerAdaptor = new Http11MethodHandlerAdaptor(Set.of(new GetHttp11MethodHandler()));
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

            String request = convertToString(inputStream);

            HttpMethod httpMethod = RequestExtractor.extractHttpMethod(request);
            String response = http11MethodHandlerAdaptor.handle(httpMethod, request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String convertToString(final InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder result = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            result.append(line).append("\r\n");
        }
        return result.toString();
//        return bufferedReader.lines()
//                .collect(Collectors.joining("\r\n", "", "\r\n")); // 이 방식 테스트에서 똑같이 했는데 통과함. 실제 실행하면 안됨
    }
}
