package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import nextstep.jwp.exception.NotSupportedRequestException;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.catalina.servlet.ServletMapping;
import org.apache.coyote.Processor;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;
import org.apache.coyote.util.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpResponse INTERNAL_SERVER_ERROR_RESPONSE = initInternalServerErrorResponse();
    private static final HttpResponse NOT_SUPPORTED_REQUEST_RESPONSE = initNotSupportedResponse();

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    private static HttpResponse initInternalServerErrorResponse() {
        return getRedirectResponse("/500.html");
    }

    private static HttpResponse initNotSupportedResponse() {
        return getRedirectResponse("/404.html");
    }

    private static HttpResponse getRedirectResponse(final String path) {
        final HttpHeaders headers = HttpHeaders.getEmptyHeaders();
        headers.put(HttpHeader.LOCATION, path);
        return new HttpResponse.Builder()
                .status(HttpStatus.REDIRECT)
                .headers(headers)
                .build();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    public void process(final Socket connection) {
        try (
                var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                var bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))
        ) {
            handleRequest(bufferedReader, bufferedWriter);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequest(BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            final HttpRequest request = HttpRequestParser.parse(bufferedReader);
            final HttpServlet servlet = ServletMapping.getSupportedServlet(request);

            final HttpResponse response = servlet.service(request);
            sendResponse(bufferedWriter, response);

        } catch (NotSupportedRequestException e) {
            sendResponse(bufferedWriter, NOT_SUPPORTED_REQUEST_RESPONSE);
        } catch (UncheckedServletException e) {
            sendResponse(bufferedWriter, INTERNAL_SERVER_ERROR_RESPONSE);
        }
    }

    private void sendResponse(final BufferedWriter bufferedWriter, final HttpResponse response) {
        try {
            bufferedWriter.write(response.getRawResponse());
            bufferedWriter.flush();
        } catch (IOException ioError) {
            log.error(ioError.getMessage(), ioError);
            sendResponse(bufferedWriter, INTERNAL_SERVER_ERROR_RESPONSE);
        }
    }
}
