package nextstep.jwp.controller;

import nextstep.jwp.utils.FileReader;
import org.apache.coyote.http11.request.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class ResourceController implements Controller {


    @Override
    public HttpResponse process(HttpRequest httpRequest) throws Exception {
        String url = httpRequest.getPath().getUrl();
        return HttpResponse.from(HttpStatus.OK, ContentType.findByUrl(url), FileReader.readFile(url));
    }
}
