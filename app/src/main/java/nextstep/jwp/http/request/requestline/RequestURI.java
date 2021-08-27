package nextstep.jwp.http.request.requestline;

public class RequestURI {

    private final String value;

    // TODO: '*'이 들어올 경우 모든 경로를 처리한다.
    public RequestURI(String value) {
        this.value = value;
    }
}
