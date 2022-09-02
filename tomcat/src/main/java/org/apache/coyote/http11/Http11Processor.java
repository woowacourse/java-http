package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Controller;
import org.apache.coyote.ControllerMappings;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.spec.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.ResourceLocator;
import org.apache.coyote.http11.request.HttpRequestHandler;
import org.apache.coyote.http11.request.spec.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_RESOURCE_PREFIX = "/static";

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
        try (final InputStream in = connection.getInputStream();
             final OutputStream out = connection.getOutputStream();
             final BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

            HttpRequestHandler httpRequestHandler = new HttpRequestHandler(new ResourceLocator(STATIC_RESOURCE_PREFIX));
            HttpRequest httpRequest = createHttpRequest(br);

            handleRequestIfPossible(httpRequest);
            HttpResponse httpResponse = httpRequestHandler.process(httpRequest);

            out.write(httpResponse.getBytes());
            out.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(BufferedReader br) throws IOException {
        StartLine startLine = parseStartLine(br);
        HttpHeaders headers = parseHeaders(br);
        return new HttpRequest(startLine, headers);
    }

    private StartLine parseStartLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        log.debug("start line = {}", line);
        return StartLine.from(line);
    }

    private HttpHeaders parseHeaders(BufferedReader br) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line = br.readLine();
        while (!line.equals("")) {
            headerLines.add(line);
            line = br.readLine();
        }
        return new HttpHeaders(headerLines);
    }

    private void handleRequestIfPossible(HttpRequest httpRequest) {
        ControllerMappings controllerMappings = new ControllerMappings();
        Controller controller = controllerMappings.getAdaptiveController(httpRequest.getPathString());
        if (Objects.nonNull(controller)) {
            Map<String, String> params = httpRequest.getParams();
            String pathName = controller.process(params);
            httpRequest.setPath(pathName);
        }
    }
}
