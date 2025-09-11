package org.apache.catalina.requesthandler;

import java.util.HashMap;
import java.util.Map;
import javassist.NotFoundException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.Method;

public class RequestMappings {

    private final Map<RequestMapping, Controller> requestMappings;

    public RequestMappings() {
        this.requestMappings = new HashMap<>();
        RootController rootController = new RootController();
        StaticResourceController staticController = new StaticResourceController();
        UserController userController = new UserController();

        // Root mappings
        requestMappings.put(new RequestMapping("/", Method.GET), rootController);
        requestMappings.put(new RequestMapping("", Method.GET), rootController);

        // Static resource mappings
        requestMappings.put(new RequestMapping("/index.html", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/index", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/register.html", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/register", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/css/styles.css", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/assets/chart-area.js", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/js/scripts.js", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/assets/chart-bar.js", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/assets/chart-pie.js", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/assets/img/error-404-monochrome.svg", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/login.html", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/login", Method.GET), staticController);

        // User controller mappings
        requestMappings.put(new RequestMapping("/register", Method.POST), userController);
        requestMappings.put(new RequestMapping("/login", Method.POST), userController);
    }

    public Controller getSupportController(HttpRequest request) throws NotFoundException {
        for (var entry : requestMappings.entrySet()) {
            if (entry.getKey().isSupported(request)) {
                return entry.getValue();
            }
        }
        throw new NotFoundException("지원하지 않는 요청입니다.");
    }

    record RequestMapping(
            String path,
            Method method
    ) {
        public boolean isSupported(HttpRequest httpRequest) {
            return httpRequest.getPath().equals(path)
                    && httpRequest.getMethod() == method;
        }
    }
}
