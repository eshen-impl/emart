package com.chuwa.accountservice.filter;

import com.chuwa.accountservice.model.UserSession;
import com.chuwa.accountservice.service.RedisUserSessionService;
import com.chuwa.accountservice.util.JwtUtil;
import com.chuwa.accountservice.util.UUIDUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisUserSessionService redisUserSessionService;

    public JwtAuthenticationFilter(RedisUserSessionService redisUserSessionService) {
        this.redisUserSessionService = redisUserSessionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJWTfromRequest(request);
        if (token == null) {
            filterChain.doFilter(request, response); //not signed in: no jwt
            return;
        }

        try {
            JwtUtil.validateToken(token); //ExpiredJwtException | MalformedJwtException | SignatureException
        } catch (JwtException ex){
            filterChain.doFilter(request, response); //not signed in: invalid jwt
            return;
        }
        String encodedUserId = JwtUtil.getUserIdFromToken(token);

        UserSession userSession = redisUserSessionService.getUserSession(encodedUserId);
        if (userSession == null) {
            filterChain.doFilter(request, response); //not signed in: session deleted
            return;
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userSession,null,userSession.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request, response);


    }

    private String getJWTfromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
