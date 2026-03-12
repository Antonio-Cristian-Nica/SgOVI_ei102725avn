package es.uji.ei1027.ovi;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication
public class OVIApplication implements CommandLineRunner {

	// Plantilla per a executar operacions sobre la connexió
	private JdbcTemplate jdbcTemplate;

	// Crea el jdbcTemplate a partir del DataSource que hem configurat
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}


	private static final Logger log = Logger.getLogger(OVIApplication.class.getName());

	public static void main(String[] args) {
		// Auto-configura l'aplicació
		new SpringApplicationBuilder(OVIApplication.class).run(args);
	}

	// Funció principal
	public void run(String... strings) throws Exception {
		log.info("Ací va el meu codi");

		log.info("Selecciona la nadadora Gemma Mengual");
		User n1 = jdbcTemplate.queryForObject(
				"SELECT * FROM ovi_user WHERE oviid='OVI-001'",
				new UserRowMapper());
		log.info(n1.toString());
	}
}
