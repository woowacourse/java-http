package servlet.http.response;

import servlet.http.MimeType;
import servlet.http.StatusCode;

public class Response {

    private final ResponseLine responseLine;

    private final ResponseHeaders responseHeaders;

    private final ResponseBody responseBody;

    private String viewName;

    public Response() {
        this.responseLine = new ResponseLine();
        this.responseHeaders = new ResponseHeaders();
        this.responseBody = new ResponseBody();
    }

    public void configureViewAndStatus(String viewName, StatusCode statusCode) {
        this.viewName = viewName;
        responseLine.setStatusCode(statusCode);

        if (StatusCode.FOUND.equals(statusCode)) {
            responseHeaders.setLocation(viewName);
        }
    }

    public void setContentType(MimeType mimeType) {
        responseHeaders.setContentType(mimeType.getType());
    }

    public void setJsessionid(String jsessionid) {
        responseHeaders.setJsessionid(jsessionid);
    }

    public void setBody(String body) {
        responseBody.setBody(body);
        responseHeaders.setContentLength(responseBody.length());
    }

    public String getViewName() {
        if (viewName == null) {
            throw new IllegalStateException("viewName이 설정되지 않았습니다.");
        }
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
