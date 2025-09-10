package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.util.ControllerMapper;
import org.apache.coyote.http11.exception.Http11ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";

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

            final Http11Request request;
            try {
                request = new Http11Request(inputStream);
            } catch (Http11ParseException e) {
                sendErrorResponse(outputStream);
                return;
            }
            final Http11Response response = new Http11Response();

            final String path = request.getPath();
            final Http11Method method = request.getMethod();

            final Controller controller = ControllerMapper.getController(request.getPath());
            controller.service(request, response);

            String statusLine = "HTTP/1.1 200 OK";
            String responseBody = "Hello world!";
            final Map<String, String> responseHeaders = new LinkedHashMap<>();
            responseHeaders.put("Content-Type", MediaType.detectMimeType(path));

            if (Http11Method.GET.equals(method)) {
                Entry<String, String> getResult = handleGetRequest(path, request, responseHeaders);
                statusLine = getResult.getKey();
                responseBody = getResult.getValue();
            } else if (Http11Method.POST.equals(method)) {
                statusLine = handlePostRequest(path, request, responseHeaders);
                responseBody = "";
            }

            responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
            response.putStatusLine(statusLine);
            response.putHeaders(responseHeaders);
            response.putBody(responseBody);

            outputStream.write(response.buildResponse(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (Http11ParseException ex) {
            log.error(ex.getMessage(), ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendErrorResponse(OutputStream outputStream)
            throws IOException {
        String statusLine = "HTTP/1.1 400 Bad Request"; // TODO: 별도의 핸들러로 관리
        String responseBody = readFileFromClasspath("static/400.html");
        final Map<String, String> responseHeaders = new LinkedHashMap<>();
        responseHeaders.put("Content-Type", MediaType.HTML.getMimeType());
        responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        final String response = buildResponse(statusLine, responseHeaders, responseBody);

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private Entry<String, String> handleGetRequest(final String path, final Http11Request request, 
                                                   final Map<String, String> responseHeaders) {
        String statusLine = "HTTP/1.1 200 OK";
        String responseBody;

        if (!"/".equals(path)) {
            responseBody = readFileFromClasspath("static" + path);
            if (responseBody.isEmpty()) {
                statusLine = "HTTP/1.1 404 Not Found";
                responseBody = readFileFromClasspath("static/404.html");
            }
        } else {
            responseBody = "Hello world!";
        }

        return new AbstractMap.SimpleEntry<>(statusLine, responseBody);
    }

    private String handlePostRequest(final String path, final Http11Request request, 
                                     final Map<String, String> responseHeaders) {
        Map<String, String> params = request.getParams();
        return "HTTP/1.1 404 Not Found";
    }

    private String readFileFromClasspath(String resourcePath) {
        final InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (input == null) {
            log.error("resource not found: {}", resourcePath);
            return "";
        }
        final StringBuilder fileContents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line).append(CRLF);
            }
        } catch (IOException e) {
            log.error("Failed to read file: {}", resourcePath, e);
            return "";
        }
        return fileContents.toString();
    }

    private String buildResponse(String statusLine, Map<String, String> responseHeaders, String responseBody) {
        final StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(statusLine).append(CRLF);
        appendResponseHeaders(responseHeaders, responseBuilder);
        responseBuilder.append(CRLF);
        responseBuilder.append(responseBody);
        return responseBuilder.toString();
    }

    private void appendResponseHeaders(Map<String, String> responseHeaders, StringBuilder responseBuilder) {
        for (Entry<String, String> entry : responseHeaders.entrySet()) {
            responseBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(CRLF);
        }
    }
}
