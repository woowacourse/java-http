package nextstep.jwp.http.request.requestline;

public class RequestURI {

    private final String requestURI;

    public RequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    // todo : query parsing
    public String getRequestURI() {
        return requestURI;
    }
}
