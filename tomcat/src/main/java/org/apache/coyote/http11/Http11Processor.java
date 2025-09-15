package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import com.techcourse.controller.core.RequestController;
import com.techcourse.exception.NotFoundException;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpVersion;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestHeader;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final RequestMapping requestMapping = new RequestMapping(HttpVersion.HTTP_1_1);
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
        try (final InputStream inputStream = connection.getInputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             final OutputStream outputStream = connection.getOutputStream()
        ) {
            try {
                String requestLineString = bufferedReader.readLine();
                RequestHeader requestHeader = RequestHeader.from(parseRequestHeader(bufferedReader));
                RequestBody requestBody = parseRequestBody(bufferedReader, requestHeader);

                RequestLine requestLine = RequestLine.from(requestLineString);
                HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);
                RequestController requestController = requestMapping.getRequestController(httpRequest);

                HttpResponse httpResponse = requestController.service(httpRequest);
                outputStream.write(httpResponse.toBytes());
                outputStream.flush();
            } catch (NotFoundException e) {
                log.error("File not found: {}", e.getMessage());
                redirectToErrorPage(outputStream, new Location("/404.html"));
            } catch (IOException | UncheckedServletException e) {
                log.error(e.getMessage(), e);
                redirectToErrorPage(outputStream, new Location("/500.html"));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> parseRequestHeader(
            final BufferedReader reader
    ) throws IOException {
        List<String> httpRequestHeaders = new ArrayList<>();
        String line = reader.readLine();
        if (line == null) {
            return httpRequestHeaders;
        }
        while (!"".equals(line)) {
            httpRequestHeaders.add(line);
            line = reader.readLine();
        }
        return httpRequestHeaders;
    }

    private RequestBody parseRequestBody(
            final BufferedReader reader, final RequestHeader requestHeader
    ) throws IOException {
        if (requestHeader.hasContentLengthKey()) {
            int contentLength = requestHeader.getContentLength();

            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);

            return RequestBody.from(new String(buffer));
        }
        return RequestBody.empty();
    }

    private void redirectToErrorPage(final OutputStream outputStream, final Location location) throws IOException {
        HttpResponse errorResponse = HttpResponse.found(HttpVersion.HTTP_1_1, location, ContentType.TEXT_HTML,
                HttpCookie.empty());
        outputStream.write(errorResponse.toBytes());
        outputStream.flush();
    }
}
