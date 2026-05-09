package es.uji.ei1027.ovi.config;

import es.uji.ei1027.ovi.model.Credentials;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Comprova que l'usuari té sessió i el seu compte està actiu abans
 * d'accedir a les zones privades. Es registra en OVIConfiguration.
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        Credentials credentials = (Credentials) request.getSession().getAttribute("user");

        // Sense sessió → guardar la URL i anar al login
        if (credentials == null) {
            saveRequestedUrl(request);
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        // Compte no activat o rebutjat → pending
        if (!credentials.getActivated() || credentials.isRejected()) {
            response.sendRedirect(request.getContextPath() + "/pending");
            return false;
        }

        return true;
    }

    // Desa la URL sol·licitada perquè el LoginController hi redirigisca després del login.
    // Només per a GET: reintentar un POST després del login no té sentit.
    private void saveRequestedUrl(HttpServletRequest request) {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            String uri = request.getRequestURI();
            String query = request.getQueryString();
            String fullUrl = (query != null) ? uri + "?" + query : uri;
            request.getSession().setAttribute("nextUrl", fullUrl);
        }
    }
}