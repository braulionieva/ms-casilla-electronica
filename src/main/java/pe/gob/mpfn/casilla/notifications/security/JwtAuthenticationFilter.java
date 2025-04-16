package pe.gob.mpfn.casilla.notifications.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.gob.mpfn.casilla.notifications.model.user.UserDetail;
import pe.gob.mpfn.casilla.notifications.repository.TokenRepository;
import pe.gob.mpfn.casilla.notifications.service.JwtService;

import java.io.IOException;

import static pe.gob.mpfn.casilla.notifications.util.enums.TokenStatus.VALID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getServletPath().contains("auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        var jwt = authHeader.substring(7);
        var dni = jwtService.extractUsername(jwt);
        if (dni != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = this.userDetailsService.loadUserByUsername(dni);

            var tokenOK = tokenRepository.findFirstByUserAndStatusOrderByCreateAtDesc(dni, VALID.getValue()).isPresent();
            if (tokenOK && jwtService.isTokenValid(jwt, userDetails)) {
                var session = jwtService.extractClaim(jwt, claims ->
                 String.valueOf(claims.get("session"))
                );
                ((UserDetail)userDetails).setSession(session);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                if (tokenOK) {
                    /*  permisos para eliminar */


                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
