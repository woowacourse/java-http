package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.Request;
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

    private static final String CONTENT_LENGTH_DELIMITER = ": ";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String EMPTY_BODY = "";

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

            Request request = saveRequest(bufferedReader);
            request.temp();

            RequestManager requestManager = RequestMethod.selectManager(request);

            final var response = requestManager.generateResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Request saveRequest(BufferedReader bufferedReader) throws IOException {
        List<String> requestStrings = new ArrayList<>();
        String now;
        int bodyLength = -1;
        while (!(now = bufferedReader.readLine()).isEmpty()) {
            requestStrings.add(now);
            if (now.startsWith(CONTENT_LENGTH)) {
                bodyLength = Integer.parseInt(now.split(CONTENT_LENGTH_DELIMITER)[1]);
            }
        }
        if (bodyLength > 0) {
            char[] buffer = new char[bodyLength];
            bufferedReader.read(buffer, 0, bodyLength);
            return Request.of(requestStrings, new String(buffer));
        }
        return Request.of(requestStrings, EMPTY_BODY);
    }
}
