package org.apache.coyote.http11.request.mapping;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {

    private final Map<MappingKey, RequestHandler> mappings = new HashMap<>();

    private static final RequestMapper requestMapper = new RequestMapper();

    private RequestMapper() {
    }

    public static RequestMapper getInstance() {
        return requestMapper;
    }

    public RequestHandler getHandler(final MappingKey mappingKey) {
        final RequestHandler requestHandler = mappings.get(mappingKey);
        if (requestHandler != null) {
            return requestHandler;
        }
        if (isFileRequest(mappingKey.getUri())) {
            return FileRequestHandler.from(mappingKey);
        }
        return new NotFoundRequestHandler();
    }

    private boolean isFileRequest(final String uriPath) {
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static" + uriPath);
        return resource != null && !(new File(resource.getPath())).isDirectory();
    }

    public void registerMapping(final MappingKey mappingKey, final RequestHandler requestHandler) {
        if (mappings.containsKey(mappingKey)) {
            throw new IllegalArgumentException(String.format("중복 매핑입니다! %s", mappingKey));
        }
        mappings.put(mappingKey, requestHandler);
    }

    public void deleteAllMapping() {
        mappings.clear();
    }

    public Map<MappingKey, RequestHandler> getMappings() {
        return mappings;
    }
}
