package com.goit.fry.migration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class ClientServiceTest {

	private static final Logger logger = LogManager.getRootLogger();
	private Connection conn;

	@BeforeEach
	void initConnection() {

		try {
			conn = DriverManager.getConnection("jdbc:h2:mem:megasoft");
			try (Statement stmt = conn.createStatement()) {
				stmt.executeUpdate("CREATE TABLE client(\n" +
						" id INTEGER AUTO_INCREMENT PRIMARY KEY,\n" +
						" name TEXT(1000) NOT NULL,\n" +
						" CONSTRAINT client_name_min_length CHECK (char_length(name) >= 2))");
			}
		}
		catch (Exception e) {

			try {
				if (conn != null && !conn.isClosed())
					conn.close();
			}
			catch (Exception e2) {

				e.addSuppressed(e2);
			}
			conn = null;
			logger.error(e);
		}
	}

	@Test
	void createRecInEmptyTable() {

		try (ClientService srv = new ClientService(conn)) {

			assertDoesNotThrow(() -> srv.create("Simon"));
		}
		catch (Exception e) {

			logger.error(e);
		}
	}

	@Test
	void createEmptyNameThrows() {

		try (ClientService srv = new ClientService(conn)) {

			assertThrows(SQLException.class, () -> srv.create(""));
		}
		catch (Exception e) {

			logger.error(e);
		}
	}

	@Test
	void getById() {

	}

	@Test
	void setName() {

	}

	@Test
	void deleteById() {

	}

	@Test
	void listAll() {

	}

	@AfterEach
	void closeConnection() {

		try {
			if (conn != null && !conn.isClosed())
				conn.close();
		}
		catch (Exception e) {

			logger.error(e);
		}
	}
}