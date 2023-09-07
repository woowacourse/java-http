package org.apache.handler;

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

        String content = FileReader.read(METHOD_NOT_ALLOWED_PAGE);
        httpResponse.setHttpStatus(HttpStatus.METHOD_NOT_ALLOWED);
        httpResponse.setContentType(ContentType.TEXT_HTML);
        httpResponse.setBody(content);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {}

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {}
}
