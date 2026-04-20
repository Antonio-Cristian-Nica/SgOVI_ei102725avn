package es.uji.ei1027.ovi;

import java.util.logging.Logger;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class OVIApplication {
	private static final Logger log = Logger.getLogger(OVIApplication.class.getName());

	public static void main(String[] args) {
		// Auto-configura l'aplicació
		new SpringApplicationBuilder(OVIApplication.class).run(args);
	}
}
