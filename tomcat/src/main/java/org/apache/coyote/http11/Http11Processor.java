package org.apache.coyote.http11;

import nextstep.FileResolver;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Controller;
import org.apache.coyote.ControllerMapper;
import org.apache.coyote.Processor;
import org.apache.coyote.domain.HttpRequestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HEADER_DELIMITER = ": ";

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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final List<String> httpRequestHeaders = parseHttpRequestHeaders(bufferedReader);
            final HttpRequestHeader httpRequestHeader = parseHttpRequestHeader(httpRequestHeaders);
            final String contentLength = httpRequestHeader.getHeaders().get("Content-Length");
            if (contentLength != null) {
                final HttpRequestBody httpRequestBody = parseHttpRequestBody(bufferedReader, Integer.parseInt(contentLength));
            }
            final String parsedUri = httpRequestHeader.getUri();
            String response;
            if (!FileResolver.containsExtension(parsedUri)) {
                final Controller controller = ControllerMapper.getController(parsedUri);
                response = controller.run(httpRequestHeader);
            } else {
                final FileResolver fileResolver = FileResolver.getFile(parsedUri);
                final URL url = getClass().getClassLoader().getResource(fileResolver.getFilePath());
                final var responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));
                response = String.join("\r\n",
                        fileResolver.getResponseHeader(),
                        "Content-Type: " + fileResolver.getContentType(),
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequestBody parseHttpRequestBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String body = new String(buffer);
        return HttpRequestBody.from(body);
    }

    private List<String> parseHttpRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!line.isEmpty()) {
            lines.add(line);
            line = bufferedReader.readLine();
        }
        return lines;
    }

    private HttpRequestHeader parseHttpRequestHeader(final List<String> httpRequestHeader) {
        final String[] firstHeader = httpRequestHeader.remove(0).split(" ");
        final String method = firstHeader[0];
        final String uri = firstHeader[1];
        final String version = firstHeader[2];
        final Map<String, String> requestHeaders = new HashMap<>();
        for (final String header : httpRequestHeader) {
            final String[] splitHeader = header.split(HEADER_DELIMITER);
            requestHeaders.put(splitHeader[0], splitHeader[1]);
        }
        return new HttpRequestHeader(method, uri, version, requestHeaders);
    }
}
