package org.apache.coyote.http.util;

import java.util.List;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.request.Request;
import servlet.request.HttpRequest;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import servlet.response.HttpResponse;

public final class ResponseGenerator {

    public static Response generate(final HttpRequest request, final HttpResponse response) {
        final HttpStatusCode statusCode = response.statusCode();
        final ContentType contentType = response.contentType();
        final String body = response.body();
        final List<HttpCookie> cookies = response.cookies();
        final List<HeaderDto> headerDtos = response.headerDtos();

        if (headerDtos.isEmpty()) {
            return Response.of(request.request(), statusCode, contentType, body, cookies);
        }

        return Response.of(
                request.request(),
                statusCode,
                contentType,
                body,
                cookies,
                headerDtos.toArray(HeaderDto[]::new)
        );
    }

    public static Response createRedirectResponse(final Request request, final String targetPath) {
        return Response.of(
                request,
                HttpStatusCode.FOUND,
                ContentType.JSON,
                HttpConsts.BLANK,
                new HeaderDto(HttpHeaderConsts.LOCATION, targetPath)
        );
    }

    private ResponseGenerator() {
    }
}
