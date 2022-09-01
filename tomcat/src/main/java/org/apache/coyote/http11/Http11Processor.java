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
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.ResourceLocator;
import org.apache.coyote.http11.request.RequestProcessor;
import org.apache.coyote.http11.request.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
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
        try (final InputStream in = connection.getInputStream();
             final OutputStream out = connection.getOutputStream();
             final BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

            StartLine startLine = parseStartLine(br);
            HttpHeaders headers = parseHeaders(br);

            HttpRequest httpRequest = new HttpRequest(startLine.getMethod(), startLine.getUrl().getValue());
            RequestProcessor requestProcessor = new RequestProcessor(new ResourceLocator("/static"));
            HttpResponse httpResponse = requestProcessor.process(httpRequest);
            out.write(httpResponse.getBytes());
            out.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
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
}
