package pt.ua.es.smartfarm.ServiceLayer;


import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import javax.servlet.*;
import javax.servlet.http.*;
import org.springframework.core.Ordered;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCORSFilter implements Filter {

    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpServletRequest request = (HttpServletRequest) req;
        response.setHeader("Access-Control-Allow-Origin", "http://deti-engsoft-07.ua.pt:3000");
        response.setHeader("Access-Control-Allow-Methods", "PATCH,POST,GET,OPTIONS,DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN");
        response.setHeader("Access-Control-Allow-Credentials", "true"); 

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, resp);
        }

    }

    @Override
    public void destroy() {
    }

}
