package org.apache.coyote.http11.request;

import java.util.ArrayList;
import java.util.List;

public class RequestParams {

    private static final String DELIMITER_OF_REQUEST_PARAM = "&";

    private final List<RequestParam> requestParams = new ArrayList<>();

    public RequestParams(String rawRequestParams) {
        String[] tokens = rawRequestParams.split(DELIMITER_OF_REQUEST_PARAM);
        for (String token : tokens) {
            this.requestParams.add(new RequestParam(token)); // account=gugu
        }
    }

    public RequestParams() {
    }
}
