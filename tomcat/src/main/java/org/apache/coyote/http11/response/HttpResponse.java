package org.apache.coyote.http11.response;

import org.apache.coyote.http11.context.Context;
import org.apache.coyote.http11.context.HttpCookie;
import org.apache.coyote.http11.header.HttpVersion;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.headers.ResponseHeader;

public class HttpResponse implements Response {

    private final ResponseGeneral general;
    private final ResponseHeaders headers;
    private final ResponseBody body;
    private final Context context;

    private HttpResponse(ResponseGeneral general, ResponseHeaders headers, ResponseBody body, Context context) {
        this.general = general;
        this.headers = headers;
        this.body = body;
        this.context = context;
    }

    public static HttpResponse initial() {
        return new HttpResponse(
                new ResponseGeneral(HttpVersion.HTTP11, HttpStatus.OK),
                ResponseHeaders.empty(),
                ResponseBody.empty(),
                new Context(new HttpCookie())
        );
    }

    public HttpResponse postProcess(HttpRequest request) {
        PostProcessMeta meta = new PostProcessMeta(request, this.body);
        return new HttpResponse(general, this.headers.postProcess(meta), body, context.postProcess(meta));
    }

    public HttpResponse update(HttpStatus status, String bodyString) {
        return new HttpResponse(
                new ResponseGeneral(HttpVersion.HTTP11, status),
                this.headers.update(bodyString),
                new ResponseBody(bodyString),
                this.context
        );
    }

    public HttpResponse addHeader(ResponseHeader header) {
        return new HttpResponse(general, this.headers.replace(header), body, context);
    }

    @Override
    public String getAsString() {
        return String.join("\n",
                general.getAsString(), headers.getAsString(), context.getAsString(), "", body.getAsString());
    }
}
