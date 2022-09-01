package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequestHeader httpRequestHeader = getHttpRequestHeader(inputStream);
            assert httpRequestHeader != null;
            final String response = getResponse(httpRequestHeader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(HttpRequestHeader httpRequestHeader) throws IOException {
        final String requestUri = httpRequestHeader.getRequestUri();
        System.out.println("requestUri = " + requestUri);
        final URL resource = getClass().getClassLoader().getResource("static" + requestUri);
        assert resource != null;
        final Path path = new File(resource.getPath()).toPath();
        byte[] bytes = Files.readAllBytes(path);
        final String responseBody = new String(bytes);

        String responseContentType = "text/html";
        if (httpRequestHeader.isAcceptValueCss()) {
            responseContentType = httpRequestHeader.getAcceptHeaderValue();
        }

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + responseContentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return response;
    }

    private HttpRequestHeader getHttpRequestHeader(java.io.InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Map<String, String> headerFields = new HashMap<>();
        final String HttpMethodInformation = reader.readLine();
        String line = reader.readLine();
        while (!"".equals(line)) {
            String[] parsedHeaderField = line.split(": ");
            if (parsedHeaderField.length != 2) {
                throw new IllegalArgumentException("헤더는 속성과 정보 두 가지로 이루어 집니다.");
            }
            headerFields.put(parsedHeaderField[0], parsedHeaderField[1]);
            line = reader.readLine();
            if (line == null) {
                return null;
            }
        }
        return new HttpRequestHeader(HttpMethodInformation, headerFields);
    }

    private class HttpRequestHeader {
        private static final String ACCEPT = "Accept";

        private final String requestLine;
        private final Map<String, String> requestHeaders;

        public HttpRequestHeader(String requestLine, Map<String, String> headers) {
            this.requestLine = requestLine;
            this.requestHeaders = headers;
        }

        public String getRequestUri() {
            List<String> httpRequestMethodInformation = Arrays.stream(requestLine
                    .split(" "))
                    .collect(Collectors.toList());
            return httpRequestMethodInformation.get(1);
        }

        public String getAcceptHeaderValue() {
            return requestHeaders.get(ACCEPT);
        }

        public boolean isAcceptValueCss() {
            return isExistAccept() && (requestHeaders.get(ACCEPT).contains("text/css"));
        }

        private boolean isExistAccept() {
            return requestHeaders.containsKey(ACCEPT);
        }

        public String getRequestLine() {
            return requestLine;
        }

        public Map<String, String> getRequestHeaders() {
            return requestHeaders;
        }
    }
}
