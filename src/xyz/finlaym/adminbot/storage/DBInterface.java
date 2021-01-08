package xyz.finlaym.adminbot.storage;

import java.sql.Connection;
import java.sql.DriverManager;
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
			statement.executeUpdate("INSERT INTO `server_config` (`id`, `levelsEnabled`) VALUES(\""+id+"\",\""+sConfig.getLevelsEnabled(id)+"\");");
		}else {
			statement.executeUpdate("UPDATE `server_config` SET `levelsEnabled`=\""+sConfig.getLevelsEnabled(id)+"\" WHERE `id`=\""+id+"\";");
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
		Statement statement = conn.createStatement();
		statement.executeUpdate("DELETE FROM `server_swears` WHERE `id`=\""+id+"\";");
		for(SwearWord s : sConf.getSwears(id)) {
			statement.executeUpdate("INSERT INTO `server_swears` (`id`, `word`) VALUES (\""+id+"\",\""+s.toString()+"\");");
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
			statement.executeUpdate("INSERT INTO `user_levels` (`id`, `level`) VALUES(\""+id+"\",\""+uConf.getUserLevels(id)+"\");");
		}else {
			statement.executeUpdate("UPDATE `user_levels` SET `level`=\""+uConf.getUserLevels(id)+"\" WHERE `id`=\""+id+"\";");
		}
	}
}
