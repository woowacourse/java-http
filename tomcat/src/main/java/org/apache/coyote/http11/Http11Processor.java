package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.Request;
import org.apache.coyote.common.Response;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var requestReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final var request = parseRequest(requestReader);
            log.info("request: {}", request);
            final var responseBody = getStaticResource(request.getUri());
            final var response = makeResponse(responseBody);
            log.info("response: {}", response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Request parseRequest(BufferedReader reader) throws IOException {
        String startLine = reader.readLine();
        String[] token = startLine.split(" ");
        String[] headers = reader.lines().takeWhile(line -> !line.isEmpty()).toArray(String[]::new);
        return new Request(token[0], token[1], token[2], headers, null);
    }

    private File getStaticResource(String location) throws IOException {
        if (location.equals("/")) {
            location = "/hello.html";
        }
        File file;
        try {
            file = new File(getClass().getClassLoader().getResource("static" + location).getFile());
        } catch (NullPointerException e) {
            file = new File(getClass().getClassLoader().getResource("static/404.html").getFile());
        }
        return file;
    }

    private Response makeResponse(File resource) throws IOException {
        byte[] responseBody = Files.readAllBytes(resource.toPath());
        return new Response("200 OK",
                            Map.of("Content-Type", getContentType(resource),
                                   "Content-Length", getResponseLength(responseBody)),
                            new String(responseBody));
    }

    private String getContentType(File resource) {
        return ContentType.of(resource).getMimeType();
    }

    private String getResponseLength(byte[] responseBody) {
        return "Content-Length: " + responseBody.length + " ";
    }
}
