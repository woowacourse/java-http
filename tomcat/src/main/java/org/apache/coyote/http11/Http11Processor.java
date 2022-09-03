package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.Controllers;
import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int QUERY_KEY_INDEX = 0;
    private static final int QUERY_VALUE_INDEX = 1;

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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {

            List<String> input = new ArrayList<>();
            String line = bufferedReader.readLine();
            while (!line.isBlank() && !line.isEmpty()) {
                input.add(line);
                line = bufferedReader.readLine();
            }

            HttpRequest request = HttpRequest.from(input);
            Controller controller = Controllers.findController(request.getUri());
            HttpResponse response = controller.doService(request);

            outputStream.write(response.toResponseString().getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
