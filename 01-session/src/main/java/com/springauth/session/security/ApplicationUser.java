package com.springauth.session.security;

import lombok.Data;

@Data
public class ApplicationUser {
	private String username;
	private String password;

	public ApplicationUser(){}

	public ApplicationUser(String username,String password){
		this.username = username;
		this.password = password;
	}
}
