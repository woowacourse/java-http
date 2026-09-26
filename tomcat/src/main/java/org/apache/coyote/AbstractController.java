package org.apache.coyote;

import org.apache.coyote.http11.ResourceLoader;
import java.util.Optional;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

/**
 * 컨트롤러의 공통 동작을 담음. HTTP 메서드에 따라 doGet/doPost로 분기하고, 404 응답 생성을 지원함.
 */
public abstract class AbstractController implements Controller {
    private static final String NOT_FOUND_PAGE = "/404.html";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isGet()) {
            doGet(request, response);
        } else if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected void renderNotFound(HttpResponse response) throws Exception {
        Optional<String> body = ResourceLoader.read(NOT_FOUND_PAGE);
        response.notFound(ResourceLoader.contentTypeOf(NOT_FOUND_PAGE), body.orElse(""));
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }
}
