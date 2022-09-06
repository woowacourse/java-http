package servlet;

import java.io.BufferedReader;
import java.io.IOException;

public interface Servlet {
    String doService(BufferedReader reader, String request) throws IOException;
}
