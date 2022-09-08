package org.apache.coyote.http11.response;

public class HttpResponse {

    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";

    private General general;
    private final ResponseHeaders headers = new ResponseHeaders();
    private ResponseBody responseBody;

    public void initResponseValues(ResponseEntity responseEntity) {
        this.general = new General(responseEntity.getHttpStatus());
        this.responseBody = ResponseBody.of(responseEntity);
        this.headers.setHeaders(responseEntity);
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
