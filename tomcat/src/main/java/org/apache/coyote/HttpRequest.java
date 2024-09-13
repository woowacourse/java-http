package org.apache.coyote;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Http11RequestHeader;
import org.apache.coyote.http11.request.Http11RequestStartLine;
import org.apache.coyote.session.Session;

public interface HttpRequest {

    HttpMethod getMethod();

    String getEndpoint();

    Http11RequestStartLine getStartLine();

    Http11RequestHeader getHeaders();

    String getBodyValue();

    Session getSession();

    String getQueryParamFromUrl(String param);

    String getQueryParamFromBody(String param);
}
