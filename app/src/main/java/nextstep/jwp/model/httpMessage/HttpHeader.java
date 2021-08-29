package nextstep.jwp.model.httpMessage;

public interface HttpHeader {

    ContentType getContentType();

    int getContentLength();

    String getCookie();
}
