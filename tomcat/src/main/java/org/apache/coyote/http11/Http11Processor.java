package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.reqeust.HttpRequestLine;
import nextstep.jwp.io.ClassPathResource;
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
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));

            HttpRequest httpRequest = createHttpRequest(bufferReader);
            String httpResponse = createHttpResponse(httpRequest);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(final BufferedReader bufferReader) throws IOException {
        String requestLine = bufferReader.readLine();
        HttpRequestLine httpRequestLine = HttpRequestLine.from(requestLine);
        HttpHeader httpHeader = createHttpHeader(bufferReader);
        return new HttpRequest(httpRequestLine, httpHeader);
    }

    private HttpHeader createHttpHeader(final BufferedReader bufferReader) throws IOException {
        Map<String, String> params = new HashMap<>();
        String line = "";
        while ((line = bufferReader.readLine()).isBlank()) {
            String[] requestHeader = line.split(":", 2);
            params.put(requestHeader[0], requestHeader[1]);
        }
        return new HttpHeader(params);
    }

    private String createHttpResponse(final HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        if (url.equals("/")) {
            return rootResponse();
        }
        return httpResponse(url);
    }

    private String rootResponse() {
        String responseBody = "Hello world!";
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String httpResponse(final String url) {
        ClassPathResource resource = new ClassPathResource(url);
        String responseBody = resource.getFileContents();
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
