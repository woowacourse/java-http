package org.apache.catalina.controller;

import java.util.NoSuchElementException;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.response.HttpResponse;

public abstract class DynamicSourceController extends AbstractController {

    private static final String JSESSIONID = "JSESSIONID";

    private static final SessionManager sessionManager = new SessionManager();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        super.service(request, response);
    }

    protected final Session getSession(HttpRequest request) {
        return request.getCookie()
                .get(JSESSIONID)
                .map(sessionManager::findSession)
                .orElseGet(this::createNewSession);
    }

    private Session createNewSession() {
        Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        return session;
    }

    protected final String getRequiredParameter(HttpRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        validateNotNull(value);
        return value;
    }

    protected final String getRequiredBodyField(HttpRequest request, String fieldName) {
        RequestBody requestBody = request.getBody();
        String value = requestBody.getValue(fieldName);
        validateNotNull(value);
        return value;
    }

    private void validateNotNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new NoSuchElementException("필수 파라미터가 누락되었습니다.");
        }
    }
}
