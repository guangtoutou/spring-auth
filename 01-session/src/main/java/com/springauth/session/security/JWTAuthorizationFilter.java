package com.springauth.session.security;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.springauth.session.security.SecurityConstants.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
	private AuthenticationManager authenticationManager;


	public JWTAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
		this.authenticationManager = authManager;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req,
									HttpServletResponse res,
									FilterChain chain) throws IOException, ServletException {
		String header = req.getHeader(HEADER_STRING);

		if (header == null || !header.startsWith(TOKEN_PREFIX)) {
			authenticate(req,res);

			chain.doFilter(req, res);
			return;
		}

		UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(req, res);
	}

	@Override
	protected void onSuccessfulAuthentication(HttpServletRequest request,
											  HttpServletResponse response, Authentication authResult) throws IOException {
		String token = Jwts.builder()
				.setSubject(((User) authResult.getPrincipal()).getUsername())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
				.compact();
		response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
		if (token != null) {
			// parse the token.
			String user = Jwts.parser()
					.setSigningKey(SECRET.getBytes())
					.parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
					.getBody()
					.getSubject();

			if (user != null) {
				return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
			}
			return null;
		}
		return null;
	}

	private void authenticate(HttpServletRequest request,
							  HttpServletResponse response){
		try {
			ApplicationUser creds = new ObjectMapper()
					.readValue(request.getInputStream(), ApplicationUser.class);


				UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
						creds.getUsername(), creds.getPassword());

				Authentication authResult = this.authenticationManager
						.authenticate(authRequest);


				SecurityContextHolder.getContext().setAuthentication(authResult);

				onSuccessfulAuthentication(request, response, authResult);
		}
		catch (AuthenticationException failed) {
			SecurityContextHolder.clearContext();

			return;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
