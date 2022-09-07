package nextstep.jwp;

import static org.apache.coyote.http11.StatusCode.FOUND;
import static org.apache.coyote.http11.StatusCode.OK;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11QueryParams;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Url {

    DEFAULT("/"::equals, request -> Http11Response.withResponseBody(OK, "html", "Hello world!")),
    LOGIN(url -> isMatchRegex("/login.*", url), request -> {
        final Http11QueryParams queryParams = Http11QueryParams.from(request.getRequestUrl());
        if (!queryParams.hasParam()) {
            return Http11Response.of(OK, "/login.html");
        }
        if (isLoginSuccess(queryParams)) {
            return Http11Response.of(FOUND, "/index.html");
        }
        return Http11Response.of(FOUND, "/401.html");
    }),
    REGISTER("/register"::equals, request -> {
        if (request.isGetMethod()) {
            return Http11Response.of(OK, "/register.html");
        }
        if (request.isPostMethod()) {
            final Http11QueryParams queryParams = Http11QueryParams.from(request.getRequestBody());
            registerUser(queryParams);
            return Http11Response.of(FOUND, "/index.html");
        }
        return Http11Response.of(FOUND, "/404.html");
    }),
    RESOURCE(url -> isMatchRegex(".*\\..*", url), request -> Http11Response.of(OK, request.getRequestUrl()));

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

    private static boolean isLoginSuccess(Http11QueryParams queryParams) {
        final String account = queryParams.getValueFrom("account");
        final String password = queryParams.getValueFrom("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return true;
        }
        return false;
    }

    private static void registerUser(Http11QueryParams queryParams) {
        String account = queryParams.getValueFrom("account");
        String email = queryParams.getValueFrom("email");
        String password = queryParams.getValueFrom("password");
        User user = new User(account, email, password);
        InMemoryUserRepository.save(user);
        log.info(user.toString());
    }
}
