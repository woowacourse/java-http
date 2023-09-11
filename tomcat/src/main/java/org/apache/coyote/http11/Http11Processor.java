package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.apache.catalina.exception.NotSupportedRequestException;
import org.apache.catalina.exception.UncheckedServletException;
import org.apache.catalina.servlet.Controller;
import org.apache.catalina.servlet.ServletMapping;
import org.apache.catalina.util.ResourceFileReader;
import org.apache.coyote.Processor;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.SupportFile;
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
        return getResponseOf(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
    }

    private static HttpResponse initNotSupportedResponse() {
        return getResponseOf(HttpStatus.BAD_REQUEST, "/404.html");
    }

    private static HttpResponse getResponseOf(final HttpStatus status, final String bodyPath) {
        return new HttpResponse()
                .setStatus(status)
                .setHeader(HttpHeader.CONTENT_TYPE, SupportFile.HTML.getContentType())
                .setBody(ResourceFileReader.readFile(bodyPath));
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
            processRequest(bufferedReader, bufferedWriter);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processRequest(final BufferedReader bufferedReader, final BufferedWriter bufferedWriter) {
        try {
            final HttpRequest request = HttpRequestParser.parse(bufferedReader);
            final HttpResponse response = new HttpResponse();
            execute(request, response);
            sendResponse(bufferedWriter, response);
        } catch (NotSupportedRequestException e) {
            sendResponse(bufferedWriter, NOT_SUPPORTED_REQUEST_RESPONSE);
        }
    }

    private static void execute(final HttpRequest request, final HttpResponse response) {
        final Controller controller = ServletMapping.getSupportedServlet(request);
        controller.service(request, response);
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
