package org.apache.coyote.http.adapter;

import com.techcourse.web.view.AppResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpResponseBody;
import org.apache.coyote.http.response.HttpResponseHeader;
import org.apache.coyote.http.response.HttpStatusLine;

@NoArgsConstructor(access = AccessLevel.NONE)
public class ResponseConverter {

    private static final ResponseConverter INSTANCE = new ResponseConverter();

    public static ResponseConverter getInstance() {
        return INSTANCE;
    }

    public HttpResponse toHttpResponse(final HttpRequest request, final AppResponse appResponse) {
        final HttpStatusLine statusLine = HttpStatusLine.from(request.getVersion(), appResponse.status());

        final HttpResponseHeader header = HttpResponseHeader.withContentType(request.getContentType());
        appResponse.headers().forEach(header::add);

        final HttpResponseBody responseBody = HttpResponseBody.from(appResponse.body());
        return HttpResponse.from(statusLine, header, responseBody);
    }
}
