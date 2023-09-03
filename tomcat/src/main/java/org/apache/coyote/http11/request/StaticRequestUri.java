package org.apache.coyote.http11.request;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

public class StaticRequestUri extends Request {


    public StaticRequestUri(String method, String uri, Map<String, String> header) {
        super(method, uri, header);
    }

    @Override
    public String getContentType() {
        final String[] contentType = uri.split("\\.");
        return contentType[contentType.length - 1];
    }

    @Override
    public String getResponseBody() {
        try {
            final var fileUrl = getClass().getClassLoader().getResource("static" + uri);
            final var fileBytes = Files.readAllBytes(new File(fileUrl.getFile()).toPath());
            return new String(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e){
            return "";
        }
    }

    @Override
    public Optional<Map<String, String>> getQueries() {
        return Optional.empty();
    }

    @Override
    public String getApi() {
        return null;
    }
}
