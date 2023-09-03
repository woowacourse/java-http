package org.apache.coyote.http;

public class HttpResponseComposer {

    private boolean isRenderingNecessary = true;
    private HttpResponse httpResponse;
    private String viewName;

    public boolean isViewRenderingNecessary() {
        return isRenderingNecessary;
    }

    public HttpResponse getResponse() {
        return httpResponse;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
        isRenderingNecessary = false;
    }
}
