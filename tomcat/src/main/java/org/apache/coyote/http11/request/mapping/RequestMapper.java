package org.apache.coyote.http11.request.mapping;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {

    private final Map<String, RequestHandler> mappings = new HashMap<>();

    private static final RequestMapper requestMapper = new RequestMapper();

    private RequestMapper() {
    }

    public static RequestMapper getInstance() {
        return requestMapper;
    }

    public RequestHandler getHandler(final String uri) {
        final RequestHandler requestHandler = mappings.get(uri);
        if (requestHandler != null) {
            return requestHandler;
        }
        if (isFileRequest(uri)) {
            return FileRequestHandler.from(uri);
        }
        return new NotFoundRequestHandler();
    }

    private boolean isFileRequest(final String uriPath) {
        System.out.println("uriPath = " + uriPath);
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static" + uriPath);
        return resource != null && !(new File(resource.getPath())).isDirectory();
    }

    public void registerMapping(final String mappingUri, final RequestHandler requestHandler) {
        if (mappings.containsKey(mappingUri)) {
            throw new IllegalArgumentException(String.format("중복 매핑입니다! %s", mappingUri));
        }
        mappings.put(mappingUri, requestHandler);
    }
}
