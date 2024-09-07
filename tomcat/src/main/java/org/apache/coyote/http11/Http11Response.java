package org.apache.coyote.http11;

public class Http11Response {

    private static final String RESPONSE_FORMAT = String.join("\r\n",
            "%s",
            "%s");

    private final Http11ResponseHeader header;
    private final Http11ResponseBody responseBody;

    private Http11Response(Http11ResponseHeader header, Http11ResponseBody responseBody) {
        this.header = header;
        this.responseBody = responseBody;
    }

    public static Http11Response of(Http11ResponseHeader header, Http11ResponseBody responseBody) {
        return new Http11Response(header, responseBody);
    }

    public String getResponse() {
        return String.format(RESPONSE_FORMAT, header.getResponseHeader(), responseBody.getBody());
    }
}
