package nextstep.jwp.http;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHeader {
    private final String headers;

    public RequestHeader(String headers) {
        this.headers = headers;
    }

    public Optional<String> get(String key) {
        final Pattern pattern = Pattern.compile("(?<=" + key + ": ).+");
        Matcher matcher = pattern.matcher(headers);
        if (matcher.find()) {
            return Optional.of(matcher.group());
        }
        return Optional.empty();
    }
}
