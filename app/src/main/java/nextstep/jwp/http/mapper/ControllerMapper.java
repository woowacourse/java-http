package nextstep.jwp.http.mapper;

import nextstep.jwp.http.common.HttpUri;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.controller.StaticResourceController;
import nextstep.jwp.http.exception.UriMappingNotFoundException;
import nextstep.jwp.mapping.RequestMappings;

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
    public Controller resolve(String uri) {
        HttpUri httpUri = new HttpUri(uri);
        if (httpUri.isStaticFilePath()) {
            return staticResourceController;
        }

        Map<String, Controller> requestMappings = RequestMappings.getInstance().getMappings();
        if (requestMappings.containsKey(uri)) {
            return requestMappings.get(uri);
        }
        throw new UriMappingNotFoundException(String.format("해당 uri의 매핑을 찾을 수 없습니다.(%s)", uri));
    }
}
