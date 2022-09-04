package nextstep.jwp.ui;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.mvc.Controller;
import org.apache.mvc.annotation.RequestMapping;

public class DashboardController implements Controller {

    private final InMemoryUserRepository repository;

    public DashboardController(InMemoryUserRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity handleRoot(HttpRequest request) {
        return new ResponseEntity(HttpStatus.OK, "Hello world!");
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity handleLogin(HttpRequest request) {
        Optional<User> user = repository.findByAccount(request.getParameter("account"));
        user.ifPresent(it -> printUser(request, it));
        return new ResponseEntity(HttpStatus.OK, "redirect:/login.html");
    }

    private static void printUser(HttpRequest request, User user) {
        if (user.checkPassword(request.getParameter("password"))) {
            System.out.println(user.getAccount() + "의 패스워드가 일치합니다");
            System.out.println(user);
            return;
        }
        System.out.println(user.getAccount() + "의 패스워드가 일치하지 않습니다");
    }

}
