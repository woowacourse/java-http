package nextstep.jwp.framework;

import static nextstep.jwp.framework.RequestHeader.DELIMITER;

import java.net.URL;
import java.util.Objects;

public class RequestURI {

    private static final String PARSING = ".html";
    private static final int PARSING_INDEX = -5;
    private static final int HTTP_PAGE_INDEX = 1;

    private RequestURI() {
    }

    public static URL findResourceURL(final String line) {
        final String requestURI = page(line);
        final URL resource = RequestURI.class.getClassLoader().getResource("static/" + requestURI);

        if (Objects.isNull(resource)) {
            return RequestURI.class.getClassLoader().getResource("static/" + requestURI + ".html");
        }

        return resource;
    }

    private static String page(final String line) {
        return line.split(DELIMITER)[HTTP_PAGE_INDEX];
    }
}
