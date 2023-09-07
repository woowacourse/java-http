package nextstep.jwp.dto;

public class LoginResponseDto {

    private final String redirectUrl;

    public LoginResponseDto(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
