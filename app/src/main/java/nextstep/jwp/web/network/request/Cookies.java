package nextstep.jwp.web.network.request;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Cookies {

    private static final String COOKIE_DELIMITER = ";";

    private final List<Cookie> cookies;

    public Cookies(String cookiesAsString) {
        this(Arrays.stream(cookiesAsString.split(COOKIE_DELIMITER, 0))
                .map(Cookie::new)
                .collect(Collectors.toList())
        );
    }

    public Cookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }
}
