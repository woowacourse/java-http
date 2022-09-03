package nextstep.jwp.controller;

import static nextstep.jwp.views.RequestLoginUserOutput.printRequestLoginUser;

import org.apache.coyote.page.PageMapper;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;


public class UserController{

    private PageMapper pageMapper = new PageMapper();

    public HttpResponse login(HttpRequest httpRequest){
        printRequestLoginUser(httpRequest);
        return HttpResponse.ok()
                .body(pageMapper.getFilePath(httpRequest.getUrl()))
                .build();
    }
}
