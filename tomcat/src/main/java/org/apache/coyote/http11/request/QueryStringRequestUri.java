package org.apache.coyote.http11.request;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QueryStringRequestUri extends Request {

    public QueryStringRequestUri(String method, String uri, Map<String, String> header, Map<String, String> body) {
        super(method, uri, header, body);
    }

    @Override
    public String getContentType() {
        return "html";
    }

    @Override
    public String getResponseBody() {
        try {
            final var fileUrl = getClass().getClassLoader().getResource("static/index.html");
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
        final Map<String, String> queriesMap = new HashMap<>();
        String[] queries = uri.split("\\?")[1].split("&");
        for(String query : queries){
            String[] q = query.split("=");
            queriesMap.put(q[0],q[1]);
        }
        return Optional.of(queriesMap);
    }

    @Override
    public String getApi() {
        return uri.split("\\?")[0];
    }
}
