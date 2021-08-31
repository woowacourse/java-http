package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResponseBody {
    private String responseBody;

    public ResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public static ResponseBody createByPath(String path) throws IOException {
        URL resource = ResponseBody.class.getClassLoader().getResource("static" + path);
        return new ResponseBody(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }

    public String getResponseBody() {
        return responseBody;
    }
}
