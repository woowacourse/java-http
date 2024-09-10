package hoony.was;

import com.techcourse.handler.LoginRequestHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class FrontController {

    private static final FrontController instance = new FrontController();

    private final RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();

    private FrontController() {
        requestHandlerMapper.register(new LoginRequestHandler());
        requestHandlerMapper.register(new StaticResourceRequestHandler());
    }

    public static FrontController getInstance() {
        return instance;
    }

    public HttpResponse service(HttpRequest request) {
        return requestHandlerMapper.handle(request);
    }
}
