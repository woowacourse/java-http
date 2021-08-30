package nextstep.jwp.exception;

import java.util.Arrays;

public enum Exception {

    INTERNAL_SERVER_ERROR(500, "Internal Server Error.", InternalServerErrorException.class),

    INVALID_HTTP_REQUEST(400, "Invalid Http Request.", InvalidHttpRequestException.class),
    INVALID_REQUEST_URI(400, "Invalid Request URI.", InvalidRequestUriException.class),
    INVALID_REQUEST_LINE(400, "Invalid Request Line.", InvalidRequestLineException.class),
    INVALID_REQUEST_HEADER(400, "Invalid Request Header.", InvalidRequestHeader.class),
    INVALID_FILE_EXTENSION(400, "Invalid File Extension.", InvalidFileExtensionException.class),
    NOT_ALLOWED_HTTP_VERSION(400, "Not Allowed Http Version.", NotAllowedHttpVersionException.class),
    EMPTY_QUERY_PARAMETERS(400, "Empty Query Parameters.", EmptyQueryParametersException.class),
    HTTP_REQUEST_NOT_HAVE_BODY(400, "Http Request Not Have Body.", HttpRequestNotHaveBodyException.class),
    UNAUTHORIZED(401, "Unauthorized.", UnauthorizedException.class),
    QUERY_PARAMETER_NOT_FOUND(404, "Query Parameter Not Found.", QueryParameterNotFoundException.class),
    STATIC_RESOURCE_NOT_FOUND(404, "Static Resource Not Found.", StaticResourceNotFoundException.class),
    DUPLICATE_ACCOUNT(409, "Duplicate Account.", DuplicateAccountException.class),

    NO_RESPONSE_BODY(500, "No Response Body.", NoResponseBodyException.class),
    NOT_IMPLEMENTED(501, "Not Implemented Http Method.", NotImplementedException.class);

    private final int statusCode;
    private final String message;
    private final Class<?> type;

    Exception(int statusCode, String message, Class<?> type) {
        this.statusCode = statusCode;
        this.message = message;
        this.type = type;
    }

    public static Exception findByClass(Class<? extends RuntimeException> type) {
        return Arrays.stream(values())
            .filter(exception -> type.equals(exception.type))
            .findAny()
            .orElseThrow(InternalServerErrorException::new);
    }
}
