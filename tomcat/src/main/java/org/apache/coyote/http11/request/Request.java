package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Request {
    protected final String method;
    protected final String uri;
    protected final Map<String,String> header;

    public Request(String method, String uri, Map<String,String> header) {
        this.method = method;
        this.uri = uri;
        this.header = header;
    }

    public static Request of(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();
        valid(line);
        final String[] methodUri = line.split(" ");
        final String method = methodUri[0];
        final String uri = methodUri[1];
        final Map<String, String> header = readHeader(bufferedReader);

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
            return new ApiRequestUri(method,uri,header);
        }
        throw new IllegalArgumentException();
    }

    private static Map<String, String> readHeader(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> header = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            String[] entry = line.split(": ");
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

    public String getUri(){
        return uri;
    }

    public abstract String getContentType();
    public abstract String getResponseBody();
    public abstract Optional<Map<String, String>> getQueries();
    public abstract String getApi();
}
