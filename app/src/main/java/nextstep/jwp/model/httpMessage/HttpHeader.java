package nextstep.jwp.model.httpMessage;

public interface HttpHeader {

    void setContentType(String contentType);

    String getContentType();

    void setContentLength(int contentLength);

    int getContentLength();

    void addHeader(String type, String value);

    String getHeader(String type);
}
