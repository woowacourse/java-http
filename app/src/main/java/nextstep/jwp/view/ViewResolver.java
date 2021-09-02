package nextstep.jwp.view;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.ViewNotFoundException;

public class ViewResolver {

    private final Map<String, View> viewContainer = new HashMap<>();
    private final String resourceBasePath;

    public ViewResolver(String rootResponse, String resourceBasePath) {
        this.resourceBasePath = resourceBasePath;

        viewContainer.put("", View.EMPTY);
        viewContainer.put("/", View.of("rootResponse", rootResponse));
    }

    public View resolve(String viewName) {
        if(!viewContainer.containsKey(viewName)){
            viewContainer.putIfAbsent(viewName, getView(viewName));
        }
        return viewContainer.get(viewName);
    }

    private View getView(String viewName) {
        try {
            final URL resourceUrl = getClass().getResource(resourceBasePath + viewName);
            File file = new File(resourceUrl.getFile());
            String content = String.join("\n", Files.readAllLines(file.toPath())) + "\n";
            return View.of(viewName, content);
        } catch (Exception e) {
            throw new ViewNotFoundException();
        }
    }
}
