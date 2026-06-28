package com.br.desafioirrahbackend.security;

import com.br.desafioirrahbackend.domain.AuthToken;
import com.br.desafioirrahbackend.domain.Client;
import com.br.desafioirrahbackend.repository.AuthTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BearerTokenFilter extends OncePerRequestFilter {

    private final AuthTokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            authenticate(authorization.substring(7));
        }
        chain.doFilter(request, response);
    }

    private void authenticate(String rawToken) {
        try {
            AuthToken token = tokenRepository.findByIdAndRevokedFalseAndExpiresAtAfter(
                    UUID.fromString(rawToken), Instant.now()).orElse(null);
            if (token == null || !token.getClient().isActive()) {
                return;
            }
            Client client = token.getClient();
            AuthenticatedClient principal = new AuthenticatedClient(client.getId(), client.getName(), client.getRole());
            var authentication = new UsernamePasswordAuthenticationToken(principal, rawToken,
                    List.of(new SimpleGrantedAuthority("ROLE_" + client.getRole().name())));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (IllegalArgumentException ignored) {
            SecurityContextHolder.clearContext();
        }
    }
}
