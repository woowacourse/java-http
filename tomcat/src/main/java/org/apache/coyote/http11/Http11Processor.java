package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.http.request.HttpRequestParser;
import org.apache.http.request.HttpTomcatRequest;
import org.apache.http.response.HttpResponseParser;
import org.apache.http.response.HttpTomcatResponse;
import org.qupring.mvc.QupringMvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final QupringMvc qupringMvc;

    private final HttpRequestParser httpRequestParser = new HttpRequestParser();
    private final HttpResponseParser httpResponseParser = new HttpResponseParser();

    public Http11Processor(final Socket connection, QupringMvc qupringMvc) {
        this.connection = connection;
        this.qupringMvc = qupringMvc;
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

            final HttpTomcatRequest httpTomcatRequest =
                    httpRequestParser.parse(
                            HttpTomcatRequest.class,
                            inputStreamToString(inputStream));

            final HttpTomcatResponse httpTomcatResponse = HttpTomcatResponse.createDefault();

            qupringMvc.run(httpTomcatRequest, httpTomcatResponse);

            final String response = httpResponseParser.parse(httpTomcatResponse);

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String inputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8
                )
        );

        StringBuilder requestBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            requestBuilder.append(line).append("\r\n");

            if (line.isEmpty()) {
                break;
            }
        }

        return requestBuilder.toString();
    }
}
