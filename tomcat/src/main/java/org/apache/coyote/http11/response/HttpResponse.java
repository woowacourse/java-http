package org.apache.coyote.http11.response;

import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.http11.common.HttpProtocolVersion.HTTP11;

@Getter
public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String BLANK_LINE = "";
    private static final String BLANK_SPACE = " ";

    private final HttpResponseStatusLine httpResponseStatusLine;
    private final HttpResponseHeaders httpResponseHeaders;
    private final HttpResponseBody httpResponseBody;

    @Builder
    public HttpResponse(HttpResponseStatusLine httpResponseStatusLine, HttpResponseHeaders httpResponseHeaders, HttpResponseBody httpResponseBody) {
        this.httpResponseStatusLine = httpResponseStatusLine;
        this.httpResponseHeaders = httpResponseHeaders;
        this.httpResponseBody = httpResponseBody;
    }

    public static HttpResponse from(ResponseEntity responseEntity) throws IOException {
        HttpStatus httpStatus = responseEntity.getHttpStatus();
        HttpResponseBody responseBody = getOrGenerateResponseBody(responseEntity);
        HttpResponseHeaders headers = generateHttpResponseHeaders(responseEntity, responseBody);

        return HttpResponse.builder()
                .httpResponseStatusLine(HttpResponseStatusLine.of(HTTP11, httpStatus))
                .httpResponseHeaders(headers)
                .httpResponseBody(responseBody)
                .build();
    }

    private static HttpResponseBody getOrGenerateResponseBody(ResponseEntity responseEntity) throws IOException {
        String location = responseEntity.getLocation();
        HttpResponseBody responseBody = responseEntity.getResponseBody();
        if (responseBody == null) {
            responseBody = findResponseBodyFrom(location);
        }
        return responseBody;
    }

    private static HttpResponseHeaders generateHttpResponseHeaders(ResponseEntity responseEntity, HttpResponseBody responseBody) {
        HttpStatus httpStatus = responseEntity.getHttpStatus();
        String location = responseEntity.getLocation();

        if (httpStatus == HttpStatus.FOUND) {
            return new HttpResponseHeaders()
                    .location(location)
                    .setCookie(responseEntity.getHttpCookie());
        }
        return new HttpResponseHeaders()
                .contentType(responseEntity.getContentType())
                .contentLength(responseBody);
    }

    private static HttpResponseBody findResponseBodyFrom(String location) throws IOException {
        URL resource = ClassLoader.getSystemClassLoader().getResource("static" + location);
        File file = new File(resource.getFile());
        return HttpResponseBody.from(new String(Files.readAllBytes(file.toPath())));
    }

    public String getResponse() {
        String responseStatusLine = formatResponseStatusLine(httpResponseStatusLine);
        String responseHeaders = formatResponseHeaders(httpResponseHeaders);
        String responseBody = formatResponseBody();

        return String.join(
                CRLF,
                responseStatusLine,
                responseHeaders,
                BLANK_LINE,
                responseBody
        );
    }

    private String formatResponseStatusLine(HttpResponseStatusLine httpResponseStatusLine) {
        return String.format("%s %s %s ",
                httpResponseStatusLine.getHttpProtocolVersion().getName(),
                httpResponseStatusLine.getHttpStatus().getStatusCode(),
                httpResponseStatusLine.getHttpStatus().name()
        );
    }

    private String formatResponseHeaders(HttpResponseHeaders headers) {
        return headers.getHeaders().entrySet().stream()
                .map(this::convertHeader)
                .collect(Collectors.joining(CRLF));
    }

    private String convertHeader(Map.Entry<String, String> entry) {
        return String.format("%s: %s ", entry.getKey(), entry.getValue());
    }

    private String formatResponseBody() {
        return httpResponseBody.getBody();
    }
}
