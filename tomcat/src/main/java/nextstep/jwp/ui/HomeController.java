package nextstep.jwp.ui;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.mapping.MappingKey;
import org.apache.coyote.http11.request.mapping.RequestMapper;
import org.apache.coyote.http11.request.mapping.controllerscan.Controller;
import org.apache.coyote.http11.request.mapping.controllerscan.RequestMapping;
import org.apache.coyote.http11.response.HttpResponse;

@Controller
public class HomeController {

    public void init() {
        final RequestMapper requestMapper = RequestMapper.getInstance();
        requestMapper.registerMapping(new MappingKey(HttpMethod.GET, "/"), this::home);
    }

    @RequestMapping(method = HttpMethod.GET, uri = "/")
    public HttpResponse home(final HttpRequest httpRequest) {
        return new HttpResponse(ContentType.TEXT_HTML, HttpStatus.OK, HttpHeaders.empty(), "Hello world!");
    }
}
