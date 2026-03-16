package es.uji.ei1027.ovi;

import java.util.logging.Logger;

import es.uji.ei1027.ovi.dao.OviUserRowMapper;
import es.uji.ei1027.ovi.model.OviUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication
public class OVIApplication {
	private static final Logger log = Logger.getLogger(OVIApplication.class.getName());

	public static void main(String[] args) {
		// Auto-configura l'aplicació
		new SpringApplicationBuilder(OVIApplication.class).run(args);
	}
}
