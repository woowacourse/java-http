package org.apache.coyote.http11.reqeust.util;

import java.util.List;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpProtocolVersion;
import org.apache.coyote.http11.reqeust.HttpMethod;
import org.apache.coyote.http11.reqeust.QueryParameters;

public class HttpRequestParser {

    private static final HttpRequestParser instance = new HttpRequestParser();

    private HttpRequestParser() {
    }

    public HttpMethod parseHttpMethod(final String startLine) {
        final String method = startLine.split(" ")[0];

        return HttpMethod.valueOf(method);
    }

    public String parseRequestUri(final String startLine) {
        final String uriWithParams = startLine.split(" ")[1];
        final int queryParamStartIndex = uriWithParams.indexOf("?");
        if (queryParamStartIndex == -1) {
            return uriWithParams;
        }

        return uriWithParams.substring(0, queryParamStartIndex);
    }

    public QueryParameters parseQueryParameters(final String startLine) {
        final QueryParameters queryParameters = new QueryParameters();
        final String uriWithParams = startLine.split(" ")[1];
        final int queryParamStartIndex = uriWithParams.indexOf("?");
        if (queryParamStartIndex == -1) {
            return queryParameters;
        }
        final String queryParamStrings = uriWithParams.substring(queryParamStartIndex + 1);
        final String[] queryParams = queryParamStrings.split("&");
        for (String queryParam : queryParams) {
            final String[] keyValue = queryParam.split("=");
            queryParameters.addParameter(keyValue[0], keyValue[1]);
        }

        return queryParameters;
    }

    public HttpProtocolVersion parseHttpVersion(final String startLine) {
        final String httpVersion = startLine.split(" ")[2];

        return HttpProtocolVersion.valueOfVersion(httpVersion);
    }

    public HttpHeaders parseHeaders(final List<String> headerLines) {
        final HttpHeaders headers = new HttpHeaders();
        for (final String rawHeader : headerLines) {
            final String[] rawHeaders = rawHeader.split(": ", 2);
            headers.addHeader(rawHeaders[0], rawHeaders[1]);
        }

        return headers;
    }

    public static HttpRequestParser getInstance() {
        return instance;
    }
}
