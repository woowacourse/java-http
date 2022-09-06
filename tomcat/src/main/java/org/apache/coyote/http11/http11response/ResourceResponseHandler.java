package org.apache.coyote.http11.http11response;

import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.dto.ResponseComponent;

public class ResourceResponseHandler implements Http11ResponseHandler {

    private static final StatusCode SUPPORT_STATUS_CODE = StatusCode.OK;
    private static final String LINE = "\r\n";

    @Override
    public boolean isProper(ResponseComponent responseComponent) {
        return responseComponent.getStatusCode().equals(SUPPORT_STATUS_CODE);
    }

    @Override
    public String makeResponse(ResponseComponent responseComponent) {
        return String.join(LINE,
                String.format("HTTP/1.1 %d %s ", responseComponent.getStatusCode().getCode(),
                        responseComponent.getStatusCode()),
                String.format("Content-Type: %s;charset=utf-8 ", responseComponent.getContentType()),
                String.format("Content-Length: %s ", responseComponent.getContentLength()),
                "",
                responseComponent.getBody());
    }
}
