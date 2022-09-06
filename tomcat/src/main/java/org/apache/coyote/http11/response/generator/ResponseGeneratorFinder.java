package org.apache.coyote.http11.response.generator;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;

public class ResponseGeneratorFinder {

    private static final List<ResponseGenerator> STRATEGIES = List.of(
            new HtmlResponseGenerator(), new CssResponseGenerator(), new JSResponseGenerator(),
            new LoginResponseGenerator(), new RootResponseGenerator(), new RegisterResponseGenerator());
    private static final ResponseGenerator NOT_FOUND_RESPONSE_GENERATOR = new NotFoundResponseGenerator();

    private ResponseGeneratorFinder() {
    }

    public static ResponseGenerator find(HttpRequest httpRequest) {
        return STRATEGIES.stream()
                .filter(strategy -> strategy.isSuitable(httpRequest))
                .findAny()
                .orElse(NOT_FOUND_RESPONSE_GENERATOR);
    }
}
