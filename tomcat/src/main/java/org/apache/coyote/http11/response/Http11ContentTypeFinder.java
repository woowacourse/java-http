package org.apache.coyote.http11.response;

import java.nio.file.Path;
import java.util.Arrays;

class Http11ContentTypeFinder {

    String find(Path resourcePath) {
        String fileName = getFileName(resourcePath);
        ContentType[] contentTypes = ContentType.fromResourceName(fileName);
        String[] rawContentTypes = Arrays.stream(contentTypes)
                .map(ContentType::getRawContentType)
                .toArray(String[]::new);
        return String.join(",", rawContentTypes);
    }

    private String getFileName(Path resourcePath) {
        return resourcePath.getFileName().toString();
    }
}
