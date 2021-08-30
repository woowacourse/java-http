package nextstep.jwp.response;

public class CharlieHttpResponse implements HttpResponse {

    private final ResponseLine responseLine;
    private final ResponseHeader responseHeader;

    public CharlieHttpResponse(ResponseLine responseLine, ResponseHeader responseHeader) {
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
    }


}
