package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final List<String> request = getRequest(inputStream);
            final HttpRequest httpRequest = new HttpRequest(request);
            log.debug(httpRequest.toString());

            Controller controller = new Controller();
            HttpResponse httpResponse = controller.run(httpRequest);
            String response = httpResponse.parseToString();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getRequest(final InputStream inputStream) throws IOException {
        final List<String> request = new ArrayList<>();

        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())));
        String line = reader.readLine();

        while (!"".equals(line)) {
            if (Objects.isNull(line)) {
                break;
            }
            request.add(line);
            line = reader.readLine();
        }
        return request;
    }
}
