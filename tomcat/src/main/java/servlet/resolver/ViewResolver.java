package servlet.resolver;

import java.io.File;
import java.net.URL;

public class ViewResolver {

    private static ViewResolver INSTANCE;

    private ViewResolver() {
    }

    public static ViewResolver getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ViewResolver();
        }
        return INSTANCE;
    }

    private static final String CLASS_PATH = "static";

    public File resolveViewName(String viewName) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL viewResource = classLoader.getResource(convert(viewName));
        if (viewResource != null) {
            return new File(viewResource.getFile());
        }
        return null;
    }

    private String convert(String viewName) {
        String classPathViewName = "%s%s".formatted(CLASS_PATH, viewName);
        if (!classPathViewName.contains(".")) {
            return "%s.html".formatted(classPathViewName);
        }
        return classPathViewName;
    }
}
