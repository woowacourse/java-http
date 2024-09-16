package jakarta.controller;

import jakarta.http.HttpMethod;
import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;

public class AbstractController implements Controller {

    private final ResourceFinder resourceFinder = new ResourceFinder();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod httpMethod = request.getHttpMethod();
        if (httpMethod.isGet()) {
            doGet(request, response);
            return;
        }

        if (httpMethod.isPost()) {
            doPost(request, response);
            return;
        }

        throw new UnsupportedOperationException("지원하지 않는 HTTP 메서드입니다.");
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected final byte[] readResource(String resourceName) {
        return resourceFinder.readResource(resourceName);
    }
}
