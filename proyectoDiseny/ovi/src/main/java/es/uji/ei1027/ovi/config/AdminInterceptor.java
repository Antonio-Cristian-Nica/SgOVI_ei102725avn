package es.uji.ei1027.ovi.config;

import es.uji.ei1027.ovi.model.Credentials;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/*
 * Comprova que l'usuari té rol "admin" abans d'accedir a /admin/**.
 * Es registra després de l'AuthInterceptor, que ja garanteix que hi ha sessió.
 */
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        Credentials credentials = (Credentials) request.getSession().getAttribute("user");

        // L'AuthInterceptor ja ha comprovat que credentials no és null.
        // Aquí només comprovem el rol.
        if (!"admin".equals(credentials.getRole())) {
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }

        return true;
    }
}
