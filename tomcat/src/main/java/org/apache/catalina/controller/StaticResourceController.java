package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResource;

//매핑된 컨트롤러가 없는 경로는 정적 파일을 그대로 응답
public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setBody(new StaticResource(request.getPath()));
    }
}
