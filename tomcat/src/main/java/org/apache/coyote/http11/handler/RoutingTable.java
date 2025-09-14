package org.apache.coyote.http11.handler;

import com.techcourse.controller.UserLoginController;
import com.techcourse.controller.UserRegisterController;
import com.techcourse.service.UserService;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.session.SessionManager;

public class RoutingTable {

    private final Map<String, Controller> routeTable;

    public RoutingTable(Map<String, Controller> routeTable) {
        this.routeTable = routeTable;
    }

    public static RoutingTable initializeRouteMap() {
        Map<String, Controller> routeMap = new HashMap<>();
        UserService userService = new UserService(new SessionManager());
        routeMap.put("/login", new UserLoginController(userService));
        routeMap.put("/register", new UserRegisterController(userService));
        return new RoutingTable(routeMap);
    }

    public Controller findControllerOfPath(String path) {
        return this.routeTable.get(path);
    }
}
