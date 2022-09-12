package org.apache.coyote.http11;

import static org.apache.coyote.http11.message.common.HttpHeader.CONTENT_LENGTH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.RequestMapping;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestLine;
import org.apache.coyote.http11.message.response.HttpResponse;
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
        try (final var is = connection.getInputStream();
             final var os = connection.getOutputStream();
             final var bs = new BufferedReader(new InputStreamReader(is))) {
            HttpRequest request = generateHttpRequest(bs);

            Controller controller = RequestMapping.getController(request);
            HttpResponse response = controller.service(request);

            writeHttpResponse(os, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest generateHttpRequest(final BufferedReader bs) throws IOException {
        RequestLine requestLine = RequestLine.parse(bs.readLine());
        HttpHeaders httpHeaders = generateHttpHeaders(bs);
        String body = generateHttpBody(bs, httpHeaders);

        return new HttpRequest(requestLine, httpHeaders, body);
    }

    private HttpHeaders generateHttpHeaders(final BufferedReader bs) throws IOException {
        StringBuilder requestHeaders = new StringBuilder();

        String line;
        while (!(line = bs.readLine()).isEmpty()) {
            requestHeaders.append(line).append("\r\n");
        }

        return HttpHeaders.parse(requestHeaders.toString());
    }

    private String generateHttpBody(final BufferedReader bs, final HttpHeaders httpHeaders) throws IOException {
        String rawContentLength = httpHeaders.getHeader(CONTENT_LENGTH).orElse("0");
        int contentLength = Integer.parseInt(rawContentLength);

        char[] body = new char[contentLength];
        bs.read(body);

        return new String(body);
    }

    private void writeHttpResponse(final OutputStream os, final HttpResponse httpResponse) throws IOException {
        os.write(httpResponse.generateMessage().getBytes());
        os.flush();
    }
}
