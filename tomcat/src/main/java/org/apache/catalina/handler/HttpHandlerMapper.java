package org.apache.catalina.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.HttpSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHandlerMapper {

    private static final Logger log = LoggerFactory.getLogger(AuthHandler.class);
    private static final Map<List<String>, HttpHandler> MAPPER = new HashMap<>();

    static {
        HttpHandler staticHandler = new StaticHandler();
        HttpHandler loginHandler = new AuthHandler(new HttpSessionManager());
        MAPPER.put(staticHandler.getAllPath(), staticHandler);
        MAPPER.put(loginHandler.getAllPath(), loginHandler);
    }

    public HttpHandler mappingHandler(String path) {
        log.info("찾으려는 경로 : {}", path);
        return MAPPER.keySet().stream()
                .filter(paths -> paths.contains(path))
                .map(MAPPER::get)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 경로를 찾을 수 없습니다."));
    }
}
