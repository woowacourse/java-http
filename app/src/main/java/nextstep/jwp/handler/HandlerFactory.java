package nextstep.jwp.handler;

import nextstep.jwp.model.PathType;
import nextstep.jwp.model.Request;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;

public class HandlerFactory {

    private HandlerFactory() {
    }

    public static Handler handler(Request request) {
        if (request.isPath(PathType.BASE)) {
            return new BaseHandler();
        }
        if (request.containsPath(PathType.LOGIN)) {
            return new LoginHandler(new LoginService());
        }
        if (request.containsPath(PathType.REGISTER)) {
            return new RegisterHandler(new RegisterService());
        }
        if (request.containsExtension()) {
            return new StaticFileHandler();
        }
        return new NotFoundHandler();
    }
}
