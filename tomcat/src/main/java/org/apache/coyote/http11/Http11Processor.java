package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final String STATIC_PATH = "static";
    private static final String DEFAULT_MESSAGE = "Hello world!";
    private static final String DEFAULT_CONTENT_TYPE = "html";
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
             final var outputStream = connection.getOutputStream()) {

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final String firstLine = bufferedReader.readLine();
            final String requestUrl = Arrays.asList(firstLine.split(" ")).get(1);
            final String extension = getExtension(requestUrl);

            final String responseBody = getResponseBody(requestUrl);
            final String mimeType = getMimeType(bufferedReader, extension);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + mimeType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getExtension(String requestUrl) {
        if (requestUrl.equals("/")) {
            return DEFAULT_CONTENT_TYPE;
        }
        return Arrays.asList(requestUrl.split("\\.")).getLast();
    }

    private String getResponseBody(String requestUrl) throws IOException {
        if (requestUrl.equals("/")) {
            return DEFAULT_MESSAGE;
        }
        final URL resourceUrl = getClass().getClassLoader().getResource(STATIC_PATH + requestUrl);
        return new String(Files.readAllBytes(new File(resourceUrl.getFile()).toPath()));
    }

    private String getMimeType(BufferedReader bufferedReader, String extension) throws IOException {
        String mimeType = null;
        String line;
        while (!"".equals(line = bufferedReader.readLine())) {
            if (line == null) {
                break;
            }
            if (line.startsWith("Accept")) {
                String acceptLine = Arrays.asList(line.split(" ")).get(1);
                mimeType = Arrays.asList(acceptLine.split(",")).getFirst();
                break;
            }
        }

        if (mimeType == null || mimeType.equals("*/*")) {
            mimeType = MimeTypeMaker.getMimeTypeFromExtension(extension);
        }

        return mimeType;
    }
}
