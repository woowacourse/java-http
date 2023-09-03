package org.apache.coyote.http11;

import nextstep.jwp.model.LoginValidator;

public class RequestHandler {

    private final RequestHeader requestHeader;
    private final QueryString queryString;

    public RequestHandler(final RequestHeader requestHeader, final QueryString queryString) {
        this.requestHeader = requestHeader;
        this.queryString = queryString;
    }

    public static RequestHandler from(final RequestHeader requestHeader, final QueryString queryString) {
        return new RequestHandler(requestHeader, queryString);
    }

    public String execute() {
        final String contentType = ContentType.findContentTypeByURI(requestHeader.getParsedRequestURI());
        if (requestHeader.getHttpMethod() == HttpMethod.GET && requestHeader.isSameParsedRequestURI("/login")) {
            LoginValidator.check(requestHeader.getOriginRequestURI(), queryString);
        }
        return ResponseBody.from(requestHeader.getParsedRequestURI()).getResponseMessage(contentType);
    }
}
