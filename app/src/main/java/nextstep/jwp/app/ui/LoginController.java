package nextstep.jwp.app.ui;

import java.util.Optional;
import nextstep.jwp.app.db.MemberRepository;
import nextstep.jwp.app.model.Member;
import nextstep.jwp.app.ui.dto.MemberRequest;
import nextstep.jwp.core.annotation.Autowired;
import nextstep.jwp.mvc.annotation.Controller;
import nextstep.jwp.mvc.annotation.ModelAttribute;
import nextstep.jwp.mvc.annotation.RequestMapping;
import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.jwp.webserver.request.HttpSession;

@Controller
public class LoginController {

    private MemberRepository memberRepository;

    @Autowired
    public LoginController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @RequestMapping(method = HttpMethod.GET, path = "/login")
    public String loginPage(HttpSession httpSession) {
        final Member member = (Member) httpSession.getAttribute("member");
        if(member != null) {
            return "redirect:/";
        }
        return "login.html";
    }

    @RequestMapping(method = HttpMethod.POST, path = "/login")
    public String login(@ModelAttribute MemberRequest memberRequest, HttpSession httpSession) {

        if(memberRequest.getAccount() == null || memberRequest.getPassword() == null) return "login.html";

        final Optional<Member> member = memberRepository.findByAccount(memberRequest.getAccount());
        if(member.isEmpty() || member.get().invalidPassword(memberRequest.getPassword())) {
            return "redirect:/unauthorized";
        }
        httpSession.setAttribute("member", member.get());
        return "redirect:/";
    }

    @RequestMapping(method = HttpMethod.GET, path = "/unauthorized")
    public String unauthorizedPage() {
        return "401.html";
    }
}
