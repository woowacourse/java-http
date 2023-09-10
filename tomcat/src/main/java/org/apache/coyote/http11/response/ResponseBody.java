package org.apache.coyote.http11.response;

public class ResponseBody {

    private String responseBody;

    public ResponseBody() {
        this.responseBody = "";
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
