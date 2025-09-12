package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public record HttpRequest(
        HttpMethod httpMethod,
        RequestUri requestUri,
        QueryParameters queryParameters,
        Map<String, String> headers,
        String body,
        HttpCookie cookies
) {

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        final RequestLine requestLine = RequestLine.from(reader);
        final RequestUri uri = RequestUri.from(requestLine.uri());
        final QueryParameters queryParameters = QueryParameters.from(uri.queryString());
        final HttpMethod httpMethod = HttpMethod.from(requestLine.method());

        final Map<String, String> headers = RequestHeaderParser.parse(reader);
        
        String body = "";
        if (httpMethod.type() == HttpMethodType.POST) {
            body = RequestBodyParser.parse(reader, headers);
        }

        final HttpCookie cookies = HttpCookie.from(headers.get("cookie"));

        return new HttpRequest(
                httpMethod,
                uri,
                queryParameters,
                headers,
                body,
                cookies
        );
    }

    public HttpMethodType getMethodType() {
        return httpMethod.type();
    }

    public String getPath() {
        return requestUri.path();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getParameter(String key) {
        String value = queryParameters.getValueByKey(key);
        if (value != null) {
            return value;
        }

        if (httpMethod.type() == HttpMethodType.POST && body != null && !body.isEmpty()) {
            QueryParameters bodyParameters = QueryParameters.from(body);
            String bodyValue = bodyParameters.getValueByKey(key);
            if (bodyValue != null) {
                try {
                    return URLDecoder.decode(bodyValue, StandardCharsets.UTF_8);
                } catch (Exception e) {
                    return bodyValue;
                }
            }
        }
        
        return null;
    }

}
