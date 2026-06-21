package es.uji.ei1027.ovi;

import es.uji.ei1027.ovi.utils.PasswordUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class OVIApplication {

	public static void main(String[] args) {
		// Auto-configura l'aplicació
		new SpringApplicationBuilder(OVIApplication.class).run(args);
	}
}
