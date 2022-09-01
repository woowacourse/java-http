package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String WELCOME_MESSAGE = "Hello world!";
    private static final String HTML_CONTENT_TYPE = "text/html";
    private static final String CSS_CONTENT_TYPE = "text/css";

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final String requestUri = getRequestUri(bufferedReader);
            final Map<String, String> headers = getHeaders(bufferedReader);

            final String responseBody = getResponseBody(requestUri);
            final String contentType = getContentType(requestUri);
            final String response = getOKResponse(responseBody, contentType);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getRequestUri(final BufferedReader bufferedReader) throws IOException {
        final String requestStartLine = bufferedReader.readLine();
        final String[] splitRequestStartLine = requestStartLine.split(" ");
        return splitRequestStartLine[1];
    }


    private Map<String, String> getHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();

        String header;
        while (!"".equals((header = bufferedReader.readLine()))) {
            final String[] splitHeader = header.split(": ");
            headers.put(splitHeader[0], splitHeader[1]);
        }

        return headers;
    }

    private String getContentType(final String requestUri) {
        String contentType = HTML_CONTENT_TYPE;

        final boolean isCSS = requestUri.contains(".css");
        if (isCSS) {
            contentType = CSS_CONTENT_TYPE;
        }

        return contentType;
    }

    private String getResponseBody(final String requestUri) throws IOException {
        String responseBody = WELCOME_MESSAGE;

        if (!requestUri.equals("/")) {
            final String resource = getClass().getClassLoader()
                    .getResource("static" + requestUri)
                    .getPath();
            final File file = new File(resource);
            final BufferedReader fileReader = new BufferedReader(new FileReader(file));
            responseBody = fileReader.lines()
                    .collect(Collectors.joining("\n"));
            responseBody += "\n";

            fileReader.close();
        }

        return responseBody;
    }

    private String getOKResponse(final String responseBody, final String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
