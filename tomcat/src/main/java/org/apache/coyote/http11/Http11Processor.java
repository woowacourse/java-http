package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String[] STATIC_FILE_EXTENSIONS = {
            "html", "htm",
            "css",
            "js",
            "png", "jpg", "jpeg", "gif", "webp", "svg",
            "woff", "woff2", "ttf", "otf", "eot",
            "mp4", "mp3", "webm", "ogg", "wav"
    };
    private static final String STATIC_FILE_ROOT_PATH = "static/";
    private static final String OK_RESPONSE_LINE = "HTTP/1.1 200 OK ";

    private static final String CONTENT_TYPE_RESPONSE_LINE_OF_HTML = "Content-Type: text/html;charset=utf-8 ";
    private static final String CONTENT_LENGTH_RESPONSE_HEADER_KEY = "Content-Length: ";
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
            String response = null;

            for (String extension : STATIC_FILE_EXTENSIONS) {
                if (url.contains(extension)) {
                    response = handleForStaticResource(url, outputStream);
                }
            }

            if (response == null) {
                final var responseBody = DEFAULT_RESPONSE_BODY;
                response = String.join("\r\n",
                        OK_RESPONSE_LINE,
                        CONTENT_TYPE_RESPONSE_LINE_OF_HTML,
                        CONTENT_LENGTH_RESPONSE_HEADER_KEY + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }

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

    private String handleForStaticResource(String url, OutputStream outputStream) throws IOException {
        String fileName = STATIC_FILE_ROOT_PATH + url;
        final URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("해당 파일이 존재하지 않습니다.");
        }

        File file = new File(resource.getFile());
        String responseBody = Files.readString(file.toPath());
        return String.join("\r\n",
                OK_RESPONSE_LINE,
                CONTENT_TYPE_RESPONSE_LINE_OF_HTML,
                CONTENT_LENGTH_RESPONSE_HEADER_KEY + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
