package com.goit.fry.migration;

import org.flywaydb.core.Flyway;

public class FlywayMigration {

	public static void main(String[] args) {

		Flyway flyway = Flyway.configure()
				.dataSource("jdbc:h2:./megasoft", "sa", "")
				.baselineOnMigrate(true)
				.table("flyway_schema_history")
				.locations("db/migration")
				.load();

		flyway.migrate();
	}
}
