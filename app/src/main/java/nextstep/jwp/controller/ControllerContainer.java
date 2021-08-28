package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.http.HttpRequest;

public class ControllerContainer {

    private static List<Controller> controllers = Arrays.asList(
        new IndexPageController()
    );

    public static Controller findController(HttpRequest httpRequest) {
        //TODO : not found page 출력하도록
        return controllers.stream()
            .filter(it -> it.isMatchingController(httpRequest))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }
}
