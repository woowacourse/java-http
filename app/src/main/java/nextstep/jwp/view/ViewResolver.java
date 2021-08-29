package nextstep.jwp.view;

import java.net.URL;

public class ViewResolver {

    private final String view;

    public ViewResolver(String view) {
        this.view = view;
    }

    public String getFilePath() {
        return getResource().getPath();
    }

    private URL getResource() {
        return getClassLoader().getResource(view);
    }

    private ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }

    public String getView() {
        return view;
    }
}
