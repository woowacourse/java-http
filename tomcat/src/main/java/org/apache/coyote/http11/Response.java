package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Response {
    private final String httpVersion;
    private final HttpStatus httpStatus;
    private final ResponseHeaders headers;
    private final String responseBody;

    public Response(final String httpVersion, final HttpStatus httpStatus, final ResponseHeaders headers,
                    final String responseBody) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static Response createByTemplate(final HttpStatus httpStatus, final String templateName) {
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        final URL resource = classLoader.getResource("static/" + templateName);
        final String responseBody;
        try {
            responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (final IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.");
        }

        final ResponseHeaders responseHeaders = new ResponseHeaders();
        final ContentType contentType = ContentType.findByFileName(templateName);
        responseHeaders.add("Content-Type", contentType.getHeaderValue() + ";charset=utf-8");
        responseHeaders.add("Content-Length", String.valueOf(responseBody.getBytes().length));
        return new Response("HTTP/1.1", httpStatus, responseHeaders, responseBody);
    }

    public static Response createByTemplate(final RequestURI requestURI) {
        final String responseBody = requestURI.readFile();
        final ResponseHeaders responseHeaders = new ResponseHeaders();
        final ContentType contentType = ContentType.findByFileName(requestURI.getFileName());
        responseHeaders.add("Content-Type", contentType.getHeaderValue() + ";charset=utf-8");
        responseHeaders.add("Content-Length", String.valueOf(responseBody.getBytes().length));
        return new Response("HTTP/1.1", HttpStatus.OK, responseHeaders, responseBody);
    }

    public static Response createByResponseBody(final HttpStatus httpStatus, final String responseBody) {
        final ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.add("Content-Type", "text/html;charset=utf-8");
        responseHeaders.add("Content-Length", String.valueOf(responseBody.getBytes().length));
        return new Response("HTTP/1.1", httpStatus, responseHeaders, responseBody);
    }

    public String getResponse() {
        final List<String> responseData = new ArrayList<>();
        final String responseLine = httpVersion + " " + httpStatus.getCode() + " " + httpStatus.getMessage() + " ";
        responseData.add(responseLine);

        final List<String> responseHeaderLines = headers.getHeaderLines();
        responseData.addAll(responseHeaderLines);
        responseData.add("");
        responseData.add(responseBody);

        return String.join("\r\n", responseData);
    }
}
