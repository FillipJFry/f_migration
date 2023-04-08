package com.goit.fry.migration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

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

		logger.info("----createRecInEmptyTable()----");
		try (ClientService srv = new ClientService(conn)) {

			assertDoesNotThrow(() -> srv.create("Simon"));
		}
		catch (Exception e) {

			assertNull(e);
			logger.error(e);
		}
	}

	@Test
	void createEmptyNameReturnsInvalidID() {

		logger.info("----createEmptyNameReturnsInvalidID()----");
		try (ClientService srv = new ClientService(conn)) {

			assertEquals(ClientService.INVALID_ID, srv.create(""));
		}
		catch (Exception e) {

			assertNull(e);
			logger.error(e);
		}
	}

	@Test
	void createOneCharNameReturnsInvalidID() {

		logger.info("----createOneCharNameReturnsInvalidID()----");
		try (ClientService srv = new ClientService(conn)) {

			assertEquals(ClientService.INVALID_ID, srv.create("A"));
		}
		catch (Exception e) {

			assertNull(e);
			logger.error(e);
		}
	}

	@Test
	void getByCorrectId() {

		logger.info("----getByCorrectId()----");
		try (ClientService srv = new ClientService(conn)) {

			long id = srv.create("Simon");
			assertEquals("Simon", srv.getById(id));
		}
		catch (Exception e) {

			assertNull(e);
			logger.error(e);
		}
	}

	@Test
	void getByIncorrectId() {

		logger.info("----getByIncorrectId()----");
		try (ClientService srv = new ClientService(conn)) {

			assertNull(srv.getById(ClientService.INVALID_ID));
		}
		catch (Exception e) {

			assertNull(e);
			logger.error(e);
		}
	}

	@Test
	void setCorrectName() {

		logger.info("----setCorrectName()----");
		try (ClientService srv = new ClientService(conn)) {

			String name = "Simon";
			String newName = "Alex";
			long id = srv.create(name);
			assertDoesNotThrow(() -> srv.setName(id, newName));
			assertEquals(newName, srv.getById(id));
		}
		catch (Exception e) {

			assertNull(e);
			logger.error(e);
		}
	}

	@Test
	void deleteByCorrectId() {

		logger.info("----deleteByCorrectId()----");
		try (ClientService srv = new ClientService(conn)) {

			String name = "Simon";
			long id = srv.create(name);
			assertDoesNotThrow(() -> srv.deleteById(id));
			assertNull(srv.getById(id));
		}
		catch (Exception e) {

			assertNull(e);
			logger.error(e);
		}
	}

	@Test
	void testListAll() {

		logger.info("----testListAll()----");
		try (ClientService srv = new ClientService(conn)) {

			String[] names = {"Alex", "Simon", "Tom"};
			for (String name : names)
				srv.create(name);

			List<Client> clients = srv.listAll();
			assertEquals(names.length, clients.size());

			for (int i = 0; i < clients.size(); i++) {
				assertNotEquals(ClientService.INVALID_ID, clients.get(i).getId());
				assertEquals(names[i], clients.get(i).getName());
			}
		}
		catch (Exception e) {

			assertNull(e);
			logger.error(e);
		}
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