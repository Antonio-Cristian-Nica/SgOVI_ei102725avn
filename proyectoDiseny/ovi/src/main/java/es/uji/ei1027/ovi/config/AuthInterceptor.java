package es.uji.ei1027.ovi.config;

import es.uji.ei1027.ovi.model.Credentials;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    // Comprova que l'usuari tinga una sessió vàlida abans d'accedir
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        Credentials credentials = (Credentials) request.getSession().getAttribute("user");

        // Sin sesión → login
        if (credentials == null) {
            response.sendRedirect("/login");
            return false;
        }

        // Cuenta no activada o rechazada → pending
        if (!credentials.getActivated() || credentials.isRejected()) {
            response.sendRedirect("/pending");
            return false;
        }

        return true;
    }
}