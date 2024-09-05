package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final String STATIC_PATH = "static";
    private static final String DEFAULT_MESSAGE = "Hello world!";
    private static final String DEFAULT_CONTENT_TYPE = "html";
    private static final String URI_HEADER_KEY = "Uri";
    private static final String ACCEPT_HEADER_KEY = "Accept";
    private static final String ALL_MIME_TYPE = "*/*";
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

            final Map<String, String> httpRequestHeader = readHttpRequestHeader(bufferedReader);

            final String responseBody = getResponseBody(httpRequestHeader);
            final String mimeType = getMimeType(httpRequestHeader);

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

    private Map<String, String> readHttpRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> httpRequestHeader = new HashMap<>();

        final String requestUri = bufferedReader.readLine();
        httpRequestHeader.put(URI_HEADER_KEY, requestUri);

        String line;
        while (!"".equals(line = bufferedReader.readLine())) {
            if (line == null) {
                break;
            }
            final List<String> splitLine = Arrays.asList(line.split(": "));
            httpRequestHeader.put(splitLine.get(0), splitLine.get(1));
        }

        return httpRequestHeader;
    }

    private String getResponseBody(final Map<String, String> httpRequestHeader) throws IOException {
        final String requestUrl = getRequestUrl(httpRequestHeader);
        if (requestUrl.equals("/")) {
            return DEFAULT_MESSAGE;
        }

        final URL resourceUrl = getClass().getClassLoader().getResource(STATIC_PATH + requestUrl);
        return new String(Files.readAllBytes(new File(resourceUrl.getFile()).toPath()));
    }

    private String getMimeType(final Map<String, String> httpRequestHeader) throws IOException {
        final String requestUrl = getRequestUrl(httpRequestHeader);
        final String acceptLine = httpRequestHeader.getOrDefault(ACCEPT_HEADER_KEY, "");
        final String mimeType = Arrays.asList(acceptLine.split(",")).getFirst();

        if (mimeType.isEmpty() || mimeType.equals(ALL_MIME_TYPE)) {
            final String extension = getExtension(requestUrl);
            return MimeTypeMaker.getMimeTypeFromExtension(extension);
        }

        return mimeType;
    }

    private String getRequestUrl(final Map<String, String> httpRequestHeader) {
        final String uriLine = httpRequestHeader.get(URI_HEADER_KEY);
        return Arrays.asList(uriLine.split(" ")).get(1);
    }

    private String getExtension(final String requestUrl) {
        if (requestUrl.equals("/")) {
            return DEFAULT_CONTENT_TYPE;
        }
        return Arrays.asList(requestUrl.split("\\.")).getLast();
    }
}
