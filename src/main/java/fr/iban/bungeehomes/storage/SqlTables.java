package fr.iban.bungeehomes.storage;

import fr.iban.common.data.sql.DbAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlTables {
	
	/*
	 * Schema relationnel :
	 * 
	 * sc_warps (_idW_, name, description, datecreated, server, world x, y, z, pitch, yaw)
	 * 
	 * sc_warps_players(_uuid_, #idW)
	 * 
	 * sc_warps_rates(_#idW_, _uuid_, vote, date)
	 * 
	 * sc_warps_tags(_#idW_, _tag_)
	 */
	
	public static void createTables() {
		createTable("CREATE TABLE IF NOT EXISTS homes(" +
				"    uuid VARCHAR(36) NOT NULL," +
				"    name VARCHAR(32) NOT NULL," +
				"    server VARCHAR(50) NOT NULL," +
				"    world VARCHAR(50) NOT NULL," +
				"    x DOUBLE NOT NULL," +
				"    y DOUBLE NOT NULL," +
				"    z DOUBLE NOT NULL," +
				"    pitch FLOAT NOT NULL," +
				"    yaw FLOAT NOT NULL," +
				"    PRIMARY KEY (uuid, name)\n" +
				");"
		);
	}
	
	private static void createTable(String statement) {
		try (Connection connection = DbAccess.getDataSource().getConnection()) {
			try(PreparedStatement preparedStatemente = connection.prepareStatement(statement)){
				preparedStatemente.executeUpdate();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
