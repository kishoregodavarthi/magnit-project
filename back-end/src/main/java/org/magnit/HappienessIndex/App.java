package org.magnit.HappienessIndex;

/**
 * Hello world!
 *
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(basePackages= {
		  "org.magnit.HappienessIndex" }) 
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
