package org.apache.coyote.http11;

import static org.apache.coyote.HttpHeaders.CONTENT_LENGTH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.presentation.Controller;
import org.apache.coyote.HttpBody;
import org.apache.coyote.HttpHeaders;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.HttpStartLine;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LINE_BEFORE_READ = " ";

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
        try (final InputStream inputStream = connection.getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = readHttpRequest(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse();

            doService(httpRequest, httpResponse);

            write(outputStream, httpResponse);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final List<String> rawHeaders = readHeaders(bufferedReader);

        final String rawStartLine = rawHeaders.remove(0);
        final HttpStartLine startLine = HttpStartLine.from(rawStartLine);
        final HttpHeaders headers = HttpHeaders.from(rawHeaders);
        final HttpBody body = HttpBody.from(readBody(bufferedReader, headers));

        log.info("============= HTTP REQUEST =============");
        log.info("{}\n{}\n\n{}", rawStartLine, headers, body);

        return new HttpRequest(startLine, headers, body);
    }

    private static List<String> readHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> rawHttpRequest = new ArrayList<>();

        String line = LINE_BEFORE_READ;
        while (!line.isEmpty()) {
            line = bufferedReader.readLine();
            rawHttpRequest.add(line);
        }
        return rawHttpRequest;
    }

    private static String readBody(final BufferedReader bufferedReader, final HttpHeaders headers)
            throws IOException {
        final String header = headers.getHeader(CONTENT_LENGTH);
        if (header == null) {
            return null;
        }

        final int contentLength = Integer.parseInt(header);
        final char[] buffer = new char[contentLength];
        if (bufferedReader.ready()) {
            bufferedReader.read(buffer);

        }
        return new String(buffer);
    }

    private void doService(final HttpRequest request, final HttpResponse response) throws Exception {
        final Controller controller = RequestMapping.findController(request);
        controller.service(request, response);
    }

    private void write(final OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }
}
