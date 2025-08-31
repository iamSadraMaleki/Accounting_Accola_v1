package com.example.project.signup;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired; // << دیگه لازم نیست
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component; // <<< اضافه شد
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthTokenFilter extends OncePerRequestFilter {


    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthTokenFilter.class);


    public JwtAuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        logger.info("Instantiating JwtAuthTokenFilter with dependencies...");

        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;

        if (this.jwtUtils == null || this.userDetailsService == null) {
            logger.error("!!! CRITICAL: Dependencies NOT INJECTED into JwtAuthTokenFilter via constructor! Check bean definitions. !!!");
        } else {
            logger.info("JwtUtils and UserDetailsService injected successfully into JwtAuthTokenFilter.");
        }
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("JwtAuthTokenFilter executing for path: {}", request.getRequestURI());
        try {

            if (this.jwtUtils == null || this.userDetailsService == null) {
                logger.error("Filter dependencies are null in doFilterInternal. Aborting authentication.");

                filterChain.doFilter(request, response);
                return;
            }

            String jwt = parseJwt(request);
            logger.debug("Parsed JWT: {}", (jwt != null && jwt.length() > 10 ? jwt.substring(0, 10) + "..." : "null or short"));


            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                logger.debug("JWT is valid.");
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                logger.debug("Username from JWT: {}", username);


                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                logger.debug("UserDetails loaded for username: {}", username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Authentication set in SecurityContext for user: {}", username);
            } else {
                logger.debug("JWT is null or validation failed (validateJwtToken returned false).");
            }
        } catch (Exception e) {

            logger.error("Exception during JWT processing in JwtAuthTokenFilter", e);
        }


        filterChain.doFilter(request, response);
    }


    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}