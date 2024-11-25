package com.word.gateway.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Getter
@AllArgsConstructor
public class JwtAndRequestDetails {
    private final String jwt;
    private final WebAuthenticationDetails requestDetails ;
}
