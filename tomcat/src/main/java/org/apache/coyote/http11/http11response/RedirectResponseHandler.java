package org.apache.coyote.http11.http11response;

import java.util.List;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.dto.ResponseComponent;

public class RedirectResponseHandler implements Http11ResponseHandler {

    private static final StatusCode SUPPORT_STATUS_CODE = StatusCode.REDIRECT;
    private static final String LINE = "\r\n";
    private static final String EMPTY_BODY = "";

    @Override
    public boolean isProper(ResponseComponent responseComponent) {
        return SUPPORT_STATUS_CODE.equals(responseComponent.getStatusCode());
    }

    @Override
    public String makeResponse(ResponseComponent responseComponent) {
        return String.join(LINE,
                String.format("HTTP/1.1 %d %s ", responseComponent.getStatusCode().getCode(),
                        responseComponent.getStatusCode()),
                String.format("Location: %s ", responseComponent.getLocation()),
                "",
                EMPTY_BODY);
    }
}
