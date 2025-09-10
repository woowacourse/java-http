package org.apache.catalina.vo;

import java.util.HashMap;
import java.util.Map;

public enum HttpStatus {

    // 1xx Informational
    CONTINUE(100, "Continue"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    PROCESSING(102, "Processing"),
    EARLY_HINTS(103, "Early Hints"),

    // 2xx Success
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
    NO_CONTENT(204, "No Content"),
    RESET_CONTENT(205, "Reset Content"),
    PARTIAL_CONTENT(206, "Partial Content"),
    MULTI_STATUS(207, "Multi-Status"),
    ALREADY_REPORTED(208, "Already Reported"),
    IM_USED(226, "IM Used"),

    // 3xx Redirection
    MULTIPLE_CHOICES(300, "Multiple Choices"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    USE_PROXY(305, "Use Proxy"),
    UNUSED(306, "Unused"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    PERMANENT_REDIRECT(308, "Permanent Redirect"),

    // 4xx Client Error
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
    URI_TOO_LONG(414, "URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable"),
    EXPECTATION_FAILED(417, "Expectation Failed"),
    IM_A_TEAPOT(418, "I'm a Teapot"),
    MISDIRECTED_REQUEST(421, "Misdirected Request"),
    UNPROCESSABLE_CONTENT(422, "Unprocessable Content"),
    @Deprecated
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"), // alias for 422
    LOCKED(423, "Locked"),
    FAILED_DEPENDENCY(424, "Failed Dependency"),
    TOO_EARLY(425, "Too Early"),
    UPGRADE_REQUIRED(426, "Upgrade Required"),
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),

    // 5xx Server Error
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
    LOOP_DETECTED(508, "Loop Detected"),
    NOT_EXTENDED(510, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");

    private static final Map<Integer, HttpStatus> LOOKUP = new HashMap<>();

    static {
        for (HttpStatus status : values()) {
            LOOKUP.putIfAbsent(status.code, status);
        }
    }

    private final int code;
    private final String reason;

    HttpStatus(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int value() {
        return code;
    }

    public String reason() {
        return reason;
    }

    public Series series() {
        return Series.fromCode(this.code);
    }

    public boolean is1xxInformational() {
        return series() == Series.INFORMATIONAL;
    }
    public boolean is2xxSuccessful() {
        return series() == Series.SUCCESSFUL;
    }
    public boolean is3xxRedirection() {
        return series() == Series.REDIRECTION;
    }
    public boolean is4xxClientError() {
        return series() == Series.CLIENT_ERROR;
    }
    public boolean is5xxServerError() {
        return series() == Series.SERVER_ERROR;
    }
    public boolean isError() {
        return is4xxClientError() || is5xxServerError();
    }

    public static HttpStatus valueOfCode(int code) {
        HttpStatus status = LOOKUP.get(code);
        if (status == null) {
            throw new RuntimeException("Unknown HTTP status code: " + code);
        }
        return status;
    }

    @Override
    public String toString() {
        return code + " " + reason;
    }

    public enum Series {

        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5),
        UNKNOWN(0)
        ;

        private final int value;

        Series(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static Series fromCode(int statusCode) {
            return switch (statusCode / 100) {
                case 1 -> INFORMATIONAL;
                case 2 -> SUCCESSFUL;
                case 3 -> REDIRECTION;
                case 4 -> CLIENT_ERROR;
                case 5 -> SERVER_ERROR;
                default -> UNKNOWN;
            };
        }
    }
}

