package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.vo.RequestMethod;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            RequestParser requestParser = new RequestParser(saveRequest(bufferedReader));
            String method = requestParser.generateMethod();

            RequestManager requestManager = RequestMethod.selectManager(method, requestParser);

            final var response = requestManager.generateResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> saveRequest(BufferedReader bufferedReader) throws IOException {
        List<String> requestStrings = new ArrayList<>();
        String now;
        Integer bodyLength = -1;
        while (!(now = bufferedReader.readLine()).isEmpty()) {
            requestStrings.add(now);
            if (now.startsWith("Content-Length")) {
                bodyLength = Integer.parseInt(now.split(": ")[1]);
            }
        }
        if (bodyLength > 0) {
            char[] buffer = new char[bodyLength];
            bufferedReader.read(buffer, 0, bodyLength);
            requestStrings.add(new String(buffer));
        }
        return requestStrings;
    }
}
