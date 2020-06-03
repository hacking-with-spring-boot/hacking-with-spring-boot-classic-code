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
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
@EnableGlobalMethodSecurity
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
				.orElseThrow(() -> new UsernameNotFoundException("Couldn't find user " + username));
	}

	// tag::custom-policy[]

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http //
				.authorizeRequests(registry -> registry //
						.anyRequest().authenticated()) //
				.httpBasic() //
				.and() //
				.formLogin() //
				.and() //
				.csrf().disable();
	}
	// end::custom-policy[]

	// tag::users[]
	static String role(String auth) {
		return "ROLE_" + auth;
	}

	static final String USER = "USER";
	static final String INVENTORY = "INVENTORY";

	@Bean
	CommandLineRunner userLoader(MongoOperations operations) {
		return args -> {
			operations.save(new com.greglturnquist.hackingspringboot.classic.User( //
					"greg", "password", Arrays.asList(role(USER))));

			operations.save(new com.greglturnquist.hackingspringboot.classic.User( //
					"manager", "password", Arrays.asList(role(USER), role(INVENTORY))));
		};
	}
	// end::users[]
}
