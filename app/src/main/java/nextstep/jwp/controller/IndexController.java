package nextstep.jwp.controller;

@Controller
public class IndexController {

    @RequestMapping(path = "/index", method = "GET")
    public String index() {
        return "index.html";
    }
}
