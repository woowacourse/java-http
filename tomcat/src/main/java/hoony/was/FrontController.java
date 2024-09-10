package hoony.was;

import com.techcourse.handler.LoginRequestHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class FrontController {

    private final RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();

    public FrontController() {
        requestHandlerMapper.register(new LoginRequestHandler());
        requestHandlerMapper.register(new StaticResourceRequestHandler());
    }

    public HttpResponse service(HttpRequest request) {
        return requestHandlerMapper.handle(request);
    }
}
