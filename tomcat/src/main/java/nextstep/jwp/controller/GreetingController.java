package nextstep.jwp.controller;

public class GreetingController implements Controller {

    @Override
    public String execute(final String target) throws Exception {
        return "Hello world!";
    }
}
