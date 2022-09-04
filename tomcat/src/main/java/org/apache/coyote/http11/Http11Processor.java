package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.handler.RegisterHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.enums.HttpStatus;
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
        final List<String> messageHeader = extractMessageHeader(bufferedReader);
        final int contentLength = findContentLength(messageHeader);
        final String messageBody = extractMessageBody(bufferedReader, contentLength);

        return new HttpRequest(startLine, messageBody);
    }

    private List<String> extractMessageHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> messageHeader = new LinkedList<>();
        String header = bufferedReader.readLine();
        while (StringUtils.isNotBlank(header)) {
            messageHeader.add(header);
            header = bufferedReader.readLine();
        }
        return messageHeader;
    }

    private int findContentLength(final List<String> messageHeader) {
        for (final String message : messageHeader) {
            if (message.contains("Content-Length")) {
                return Integer.parseInt(message.split(": ")[1]);
            }
        }
        return 0;
    }

    private String extractMessageBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private HttpResponse process(final HttpRequest httpRequest) {
        final String url = httpRequest.getUrl();

        if ("/".equals(url)) {
            return new HttpResponse(HttpStatus.OK, "text/plain", "Hello world!");
        }

        if ("/login".equals(url)) {
            return new LoginHandler().login(httpRequest);
        }

        if ("/register".equals(url)) {
            return new RegisterHandler().register(httpRequest);
        }

        return HttpResponse.of(HttpStatus.OK, url);
    }
}
