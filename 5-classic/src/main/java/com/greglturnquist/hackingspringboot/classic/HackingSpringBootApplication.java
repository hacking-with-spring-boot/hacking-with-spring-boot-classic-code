package com.greglturnquist.hackingspringboot.classic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HackingSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackingSpringBootApplication.class, args);
	}

	// tag::http-trace[]
	HttpTraceRepository traceRepository() { // <2>
		return new InMemoryHttpTraceRepository(); // <3>
	}
	// end::http-trace[]
}
