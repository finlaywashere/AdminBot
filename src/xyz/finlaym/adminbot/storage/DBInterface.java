package xyz.finlaym.adminbot.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.permission.Permission;
import xyz.finlaym.adminbot.action.swear.SwearWord;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;
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
	public void loadUserLevels(long gid, long id, UserLevelConfig uConf) throws Exception{
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `user_levels` WHERE `id`=\""+id+"\" AND `gid`=\""+gid+"\";");
		if(rs.getFetchSize() == 0)
			return;
		uConf.setUserLevels(rs.getLong("gid"),rs.getLong("id"), rs.getInt("level"));
	}
	public void saveUserLevels(long gid, long id, UserLevelConfig uConf) throws Exception{
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `user_levels` WHERE `id`=\""+id+"\" AND `gid`=\""+gid+"\";");
		if(rs.getFetchSize() == 0) {
			PreparedStatement pS = conn.prepareStatement("INSERT INTO `user_levels` (`gid`, `id`, `level`) VALUES(?, ?, ?);");
			pS.setLong(1, gid);
			pS.setLong(2, id);
			pS.setInt(3, uConf.getUserLevels(gid,id));
			pS.executeUpdate();
		}else {
			PreparedStatement pS = conn.prepareStatement("UPDATE `user_levels` SET `level`=? WHERE `id`=? AND `gid`=?;");
			pS.setLong(2, id);
			pS.setLong(3, gid);
			pS.setInt(1, uConf.getUserLevels(gid,id));
			pS.executeUpdate();
		}
	}

	public void loadPermissions(long gid, long id, PermissionsConfig pConfig) throws Exception{
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `user_perms` WHERE `id`=\""+id+"\" AND `gid`=\""+gid+"\";");
		if(rs.getFetchSize() == 0)
			return;
		List<Permission> perms = new ArrayList<Permission>();
		for(String s : rs.getString("permissions").split(":")) {
			perms.add(new Permission(s));
		}
		pConfig.setUserPerms(rs.getLong("gid"), rs.getLong("id"), perms);
	}
	public void savePermissions(long gid, long id, PermissionsConfig pConfig) throws Exception{
		String result = "";
		List<Permission> perms = pConfig.getUserPerms(gid,id);
		if(perms == null)
			return;
		for(int i = 0; i < perms.size(); i++) {
			if(i != 0)
				result += ":"+perms.get(i).toString();
			else
				result += perms.get(i).toString();
		}
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `user_perms` WHERE `id`=\""+id+"\" AND `gid`=\""+gid+"\";");
		if(rs.getFetchSize() == 0) {
			PreparedStatement pS = conn.prepareStatement("INSERT INTO `user_perms` (`gid`, `id`, `permissions`) VALUES(?,?, ?);");
			pS.setLong(2, id);
			pS.setLong(1, gid);
			pS.setString(3, result);
			pS.executeUpdate();
		}else {
			PreparedStatement pS = conn.prepareStatement("UPDATE `user_perms` SET `permissions`=? WHERE `id`=? AND `gid`=?;");
			pS.setLong(2, id);
			pS.setLong(3, gid);
			pS.setString(1, result);
			pS.executeUpdate();
		}
	}
}
