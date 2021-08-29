package nextstep.jwp;

import java.util.List;

public class HttpRequestHeader {

    private List<String> request;
    private String[] firstLine;

    public HttpRequestHeader(List<String> request) {
        this.request = request;
        this.firstLine = request.get(0).split(" ");
    }

    public String getRequestURI() {
        return this.firstLine[1];
    }

    public String getHttpMethod() {
        return this.firstLine[0];
    }
}
