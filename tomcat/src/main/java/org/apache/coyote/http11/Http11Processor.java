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
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.servlets.Controller;
import org.apache.catalina.servlets.RequestMappings;
import org.apache.coyote.Processor;
import org.apache.coyote.WebConfig;
import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.spec.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.spec.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ResourceLocator resourceLocator;
    private final RequestMappings requestMappings;

    public Http11Processor(Socket connection, WebConfig webConfig) {
        this.connection = connection;
        this.resourceLocator = webConfig.getResourceLocator();
        this.requestMappings = webConfig.getControllerMappings();
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

            HttpRequest httpRequest = createHttpRequest(br);
            HttpResponse httpResponse = createHttpResponse();
            service(httpRequest, httpResponse);
            out.write(httpResponse.getBytes());
            out.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createHttpResponse() {
        return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpRequest createHttpRequest(BufferedReader br) throws IOException {
        StartLine startLine = readStartLine(br);
        HttpHeaders headers = readHeaders(br);

        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            String body = readBody(br, contentLength);
            return new HttpRequest(startLine, headers, body);
        }
        return new HttpRequest(startLine, headers, "");
    }

    private StartLine readStartLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        log.debug("start line = {}", line);
        return StartLine.from(line);
    }

    private HttpHeaders readHeaders(BufferedReader br) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line = br.readLine();
        while (!line.equals("")) {
            headerLines.add(line);
            line = br.readLine();
        }
        return HttpHeaders.from(headerLines);
    }

    private String readBody(BufferedReader br, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        br.read(buffer);
        return new String(buffer);
    }

    private void service(HttpRequest request, HttpResponse response) {
        Controller controller = this.requestMappings.getAdaptiveController(request);
        if (controller == null) {
            Resource resource = this.resourceLocator.locate("/404.html");
            response.setStatus(HttpStatus.NOT_FOUND);
            response.addHeader("Content-Type", resource.getContentType().getValue());
            response.setBody(resource.getData());
            return;
        }
        controller.service(request, response);
    }
}
