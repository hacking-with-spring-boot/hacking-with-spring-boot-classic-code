/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.greglturnquist.hackingspringboot.classic;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	// end::code[]

	@Bean
	public UserDetailsService userDetailsService(UserRepository repository) { // <1>
		return username -> repository.findByName(username) // <2>
				.map(user -> User.withDefaultPasswordEncoder() // <3>
						.username(user.getName()) //
						.password(user.getPassword()) //
						.authorities(user.getRoles().toArray(new String[0])) //
						.build()) // <4>
				.orElseThrow(() -> new UsernameNotFoundException("No user named " + username));
	}

	// tag::custom-policy[]
	static final String USER = "USER";
	static final String INVENTORY = "INVENTORY";

	@Override
	protected void configure(HttpSecurity http) throws Exception { // <1>
		http //
				.authorizeRequests() //
				.mvcMatchers(HttpMethod.POST, "/").hasRole(INVENTORY) // <2>
				.mvcMatchers(HttpMethod.DELETE, "/**").hasRole(INVENTORY) //
				.anyRequest().authenticated() // <3>
				.and() //
				.httpBasic() // <4>
				.and() //
				.formLogin() // <5>
				.and() //
				.csrf().disable();
	}
	// end::custom-policy[]

	// tag::users[]
	static String role(String auth) {
		return "ROLE_" + auth;
	}

	@Bean
	CommandLineRunner userLoader(UserRepository repository) {
		return args -> {
			repository.save(new com.greglturnquist.hackingspringboot.classic.User( //
					"greg", "password", Arrays.asList(role(USER))));

			repository.save(new com.greglturnquist.hackingspringboot.classic.User( //
					"manager", "password", Arrays.asList(role(USER), role(INVENTORY))));
		};
	}
	// end::users[]
}
