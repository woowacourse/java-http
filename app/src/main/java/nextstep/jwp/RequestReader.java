package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;

public class RequestReader {
    public RequestReader() {
    }

    public String readHeader(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder request = new StringBuilder();
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();

            if(line == null){
                return "";
            }

            if("".equals(line)){
                return request.toString();
            }

            request.append(line)
                    .append("\r\n");

        }
        return request.toString();
    }

    public String parseUri(String input){
        String[] values = input.split(" ");
        if (values.length < 2){
            return "";
        }
        return input.split(" ")[1];
    }

    public String getResponseBody(String uri) throws IOException {
        if ("/".equals(uri)){
            return "Hello world!";
        }

        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
