package nextstep.jwp.model.httpMessage.common;

public interface HttpHeader {

    String getContentType();

    void setContentType(String contentType);

    int getContentLength();

    void setContentLength(int contentLength);

    void addHeader(String type, String value);

    String getHeader(String type);
}
