package org.apache.handler;

import java.io.IOException;
import java.util.List;
import org.apache.common.FileReader;
import org.apache.common.HttpMethod;
import org.apache.request.HttpRequest;
import org.apache.response.ContentType;
import org.apache.response.HttpResponse;
import org.apache.response.HttpStatus;

public abstract class AbstractController implements Controller {

    private static final String METHOD_NOT_ALLOWED_PAGE = "/405.html";
    private static final List<HttpMethod> ALLOW_METHOD = List.of(HttpMethod.GET, HttpMethod.POST);

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        if (httpRequest.isGet()) {
            doGet(httpRequest, httpResponse);
            return;
        }

        if (httpRequest.isPost()) {
            doPost(httpRequest, httpResponse);
            return;
        }

        resolveMethodNotAllowed(httpResponse);
    }

    protected abstract void doPost(HttpRequest request, HttpResponse httpResponse) throws Exception;

    protected abstract void doGet(HttpRequest request, HttpResponse httpResponse) throws Exception;

    private void resolveMethodNotAllowed(HttpResponse httpResponse) throws IOException {
        String content = FileReader.read(METHOD_NOT_ALLOWED_PAGE);
        httpResponse.setHttpStatus(HttpStatus.METHOD_NOT_ALLOWED);
        httpResponse.setAllow(ALLOW_METHOD);
        httpResponse.setContentType(ContentType.TEXT_HTML);
        httpResponse.setBody(content);
    }
}
