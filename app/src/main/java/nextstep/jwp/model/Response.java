package nextstep.jwp.model;

public class Response {

    private String message;

    public Response(String message) {
        this.message = message;
    }

    public byte[] getBytes() {
        return message.getBytes();
    }
}
