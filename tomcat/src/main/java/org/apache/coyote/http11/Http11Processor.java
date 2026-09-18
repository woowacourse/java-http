package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import jakarta.servlet.Servlet;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
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

    record Request(
            RequestPoint requestPoint,
            Map<String, String> requestHeaderMap,
            String requestBody) {

    }

     record RequestPoint(
            String method,
            String path,
            String version) {
    }

    private RequestPoint parseRequestLine(final String requestLine) {
        final String[] requestLineParts = requestLine.split(" ");
        return new RequestPoint(requestLineParts[0], requestLineParts[1], requestLineParts[2]);
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
        return buildResponse(response.statusCode, response.responseHeaderMap, response.responseBody);
    }

    private Request parseRequest(final InputStream requestInputStream) throws IOException {
        final byte[] bytes = requestInputStream.readAllBytes();
        final String[] request = new String(bytes, StandardCharsets.UTF_8).split("\r\n\r\n");
        final String requestHeaders = request[0];
        final String requestBody = request.length > 1 ? request[1] : "";

        boolean isFirstLine = true;
        RequestPoint requestEndPoint = null;
        final Map<String, String> requestHeaderMap = new HashMap<>();
        for (String line : requestHeaders.split("\r\n")) {
            if (isFirstLine) {
                requestEndPoint = parseRequestLine(line);
                isFirstLine = false;
                continue;
            }
            final String[] headerField = line.split("\\s?:\\s?");
            requestHeaderMap.put(headerField[0], headerField[1]);
        }

        return new Request(requestEndPoint, requestHeaderMap, requestBody);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final Request request = parseRequest(inputStream);
            final Response response = handleRequest(request);

            outputStream.write(buildResponse(response).getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private record Response(
            int statusCode,
            Map<String, String> responseHeaderMap,
            String responseBody) {

        public static Response ok(
                final Map<String, String> responseHeaderMap,
                final String responseBody) {

            return new Response(200, responseHeaderMap, responseBody);
        }

        public static Response notFound() {
            return new Response(404, Map.of(), "Not Found");
        }
    }

    private interface RequestResolver {
        Response handleRequest(Request request);

        boolean canHandle(Request request);
    }

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String DEFAULT_CHARSET_NAME = DEFAULT_CHARSET.name().toLowerCase();

    private static class ServletResolver implements RequestResolver {
        private final List<RequestHandler> servlets = List.of(
                new StaticResourceRequestHandler(),
                new RootRequestHandler()
        );

        @Override
        public Response handleRequest(Request request) {
            for (RequestHandler servlet : servlets) {
                if (servlet.canHandle(request)) {
                    return servlet.handle(request);
                }
            }

            return Response.notFound();
        }

        @Override
        public boolean canHandle(Request request) {
          return true;
        }
    }

    private interface RequestHandler {
        Response handle(Request request);
        boolean canHandle(Request request);
    }

    private static class StaticResourceRequestHandler implements RequestHandler {

        @Override
        public Response handle(Request request) {
            final String resourcePath = request.requestPoint().path();

            try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("static" + resourcePath)) {
                if (resourceStream == null) {
                    return Response.notFound();
                }

                final byte[] resourceBytes = resourceStream.readAllBytes();
                final String responseBody = new String(resourceBytes, DEFAULT_CHARSET);

                return Response.ok(
                        Map.of("Content-Type", getContentType(resourcePath) + ";charset=" + DEFAULT_CHARSET_NAME),
                        responseBody
                );
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

            throw new RuntimeException("Failed to read resource: " + resourcePath);
        }

        @Override
        public boolean canHandle(Request request) {
            final String endpoint = request.requestPoint.path.toLowerCase();
            return endpoint.endsWith(".html")
                    || endpoint.endsWith(".css")
                    || endpoint.endsWith(".js");
        }

        private String getContentType(String resourcePath) {
            if (resourcePath.endsWith(".html")) {
                return "text/html";
            } else if (resourcePath.endsWith(".css")) {
                return "text/css";
            } else if (resourcePath.endsWith(".js")) {
                return "text/javascript";
            }

            throw new IllegalArgumentException("Unsupported resource type: " + resourcePath);
        }
    }

    private static class RootRequestHandler implements Http11Processor.RequestHandler {

        @Override
        public Http11Processor.Response handle(Http11Processor.Request request) {
            return Response.ok(
                    Map.of(
                            "Content-Type", "text/html;charset=" + DEFAULT_CHARSET_NAME
                    ),
                    "Hello world!"
            );
        }

        @Override
        public boolean canHandle(Request request) {
            return request.requestPoint().path().equals("/");
        }
    }

    private static Http11Processor.RequestResolver[] requestResolvers = new RequestResolver[]{
            new Http11Processor.ServletResolver()
    };

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
