package nextstep.jwp.request;

public interface HttpRequest {

    RequestLine getRequestLine();
    RequestHeader getRequestHeader();
    String getResourceName();
    String getAttribute(String name);
}
