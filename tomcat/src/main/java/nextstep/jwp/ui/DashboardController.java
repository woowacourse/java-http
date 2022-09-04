package nextstep.jwp.ui;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.mvc.Controller;
import org.apache.mvc.annotation.RequestMapping;
import org.apache.util.UrlUtil;

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
    public ResponseEntity showLogin(HttpRequest request) {
        return new ResponseEntity(HttpStatus.FOUND, "/login.html");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity handleLogin(HttpRequest request) {
        Map<String, String> userMap = UrlUtil.parseQueryString(request.getRequestBody());
        Optional<User> optionalUser = repository.findByAccount(userMap.get("account"));
        if (optionalUser.isPresent() && optionalUser.get().checkPassword(userMap.get("password"))) {
            return new ResponseEntity(HttpStatus.FOUND, "/index.html");
        }
        return new ResponseEntity(HttpStatus.FOUND, "/401.html");
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ResponseEntity showRegister(HttpRequest request) {
        return new ResponseEntity(HttpStatus.FOUND, "/register.html");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity handleRegister(HttpRequest request) {
        Map<String, String> userMap = UrlUtil.parseQueryString(request.getRequestBody());
        User user = new User(userMap.get("account"), userMap.get("password"), userMap.get("email"));
        repository.save(user);
        return new ResponseEntity(HttpStatus.FOUND, "/index.html");
    }
}
