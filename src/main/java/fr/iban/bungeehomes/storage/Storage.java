package fr.iban.bungeehomes.storage;

import fr.iban.bungeehomes.Home;
import fr.iban.common.data.sql.DbAccess;
import fr.iban.common.teleport.SLocation;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Storage {
	
	private DataSource ds = DbAccess.getDataSource();

	public Storage(){
		SqlTables.createTables();
	}

	public List<Home> getHomes(UUID uuid){
		long now = System.currentTimeMillis();
		List<Home> homes = new ArrayList<>();
		String sql = "SELECT * FROM homes WHERE uuid LIKE ?;";
		try(Connection connection = ds.getConnection()){
			try(PreparedStatement ps = connection.prepareStatement(sql)){
				ps.setString(1, uuid.toString());
				try(ResultSet rs = ps.executeQuery()){
					while(rs.next()) {
						//location
						String server = rs.getString("server");
						String world = rs.getString("world");
						double x = rs.getDouble("x");
						double y = rs.getDouble("y");
						double z = rs.getDouble("z");
						float pitch = rs.getFloat("pitch");
						float yaw = rs.getFloat("yaw");
						String name = rs.getString("name");

						homes.add(new Home(name, new SLocation(server, world, x, y, z, pitch, yaw)));
					}
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return homes;
	}
	public void addHome(UUID uuid, Home home){
		String sql = "INSERT INTO homes (uuid, name, server, world, x, y, z, pitch, yaw) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";
		try(Connection connection = ds.getConnection()){
			try(PreparedStatement ps = connection.prepareStatement(sql)){
				ps.setString(1, uuid.toString());
				ps.setString(2, home.getName());
				SLocation sloc = home.getSLocation();
				ps.setString(3, sloc.getServer());
				ps.setString(4, sloc.getWorld());
				ps.setDouble(5, sloc.getX());
				ps.setDouble(6, sloc.getY());
				ps.setDouble(7, sloc.getZ());
				ps.setFloat(8, sloc.getPitch());
				ps.setFloat(9, sloc.getYaw());
				ps.executeUpdate();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleleHome(UUID uuid, String name){
		String sql = "DELETE FROM homes WHERE uuid LIKE ? AND name LIKE ?;";
		try (Connection connection = ds.getConnection()) {
			try(PreparedStatement ps = connection.prepareStatement(sql)){
				ps.setString(1, uuid.toString());
				ps.setString(2, name);
				ps.executeUpdate();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
