package org.apache.coyote.http11;

import static org.apache.catalina.RequestMapping.getController;
import static org.apache.coyote.response.Status.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.utils.Constant.BASE_PATH;
import static org.apache.coyote.utils.Constant.EMPTY;
import static org.apache.coyote.utils.Constant.LINE_SEPARATOR;
import static org.apache.coyote.utils.Converter.convertPathToUrl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.exception.HttpException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestHeader;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.Status;
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
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest request = readRequest(inputStream);
            final HttpResponse response = handleRequest(request);

            outputStream.write(response.stringify().getBytes());
            outputStream.flush();

        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readRequest(final InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final HttpRequest request = readRequestHeader(bufferedReader);
        final String contentLength = request.getHeader(RequestHeader.CONTENT_LENGTH);
        if (contentLength != null) {
            final String requestBody = readRequestBody(bufferedReader, Integer.parseInt(contentLength));
            request.setBody(requestBody);
        }
        return request;
    }

    private HttpRequest readRequestHeader(final BufferedReader bufferedReader) {
        final StringBuilder input = new StringBuilder();
        try {
            for (String s = bufferedReader.readLine();
                 !EMPTY.equals(s);
                 s = bufferedReader.readLine()) {
                input.append(s)
                        .append(LINE_SEPARATOR);
            }
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }

        return HttpRequest.from(input.toString());
    }

    private String readRequestBody(final BufferedReader bufferedReader, final int contentLength) {
        final char[] buffer = new char[contentLength];
        try {
            bufferedReader.read(buffer, 0, contentLength);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
        return new String(buffer);
    }

    private HttpResponse handleRequest(final HttpRequest request) {
        final Controller controller = getController(request);

        final HttpResponse response = HttpResponse.create(
                request.getProtocol(),
                request.getContentType()
        );
        try {
            controller.service(request, response);
            return response;
        } catch (final HttpException e) {
            log.error(e.getMessage());
            return handleResponseWithException(response, e.getStatus());
        } catch (final Exception e) {
            log.error(e.getMessage());
            return handleResponseWithException(response, INTERNAL_SERVER_ERROR);
        }
    }

    private HttpResponse handleResponseWithException(final HttpResponse response, final Status status) {
        final URL resource = convertPathToUrl(BASE_PATH + status.getCode());
        response.setStatus(status);
        response.setBody(resource);
        return response;
    }
}
