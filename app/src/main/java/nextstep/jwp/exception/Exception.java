package nextstep.jwp.exception;

import java.util.Arrays;

public enum Exception {

    INTERNAL_SERVER_ERROR(500, "Internal Server Error.", InternalServerErrorException.class),

    INVALID_REQUEST_URI(400, "Invalid Request URI.", InvalidRequestUriException.class),
    INVALID_REQUEST_LINE(400, "Invalid Request Line.", InvalidRequestLineException.class),
    INVALID_REQUEST_HEADER(400, "Invalid Request Header.", InvalidRequestHeader.class),
    INVALID_FILE_EXTENSION(400, "Invalid File Extension.", InvalidFileExtensionException.class),
    NOT_ALLOWED_HTTP_VERSION(400, "Not Allowed Http Version.", NotAllowedHttpVersionException.class),
    UNAUTHORIZED(401, "Unauthorized.", UnauthorizedException.class),
    DUPLICATE_ACCOUNT(409, "Duplicate Account.", DuplicateAccountException.class),

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
