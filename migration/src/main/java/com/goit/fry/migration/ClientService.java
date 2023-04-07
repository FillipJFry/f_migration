package com.goit.fry.migration;

import com.goit.fry.migration.entities.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientService implements Closeable {

	private static final Logger logger = LogManager.getRootLogger();
	private final PreparedStatement stmtInsert, stmtGetMaxId;
	private final PreparedStatement stmtGetById, stmtSet;
	private final PreparedStatement stmtDelete, stmtGetAll;

	public ClientService(Connection conn) throws SQLException {

		stmtInsert = conn.prepareStatement("INSERT INTO client VALUES(?, ?)");
		stmtGetMaxId = conn.prepareStatement("SELECT MAX(id) as max_id FROM client");
		stmtGetById = conn.prepareStatement("SELECT name FROM client WHERE id = ?");
		stmtSet = conn.prepareStatement("UPDATE client SET name = ? WHERE id = ?");
		stmtDelete = conn.prepareStatement("DELETE FROM client WHERE id = ?");
		stmtGetAll = conn.prepareStatement("SELECT id, name FROM client ORDER BY 2");
	}

	long create(String name) {

		if (name == null || name.length() == 0) {
			logger.error("the client name cannot be NULL or empty");
			return -1;
		}

		int affected;
		long id;
		try {
			try(ResultSet rs = stmtGetMaxId.executeQuery()) {

				if (rs.next()) id = rs.getLong(1) + 1;
				else id = 0;
			}

			stmtInsert.setLong(1, id);
			stmtInsert.setString(2, name);
			affected = stmtInsert.executeUpdate();
		}
		catch (SQLException e) {

			logger.error(e);
			return -1;
		}

		logger.info("ClientService::create() : INSERT INTO returned " + affected);
		return id;
	}

	String getById(long id) {

		String name = null;
		try {
			stmtGetById.setLong(1, id);
			try (ResultSet rs = stmtGetById.executeQuery()) {

				name = rs.getString(1);
			}
		}
		catch (SQLException e) {

			logger.error(e);
		}

		return name;
	}

	void setName(long id, String name) {

		try {
			stmtSet.setString(1, name);
			stmtSet.setLong(2, id);
			int affected = stmtSet.executeUpdate();
			if (affected != 1)
				throw new SQLException("ClientService::setName() : UPDATE returned " + affected);
		}
		catch (SQLException e) {

			logger.error(e);
		}
	}

	void deleteById(long id) {

		try {
			stmtDelete.setLong(1, id);
			int affected = stmtDelete.executeUpdate();
			if (affected != 1)
				throw new SQLException("ClientService::deleteById() : DELETE returned " + affected);
		}
		catch (SQLException e) {

			logger.error(e);
		}
	}

	List<Client> listAll() {

		ArrayList<Client> list = new ArrayList<>();
		try (ResultSet rs = stmtGetAll.executeQuery()) {

			while (rs.next()) {
				Client client = new Client(rs.getLong(1), rs.getString(2));
				list.add(client);
			}
		}
		catch (SQLException e) {

			logger.error(e);
		}
		return list;
	}

	@Override
	public void close() throws IOException {

		try {
			stmtGetAll.close();
			stmtDelete.close();
			stmtSet.close();
			stmtGetById.close();
			stmtGetMaxId.close();
			stmtInsert.close();
		}
		catch (SQLException e) {

			logger.error(e);
		}
	}
}
