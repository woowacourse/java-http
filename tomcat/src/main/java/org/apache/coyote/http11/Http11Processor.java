package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.request.RequestCreator;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestCreator requestCreator;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestCreator = new RequestCreator();
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
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            List<String> header = readHeader(in);
            String payload = readPayload(in);

            HttpRequest httpRequest = requestCreator.create(header, payload);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readHeader(BufferedReader in) throws IOException {
        String line;
        List<String> header = new ArrayList<>();
        while ((line = in.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            header.add(line);
        }
        return header;
    }

    private String readPayload(BufferedReader in) throws IOException {
        StringBuilder payloadBuilder = new StringBuilder();
        char c;
        while (in.ready()) {
            c = (char) in.read();
            payloadBuilder.append(c);
        }
        return URLDecoder.decode(payloadBuilder.toString(), StandardCharsets.UTF_8);
    }
}
