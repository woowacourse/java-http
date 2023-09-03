package org.apache.coyote.http;

public class HttpResponseComposer {

    private boolean isRenderingNecessary = true;
    private HttpResponse httpResponse;

    public boolean isViewRenderingNecessary() {
        return isRenderingNecessary;
    }

    public HttpResponse getResponse() {
        return httpResponse;
    }
}
