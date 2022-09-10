package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.response.ResponseHeaders.CONTENT_LENGTH_HEADER;
import static org.apache.coyote.http11.response.ResponseHeaders.SET_COOKIE_HEADER;

import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse {

    private General general;
    private final ResponseHeaders headers = new ResponseHeaders();
    private ResponseBody responseBody;

    public void initResponseValues(HttpRequest request, ResponseEntity responseEntity) {
        this.general = new General(responseEntity.getHttpStatus());
        this.responseBody = ResponseBody.of(responseEntity);
        this.headers.setHeaders(request.getRequestHeaders(), responseEntity);
        setContentLength(headers, responseBody.getContentLength());
    }

    private void setContentLength(ResponseHeaders headers, int contentLength) {
        if (contentLength == 0) {
            return;
        }
        headers.add(CONTENT_LENGTH_HEADER, String.valueOf(contentLength));
    }

    public void setJSessionCookie(String jSessionId) {
        headers.add(SET_COOKIE_HEADER, "JSESSIONID=" + jSessionId);
    }

    public String asString() {
        return String.join("\r\n",
                this.general.asString(),
                this.headers.asString(),
                "",
                this.responseBody.value());
    }
}
