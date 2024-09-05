package servlet.resolver;

import java.io.File;
import java.net.URL;

public class ViewResolver {

    private static final String CLASS_PATH = "static";

    public File resolveViewName(String viewName) {
        URL resource = this.getClass().getClassLoader().getResource("%s%s".formatted(CLASS_PATH, convert(viewName)));
        if (resource == null) {
            throw new IllegalArgumentException("Not found file: %s".formatted(viewName));
        }
        return new File(resource.getFile());
    }

    public String convert(String viewName) {
        if (!viewName.contains(".")) {
            return "%s.html".formatted(viewName);
        }
        return viewName;
    }
}
