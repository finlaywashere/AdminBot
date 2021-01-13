package xyz.finlaym.adminbot.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.swear.SwearWord;
import xyz.finlaym.adminbot.storage.config.ServerConfig;
import xyz.finlaym.adminbot.storage.config.SwearsConfig;
import xyz.finlaym.adminbot.storage.config.UserLevelConfig;

public class DBInterface {
	
	private static final Logger logger = LoggerFactory.getLogger(DBInterface.class);
	
	private Connection conn;
	public void init(String dbName, String username, String password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbName, username, password);
		}catch(Exception e) {
			logger.error("UwU DBInterface go not brrrr", e.getCause());
		}
	}
	public void getServerConfig(long id, ServerConfig sConfig) throws Exception{
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `server_config` WHERE `id`=\""+id+"\";");
		if(rs.getFetchSize() == 0)
			return;
		sConfig.setLevelsEnabled(rs.getLong("id"),rs.getBoolean("levelsEnabled"));
		rs.close();
	}
	public void saveServerConfig(long id, ServerConfig sConfig) throws Exception{
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `server_config` WHERE `id`=\""+id+"\";");
		if(rs.getFetchSize() == 0) {
			PreparedStatement pS = conn.prepareStatement("INSERT INTO `server_config` (`id`, `levelsEnabled`) VALUES(?,?);");
			pS.setLong(1, id);
			pS.setBoolean(2, sConfig.getLevelsEnabled(id));
			pS.executeUpdate();
		}else {
			PreparedStatement pS = conn.prepareStatement("UPDATE `server_config` SET `levelsEnabled` = ? WHERE `id` = ?");
			pS.setLong(2, id);
			pS.setBoolean(1, sConfig.getLevelsEnabled(id));
			pS.executeUpdate();
		}
	}
	public void getSwears(long id, SwearsConfig sConf) throws Exception{
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `server_swears` WHERE `id`=\""+id+"\";");
		while(rs.next()) {
			sConf.addSwear(SwearWord.fromString(rs.getString("word")), rs.getLong("id"));
		}
		rs.close();
	}
	public void saveSwears(long id, SwearsConfig sConf) throws Exception{
		PreparedStatement statement = conn.prepareStatement("DELETE FROM `server_swears` WHERE `id`=?;");
		statement.setLong(1, id);
		statement.executeUpdate();
		for(SwearWord s : sConf.getSwears(id)) {
			statement = conn.prepareStatement("INSERT INTO `server_swears` (`id`, `word`) VALUES (?, ?);");
			statement.setLong(1, id);
			statement.setString(2, s.toString());
			statement.executeUpdate();
		}
	}
	public void loadUserLevels(long id, UserLevelConfig uConf) throws Exception{
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `user_levels` WHERE `id`=\""+id+"\";");
		if(rs.getFetchSize() == 0)
			return;
		uConf.setUserLevels(rs.getLong("id"), rs.getInt("level"));
	}
	public void saveUserLevels(long id, UserLevelConfig uConf) throws Exception{
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `user_levels` WHERE `id`=\""+id+"\";");
		if(rs.getFetchSize() == 0) {
			PreparedStatement pS = conn.prepareStatement("INSERT INTO `user_levels` (`id`, `level`) VALUES(?, ?);");
			pS.setLong(1, id);
			pS.setInt(2, uConf.getUserLevels(id));
			pS.executeUpdate();
		}else {
			PreparedStatement pS = conn.prepareStatement("UPDATE `user_levels` SET `level`=? WHERE `id`=?;");
			pS.setLong(2, id);
			pS.setInt(1, uConf.getUserLevels(id));
			pS.executeUpdate();
		}
	}
}
