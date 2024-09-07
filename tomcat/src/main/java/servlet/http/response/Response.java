package servlet.http.response;

import servlet.http.MimeType;
import servlet.http.StatusCode;

public class Response {

    private final ResponseLine responseLine;

    private final ResponseHeaders responseHeaders;

    private final ResponseBody responseBody;

    private String viewName;

    public Response() {
        this(new ResponseLine(), new ResponseHeaders(), new ResponseBody());
    }

    private Response(ResponseLine responseLine, ResponseHeaders responseHeaders, ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public void configureViewAndStatus(String viewName, StatusCode statusCode) {
        if (StatusCode.FOUND.equals(statusCode)) {
            responseHeaders.location(viewName);
        }
        this.viewName = viewName;
        responseLine.setStatusCode(statusCode);
    }

    public void contentType(MimeType mimeType) {
        responseHeaders.contentType(mimeType.getType());
    }

    public void setJsessionid(String jsessionid) {
        responseHeaders.setJsessionid(jsessionid);
    }

    public void setBody(String body) {
        responseBody.setBody(body);
        responseHeaders.contentLength(responseBody.length());
    }

    public String getViewName() {
        return viewName;
    }

    public byte[] getBytes() {
        return build().getBytes();
    }

    private String build() {
        StringBuilder builder = new StringBuilder();
        responseLine.assemble(builder);
        responseHeaders.assemble(builder);
        responseBody.assemble(builder);
        return builder.toString();
    }
}
