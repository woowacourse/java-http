package nextstep.jwp.app.ui;

import java.util.Optional;
import nextstep.jwp.app.db.MemberRepository;
import nextstep.jwp.app.model.Member;
import nextstep.jwp.core.annotation.Autowired;
import nextstep.jwp.core.annotation.Controller;
import nextstep.jwp.core.mvc.mapping.RequestMapping;
import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

@Controller
public class LoginController {

    private MemberRepository memberRepository;

    @Autowired
    public LoginController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @RequestMapping(method = HttpMethod.GET, path = "/login")
    public String loginPage(HttpRequest httpRequest, HttpResponse httpResponse) {
        return "login.html";
    }

    @RequestMapping(method = HttpMethod.POST, path = "/login")
    public String login(HttpRequest httpRequest, HttpResponse httpResponse) {
        final String account = httpRequest.getAttribute("account");
        final String password = httpRequest.getAttribute("password");
        if(account == null || password == null) return "login.html";

        final Optional<Member> member = memberRepository.findByAccount(account);
        if(member.isEmpty() || member.get().invalidPassword(password)) {
            return "redirect:/unauthorized";
        }
        return "redirect:/";
    }

    @RequestMapping(method = HttpMethod.GET, path = "/unauthorized")
    public String unauthorizedPage(HttpRequest httpRequest, HttpResponse httpResponse) {
        return "401.html";
    }
}
