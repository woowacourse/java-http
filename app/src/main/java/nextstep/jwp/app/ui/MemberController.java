package nextstep.jwp.app.ui;

import nextstep.jwp.app.db.MemberRepository;
import nextstep.jwp.app.model.Member;
import nextstep.jwp.core.annotation.Autowired;
import nextstep.jwp.core.annotation.Controller;
import nextstep.jwp.core.mvc.mapping.RequestMapping;
import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

@Controller
public class MemberController {

    private MemberRepository memberRepository;

    @Autowired
    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @RequestMapping(method = HttpMethod.GET, path = "/register")
    public String registerPage(HttpRequest httpRequest, HttpResponse httpResponse) {
        return "register.html";
    }

    @RequestMapping(method = HttpMethod.POST, path = "/register")
    public String register(HttpRequest httpRequest, HttpResponse httpResponse) {
        final String account = httpRequest.getAttribute("account");
        final String password = httpRequest.getAttribute("password");
        final String email = httpRequest.getAttribute("email");
        if(memberRepository.findByAccount(account).isPresent()) {
            return "redirect:/register";
        }
        memberRepository.save(new Member(account, password, email));
        return "redirect:/";
    }
}
