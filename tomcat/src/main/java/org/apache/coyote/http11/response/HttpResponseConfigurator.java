package org.apache.coyote.http11.response;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.StaticResource;
import org.apache.coyote.http11.response.body.ResponseBody;
import org.apache.coyote.http11.response.header.ResponseHeader;
import org.apache.coyote.http11.response.startline.HttpStatusCode;
import org.apache.coyote.http11.response.startline.ResponseLine;

public class HttpResponseConfigurator {

    public static void okWithStaticResource(final HttpResponse response, final String path) throws IOException {
        final StaticResource resource = new StaticResource(path);
        final byte[] content = resource.readFile();
        final ResponseBody body = ResponseBody.createStaticResourceResponseBody(content, resource.getExtension());
        final List<ResponseHeader> headers = List.of(
                ResponseHeader.createContentTypeHeader(body),
                ResponseHeader.createContentLength(body)
        );
        HttpResponseConfigurator.applyToHttpResponse(response, HttpStatusCode.OK, headers, body);
    }

    public static void redirect(final HttpResponse response, final String location) {
        final ResponseBody body = ResponseBody.createEmptyResponseBody();
        final List<ResponseHeader> headers = List.of(
                ResponseHeader.createContentTypeHeader(body),
                ResponseHeader.createContentLength(body),
                ResponseHeader.createLocationHeader(location)
        );
        HttpResponseConfigurator.applyToHttpResponse(response, HttpStatusCode.FOUND, headers, body);
    }

    public static void okWithPlainText(final HttpResponse response, final String content) {
        final ResponseBody body = ResponseBody.createPlainTextResponseBody(content);
        final List<ResponseHeader> headers = List.of(
                ResponseHeader.createContentTypeHeader(body),
                ResponseHeader.createContentLength(body)
        );

        HttpResponseConfigurator.applyToHttpResponse(response, HttpStatusCode.OK, headers, body);
    }

    public static void errorResponse(final HttpStatusCode statusCode, final HttpResponse response) throws IOException {
        okWithStaticResource(response, "/" + statusCode.getStatusCode() + ".html");
        response.setResponseLine(ResponseLine.of(HttpStatusCode.NOT_FOUND));
    }

    private static void applyToHttpResponse(
            final HttpResponse response,
            final HttpStatusCode statusCode,
            final List<ResponseHeader> headers,
            final ResponseBody body
    ) {
        final ResponseLine responseLine = new ResponseLine("HTTP/1.1", statusCode);

        response.setResponseLine(responseLine)
                .setResponseBody(body)
                .addResponseHeaders(headers);
    }
}
