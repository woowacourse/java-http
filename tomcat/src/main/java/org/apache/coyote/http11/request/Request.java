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
    protected final Map<String, String> body;

    public Request(String method, String uri, Map<String, String> header, Map<String, String> body) {
        this.method = method;
        this.uri = uri;
        this.header = header;
        this.body = body;
    }
    public static Request ofStaticRequest(String method, String uri){
        return new StaticRequestUri(method,uri,Map.of(),Map.of());
    }
    public static Request from(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();
        valid(line);
        final String[] methodUri = line.split(" ");
        final String method = methodUri[0];
        final String uri = methodUri[1];
        final Map<String, String> header = readHeader(bufferedReader);
        Map<String, String> body = new HashMap<>();
        if(header.containsKey("Content-Length")){
            int contentLength = Integer.parseInt(header.get("Content-Length"));
            body = readBody(bufferedReader,contentLength);
        }
        if(uri.equals("/")) {
            return new DefaultRequestUri(method,uri,header,body);
        }
        if(uri.contains(".") && !uri.contains("?")){
            return new StaticRequestUri(method,uri,header,body);
        }
        if(uri.contains("?")){
            return new QueryStringRequestUri(method,uri,header,body);
        }
        if(!uri.contains("?")&& !uri.contains(".")){
            return new ApiRequestUri(method,uri,header,body);
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

    private static Map<String, String> readBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        final Map<String, String> body = new HashMap<>();
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer,0,contentLength);
        String line = new String(buffer);
        for(String entry : line.split("&")){
            String[] bodyKeyValue = entry.split("=");
            body.put(bodyKeyValue[0],bodyKeyValue[1]);
        }
        return body;
    }

    private static void valid(String line) {
        if (line == null) {
            throw new UncheckedIOException(new IOException());
        }
    }

    public String getUri(){
        return uri;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public Map<String, String> getCookie(){
        final Map<String, String> cookie = new HashMap<>();
        if(!header.containsKey("Cookie")){
            return cookie;
        }
        final String cookieString = header.get("Cookie");
        for(String entryString : cookieString.split("; ")){
            String[] entry =entryString.split("=");
            cookie.put(entry[0],entry[1]);
        }
        return cookie;
    }
    public abstract String getContentType();
    public abstract String getResponseBody();
    public abstract Optional<Map<String, String>> getQueries();
    public abstract String getApi();
}
