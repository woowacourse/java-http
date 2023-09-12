package org.apache.coyote.http11;

import org.apache.coyote.http11.response.HttpStatusCode;

public class HttpException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final String errMsg;


    public HttpException(final HttpStatusCode statusCode, final String errMsg) {
        super(String.join("\n",
                "title: " + statusCode.getMessage(),
                "status: " + statusCode.getCode(),
                "detail: " + errMsg));

        this.statusCode = statusCode;
        this.errMsg = errMsg;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
