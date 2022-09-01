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
import org.apache.coyote.http11.request.HttpRequestProcessor;
import org.apache.coyote.http11.request.ResourceRequestProcessor;
import org.apache.coyote.http11.request.RootRequestProcessor;
import org.apache.coyote.http11.request.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Map<String, HttpRequestProcessor> requestMappings = new ConcurrentHashMap<>();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        initRequestMappings();
    }

    private void initRequestMappings() {
        requestMappings.put("root", new RootRequestProcessor());
        requestMappings.put("resource", new ResourceRequestProcessor());
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

            String line = br.readLine();
            log.debug("start line = {}", line);
            StartLine startLine = StartLine.from(line);

            List<String> headerLines = new ArrayList<>();
            line = br.readLine();
            while (!line.equals("")) {
                log.debug("header line = {}", line);
                headerLines.add(line);
                line = br.readLine();
            }
            HttpHeaders headers = new HttpHeaders(headerLines);

            HttpRequest httpRequest = new HttpRequest(startLine.getMethod(), startLine.getUrl().getValue());
            HttpRequestProcessor requestProcessor = getMatchedRequestProcessor(httpRequest.getRequestURI());
            HttpResponse httpResponse = requestProcessor.process(httpRequest);
            out.write(httpResponse.getBytes());
            out.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequestProcessor getMatchedRequestProcessor(String requestURI) {
        if ("/".equals(requestURI)) {
            return requestMappings.get("root");
        }
        return requestMappings.get("resource");
    }
}
