package nextstep.jwp.http.response;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.exception.http.response.CannotCreateResponseBodyException;
import nextstep.jwp.view.ViewResolver;

public class ResponseBody {

    private final String body;

    private ResponseBody(byte[] bytes) {
        this(new String(bytes));
    }

    private ResponseBody(String body) {
        this.body = body;
    }

    public static ResponseBody from(ViewResolver viewResolver) {
        try {
            Path filePath = new File(viewResolver.getFilePath()).toPath();
            return new ResponseBody(Files.readAllBytes(filePath));
        } catch (Exception e) {
            throw new CannotCreateResponseBodyException();
        }
    }

    public int getLength() {
        return getBytes().length;
    }

    public byte[] getBytes() {
        return body.getBytes();
    }

    public String getBody() {
        return body;
    }
}
