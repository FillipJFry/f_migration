package com.goit.fry.migration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientService implements Closeable {

	public static final int MAX_CLIENT_NAME_LEN = 60;
	public static final int INVALID_ID = -1;
	private static final Logger logger = LogManager.getRootLogger();
	private final Connection conn;
	private final PreparedStatement stmtInsert, stmtGetMaxId;
	private final PreparedStatement stmtGetById, stmtSet;
	private final PreparedStatement stmtDelete, stmtGetAll;

	public ClientService(Connection conn) throws SQLException {

		this.conn = conn;
		stmtInsert = conn.prepareStatement("INSERT INTO client VALUES(?, ?)");
		stmtGetMaxId = conn.prepareStatement("SELECT MAX(id) as max_id FROM client");
		stmtGetById = conn.prepareStatement("SELECT name FROM client WHERE id = ?");
		stmtSet = conn.prepareStatement("UPDATE client SET name = ? WHERE id = ?");
		stmtDelete = conn.prepareStatement("DELETE FROM client WHERE id = ?");
		stmtGetAll = conn.prepareStatement("SELECT id, name FROM client ORDER BY 2");
	}

	public long create(String name) {

		if (!checkName(name)) return INVALID_ID;

		int affected;
		long id;
		try {
			try (TransactionGuard guard = new TransactionGuard(conn)) {
				try (ResultSet rs = stmtGetMaxId.executeQuery()) {

					if (rs.next()) id = rs.getLong(1) + 1;
					else id = 0;
				}

				stmtInsert.setLong(1, id);
				stmtInsert.setString(2, name);
				affected = stmtInsert.executeUpdate();
				guard.commit();
			}
		}
		catch (Exception e) {

			logger.error(e);
			return INVALID_ID;
		}

		logger.info("ClientService::create(" + name + ") : INSERT INTO returned " + affected);
		return id;
	}

	public String getById(long id) {

		if (!checkId(id)) return null;

		String name = null;
		try {
			stmtGetById.setLong(1, id);
			try (ResultSet rs = stmtGetById.executeQuery()) {

				if (!rs.next())
					throw new SQLException("no such ID = " + id);

				name = rs.getString(1);
			}
		}
		catch (SQLException e) {

			logger.error("ClientService::getById(" + id + ") : " + e);
		}

		return name;
	}

	public void setName(long id, String name) {

		boolean paramsValid = checkId(id);
		paramsValid = checkName(name) && paramsValid;
		if (!paramsValid) return;

		try {
			stmtSet.setString(1, name);
			stmtSet.setLong(2, id);
			int affected = stmtSet.executeUpdate();
			if (affected != 1)
				throw new SQLException("ClientService::setName(" + id +", " + name +
										") : UPDATE returned " + affected);
		}
		catch (SQLException e) {

			logger.error(e);
		}
	}

	public void deleteById(long id) {

		if (!checkId(id)) return;

		try {
			stmtDelete.setLong(1, id);
			int affected = stmtDelete.executeUpdate();
			if (affected != 1)
				throw new SQLException("ClientService::deleteById(" + id +
										") : DELETE returned " + affected);
		}
		catch (SQLException e) {

			logger.error(e);
		}
	}

	public List<Client> listAll() {

		ArrayList<Client> list = new ArrayList<>();
		try (ResultSet rs = stmtGetAll.executeQuery()) {

			while (rs.next()) {
				Client client = new Client(rs.getLong(1), rs.getString(2));
				list.add(client);
			}
		}
		catch (SQLException e) {

			logger.error("ClientService::listAll() :" + e);
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

	private boolean checkId(long id) {

		if (id < 0) {
			logger.error("the ID is incorrect: " + id);
			return false;
		}
		return true;
	}

	private boolean checkName(String name) {

		if (name == null || name.length() == 0) {
			logger.error("the client name cannot be NULL or empty");
			return false;
		}

		if (name.length() > MAX_CLIENT_NAME_LEN) {
			logger.error("the client name is too long: " + name);
			return false;
		}
		return true;
	}
}
