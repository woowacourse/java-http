package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.JSessionIdGenerator;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.Cookie;
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
            String cookieForm = "";
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (line.contains("Content-Length")) {
                    String[] contentLengthLine = line.split(":", 2);
                    requestContentLength = Integer.parseInt(contentLengthLine[1].trim());
                }
                if (line.contains("Cookie")) {
                    String[] cookieLine = line.split(":", 2);
                    cookieForm = cookieLine[1].trim();
                }
            }
            Cookie cookie = Cookie.from(cookieForm);

            String url = requestLine.requestUrl();
            String method = requestLine.httpMethod();
            UriInfo uriInfo = UriInfo.makeUriInfo(url);

            if ("GET".equals(method)) {
                getProcess(outputStream, uriInfo, cookie.hasCookie("JSESSIONID"));
            } else if ("POST".equals(method)) {
                String requestBodyForm = extractRequestBodyForm(requestContentLength, reader);
                FormParameters requestBody = FormParameters.from(requestBodyForm);
                postProcess(outputStream, uriInfo, requestBody, cookie.hasCookie("JSESSIONID"));
            }

        } catch (IOException | URISyntaxException | RuntimeException e) {
            log.error("HTTP 요청 처리 실패. path={}", requestPath, e);
        }
    }

    private String extractRequestBodyForm(int requestContentLength, BufferedReader reader) throws IOException {
        int readLength = 0;
        char[] buffer = new char[requestContentLength];
        while (readLength < requestContentLength) {
            int nowReadLength = reader.read(buffer, readLength, requestContentLength - readLength);
            if (nowReadLength == -1) {
                throw new IOException("잘못된 요청");
            }
            readLength += nowReadLength;
        }
        return new String(buffer);
    }

    private void getProcess(
            OutputStream outputStream,
            UriInfo uriInfo,
            boolean hasJSessionId
    ) throws IOException, URISyntaxException {
        byte[] responseBody = RequestHandler.get(uriInfo.path());
        String responseHeader = buildResponseHeader(responseBody, findContentType(uriInfo.path()));
        if (!hasJSessionId) {
            responseHeader = addCookieToResponseHeader(responseHeader);
        }
        responseHeader = finishResponseHeader(responseHeader);
        outputStream.write(responseHeader.getBytes());
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private void postProcess(
            OutputStream outputStream,
            UriInfo uriInfo,
            FormParameters formParameters,
            boolean hasJSessionId
    ) throws IOException, URISyntaxException {
        String redirectPath = RequestHandler.post(uriInfo, formParameters);
        String responseHeader = buildResponseHeader(redirectPath);
        if (!hasJSessionId) {
            responseHeader = addCookieToResponseHeader(responseHeader);
        }
        responseHeader = finishResponseHeader(responseHeader);
        outputStream.write(responseHeader.getBytes());
        outputStream.flush();
    }

    private String buildResponseHeader(String redirectPath) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: " + redirectPath,
                "Content-Length: 0");
    }

    private String addCookieToResponseHeader(String responseHeader) {
        return responseHeader +
                "\r\n" +
                "Set-Cookie: JSESSIONID=" + JSessionIdGenerator.generateUuid();
    }

    private String finishResponseHeader(String responseHeader) {
        return responseHeader + "\r\n\r\n";
    }

    private String buildResponseHeader(
            byte[] responseBody,
            String contentType
    ) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.length + " ");
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
