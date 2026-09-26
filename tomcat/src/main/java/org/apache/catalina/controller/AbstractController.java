package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractController implements Controller {
    private static final String DO_GET = "doGet";
    private static final String DO_POST = "doPost";
    private static final String ALLOW_DELIMITER = ", ";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        switch (request.getMethod()) {
            case GET, HEAD -> doGet(request, response);
            case POST -> doPost(request, response);
            default -> sendMethodNotAllowed(response);
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        sendMethodNotAllowed(response);
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        sendMethodNotAllowed(response);
    }

    // RFC 9110 15.5.6: 405 응답에는 허용하는 메서드 목록(Allow)을 반드시 포함한다
    private void sendMethodNotAllowed(final HttpResponse response) {
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
        response.setHeader(HttpHeaderName.ALLOW, allowedMethods());
    }

    private String allowedMethods() {
        final List<String> allowed = new ArrayList<>();
        if (isOverridden(DO_GET)) {
            allowed.add("GET");
            allowed.add("HEAD");
        }
        if (isOverridden(DO_POST)) {
            allowed.add("POST");
        }
        return String.join(ALLOW_DELIMITER, allowed);
    }

    // 하위 클래스가 해당 메서드를 재정의했는지 확인한다 (HttpServlet의 OPTIONS 처리와 같은 방식)
    private boolean isOverridden(final String methodName) {
        for (Class<?> type = getClass(); type != AbstractController.class; type = type.getSuperclass()) {
            final boolean declared = Arrays.stream(type.getDeclaredMethods())
                    .anyMatch(method -> method.getName().equals(methodName));
            if (declared) {
                return true;
            }
        }
        return false;
    }
}
