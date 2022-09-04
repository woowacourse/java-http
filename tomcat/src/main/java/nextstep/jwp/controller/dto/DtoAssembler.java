package nextstep.jwp.controller.dto;

import nextstep.jwp.service.dto.LoginDto;
import nextstep.jwp.service.dto.SaveUserDto;
import org.apache.coyote.servlet.request.HttpRequest;

public class DtoAssembler {

    public static LoginDto ofLoginDto(HttpRequest request) {
        final var parameters = request.getParameters();
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        return new LoginDto(account, password);
    }

    public static SaveUserDto ofSaveUserDto(HttpRequest request) {
        final var parameters = request.getParameters();
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        final var email = parameters.get("email");
        return new SaveUserDto(account, password, email);
    }
}
