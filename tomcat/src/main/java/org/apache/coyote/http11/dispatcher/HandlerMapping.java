package org.apache.coyote.http11.dispatcher;

import com.techcourse.controller.HelloController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.db.SessionManager;
import com.techcourse.service.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.request.HttpRequest;

public class HandlerMapping {

    private static final Map<String, Object> mappings = new ConcurrentHashMap<>();

    public HandlerMapping() {
        init();
    }

    public static void init() {
        Service service = new Service();
        mappings.put("/", new HelloController());
        mappings.put("/login", new LoginController(service, SessionManager.getInstance()));
        mappings.put("/register", new RegisterController(service));
    }

    public Object getHandler(HttpRequest httpRequest) {
        return mappings.get(httpRequest.getMappingLine().getPath());
    }
}
