package com.payroll.texas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class PayrollApplication {

	public static void main(String[] args) {
		// Load .env file for environment variables and set as system properties
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		// Log the presence of PRIVATE_KEY at startup (do not log the key itself)
		String privateKey = System.getProperty("PRIVATE_KEY");
		if (privateKey != null && !privateKey.isEmpty()) {
			System.out.println("[Startup] PRIVATE_KEY is loaded and available.");
		} else {
			System.out.println("[Startup] PRIVATE_KEY is NOT loaded.");
		}

		SpringApplication.run(PayrollApplication.class, args);
	}

}
