package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequest httpRequest = readHttpRequest(bufferedReader);

            final String responseBody = makeResponseBody(httpRequest);

            final String response = makeOkResponse(responseBody, httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponseBody(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.getUriPath().equals("/")) {
            return getHomeBody();
        }
        final File file = findStaticFile(httpRequest);
        final FileInputStream fileInputStream = new FileInputStream(file);

        return new String(fileInputStream.readAllBytes());
    }

    private File findStaticFile(final HttpRequest httpRequest) {
        final String path = findStaticFilePath(httpRequest);
        return new File(path);
    }

    private String findStaticFilePath(final HttpRequest httpRequest) {
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static" + httpRequest.getUriPath());
        return Objects.requireNonNull(resource)
                .getPath();
    }

    private HttpRequest readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();

        final List<String> headers = new ArrayList<>();
        for (String header = bufferedReader.readLine();
             header != null && !header.equals("");
             header = bufferedReader.readLine()) {
            headers.add(header);
            System.out.println("header = " + header);
        }

        System.out.println("headers = " + headers);

        final StringBuilder requestBody = new StringBuilder();
        if (headers.contains("Content-Length")) {
            addRequestBodyLine(bufferedReader, requestBody);
        }

        System.out.println("requestBody = " + requestBody);

        return HttpRequest.from(firstLine, headers, requestBody.toString());
    }

    private void addRequestBodyLine(final BufferedReader bufferedReader, final StringBuilder requestBody) throws IOException {
        for (String requestBodyLine = bufferedReader.readLine();
             requestBodyLine != null && !requestBodyLine.equals("");
             requestBodyLine = bufferedReader.readLine()) {
            System.out.println("requestBodyLine = " + requestBodyLine);
            requestBody.append(requestBodyLine);
        }
    }

    private String makeOkResponse(final String responseBody, final HttpRequest httpRequest) {
        final String uriPath = httpRequest.getUriPath();
        final ContentType contentType = findContentType(uriPath);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                String.format("Content-Type: %s;charset=utf-8 ", contentType.getValue()),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getHomeBody() {
        return "Hello world!";
    }

    private ContentType findContentType(final String uriPath) {
        if (uriPath.endsWith(".html")) {
            return ContentType.TEXT_HTML;
        }
        if (uriPath.endsWith(".css")) {
            return ContentType.TEXT_CSS;
        }
        if (uriPath.endsWith(".js")) {
            return ContentType.TEXT_JAVASCRIPT;
        }
        return ContentType.TEXT_PLAIN;
    }
}
