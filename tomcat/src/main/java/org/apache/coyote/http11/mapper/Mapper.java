package org.apache.coyote.http11.mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Mapper {
    protected final String method;
    protected final String uri;
    protected final Map<String, String> header;

    protected Mapper(String method, String uri, Map<String, String> header) {
        this.method = method;
        this.uri = uri;
        this.header = header;
    }

    public static Mapper of(BufferedReader bufferedReader) throws IOException{
        String line = bufferedReader.readLine();
        valid(line);
        final String[] methodUri = line.split(" ");
        final String method = methodUri[0];
        final String uri = methodUri[1];
        final Map<String, String> header = header(bufferedReader);
        if(uri.equals("/")) {
            return new DefaultRequestUri(method,uri,header);
        }
        if(uri.contains(".") && !uri.contains("?")){
            return new StaticRequestUri(method,uri,header);
        }
        if(uri.contains("?")){
            return new QueryStringRequestUri(method,uri,header);
        }
        if(!uri.contains("?")&& !uri.contains(".")){
            return new StringRequestUri(method,uri,header);
        }
        throw new IllegalArgumentException();
    }

    private static Map<String, String> header(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> header = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            String[] entry = line.split(" ");
            header.put(entry[0], entry[1]);
            line = bufferedReader.readLine();
        }
        return header;
    }

    private static void valid(String line) {
        if (line == null) {
            throw new UncheckedIOException(new IOException());
        }
    }

    public abstract String getContentType();
    public abstract String getResponseBody();
    public abstract Optional<Map<String, String>> getQueries();
}
