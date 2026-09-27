package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping requestMapping = new RequestMapping();
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_DELIMITER = ":";

    private final Socket connection;
    private final SessionManager sessionManager = SessionManager.getInstance();

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

            List<String> headLines = readHeadLines(reader);
            String body = readBody(reader, contentLengthOf(headLines));

            HttpRequest parsedRequest = HttpRequest.from(headLines, body);
            Session session = sessionManager.findOrCreate(parsedRequest.getSessionId());

            HttpRequest request = parsedRequest.withSessionId(session.getId());
            HttpResponse response = HttpResponse.from(session);

            requestMapping.service(request, response);

            response.writeTo(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readHeadLines(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }

        return lines;
    }

    private int contentLengthOf(List<String> headLines) {
        return headLines.stream()
                .map(line -> line.split(HEADER_DELIMITER, 2))
                .filter(keyValue -> keyValue.length == 2 && keyValue[0].trim().equalsIgnoreCase(CONTENT_LENGTH))
                .map(keyValue -> Integer.parseInt(keyValue[1].trim()))
                .findFirst()
                .orElse(0);
    }

    private String readBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        int read = 0;
        while (read < contentLength) {
            int count = reader.read(buffer, read, contentLength - read);
            if (count == -1) {
                break;
            }
            read += count;
        }
        return new String(buffer, 0, read);
    }
}
