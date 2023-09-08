package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import nextstep.jwp.controller.other.OtherControllerMapping;
import nextstep.jwp.controller.page.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;

public enum ControllerMapping {

    HTML(uri -> uri.endsWith(".html") || !uri.contains("."), RequestMapping::find),
    OTHER(uri -> uri.contains("."), OtherControllerMapping::find);

    private final Predicate<String> hasUriInFile;
    private final Function<HttpRequest, Controller> findController;

    ControllerMapping(final Predicate<String> hasUriInFile, final Function<HttpRequest, Controller> findController) {
        this.hasUriInFile = hasUriInFile;
        this.findController = findController;
    }

    public static Controller find(final HttpRequest request) {
        final String uri = request.getUri();

        return Arrays.stream(ControllerMapping.values())
                .filter(value -> value.hasUriInFile.test(uri))
                .map(value -> value.findController)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 에러입니다."))
                .apply(request);
    }
}
