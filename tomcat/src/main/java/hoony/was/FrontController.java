package hoony.was;

import com.techcourse.handler.LoginRequestHandler;
import com.techcourse.handler.RegisterGetRequestHandler;
import com.techcourse.handler.RegisterPostRequestHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class FrontController {

    private static final FrontController instance = new FrontController();

    private final RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();
    private final ReturnValueResolverMapper returnValueResolverMapper = new ReturnValueResolverMapper();

    private FrontController() {
        registerRequestHandlers();
        registerMethodReturnValueResolvers();
    }

    private void registerRequestHandlers() {
        requestHandlerMapper.register(new RegisterGetRequestHandler());
        requestHandlerMapper.register(new RegisterPostRequestHandler());
        requestHandlerMapper.register(new LoginRequestHandler());
        requestHandlerMapper.register(new StaticResourceRequestHandler());
    }

    private void registerMethodReturnValueResolvers() {
        returnValueResolverMapper.register(new StaticResourceReturnValueResolver());
    }

    public static FrontController getInstance() {
        return instance;
    }

    public HttpResponse service(HttpRequest request) {
        Object returnValue = requestHandlerMapper.handle(request);
        return returnValueResolverMapper.resolve(returnValue);
    }
}
