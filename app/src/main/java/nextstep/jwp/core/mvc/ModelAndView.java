package nextstep.jwp.core.mvc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ModelAndView {

    private String currentUrl;
    private String viewName;
    private ModelAttribute modelAttribute;
    private String redirectUrl;

    public ModelAndView(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public ModelAndView(String viewName, String httpUrl) {
        this.viewName = viewName;
        this.currentUrl = httpUrl;
    }

    public String getViewAsString() {
        try {
            final URL url = getClass().getClassLoader().getResource("static/"+viewName);
            final List<String> actual = Files.readAllLines(Paths.get(url.toURI()));
            return String.join("\r\n", actual);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public boolean isRedirect() {
        return redirectUrl != null;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
