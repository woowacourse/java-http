package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.request.RequestMapping;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CONTENT_LENGTH = "Content-Length";
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
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }

            HttpRequest request = readHttpRequest(bufferedReader, line);
            HttpResponse response = getHttpResponse(request);

            bufferedWriter.write(response.toResponseFormat());
            bufferedWriter.flush();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getHttpResponse(HttpRequest request) throws Exception {
        HttpResponse response = HttpResponse.create();
        RequestMapping requestMapping = new RequestMapping();
        Controller controller = requestMapping.getController(request);
        controller.service(request, response);
        return response;
    }

    private HttpRequest readHttpRequest(BufferedReader bufferedReader, String line) throws IOException {
        Map<String, String> headers = readHeader(bufferedReader);
        String requestBody = readBody(bufferedReader, headers);
        return HttpRequest.of(RequestLine.from(line), headers, requestBody);
    }

    private Map<String, String> readHeader(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        for (String line = bufferedReader.readLine();
             !"".equals(line); line = bufferedReader.readLine()) {
            List<String> header = Arrays.stream(line.split(": ")).collect(Collectors.toList());
            headers.put(header.get(0), header.get(1));
        }
        return headers;
    }

    private String readBody(BufferedReader bufferedReader, Map<String, String> request) throws IOException {
        final String contentLength = request.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return null;
        }
        final int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return new String(buffer);
    }
}
