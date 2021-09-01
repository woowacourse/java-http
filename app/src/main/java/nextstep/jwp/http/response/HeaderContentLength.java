package nextstep.jwp.http.response;

public class HeaderContentLength implements ResponseHeaderable {
    private final Integer contentLength;

    public HeaderContentLength(Integer contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public Boolean isEmpty() {
        return contentLength == null;
    }

    @Override
    public String getHttpHeaderToString() {
        return "Content-Length: " + contentLength;
    }
}
