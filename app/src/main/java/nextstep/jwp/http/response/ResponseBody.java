package nextstep.jwp.http.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResponseBody {
    private final String responseBody;

    private ResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public static ResponseBody createStaticFileByFileName(String fileName) throws IOException {
        if (fileName.equals("")) {
            return new ResponseBody("");
        }
        URL resource = ResponseBody.class.getClassLoader().getResource("static" + fileName);
        return new ResponseBody(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }

    public static ResponseBody createByString(String responseBody) {
        return new ResponseBody(responseBody);
    }

    public String getResponseBody() {
        return responseBody;
    }
}
