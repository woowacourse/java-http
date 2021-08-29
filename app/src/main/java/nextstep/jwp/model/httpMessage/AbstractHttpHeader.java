package nextstep.jwp.model.httpMessage;

public abstract class AbstractHttpHeader implements HttpHeader {

    public static final String DELIMITER = "\r\n";

    private ContentType contentType;
    private int contentLength;
    private String cookie;

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public String getCookie() {
        return cookie;
    }
}
