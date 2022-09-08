package org.apache.coyote.http11.http11handler;

import nextstep.jwp.model.visitor.Visitor;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11response.Http11Response;

public interface Http11Handler {

    boolean isProperHandler(Http11Request http11Request);

    Http11Response handle(Http11Request http11Request, Visitor visitor);
}
