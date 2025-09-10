package org.apache.coyote.http11.response;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.StaticResource;
import org.apache.coyote.http11.response.body.ResponseBody;
import org.apache.coyote.http11.response.header.ResponseHeader;
import org.apache.coyote.http11.response.header.ResponseHeaders;
import org.apache.coyote.http11.response.startline.HttpStatusCode;
import org.apache.coyote.http11.response.startline.ResponseLine;

public class HttpResponseFactory {

    public static HttpResponse createHttpResponse(
            final HttpStatusCode statusCode,
            final List<ResponseHeader> headers,
            final ResponseBody body
    ) {
        final ResponseLine responseLine = new ResponseLine("HTTP/1.1", statusCode);
        final ResponseHeaders responseHeaders = new ResponseHeaders(headers);
        return new HttpResponse(responseLine, responseHeaders, body);
    }

    public static HttpResponse createStaticHttpResponse(final HttpStatusCode statusCode, final String filePath)
            throws IOException {
        final ResponseLine responseLine = new ResponseLine("HTTP/1.1", statusCode);

        final StaticResource staticResource = new StaticResource(filePath);
        final byte[] content = staticResource.readFile();
        final String extension = staticResource.getExtension();
        final ResponseBody responseBody = ResponseBody.createStaticResourceResponseBody(content, extension);

        final ResponseHeader contentTypeHeader = ResponseHeader.createContentTypeHeader(responseBody);
        final ResponseHeader contentLengthHeader = ResponseHeader.createContentLength(responseBody);
        final ResponseHeaders responseHeaders = new ResponseHeaders(List.of(contentTypeHeader, contentLengthHeader));

        return new HttpResponse(responseLine, responseHeaders, responseBody);
    }

    public static HttpResponse createRedirectHttpResponse(final String location) {
        final ResponseLine responseLine = new ResponseLine("HTTP/1.1", HttpStatusCode.FOUND);

        final ResponseBody responseBody = ResponseBody.createEmptyResponseBody();

        final ResponseHeader contentTypeHeader = ResponseHeader.createContentTypeHeader(responseBody);
        final ResponseHeader contentLengthHeader = ResponseHeader.createContentLength(responseBody);
        final ResponseHeader locationHeader = ResponseHeader.createLocationHeader(location);
        final ResponseHeaders responseHeaders = new ResponseHeaders(
                List.of(contentTypeHeader, contentLengthHeader, locationHeader));

        return new HttpResponse(responseLine, responseHeaders, responseBody);
    }
}
