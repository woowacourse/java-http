package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHandler {

    public HttpRequest handleRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException();
        }

        String[] requestLineSegments = requestLine.split(" ");
        String methodSegment = requestLineSegments[0];
        HttpMethod method = HttpMethod.getMethod(methodSegment);

        String uriSegment = requestLineSegments[1];
        HttpUri uri = new HttpUri(uriSegment);

        String versionSegment = requestLineSegments[2];
        HttpProtocol protocol = HttpProtocol.getHttpProtocol(versionSegment);

        HttpRequestHeader header = setHeader(reader);
        HttpCookie cookie = setCookie(header);
        HttpRequestBody body = setBody(reader, header);
        HttpQueryParameter parameter = setQueryParameter(uri);

        return new HttpRequest(method, uri, protocol, header, cookie, body, parameter);
    }

    private HttpRequestHeader setHeader(BufferedReader br) throws IOException {
        HttpRequestHeader requestHeader = new HttpRequestHeader();
        String line;
        while(!"".equals((line = br.readLine()))) {
            if (line == null) {
                throw new IllegalArgumentException();
            }
            int index = line.indexOf(":");
            String fieldName = line.substring(0, index);
            String value = line.substring(index + 1);
            requestHeader.addHeader(fieldName, value);
        }
        return requestHeader;
    }

    private HttpCookie setCookie(HttpRequestHeader header) {
        return header.getCookie();
    }

    private HttpRequestBody setBody(BufferedReader br, HttpRequestHeader header) throws IOException {
        int bodyLength = header.getBodyLength();

        char[] buffer = new char[bodyLength];
        int read = br.read(buffer, 0, buffer.length);
        if  (bodyLength == 0 || read < 0) {
            return new HttpRequestBody();
        }
        String body = new String(buffer);

        return new HttpRequestBody(body);
    }

    private HttpQueryParameter setQueryParameter(HttpUri uri) {
        Map<String, String> queryParameters = new HashMap<>();

        String queryString = uri.getQueryString();
        if (queryString.isBlank()) {
            return new HttpQueryParameter(Map.of());
        }

        String[] parameters = queryString.split("&");
        for (String parameter : parameters) {
            int index = parameter.indexOf("=");
            String key = parameter.substring(0, index);
            String value = parameter.substring(index + 1);
            queryParameters.put(key, value);
        }
        return new HttpQueryParameter(queryParameters);
    }
}
