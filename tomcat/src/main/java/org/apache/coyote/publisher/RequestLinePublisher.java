package org.apache.coyote.publisher;

import org.apache.coyote.exception.CoyoteHttpException;
import org.apache.coyote.request.RequestLine;

import java.util.regex.Pattern;

import static java.util.Objects.isNull;

public class RequestLinePublisher {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("^[^?]*\\?[^?]*$");
    private static final String QUERY_PARAM_START_DELIMITER = "?";

    private static final int NO_REQUEST_LINE = 0;
    private static final int NO_URI_AND_HTTP_VERSION = 1;
    private static final int NO_HTTP_VERSION = 2;
    private static final int REQUEST_LINE_LENGTH = 3;

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final String httpMethodValue;
    private final String httpVersionValue;
    private final String queryParamNamesAndValues;
    private final String requestPathValue;

    private RequestLinePublisher(final String httpMethodValue,
                                 final String httpVersionValue,
                                 final String requestPathValue,
                                 final String queryParamNamesAndValues
    ) {
        this.httpMethodValue = httpMethodValue;
        this.httpVersionValue = httpVersionValue;
        this.requestPathValue = requestPathValue;
        this.queryParamNamesAndValues = queryParamNamesAndValues;
    }

    public static RequestLinePublisher read(final String requestLine) {
        if (isNull(requestLine)) {
            throw new CoyoteHttpException("HTTP 요청으로 들어온 Request Line은 null일 수 없습니다.");
        }
        
        final String[] requestLineValues = requestLine.split(REQUEST_LINE_DELIMITER);
        validateRequestLine(requestLineValues);

        final String httpMethodValue = requestLineValues[HTTP_METHOD_INDEX];
        final String httpVersionValue = requestLineValues[HTTP_VERSION_INDEX];
        String requestPathValue = requestLineValues[HTTP_REQUEST_URI_INDEX];
        String queryParamNamesAndValues = null;

        if (QUERY_PARAM_PATTERN.matcher(requestPathValue).matches()) {
            final int queryParamStartIndex = requestPathValue.indexOf(QUERY_PARAM_START_DELIMITER);

            queryParamNamesAndValues = requestPathValue.substring(queryParamStartIndex);
            requestPathValue = requestPathValue.substring(0, queryParamStartIndex);
        }

        return new RequestLinePublisher(httpMethodValue, httpVersionValue, requestPathValue, queryParamNamesAndValues);
    }

    private static void validateRequestLine(final String[] requestLineValues) {
        if (requestLineValues.length == NO_REQUEST_LINE) {
            throw new CoyoteHttpException("HTTP 요청으로 들어온 값의 첫 번째 라인에 Http Method, URI, Http Version 정보가 존재해야 합니다.");
        }
        if (requestLineValues.length == NO_URI_AND_HTTP_VERSION) {
            throw new CoyoteHttpException("HTTP 요청으로 들어온 값의 첫 번째 라인에 URI, Http Version 정보가 존재해야 합니다.");
        }
        if (requestLineValues.length == NO_HTTP_VERSION) {
            throw new CoyoteHttpException("HTTP 요청으로 들어온 값의 첫 번째 라인에 Http Version 정보가 존재해야 합니다.");
        }
        if (requestLineValues.length > REQUEST_LINE_LENGTH) {
            throw new CoyoteHttpException("HTTP 요청으로 들어온 값의 첫 번째 라인에 오직 Http Method, URI, Http Version 만 존재해야 합니다.");
        }
    }

    public RequestLine toRequestLine() {
        return RequestLine.of(httpMethodValue, httpVersionValue, requestPathValue, queryParamNamesAndValues);
    }
}
