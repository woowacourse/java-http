package cache.com.example.etag;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

public class EtagHeaderFilter extends ShallowEtagHeaderFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().equals("/etag")) {
            UUID uuid = UUID.randomUUID();
            response.addHeader(HttpHeaders.ETAG, uuid.toString());
            filterChain.doFilter(request, response);
        }
    }
}
