package org.apache.handler;

import java.io.IOException;
import org.apache.common.FileReader;
import org.apache.request.HttpRequest;
import org.apache.response.ContentType;
import org.apache.response.HttpResponse;
import org.apache.response.HttpStatus;

public abstract class AbstractController implements Controller {

    private static final String METHOD_NOT_ALLOWED_PAGE = "/405.html";

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

    protected void doPost(HttpRequest request, HttpResponse httpResponse) throws Exception {
        resolveMethodNotAllowed(httpResponse);
    }

    protected void doGet(HttpRequest request, HttpResponse httpResponse) throws Exception {
        resolveMethodNotAllowed(httpResponse);
    }

    private void resolveMethodNotAllowed(HttpResponse httpResponse) throws IOException {
        String content = FileReader.read(METHOD_NOT_ALLOWED_PAGE);
        httpResponse.setHttpStatus(HttpStatus.METHOD_NOT_ALLOWED);
        httpResponse.setContentType(ContentType.TEXT_HTML);
        httpResponse.setBody(content);
    }
}
