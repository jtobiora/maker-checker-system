package com.swiftfingers.makercheckersystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try {
//            if (request.getServletPath().contains("/api/v1/auth")) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            String authHeader = request.getHeader("Authorization");
//
//            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
//                filterChain.doFilter(request,response);
//                return;
//            }
//
//            String jwt = getJwtFromRequest(request);
//
//            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
//                Long userId = tokenProvider.getUserIdFromJWT(jwt);
//
//                /*
//                    Note that you could also encode the user's username and roles inside JWT claims
//                    and create the UserDetails object by parsing those claims from the JWT.
//                    That would avoid the following database hit. It's completely up to you.
//                 */
//                UserDetails userDetails = customUserDetailsService.loadUserById(userId);
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        } catch (Exception ex) {
//            logger.error("Could not set user authentication in security context", ex);
//        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}
