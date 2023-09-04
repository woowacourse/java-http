package org.apache.coyote.http11.httpmessage.support;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.coyote.http11.httpmessage.HttpHeader;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.QueryString;
import org.apache.coyote.http11.httpmessage.request.RequestBody;

public class HttpRequestParser {

    private static final String CRLF = "\r\n";
    private static final String QUERY_STRING_DELIMITER = "\\?";
    private static final String SPACE = " ";
    private static final String MANY_QUERY_STRING_INDEX = "&";
    private static final int METHOD_INDEX = 0;
    private static final int REQUEST_INFO_INDEX = 0;
    private static final int PATH_INDEX = 0;
    private static final int HEADER_START = 1;
    private static final int URL_INDEX = 1;
    private static final int Query_String_index = 1;
    private static final int PROTOCOL_INDEX = 2;

    private HttpRequestParser() {
    }

    public static HttpMethod getHttpMethod(final String request) {
        final String[] splitRequest = request.split(CRLF);
        return HttpMethod.getMethod(splitRequest[REQUEST_INFO_INDEX].split(SPACE)[METHOD_INDEX]);
    }

    public static String getPath(final String request) {
        final String[] splitRequest = request.split(CRLF);
        return splitRequest[REQUEST_INFO_INDEX].split(SPACE)[URL_INDEX]
            .split(
                QUERY_STRING_DELIMITER)[PATH_INDEX];
    }

    public static String getProtocol(final String request) {
        final String[] splitRequest = request.split(CRLF);
        return splitRequest[REQUEST_INFO_INDEX].split(SPACE)[PROTOCOL_INDEX];
    }

    public static HttpHeader getHeader(final String request) {
        if (request.contains("Content-Type")) {
            final String[] splitRequest = request.split(CRLF);
            final String[] headers = Arrays.copyOfRange(splitRequest, HEADER_START,
                splitRequest.length - 1);
            return HttpHeader.fromRequest(headers);
        }
        final String[] splitRequest = request.split(CRLF);
        final String[] headers = Arrays.copyOfRange(splitRequest, HEADER_START,
            splitRequest.length);
        return HttpHeader.fromRequest(headers);
    }

    public static QueryString getQueryString(final String request) {
        final String[] splitRequest = request.split(CRLF);
        final String[] queryString = splitRequest[REQUEST_INFO_INDEX].split(SPACE)[URL_INDEX]
            .split(QUERY_STRING_DELIMITER);
        if (queryString.length == 1) {
            return null;
        }
        return QueryString.fromRequest(
            queryString[Query_String_index].split(MANY_QUERY_STRING_INDEX));
    }

    public static RequestBody getRequestBody(final String request) {
//        System.out.println(request);
        if (getHttpMethod(request) == HttpMethod.POST) {
            final String[] splitRequest = request.split(CRLF);
//            final int requestBodyDelimiterIndex = Arrays.asList(splitRequest).indexOf("");

            final String decodedRequestBody = URLDecoder.decode(
                splitRequest[splitRequest.length - 1],
                StandardCharsets.UTF_8);
            System.out.println("gg " + decodedRequestBody);
            return RequestBody.fromRequest(decodedRequestBody);

        }
        return null;
    }
}
