package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryParams;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.model.User;

public class RegisterController implements Controller {

    @Override
    public boolean mapping(RequestLine requestLine) {
        return requestLine.isFrom("get", "/register")
                || requestLine.isFrom("post", "/register");
    }

    // TODO :: Service와 Controller 분리
    @Override
    public ModelAndView service(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();
        if (requestLine.isFrom("get", "/register")) {
            return printRegisterPage();
        }
        if (requestLine.isFrom("post", "/register")) {
            return register(httpRequest.getRequestBody());
        }
        throw new IllegalArgumentException("핸들러가 처리할 수 있는 요청이 아닙니다.");
    }

    private ModelAndView printRegisterPage() {
        return ModelAndView.ok("/register.html");
    }

    private ModelAndView register(String requestBody) {
        QueryParams queryParams = QueryParams.of(requestBody);

        String account = queryParams.get("account");
        String password = queryParams.get("password");
        String email = queryParams.get("email");

        User requestUser = new User(2, account, password, email);
        checkIsDuplicated(requestUser);

        InMemoryUserRepository.save(requestUser);
        return ModelAndView.redirect("index.html");
    }

    private void checkIsDuplicated(User requestUser) {
        InMemoryUserRepository.findByAccount(requestUser.getAccount())
                .ifPresent((user) -> new IllegalArgumentException("이미 존재하는 유저입니다."));
    }
}
