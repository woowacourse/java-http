package kokodak;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import kokodak.controller.Controller;
import kokodak.controller.HelloController;
import kokodak.controller.LoginController;
import kokodak.controller.RegisterController;
import kokodak.controller.ResourceController;
import kokodak.http.HttpRequest;
import kokodak.http.RequestTarget;

public class RequestMapper {

    private static final Map<String, Controller> controllerByPath = new HashMap<>();
    private static final Controller resourceController = new ResourceController();

    static {
        new HelloController();
        new LoginController();
        new RegisterController();
    }

    public static void register(final String path, final Controller controller) {
        controllerByPath.put(path, controller);
    }

    public Controller mappingController(final HttpRequest httpRequest) throws IOException {
        final RequestTarget requestTarget = httpRequest.getRequestTarget();
        final String path = requestTarget.getPath();
        if (hasResource(path)) {
            return resourceController;
        }
        return controllerByPath.get(path);
    }

    private boolean hasResource(final String path) {
        final String lastPathSnippet = getLastPathSnippet(path);
        return lastPathSnippet.contains(".");
    }

    private String getLastPathSnippet(final String path) {
        if ("/".equals(path)) {
            return "";
        }
        final String[] pathSnippets = path.split("/");
        return pathSnippets[pathSnippets.length - 1];
    }
}
