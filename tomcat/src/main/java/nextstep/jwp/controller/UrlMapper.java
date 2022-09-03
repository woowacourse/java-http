package nextstep.jwp.controller;

import java.util.stream.Stream;

import org.apache.coyote.http11.request.Params;
import org.apache.coyote.http11.request.Uri;
import org.apache.coyote.http11.response.HttpResponse;

import nextstep.jwp.service.Service;

public enum UrlMapper {

    NONE("/", (controller, params) -> controller.none()),
    INDEX("/index", (controller, params) -> controller.index()),
    LOGIN("/login", Controller::login),
    ;

    private static final Service SERVICE = new Service();
    private static final Controller CONTROLLER = new Controller(SERVICE);

    private final String path;
    private final ControllerFunction function;

    UrlMapper(final String path, final ControllerFunction function) {
        this.path = path;
        this.function = function;
    }

    public static HttpResponse run(final Uri uri) {
        final String path = uri.getPath();
        final Params params = uri.getParams();

        return Stream.of(values())
                .filter(it -> it.equalsPath(path))
                .map(it -> it.function)
                .map(function -> function.apply(CONTROLLER, params))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 URL 경로가 존재하지 않습니다 : " + path));
    }

    public static boolean exist(final String path) {
        return Stream.of(values())
                .anyMatch(it -> it.equalsPath(path));
    }

    private boolean equalsPath(final String other) {
        return path.equals(other);
    }

    @FunctionalInterface
    private interface ControllerFunction {

        HttpResponse apply(final Controller controller, final Params params);
    }
}
