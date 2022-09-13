package es.upct.cpcd.indieopen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties
public class IndieopenApplication {

	public static void main(String[] args) {
		SpringApplication.run(IndieopenApplication.class, args);
	}
}
