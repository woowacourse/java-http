package hoony.was;

import com.techcourse.controller.LoginRequestHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class FrontController {

    private final RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();

    public FrontController() {
        requestHandlerMapper.register(new StaticResourceRequestHandler());
        requestHandlerMapper.register(new LoginRequestHandler());
    }

    public HttpResponse service(HttpRequest request) {
        return requestHandlerMapper.handle(request);
    }
}
