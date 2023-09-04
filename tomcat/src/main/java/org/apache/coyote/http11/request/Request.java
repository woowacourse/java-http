package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String method;
    private final String uri;
    private final Map<String, String> query;
    private final Map<String,String> header;
    private final Map<String, String> cookie;
    private final Map<String, String> body;

    public Request(
            String method,
            String uri,
            Map<String, String> query,
            Map<String, String> header,
            Map<String, String> cookie,
            Map<String, String> body
    ) {
        this.method = method;
        this.uri = uri;
        this.query = query;
        this.header = header;
        this.cookie = cookie;
        this.body = body;
    }

    public static Request from(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();
        valid(line);
        final String[] methodUri = line.split(" ");
        final String method = methodUri[0];
        final String uri = methodUri[1];
        final Map<String, String> header = readHeader(bufferedReader);
        final Map<String, String> query = readQueries(uri);
        final Map<String, String> cookie = readCookie(header);
        final Map<String, String> body = readBody(header,bufferedReader);
        return new Request(method, uri, query, header, cookie,body);
    }

    private static void valid(String line) {
        if (line == null) {
            throw new UncheckedIOException(new IOException());
        }
    }

    private static Map<String, String> readQueries(String uri){
        if(!uri.contains("?")){
            return new HashMap<>();
        }
        final Map<String, String> queriesMap = new HashMap<>();
        String[] queries = uri.split("\\?")[1].split("&");
        for(String query : queries){
            String[] q = query.split("=");
            queriesMap.put(q[0],q[1]);
        }
        return queriesMap;
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

    private static Map<String, String> readBody(Map<String, String> header,BufferedReader bufferedReader) throws IOException {
        final Map<String, String> body = new HashMap<>();
        if(!header.containsKey("Content-Length")){
            return body;
        }
        final int contentLength = Integer.parseInt(header.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer,0,contentLength);
        String line = new String(buffer);
        for(String entry : line.split("&")){
            String[] bodyKeyValue = entry.split("=");
            body.put(bodyKeyValue[0],bodyKeyValue[1]);
        }
        return body;
    }
    private static Map<String, String> readCookie(Map<String, String> header){
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

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public Map<String, String> getCookie() {
        return cookie;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
