package nextstep.jwp.handler;

import nextstep.jwp.model.PathType;
import nextstep.jwp.model.Request;

public class HandlerFactory {

    private HandlerFactory() {
    }

    public static Handler handler(Request request) {
        if (request.isPath(PathType.BASE)) {
            return new BaseHandler(request);
        }
        if (request.containsPath(PathType.LOGIN)) {
            return new LoginHandler(request);
        }
        if (request.containsPath(PathType.REGISTER)) {
            return new RegisterHandler(request);
        }
        if (request.containsExtension()) {
            return new StaticFileHandler(request);
        }
        return new NotFoundHandler(request);
    }
}
