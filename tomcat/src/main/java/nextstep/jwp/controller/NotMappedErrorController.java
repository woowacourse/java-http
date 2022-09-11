package nextstep.jwp.controller;

public class NotMappedErrorController extends AbstractController {

    private static final NotMappedErrorController INSTANCE = new NotMappedErrorController();

    public static NotMappedErrorController getInstance() {
        return INSTANCE;
    }

    private NotMappedErrorController() {
    }
}
