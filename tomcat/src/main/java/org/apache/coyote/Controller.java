package org.apache.coyote;

import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;

public interface Controller {

    SessionManager SESSION_MANAGER = new SessionManager();

    Response service(Request request) throws Exception;

    boolean canHandle(Request request);
}
