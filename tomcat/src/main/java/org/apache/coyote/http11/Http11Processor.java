package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String OK_RESPONSE_LINE = "HTTP/1.1 200 OK ";
    private static final String CONTENT_TYPE_RESPONSE_HEADER_KEY = "Content-Type: ";
    private static final String CONTENT_LENGTH_RESPONSE_HEADER_KEY = "Content-Length: ";
    private static final String CONTENT_TYPE_RESPONSE_LINE_CHARSET_UTF_8 = ";charset=utf-8 ";
    public static final String DEFAULT_RESPONSE_BODY = "Hello world!";

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

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String url = parseUrl(bufferedReader);
            String responseBody = null;

            if (StaticResourceExtension.anyMatch(url)) {
                responseBody = handleForStaticResource(url);
            }

            if (responseBody == null) {
                responseBody = DEFAULT_RESPONSE_BODY;
            }

            final var response = String.join("\r\n",
                    OK_RESPONSE_LINE,
                    CONTENT_TYPE_RESPONSE_HEADER_KEY + StaticResourceExtension.findMimeTypeByUrl(url)
                            + CONTENT_TYPE_RESPONSE_LINE_CHARSET_UTF_8,
                    CONTENT_LENGTH_RESPONSE_HEADER_KEY + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException |
                 UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseUrl(BufferedReader bufferedReader) throws IOException {
        String firstLine = bufferedReader.readLine();
        if (firstLine.isBlank()) {
            throw new IllegalArgumentException("요청 형식이 잘못되었습니다.");
        }
        return firstLine.split(" ")[1];
    }

    private String handleForStaticResource(String url) throws IOException {
        URL resource = getPathOfResource(url);
        return readFile(resource);
    }

    private URL getPathOfResource(String url) {
        URL resource = getClass().getClassLoader().getResource("static/" + url);
        if (resource != null) {
            return resource;
        }

        throw new IllegalArgumentException("해당 파일이 존재하지 않습니다.");
    }

    private static String readFile(URL resource) throws IOException {
        File file = new File(resource.getFile());
        return Files.readString(file.toPath());
    }
}
