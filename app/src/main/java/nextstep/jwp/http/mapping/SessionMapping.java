package nextstep.jwp.http.mapping;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.handler.session.DefaultSessionHandler;
import nextstep.jwp.http.handler.session.LoginSessionHandler;
import nextstep.jwp.http.handler.session.SessionHandler;
import nextstep.jwp.http.request.HttpRequest;

public class SessionMapping {

    public static final Map<String, SessionHandler> SESSION_HANDLERS = new HashMap<>();

    static {
        SESSION_HANDLERS.put("/login", new LoginSessionHandler());
    }

    public static SessionHandler getSessionHandler(HttpRequest request){
        String path = request.getPath();
        if(SESSION_HANDLERS.containsKey(path)){
            return SESSION_HANDLERS.get(path);
        }
        return new DefaultSessionHandler();
    }
}
