package nextstep.jwp.model.web.response;

import nextstep.jwp.model.web.Headers;
import nextstep.jwp.model.web.StatusCode;

import java.util.Map;

public class CustomHttpResponse {

    private StatusLine statusLine;
    private Headers headers;
    private ResponseBody responseBody = new ResponseBody("");

    public void setStatusLine(StatusCode status, String versionOfProtocol) {
        statusLine = new StatusLine(versionOfProtocol, status.getStatusCode(), status.getStatusMessage());
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = new Headers(headers);
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
