package nextstep.jwp.model;

public class HttpResponse {

    private StatusLine statusLine;
    private final ResponseHeader header = new ResponseHeader();
    private String responseBody;

    public HttpResponse() {
    }

    public String toResponse() {
        return statusLine.getResponse() + "\r\n" +
                header.getResponse() + "\r\n" +
                "\r\n" +
                responseBody;
    }

    public void sendOk(final String httpVersion) {
        statusLine = new StatusLine(httpVersion, 200, "OK");
    }

    public void sendRedirect(final String httpVersion, final String location) {
        statusLine = new StatusLine(httpVersion, 302, "Found");
        header.addHeader("Location", location);
    }

    public void setResponseBody(final String responseBody) {
        this.responseBody = responseBody;
        header.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
    }

    public void addContentType(final String contentType) {
        header.addContentType(contentType);
    }

    public void setCookie(final String cookie) {
        header.setCookie(cookie);
    }
}
