package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.Processor;
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
        try (final var br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            final var outputStream = connection.getOutputStream();
            HttpRequest request = getHttpRequest(br);

            RequestMapping requestMapping = new RequestMapping();
            Controller controller = requestMapping.getController(request);
            HttpResponse response = controller.service(request);
            
            // 디버깅을 위한 로그
            log.info("Request: {} {}", request.getMethod(), request.getRequestUri());
            log.info("Response: {}", response != null ? response.getStatus() : "NULL");
            
            // HttpResponse를 OutputStream에 쓰기
            if (response == null) {
                log.error("Response is null for request: {} {}", request.getMethod(), request.getRequestUri());
                return;
            }
            outputStream.write(response.toHttpString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest getHttpRequest(BufferedReader br) throws IOException {
        String requestLine = br.readLine();
        Map<String, String> headers = parseHttpHeaders(br);
        String body = "POST".equals(requestLine.split("\\s+")[0]) ? getBody(headers, br) : "";
        HttpRequest request = HttpRequest.from(requestLine, headers, body);
        return request;
    }

    private Map<String, String> parseHttpHeaders(BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(":", 2);
            headers.put(headerParts[0].strip(), headerParts[1].strip());
        }
        return headers;
    }

    private String getBody(Map<String, String> headers, BufferedReader br) throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        return URLDecoder.decode(new String(buffer), StandardCharsets.UTF_8);
    }

}
