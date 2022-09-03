package nextstep.jwp.controller;

import java.nio.file.Path;
import org.apache.coyote.page.PageMapper;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;


public class UserController{

    private PageMapper pageMapper = new PageMapper();

    public HttpResponse login(HttpRequest httpRequest){
        return HttpResponse.ok()
                .body(pageMapper.getFilePath(httpRequest.getUrl()))
                .build();
    }
}
