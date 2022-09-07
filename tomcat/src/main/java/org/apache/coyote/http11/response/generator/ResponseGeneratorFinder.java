package org.apache.coyote.http11.response.generator;

import java.util.List;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;

public class ResponseGeneratorFinder {

    private static final List<ResponseGenerator> STRATEGIES = List.of(
            new HtmlResponseGenerator(SessionManager.getInstance()), new CssResponseGenerator(),
            new JSResponseGenerator(), new LoginResponseGenerator(SessionManager.getInstance()),
            new RootResponseGenerator(), new RegisterResponseGenerator());
    private static final ResponseGenerator NOT_FOUND_RESPONSE_GENERATOR = new NotFoundResponseGenerator();

    private final List<ResponseGenerator> strategies;

    private ResponseGeneratorFinder(List<ResponseGenerator> strategies) {
        this.strategies = strategies;
    }

    public static ResponseGeneratorFinder withSessionManager(Manager sessionManager) {
        List<ResponseGenerator> strategies = List.of(
                new HtmlResponseGenerator(sessionManager), new CssResponseGenerator(),
                new JSResponseGenerator(), new LoginResponseGenerator(sessionManager),
                new RootResponseGenerator(), new RegisterResponseGenerator());
        return new ResponseGeneratorFinder(strategies);
    }

    public ResponseGenerator find(HttpRequest httpRequest) {
        return STRATEGIES.stream()
                .filter(strategy -> strategy.isSuitable(httpRequest))
                .findAny()
                .orElse(NOT_FOUND_RESPONSE_GENERATOR);
    }
}
