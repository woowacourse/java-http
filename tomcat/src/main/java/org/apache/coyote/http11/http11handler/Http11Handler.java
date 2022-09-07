package org.apache.coyote.http11.http11handler;

import nextstep.jwp.model.visitor.Visitor;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11response.ResponseComponent;

public interface Http11Handler {

    boolean isProperHandler(Http11Request http11Request);

    ResponseComponent handle(Http11Request http11Request, Visitor visitor);
}
