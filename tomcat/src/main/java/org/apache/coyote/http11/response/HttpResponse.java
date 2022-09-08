package org.apache.coyote.http11.response;

import org.apache.coyote.http11.context.Context;
import org.apache.coyote.http11.context.SessionManager;
import org.apache.coyote.http11.header.HttpVersion;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.headers.ResponseHeader;

public class HttpResponse implements Response {

    private final SessionManager MANAGER = new SessionManager();

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
                Context.empty()
        );
    }

    public HttpResponse postProcess(HttpRequest request) {
        PostProcessMeta meta = new PostProcessMeta(request, this.body);
        MANAGER.add(request.getContext().getSession());
        return new HttpResponse(general, this.headers.postProcess(meta), body, request.getContext().postProcess(meta));
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
        String headerWithCookie = headers.getAsString();
        if (!"".equals(context.getAsString())) {
            headerWithCookie = headerWithCookie + "\n" + context.getAsString();
        }
        return String.join("\n",
                general.getAsString(), headerWithCookie, "", body.getAsString());
    }
}
