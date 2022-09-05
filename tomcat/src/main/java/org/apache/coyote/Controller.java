package org.apache.coyote;

import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public interface Controller {

    SessionManager SESSION_MANAGER = new SessionManager();

    HttpResponse service(HttpRequest httpRequest) throws Exception;

    boolean canHandle(HttpRequest httpRequest);
}
