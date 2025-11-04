package com.codingshuttle.linkedin.config_server;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    static void main(String[] args) {
		// Load .env file for local development (optional, won't fail if file doesn't exist)
		try {
			Dotenv dotenv = Dotenv.configure()
					.ignoreIfMissing()
					.load();

			// Set environment variables from .env file
			dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
			);
		} catch (Exception e) {
			// .env file not found or couldn't be loaded, will use system environment variables
			System.out.println("No .env file found, using system environment variables");
		}

		SpringApplication.run(ConfigServerApplication.class, args);
	}

}
