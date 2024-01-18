package tn.monetique.cardmanagment.security.jwt;

import java.io.IOException;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AgentBankRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.MonetiqueAdminRepo;
import tn.monetique.cardmanagment.security.services.AdminDetailsServiceImpl;
import tn.monetique.cardmanagment.security.services.CustomerDetailsServiceImpl;
import tn.monetique.cardmanagment.security.services.MonetiqueadminDetailsServiceImpl;

public class  AuthTokenFilter extends OncePerRequestFilter {
  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private AdminDetailsServiceImpl userDetailsService;
  @Autowired
  private MonetiqueadminDetailsServiceImpl monetiqueadminDetailsService;
  @Autowired
  private CustomerDetailsServiceImpl customerDetailsService;
  @Autowired
  MonetiqueAdminRepo monetiqueAdminRepo;
  @Autowired
  AdminBankRepository adminBankRepository;
  @Autowired
  AgentBankRepository agentBankRepository;

  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
    try {
      String jwt = parseJwt(request);
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        String username = jwtUtils.getUserNameFromJwtToken(jwt);

        UserDetails userDetails = null;

        // Determine which service to use based on some criteria
        if (isAdminUser(username)) {
          userDetails = userDetailsService.loadUserByUsername(username);
        } else if (isMonetiqueAdminUser(username)) {
          userDetails = monetiqueadminDetailsService.loadUserByUsername(username);
        } else if (isCustomerUser(username)) {
          userDetails = customerDetailsService.loadUserByUsername(username);
        }

        if (userDetails != null) {
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                  userDetails.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7, headerAuth.length());
    }

    return null;
  }
  private boolean isAdminUser(String username) {
    return adminBankRepository.findByUsername(username).isPresent();
  }

  private boolean isMonetiqueAdminUser(String username) {
    return monetiqueAdminRepo.findByUsername(username).isPresent();
  }

  private boolean isCustomerUser(String username) {
    return agentBankRepository.findByUsername(username).isPresent();
  }
}