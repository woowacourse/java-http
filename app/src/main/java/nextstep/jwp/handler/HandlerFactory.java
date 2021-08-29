package nextstep.jwp.handler;

import nextstep.jwp.model.PathType;
import nextstep.jwp.model.Request;

public class HandlerFactory {

    private HandlerFactory() {
    }

    public static Handler handler(Request request) {
        if (request.containsEXTENSION()) {
            return new StaticFileHandler(request);
        }
        if (request.isPath(PathType.BASE_PATH)) {
            return new BaseHandler(request);
        }
        if (request.containsPath(PathType.LOGIN_PATH)) {
            return new LoginHandler(request);
        }
        if (request.containsPath(PathType.REGISTER_PATH)) {
            return new RegisterHandler(request);
        }
        return new UnauthorizedHandler(request);
    }
}
