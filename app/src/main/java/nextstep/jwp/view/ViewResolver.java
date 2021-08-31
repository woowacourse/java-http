package nextstep.jwp.view;

import java.io.File;
import java.net.URL;

public class ViewResolver {

    private final String rootResponse;
    private final String resourceBasePath;

    public ViewResolver(String rootResponse, String resourceBasePath) {
        this.rootResponse = rootResponse;
        this.resourceBasePath = resourceBasePath;
    }

    public View resolve(String viewName) {
        try {
            if (viewName.isEmpty()) {
                return View.empty();
            }

            if (viewName.equals("/")) {
                return View.asString(rootResponse);
            }

            final URL resourceUrl = getClass().getResource(resourceBasePath + viewName);
            return View.asFile(new File(resourceUrl.getFile()));
        } catch (Exception e) {
            throw new IllegalArgumentException("view not found");
        }
    }
}
