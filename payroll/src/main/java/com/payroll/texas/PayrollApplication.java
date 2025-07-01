package com.payroll.texas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class PayrollApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(PayrollApplication.class, args);
		
		// Debug: Print all controller beans
		System.out.println("=== CONTROLLER BEANS ===");
		String[] controllerBeans = context.getBeanNamesForAnnotation(org.springframework.stereotype.Controller.class);
		String[] restControllerBeans = context.getBeanNamesForAnnotation(org.springframework.web.bind.annotation.RestController.class);
		
		System.out.println("Controllers found: " + controllerBeans.length);
		for (String bean : controllerBeans) {
			System.out.println("Controller: " + bean);
		}
		
		System.out.println("RestControllers found: " + restControllerBeans.length);
		for (String bean : restControllerBeans) {
			System.out.println("RestController: " + bean);
		}
		System.out.println("=== END CONTROLLER BEANS ===");
	}

}
