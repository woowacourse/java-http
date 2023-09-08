package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.ResponseEntity;
import org.apache.front.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final Proxy proxy;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.proxy = new Proxy();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final List<String> lines = readAllLines(bufferedReader);
            final Request request = Request.from(lines, bufferedReader);
            System.out.println("request = " + request);
            final ResponseEntity responseEntity = proxy.process(request);
            System.out.println("responseEntity = " + responseEntity);
            writeResponse(outputStream, responseEntity);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readAllLines(final BufferedReader reader) throws IOException {
        final ArrayList<String> lines = new ArrayList<>();
        while (true) {
            final String line = reader.readLine();
            if ("".equals(line)) {
                break;
            }
            lines.add(line);
        }
        return lines;
    }

    private void writeResponse(final OutputStream outputStream, final ResponseEntity responseEntity) throws IOException {
        outputStream.write(responseEntity.toString().getBytes());
        outputStream.flush();
    }
}
