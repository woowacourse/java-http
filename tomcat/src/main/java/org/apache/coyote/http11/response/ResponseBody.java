package org.apache.coyote.http11.response;

public class ResponseBody {

    private String responseBody;

    private ResponseBody(String responseBody) {
        this.responseBody = "";
    }

    protected static ResponseBody init() {
        return new ResponseBody("");
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
