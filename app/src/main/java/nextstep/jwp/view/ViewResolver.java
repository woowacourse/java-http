package nextstep.jwp.view;

import java.net.URL;
import java.util.Optional;
import nextstep.jwp.exception.view.NoSuchResourceException;

public class ViewResolver {

    private final String view;

    public ViewResolver(String view) {
        this.view = view;
    }

    public String getFilePath() {
        Optional<URL> resource = getResource();

        if (resource.isEmpty()) {
            throw new NoSuchResourceException();
        }
        return resource.get().getPath();
    }

    private Optional<URL> getResource() {
        return Optional.ofNullable(getClassLoader().getResource(view));
    }

    private ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }

    public String getView() {
        return view;
    }
}
