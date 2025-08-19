package org.example.commentmodel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CommentModelApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommentModelApplication.class, args);
	}

}
