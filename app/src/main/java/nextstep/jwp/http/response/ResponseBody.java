package nextstep.jwp.http.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseBody {

    private static final Logger log = LoggerFactory.getLogger(ResponseBody.class);

    private String content;

    public ResponseBody(String content) {
//        try {
//            URL resource = getClass().getClassLoader().getResource("static" + uri);
//            if (resource == null) {
//                log.info("No Resource!");
//                resource = getClass().getClassLoader().getResource("static" + NOT_FOUND_URI);
//            }
//            assert resource != null;
//            final Path resourcePath = new File(resource.getPath()).toPath();
//            final List<String> requestBody = Files.readAllLines(resourcePath);
//            this.body = String.join("\r\n", requestBody);
//        } catch (Exception exception) {
//            log.error("Exception set response body", exception);
//        }
        this.content = content;
    }



    @Override
    public String toString() {
        return content;
    }
}
