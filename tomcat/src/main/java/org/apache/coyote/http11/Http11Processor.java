package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_DIRECTORY = "static";
    private static final String SPACE = " ";
    private static final String LINE_FEED = "\r\n";
    private static final int PATH_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;
    private static List<String> STATIC_PATH = List.of("/css", "/js", "/assets");

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
             final var outputStream = connection.getOutputStream()) {

            byte[] bytes = new byte[2048];
            inputStream.read(bytes);
            final String request = new String(bytes);
            System.out.println(request);

            final String response = createResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(String request) throws IOException {
        String path = getPath(request);
        String protocol = getRequestElement(request, PROTOCOL_INDEX);
        String status = "200 OK";
        String contentType = getContentType(path);

        String content = getContent(path);
        String contentLength = "Content-Length: " + content.getBytes().length;

        return protocol + SPACE + status + SPACE + LINE_FEED +
                contentType + SPACE + LINE_FEED +
                contentLength + SPACE + LINE_FEED +
                LINE_FEED +
                content;
    }

    private static String getContentType(String path) {
        String contentType = "Content-Type: ";
        for (String staticPath : STATIC_PATH) {
            if (path.startsWith(staticPath)) {
                return contentType + "text/css;charset=utf-8";
            }
        }
        return contentType + "text/html;charset=utf-8";
    }

    private String getContent(String path) throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }
        URL resource = getClass().getClassLoader().getResource(STATIC_DIRECTORY + path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String getPath(String request) {
        return getRequestElement(request, PATH_INDEX);
    }

    private String getRequestElement(String request, int index) {
        return request.split(SPACE + "|" + LINE_FEED)[index];
    }

}
