package com.nyayasetu.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private final FirebaseAuth firebaseAuth;

    public FirebaseTokenFilter(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Skip public endpoints
        if (path.startsWith("/api/anonymous/") ||
            (path.startsWith("/api/advocates") && "GET".equals(request.getMethod())) ||
            (path.equals("/api/lawyer-applications") && "POST".equals(request.getMethod()))) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token);
                String uid = firebaseToken.getUid();
                String email = firebaseToken.getEmail();
                
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                
                // Check for admin custom claim
                Map<String, Object> claims = firebaseToken.getClaims();
                if (claims.containsKey("admin") && Boolean.TRUE.equals(claims.get("admin"))) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
                
                FirebaseUserPrincipal principal = new FirebaseUserPrincipal(uid, email, firebaseToken);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
            } catch (FirebaseAuthException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"success\":false,\"message\":\"Invalid or expired token\"}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
