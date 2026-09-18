package org.apache.coyote.http11;

import static org.apache.coyote.http11.config.TomcatServerConfiguration.requestResolvers;

import com.techcourse.exception.UncheckedServletException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;
import org.apache.coyote.http11.resolver.RequestResolver;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    Map<Integer, String> httpStatusMessage = new HashMap<>(){
        {
            put(200, "OK");
            put(400, "Bad Request");
            put(404, "Not Found");
            put(500, "Internal Server Error");
        }
    };
    private String buildResponse(
            final int statusCode,
            final Map<String, String> responseHeaderMap,
            final String responseBody) {
        final String CRLF = " \r\n";

        StringBuilder sb = new StringBuilder()
                .append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(httpStatusMessage.get(statusCode))
                .append(CRLF);

        for (var entry : responseHeaderMap.entrySet()) {
            sb.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(CRLF);
        }

        sb.append("Content-Length: ")
                .append(responseBody.getBytes().length)
                .append(CRLF);

        return sb.append("\r\n")
                .append(responseBody)
                .toString();
    }

    private String buildResponse(Response response) {
        return buildResponse(
                response.statusCode(),
                response.responseHeaderMap(),
                response.responseBody()
        );
    }



    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final Request request = Request.from(inputStream.readAllBytes());
            final Response response = handleRequest(request);

            outputStream.write(buildResponse(response).getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Response handleRequest(Request request) {
        for (RequestResolver resolver : requestResolvers) {
            if (!resolver.canHandle(request)) {
                continue;
            }
            return resolver.handleRequest(request);
        }

        return Response.notFound();
    }


}
