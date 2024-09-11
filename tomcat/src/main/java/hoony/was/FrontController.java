package hoony.was;

import com.techcourse.handler.LoginGetRequestHandler;
import com.techcourse.handler.LoginRequestHandler;
import com.techcourse.handler.RegisterGetRequestHandler;
import com.techcourse.handler.RegisterPostRequestHandler;
import org.apache.catalina.Manager;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class FrontController {

    private static final FrontController instance = new FrontController();

    private final RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();
    private final ReturnValueResolverMapper returnValueResolverMapper = new ReturnValueResolverMapper();
    private final Manager manager = new SessionManager();

    private FrontController() {
        registerRequestHandlers();
        registerMethodReturnValueResolvers();
    }

    public static FrontController getInstance() {
        return instance;
    }

    private void registerRequestHandlers() {
        requestHandlerMapper.register(new RegisterGetRequestHandler());
        requestHandlerMapper.register(new RegisterPostRequestHandler());
        requestHandlerMapper.register(new LoginGetRequestHandler());
        requestHandlerMapper.register(new LoginRequestHandler());
        requestHandlerMapper.register(new StaticResourceRequestHandler());
    }

    private void registerMethodReturnValueResolvers() {
        returnValueResolverMapper.register(new StaticResourceReturnValueResolver());
    }

    public void service(HttpRequest request, HttpResponse response) {
        request.setManager(manager);
        Object returnValue = requestHandlerMapper.handle(request, response);
        returnValueResolverMapper.resolve(request, response, returnValue);
    }
}
