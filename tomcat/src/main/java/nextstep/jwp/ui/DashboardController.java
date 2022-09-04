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
        Optional<User> optionalUser = repository.findByAccount(request.getParameter("account"));
        return optionalUser.map(user -> checkPassword(request, user))
                .orElseGet(() -> new ResponseEntity(HttpStatus.OK, "redirect:/login.html"));
    }

    private ResponseEntity checkPassword(HttpRequest request, User user) {
        if (user.checkPassword(request.getParameter("password"))) {
            return new ResponseEntity(HttpStatus.FOUND, "redirect:/index.html");
        }
        return new ResponseEntity(HttpStatus.UNAUTHORIZED, "redirect:/401.html");
    }
}
