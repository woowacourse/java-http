package org.apache.coyote.http11.request.mapping;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.mapping.controller.Controller;
import org.apache.coyote.http11.request.mapping.controller.FileController;
import org.apache.coyote.http11.request.mapping.controller.NotFoundController;
import org.apache.support.FileUtils;

public class RequestMapper {

    private final Map<MappingKey, Controller> mappings = new HashMap<>();

    private static final RequestMapper requestMapper = new RequestMapper();

    private RequestMapper() {
    }

    public static RequestMapper getInstance() {
        return requestMapper;
    }

    public Controller getHandler(final MappingKey mappingKey) {
        final Controller requestHandler = mappings.get(mappingKey);
        if (requestHandler != null) {
            return requestHandler;
        }
        if (isFileRequest(mappingKey.getUri())) {
            return FileController.from(mappingKey);
        }
        return new NotFoundController();
    }

    private boolean isFileRequest(final String uriPath) {
        final Optional<URL> resource = FileUtils.getResourceFromUri(uriPath);
        if (resource.isEmpty()) {
            return false;
        }
        final URL url = resource.orElseThrow();
        final File file = new File(url.getPath());
        return file.isFile();
    }

    public void registerMapping(final MappingKey mappingKey, final Controller requestHandler) {
        if (mappings.containsKey(mappingKey)) {
            throw new IllegalArgumentException(String.format("중복 매핑입니다! %s", mappingKey));
        }
        mappings.put(mappingKey, requestHandler);
    }

    public void deleteAllMapping() {
        mappings.clear();
    }
}
