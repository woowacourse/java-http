package servlet;

import org.apache.coyote.response.StatusCode;

public class ResponseAndView {

    private final String viewName;

    private final StatusCode statusCode;

    public ResponseAndView(String viewName, StatusCode statusCode) {
        this.viewName = viewName;
        this.statusCode = statusCode;
    }

    // todo buiider
}
