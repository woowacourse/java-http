package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import nextstep.jwp.handler.Controller;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Manager manager = new SessionManager();
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
        try (final BufferedReader bufferedReader = convert(connection.getInputStream());
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = readRequest(bufferedReader);
            final HttpResponse httpResponse = process(httpRequest);

            outputStream.write(httpResponse.generateResponse()
                    .getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private BufferedReader convert(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private HttpRequest readRequest(BufferedReader bufferedReader) throws IOException {
        final String startLine = bufferedReader.readLine();
        final HttpRequestHeader messageHeader = extractMessageHeader(bufferedReader);
        final int contentLength = messageHeader.findContentLength();
        final HttpRequestBody messageBody = extractMessageBody(bufferedReader, contentLength);

        return new HttpRequest(startLine, messageHeader, messageBody);
    }

    private HttpRequestHeader extractMessageHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> messageHeader = new LinkedList<>();
        String header = bufferedReader.readLine();
        while (StringUtils.isNotBlank(header)) {
            messageHeader.add(header);
            header = bufferedReader.readLine();
        }
        return new HttpRequestHeader(messageHeader);
    }

    private HttpRequestBody extractMessageBody(final BufferedReader bufferedReader, final int contentLength)
            throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new HttpRequestBody(new String(buffer));
    }

    private HttpResponse process(final HttpRequest httpRequest) {
        final String url = httpRequest.getUrl();
        final Controller controller = RequestMapping.of(url);

        return controller.service(httpRequest);
    }
}
