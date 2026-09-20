package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.FormParameters;
import org.apache.coyote.http11.model.RequestLine;
import org.apache.coyote.http11.model.UriInfo;
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
        String requestPath = "unknown";
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            RequestLine requestLine = RequestLine.from(reader);
            String line;
            int requestContentLength = 0;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (line.contains("Content-Length")) {
                    String[] contentLengthLine = line.split(":", 2);
                    requestContentLength = Integer.parseInt(contentLengthLine[1].trim());
                }
            }
            int readLength = 0;
            char[] buffer = new char[requestContentLength];
            while (readLength < requestContentLength) {
                int nowReadLength = reader.read(buffer, readLength, requestContentLength - readLength);
                if (nowReadLength == -1) {
                    throw new IOException("잘못된 요청");
                }
                readLength += nowReadLength;
            }

            String requestBodyForm = new String(buffer);

            String url = requestLine.requestUrl();
            String method = requestLine.httpMethod();
            UriInfo uriInfo = UriInfo.makeUriInfo(url);

            if ("GET".equals(method)) {
                getProcess(outputStream, uriInfo);
            } else if ("POST".equals(method)) {
                FormParameters requestBody = FormParameters.from(requestBodyForm);
                postProcess(outputStream, uriInfo, requestBody);
            }

        } catch (IOException | URISyntaxException | RuntimeException e) {
            log.error("HTTP 요청 처리 실패. path={}", requestPath, e);
        }
    }

    private void getProcess(OutputStream outputStream, UriInfo uriInfo) throws IOException, URISyntaxException {
        byte[] responseBody = RequestHandler.get(uriInfo.path());
        String responseHeader = buildResponseHeader(responseBody, findContentType(uriInfo.path()));
        outputStream.write(responseHeader.getBytes());
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private void postProcess(OutputStream outputStream, UriInfo uriInfo, FormParameters formParameters)
            throws IOException, URISyntaxException {
        String redirectPath = RequestHandler.post(uriInfo, formParameters);
        String responseHeader = buildResponseHeader(redirectPath);
        outputStream.write(responseHeader.getBytes());
        outputStream.flush();
    }

    private String buildResponseHeader(String redirectPath) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: " + redirectPath,
                "Content-Length: 0",
                "",
                "");
    }

    private String buildResponseHeader(
            byte[] responseBody,
            String contentType
    ) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.length + " ",
                "",
                "");
    }

    private String findContentType(String url) {
        if (url.endsWith(".html")) {
            return "text/html";
        }
        if (url.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
