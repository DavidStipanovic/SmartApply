package com.dave.smartapply.security;

import com.dave.smartapply.model.User;
import com.dave.smartapply.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // JWT Token aus Request Header extrahieren
            String jwt = getJwtFromRequest(request);

            // Wenn Token vorhanden und valide
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {

                // Email aus Token holen
                String email = jwtUtil.getEmailFromToken(jwt);

                // User aus DB laden
                User user = userService.findByEmail(email).orElse(null);

                // Wenn User existiert und approved ist
                if (user != null && user.getIsApproved()) {

                    // Authentication erstellen
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // User in Security Context setzen
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("Set Authentication for user: {}", email);
                }
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }

    // JWT Token aus Authorization Header extrahieren
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Format: "Bearer <token>"
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " entfernen
        }

        return null;
    }
}