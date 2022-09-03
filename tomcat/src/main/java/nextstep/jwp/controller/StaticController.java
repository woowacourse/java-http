package nextstep.jwp.controller;

import java.nio.file.Path;
import org.apache.coyote.page.PageMapper;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public class StaticController {

    private PageMapper pageMapper = new PageMapper();

    public HttpResponse staticPage(HttpRequest httpRequest){
        return HttpResponse.ok()
                .body(pageMapper.getStaticFilePath(httpRequest.getUrl()))
                .build();
    }

}
