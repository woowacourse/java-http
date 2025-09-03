package org.apache.coyote.response;

public class HttpResponse {

    private final ResponseInfo responseInfo;
    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;

    public HttpResponse(final ResponseInfo responseInfo, final ResponseHeader responseHeader, final ResponseBody responseBody) {
        this.responseInfo = responseInfo;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public byte[] combine() {
        String response = String.join("\r\n",
                responseInfo.toCombine() + " ",
                responseHeader.toCombine(),
                "",
                responseBody.getBody());

        return response.getBytes();
    }
}
