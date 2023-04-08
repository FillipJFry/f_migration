package com.goit.fry.migration;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionGuard implements Closeable {

	private Connection conn;

	public TransactionGuard(Connection conn) throws SQLException {

		this.conn = conn;
		conn.setAutoCommit(false);
	}

	public void commit() throws SQLException {

		assert conn != null;
		conn.commit();
		conn.setAutoCommit(true);
		conn = null;
	}

	@Override
	public void close() throws IOException {

		try {
			if (conn != null) {
				conn.rollback();
				conn.setAutoCommit(true);
			}
		}
		catch (SQLException e) {

			throw new IOException(e);
		}
		finally {
			conn = null;
		}
	}
}
