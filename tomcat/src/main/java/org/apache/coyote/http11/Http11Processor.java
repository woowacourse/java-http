package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.handler.RequestHandler;
import org.apache.coyote.http11.request.headers.RequestHeader;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.HttpResponseGenerator;
import org.apache.coyote.http11.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOG = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpResponseGenerator httpResponseGenerator = new HttpResponseGenerator();
    private final RequestHandler requestHandler = new RequestHandler();

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        LOG.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                return;
            }
            final String response = getResponse(bufferedReader, firstLine);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private String getResponse(BufferedReader bufferedReader, String firstLine) throws IOException {
        final RequestLine requestLine = RequestLine.from(firstLine);
        final RequestHeader requestHeader = getHeader(bufferedReader);
        final RequestBody requestBody = getBody(bufferedReader, requestHeader);
        final ResponseEntity responseEntity = requestHandler.getResponse(requestLine, requestHeader, requestBody);
        return httpResponseGenerator.generate(responseEntity);
    }

    private RequestHeader getHeader(final BufferedReader bufferedReader) throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        for (String line = bufferedReader.readLine(); !"".equals(line); line = bufferedReader.readLine()) {
            requestHeaders.add(line);
        }
        return RequestHeader.from(requestHeaders);
    }

    private RequestBody getBody(final BufferedReader bufferedReader, final RequestHeader requestHeader)
            throws IOException {
        List<String> contentLengths = requestHeader.headers().get("Content-Length");
        if (contentLengths == null) {
            return RequestBody.from(null);
        }
        int contentLength = Integer.parseInt(contentLengths.get(0));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return RequestBody.from(new String(buffer));
    }

}
