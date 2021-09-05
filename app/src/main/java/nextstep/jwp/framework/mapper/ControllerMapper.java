package nextstep.jwp.framework.mapper;

import nextstep.jwp.application.mapping.RequestMappings;
import nextstep.jwp.framework.common.HttpUri;
import nextstep.jwp.framework.controller.Controller;
import nextstep.jwp.framework.controller.StaticResourceController;
import nextstep.jwp.framework.exception.UriMappingNotFoundException;

import java.util.Map;

public class ControllerMapper implements Mapper<Controller, String> {

    private static final ControllerMapper instance = new ControllerMapper();

    private final Controller staticResourceController = new StaticResourceController();

    private ControllerMapper() {
    }

    public static ControllerMapper getInstance() {
        return instance;
    }

    @Override
    public Controller resolve(String requestUri) {
        HttpUri httpUri = new HttpUri(requestUri);
        if (httpUri.isStaticFilePath()) {
            return staticResourceController;
        }

        Map<String, Controller> requestMappings = RequestMappings.getInstance().getMappings();
        if (requestMappings.containsKey(requestUri)) {
            return requestMappings.get(requestUri);
        }
        throw new UriMappingNotFoundException(String.format("해당 uri의 매핑을 찾을 수 없습니다.(%s)", requestUri));
    }
}
