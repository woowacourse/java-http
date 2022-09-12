package org.apache.coyote.http11.requestmapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.FileController;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public class FileHandlerMapper implements RequestMapper {

    private static final Pattern FILE_URI_PATTERN = Pattern.compile("/.+\\.(html|css|js|ico)");

    private static final Map<Pattern, Controller> CONTROLLERS = new HashMap<>();

    static {
        CONTROLLERS.put(FILE_URI_PATTERN, new FileController());
    }

    @Override
    public Controller mapController(HttpRequest httpRequest) {
        return CONTROLLERS.entrySet()
                .stream()
                .filter(controller -> this.match(controller.getKey(), httpRequest.getPath()))
                .map(Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private boolean match(Pattern pattern, String path) {
        Matcher matcher = pattern.matcher(path);
        return matcher.matches();
    }
}
