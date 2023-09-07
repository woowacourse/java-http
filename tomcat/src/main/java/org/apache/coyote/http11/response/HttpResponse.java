package org.apache.coyote.http11.response;

import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static org.apache.coyote.http11.response.HttpProtocolVersion.HTTP11;

@Getter
public class HttpResponse {

    private static final String DELIMITER = "\r\n";
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
        String location = responseEntity.getLocation();
        HttpResponseBody responseBody = responseEntity.getResponseBody();
        HttpStatus httpStatus = responseEntity.getHttpStatus();

        if (responseBody == null) {
            responseBody = generateResponseBody(location);
        }

        if (httpStatus == HttpStatus.FOUND) {
            HttpResponseHeaders headers = new HttpResponseHeaders()
                    .location(location)
                    .setCookie(responseEntity.getHttpCookie());

            return HttpResponse.builder()
                    .httpResponseStatusLine(HttpResponseStatusLine.of(HTTP11, httpStatus))
                    .httpResponseHeaders(headers)
                    .httpResponseBody(responseBody)
                    .build();
        }

        HttpResponseHeaders headers = new HttpResponseHeaders()
                .contentType(responseEntity.getContentType())
                .contentLength(responseBody);

        return HttpResponse.builder()
                .httpResponseStatusLine(HttpResponseStatusLine.of(HTTP11, httpStatus))
                .httpResponseHeaders(headers)
                .httpResponseBody(responseBody)
                .build();
    }

    public String getResponse() throws IOException {
        String responseStatusLine = String.format("%s %s %s ",
                httpResponseStatusLine.getHttpProtocolVersion().getName(),
                httpResponseStatusLine.getHttpStatus().getStatusCode(),
                httpResponseStatusLine.getHttpStatus().name()
        );

        String responseHeaders = generateHeaders(httpResponseHeaders);

        String responseBody = httpResponseBody.getBody();

        return String.join(
                DELIMITER,
                responseStatusLine,
                responseHeaders,
                BLANK_LINE,
                responseBody
        );
    }

    private static HttpResponseBody generateResponseBody(String location) throws IOException {
        URL resource = ClassLoader.getSystemClassLoader().getResource("static" + location);
        File file = new File(resource.getFile());
        return HttpResponseBody.from(new String(Files.readAllBytes(file.toPath())));
    }

    private String generateHeaders(HttpResponseHeaders headers) {
        return headers.getHeaders().keySet()
                .stream()
                .map(it -> String.format("%s: %s ", it, headers.find(it)))
                .collect(Collectors.joining(DELIMITER));
    }
}
