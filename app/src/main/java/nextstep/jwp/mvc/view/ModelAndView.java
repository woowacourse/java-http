package nextstep.jwp.mvc.view;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import nextstep.jwp.mvc.exception.PageNotFoundException;

public class ModelAndView {

    private String viewName;

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewAsString() {
        try {
            final URL url = getClass().getClassLoader().getResource("static/"+viewName);
            final List<String> actual = Files.readAllLines(Paths.get(url.toURI()));
            return String.join("\r\n", actual);
        } catch (IOException | URISyntaxException e) {
            throw new PageNotFoundException();
        }
    }
}
