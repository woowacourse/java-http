package org.apache.coyote.http11;

public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod httpMethod = request.getHttpMethod();
        if (httpMethod.isGet()) {
            doGet(request, response);
        }

        if (httpMethod.isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected final byte[] readResource(String resourceName) {
        return ResourceFinder.readResource(resourceName, getClass().getClassLoader());
    }
}
