package nextstep.jwp.infrastructure;

import nextstep.jwp.infrastructure.processor.LoginRequestProcessor;
import nextstep.jwp.infrastructure.processor.RequestProcessor;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {

    private final static Map<String, RequestProcessor> MAPPER = new HashMap<>();

    static {
        MAPPER.put("/login", new LoginRequestProcessor());
    }

    public static RequestProcessor mappingProcessor(String path) {
        return MAPPER.get(path);
    }
}
