package nextstep.jwp.infrastructure;

import nextstep.jwp.infrastructure.processor.*;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {

    private final static Map<String, RequestProcessor> MAPPER = new HashMap<>();
    private static final String RESOURCE_DELIMITER = ".";

    static {
        MAPPER.put("/", new DefaultRequestProcessor());
        MAPPER.put("resource", new ResourceRequestProcessor());
        MAPPER.put("/login", new LoginRequestProcessor());
        MAPPER.put("/register", new RegisterRequestProcessor());
    }

    public static RequestProcessor mappingProcessor(String path) {
        if (path.contains(RESOURCE_DELIMITER)) {
            return MAPPER.get("resource");
        }
        return MAPPER.get(path);
    }
}
