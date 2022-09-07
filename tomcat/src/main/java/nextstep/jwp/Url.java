package nextstep.jwp;

import static org.apache.coyote.http11.StatusCode.OK;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Url {

    DEFAULT("/"::equals, request -> Http11Response.withResponseBody(OK, "html", "Hello world!")),
    LOGIN(url -> isMatchRegex("/login.*", url), request -> new LoginHandler().apply(request)),
    REGISTER("/register"::equals, request -> new RegisterHandler().apply(request)),
    RESOURCE(url -> isMatchRegex(".*\\..*", url), request -> new ResourceHandler().apply(request));

    private static final Logger log = LoggerFactory.getLogger(Url.class);

    private final Predicate<String> condition;

    private final Function<Http11Request, Http11Response> resourcePathExtractor;

    Url(Predicate<String> condition, Function<Http11Request, Http11Response> resourcePathExtractor) {
        this.condition = condition;
        this.resourcePathExtractor = resourcePathExtractor;
    }

    public static Http11Response getResponseFrom(Http11Request http11Request) {
        return Arrays.stream(values())
                .filter(value -> value.condition.test(http11Request.getRequestUrl()))
                .map(value -> value.resourcePathExtractor.apply(http11Request))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("존재하지 않는 페이지입니다."));
    }

    private static boolean isMatchRegex(String regex, String url) {
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }
}
