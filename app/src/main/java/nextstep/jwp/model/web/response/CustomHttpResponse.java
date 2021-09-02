package nextstep.jwp.model.web.response;

import nextstep.jwp.model.web.Headers;
import nextstep.jwp.model.web.StatusCode;

import java.util.Collections;
import java.util.List;

public class CustomHttpResponse {

    private StatusLine statusLine;
    private Headers headers = new Headers();
    private ResponseBody responseBody = new ResponseBody("");

    public void setStatusLine(StatusCode status, String versionOfProtocol) {
        statusLine = new StatusLine(versionOfProtocol, status.getStatusCode(), status.getStatusMessage());
    }

    public void forward(String url) {
        headers.addHeader("Location", Collections.singletonList(url));
    }

    public void addSessionCookieHeader(String sessionId) {
        headers.addHeader("Set-Cookie", Collections.singletonList("JSESSIONID=" + sessionId));
    }

    public void addHeaders(String key, List<String> value) {
        headers.addHeader(key, value);
    }

    public void setResponseBody(String body) {
        this.responseBody = new ResponseBody(body);
    }

    public byte[] getBodyBytes() {
        return String.join("\r\n",
                statusLine.asString(),
                headers.asString(),
                responseBody.getBody()).getBytes();
    }
}
