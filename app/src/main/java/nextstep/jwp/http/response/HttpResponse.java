package nextstep.jwp.http.response;

import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import java.util.List;
import nextstep.jwp.http.Body;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.response.response_line.ResponseLine;

public class HttpResponse implements Response {

    private final ResponseLine responseLine;
    private final Headers headers;
    private final Body body;

    public HttpResponse(ResponseLine responseline, Headers headers) {
        this(responseline, headers, null);
    }

    public HttpResponse(ResponseLine responseLine, Headers headers, Body body) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.body = body;

        setEssential();
    }

    private void setEssential() {
        headers.setBodyHeader(body);
    }

    public String asString() {
        String topOfHeader = String.join(" " + LINE_SEPARATOR, List.of(
            responseLine.asString(),
            headers.asString()
        ));

        return topOfHeader + LINE_SEPARATOR.repeat(2) + body.asString();
    }
}
