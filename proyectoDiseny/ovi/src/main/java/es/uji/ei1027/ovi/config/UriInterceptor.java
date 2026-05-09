package es.uji.ei1027.ovi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/*
 * Afegeix la URI actual al model perquè les vistes la puguen utilitzar
 * (per exemple, per a marcar com a actiu l'enllaç del menú corresponent).
 */
public class UriInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        // null en respostes sense vista (redirects, JSON...)
        if (modelAndView != null) {
            modelAndView.addObject("currentUri", request.getRequestURI());
        }
    }
}