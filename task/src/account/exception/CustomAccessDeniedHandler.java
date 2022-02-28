package account.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.logging.Logger;
import org.h2.util.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest req,
                       HttpServletResponse res,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        CustomHttpResponse.Builder responseBuilder = new CustomHttpResponse.Builder();
        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(403);
        res.getWriter().write(responseBuilder
                .setTimestamp(LocalDateTime.now().toString())
                .setStatus(403)
                .setError("Forbidden")
                .setMessage("Access Denied!")
                .setPath(req.getRequestURI())
                .build().toString());
    }
}
