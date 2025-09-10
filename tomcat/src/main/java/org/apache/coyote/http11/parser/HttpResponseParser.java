package org.apache.coyote.http11.parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.StaticResource;
import org.apache.coyote.http11.exception.HttpStatusException;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusCode;
import org.apache.coyote.http11.httpresponse.ResponseBody;
import org.apache.coyote.http11.httpresponse.ResponseHeaders;
import org.apache.coyote.http11.httpresponse.ResponseLine;

public class HttpResponseParser {

    private static final Map<String, String> MIME_TYPES = Map.of(
            "html", "text/html;charset=utf-8",
            "css", "text/css;charset=utf-8",
            "js", "text/javascript;charset=utf-8"
    );

    public static HttpResponse createWelcomeHttpResponse() {
        final ResponseLine responseLine = new ResponseLine("HTTP/1.1", HttpStatusCode.OK);

        final byte[] bodyBytes = "Hello world!".getBytes(StandardCharsets.UTF_8);
        final ResponseBody responseBody = new ResponseBody(MIME_TYPES.get("html"), bodyBytes);

        final LinkedHashMap<String, List<String>> rawResponseHeaders = new LinkedHashMap<>();
        addHeader(rawResponseHeaders, "Content-Type", responseBody.getContentType());
        addHeader(rawResponseHeaders, "Content-Length", String.valueOf(responseBody.getLength()));
        final ResponseHeaders responseHeaders = new ResponseHeaders(rawResponseHeaders);

        return new HttpResponse(responseLine, responseHeaders, responseBody);
    }

    public static HttpResponse parseToHttpResponse(final HttpStatusCode statusCode, final String filePath)
            throws IOException {
        final ResponseLine responseLine = new ResponseLine("HTTP/1.1", statusCode);

        final StaticResource staticResource = new StaticResource(filePath);
        final ResponseBody responseBody = parseResponseBody(staticResource);

        final LinkedHashMap<String, List<String>> rawResponseHeaders = new LinkedHashMap<>();
        addHeader(rawResponseHeaders, "Content-Type", responseBody.getContentType());
        addHeader(rawResponseHeaders, "Content-Length", String.valueOf(responseBody.getLength()));
        final ResponseHeaders responseHeaders = new ResponseHeaders(rawResponseHeaders);

        return new HttpResponse(responseLine, responseHeaders, responseBody);
    }

    public static HttpResponse parseToRedirectHttpResponse(final String location) {
        final ResponseLine responseLine = new ResponseLine("HTTP/1.1", HttpStatusCode.FOUND);

        final ResponseBody responseBody = ResponseBody.createEmptyResponseBody();

        final LinkedHashMap<String, List<String>> rawResponseHeaders = new LinkedHashMap<>();
        addHeader(rawResponseHeaders, "Content-Type", "text/html; charset=UTF-8");
        addHeader(rawResponseHeaders, "Content-Length", "0");
        addHeader(rawResponseHeaders, "Location", location);
        final ResponseHeaders responseHeaders = new ResponseHeaders(rawResponseHeaders);

        return new HttpResponse(responseLine, responseHeaders, responseBody);
    }

    public static HttpResponse parseToErrorResponse(final HttpStatusCode statusCode) throws IOException {
        final ResponseLine responseLine = new ResponseLine("HTTP/1.1", statusCode);

        final StaticResource staticResource = new StaticResource("/" + statusCode.getStatusCode() + ".html");
        final ResponseBody responseBody = parseResponseBody(staticResource);

        final LinkedHashMap<String, List<String>> rawResponseHeaders = new LinkedHashMap<>();
        addHeader(rawResponseHeaders, "Content-Type", responseBody.getContentType());
        addHeader(rawResponseHeaders, "Content-Length", String.valueOf(responseBody.getLength()));
        final ResponseHeaders responseHeaders = new ResponseHeaders(rawResponseHeaders);

        return new HttpResponse(responseLine, responseHeaders, responseBody);
    }

    private static ResponseBody parseResponseBody(final StaticResource staticResource) throws IOException {
        final byte[] rawResponseBody = staticResource.readFile();
        final String contentType = getContentType(staticResource.getExtension());
        return new ResponseBody(contentType, rawResponseBody);
    }

    private static void addHeader(final Map<String, List<String>> responseHeaders, final String key,
                                  final String value) {
        responseHeaders.computeIfAbsent(key, k -> new ArrayList<>())
                .add(value);
    }

    private static String getContentType(final String extension) {
        final String mimeType = MIME_TYPES.get(extension);
        if (mimeType == null) {
            throw new HttpStatusException(HttpStatusCode.NOT_FOUND);
        }
        return mimeType;
    }
}
