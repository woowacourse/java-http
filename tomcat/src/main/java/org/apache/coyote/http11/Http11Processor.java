package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.BasicController;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {

            final HttpRequest httpRequest = readHttpRequest(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse();

            final Controller controller = requestMapping(httpRequest.getUrl());
            controller.process(httpRequest, httpResponse);

            writeHttpResponse(httpResponse, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final String line = bufferedReader.readLine();

        final List<String> headerLines = new ArrayList<>();
        while (bufferedReader.ready()) {
            headerLines.add(bufferedReader.readLine());
        }

        return HttpRequest.from(line, headerLines);
    }

    private Controller requestMapping(final String url) {
        Map<String, Controller> controllers = new HashMap<>();
        controllers.put("/", new BasicController());
        controllers.put("/login", new LoginController());

        if (!controllers.containsKey(url)) {
            return new ResourceController();
        }

        return controllers.get(url);
    }

    private void writeHttpResponse(final HttpResponse httpResponse, final OutputStream outputStream) {
        final String response = httpResponse.makeResponse();
        try {
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
