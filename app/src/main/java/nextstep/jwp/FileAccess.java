package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileAccess {

    private String resource;

    public FileAccess(String resource) {
        this.resource = resource;
    }

    public String getFile() throws IOException {
        InputStream resourceStream = getClass().getResourceAsStream("/static" + resource);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceStream));

        StringBuilder stringBuilder = new StringBuilder();

        while (bufferedReader.ready()) {
            stringBuilder.append(bufferedReader.readLine());
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
