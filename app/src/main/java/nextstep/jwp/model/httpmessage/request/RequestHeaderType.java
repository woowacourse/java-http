package nextstep.jwp.model.httpmessage.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public enum RequestHeaderType {
    HOST("Host"),
    CONNECTION("Connection"),
    ACCEPT("Accept"),
    COOKIE("Cookie");

    private static final Logger LOG = LoggerFactory.getLogger(RequestHeaderType.class);
    private final String value;

    RequestHeaderType(String value) {
        this.value = value;
    }

    public static Optional<RequestHeaderType> of(String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findAny();
    }

    public String value() {
        return value;
    }
}
