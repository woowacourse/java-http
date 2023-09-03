package org.apache.coyote.http11.mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

public class StringRequestUri extends Mapper{
    protected StringRequestUri(String method, String uri, Map<String, String> header) {
        super(method, uri, header);
    }

    @Override
    public String getContentType() {
        return "html";
    }

    @Override
    public String getResponseBody() {
        try {
            final var fileUrl = getClass().getClassLoader().getResource("static" + uri +".html");
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
}
