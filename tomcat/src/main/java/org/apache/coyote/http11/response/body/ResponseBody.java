package org.apache.coyote.http11.response.body;

import static org.apache.coyote.http11.Constants.EMPTY;

public class ResponseBody {

    private final String responseBody;

    public ResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public ResponseBody() {
        this.responseBody = EMPTY;
    }

    public String toMessage() {
        return responseBody;
    }
}
