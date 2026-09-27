package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping = new RequestMapping();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String request = reader.readLine();
            if (request == null) {
                return;
            }

            RequestLine requestLine = new RequestLine(request);
            RequestHeaders requestHeaders = new RequestHeaders(readHeaderLines(reader));
            RequestBody requestBody = new RequestBody(readBody(reader, requestHeaders.get("Content-Length")));

            HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
            HttpResponse httpResponse = new HttpResponse();

            Controller controller = requestMapping.getController(httpRequest.getPath());
            controller.service(httpRequest, httpResponse);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readHeaderLines(final BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            headerLines.add(line);
            line = reader.readLine();
        }
        return headerLines;
    }

    private String readBody(final BufferedReader reader, final String contentLengthHeader) throws IOException {
        if (contentLengthHeader == null) {
            return "";
        }

        int contentLength = Integer.parseInt(contentLengthHeader.trim());
        char[] buffer = new char[contentLength];

        int totalRead = 0;
        while (totalRead < contentLength) {
            int read = reader.read(buffer, totalRead, contentLength - totalRead);
            if (read == -1) {
                break;
            }
            totalRead += read;
        }

        return new String(buffer, 0, totalRead);
    }
}
