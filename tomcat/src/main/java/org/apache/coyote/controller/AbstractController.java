package org.apache.coyote.controller;

import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod httpMethod = request.getHttpMethod();
        if (httpMethod.isPost()) {
            doPost(request, response);
        }
        if (httpMethod.isGet()) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected final byte[] readStaticResource(String path) {
        return StaticResourceFinder.readResource(getClass().getClassLoader(), path);
    }

    protected final String getRequiredParameter(HttpRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        validateNotNull(parameterName, value);
        return value;
    }

    protected final String getRequiredBodyField(HttpRequest request, String fieldName) {
        RequestBody requestBody = request.getBody();
        String value = requestBody.getValue(fieldName);
        validateNotNull(fieldName, value);
        return value;
    }

    private void validateNotNull(String parameterName, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(parameterName + " is required.");
        }
    }
}
