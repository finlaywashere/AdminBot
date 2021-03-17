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

import xyz.finlaym.adminbot.action.message.response.CustomResponse;
import xyz.finlaym.adminbot.action.message.swear.SwearWord;
import xyz.finlaym.adminbot.action.permission.GroupIdentifier;
import xyz.finlaym.adminbot.action.permission.Permission;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;
import xyz.finlaym.adminbot.storage.config.ServerConfig;
import xyz.finlaym.adminbot.storage.config.SwearsConfig;
import xyz.finlaym.adminbot.storage.config.UserLevelConfig;

public class DBInterface {
	
	private static final Logger logger = LoggerFactory.getLogger(DBInterface.class);
	
	private Connection conn;
	
	private String dbName,username,password;
	
	public void init(String dbName, String username, String password) {
		this.dbName = dbName;
		this.username = username;
		this.password = password;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbName, username, password);
		}catch(Exception e) {
			logger.error("UwU DBInterface go not brrrr", e.getCause());
		}
	}
	public void fixConnection() throws Exception{
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbName, username, password);
	}
	public void getServerConfig(long id, ServerConfig sConfig) throws Exception{
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `server_config` WHERE `id`=\""+id+"\";");
			rs.next();
			if(rs.wasNull())
				return;
			sConfig.setLevelsEnabled(rs.getLong("id"),rs.getBoolean("levelsEnabled"));
			sConfig.setResponses(rs.getLong("id"), CustomResponse.fromString(rs.getString("customResponses")));
			rs.close();
		}catch(Exception e) {
			System.err.println("Error reported! Attempting to recover");
			e.printStackTrace();
			fixConnection();
		}
	}
	public void saveServerConfig(long id, ServerConfig sConfig) throws Exception{
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `server_config` WHERE `id`=\""+id+"\";");
			rs.last();
			String responses = "";
			for(CustomResponse r : sConfig.getResponses(id)) {
				responses += ":"+r.toString();
			}
			if(responses.length() > 0)
				responses = responses.substring(1);
			if(rs.getRow() == 0) {
				PreparedStatement pS = conn.prepareStatement("INSERT INTO `server_config` (`id`, `levelsEnabled`, `customResponses`) VALUES(?,?,?);");
				pS.setLong(1, id);
				pS.setBoolean(2, sConfig.getLevelsEnabled(id));
				pS.setString(3, responses);
				pS.executeUpdate();
			}else {
				PreparedStatement pS = conn.prepareStatement("UPDATE `server_config` SET `levelsEnabled` = ?, `customResponses` = ? WHERE `id` = ?");
				pS.setLong(3, id);
				pS.setBoolean(1, sConfig.getLevelsEnabled(id));
				pS.setString(2, responses);
				pS.executeUpdate();
			}
		}catch(Exception e) {
			System.err.println("Error reported! Attempting to recover");
			e.printStackTrace();
			fixConnection();
		}
	}
	public void getSwears(long id, SwearsConfig sConf) throws Exception{
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `server_swears` WHERE `id`=\""+id+"\";");
			while(rs.next()) {
				sConf.addSwear(SwearWord.fromString(rs.getString("word")), rs.getLong("id"));
			}
			rs.close();
		}catch(Exception e) {
			System.err.println("Error reported! Attempting to recover");
			e.printStackTrace();
			fixConnection();
		}
	}
	public void saveSwears(long id, SwearsConfig sConf) throws Exception{
		try {
			PreparedStatement statement = conn.prepareStatement("DELETE FROM `server_swears` WHERE `id`=?;");
			statement.setLong(1, id);
			statement.executeUpdate();
			for(SwearWord s : sConf.getSwears(id)) {
				statement = conn.prepareStatement("INSERT INTO `server_swears` (`id`, `word`) VALUES (?, ?);");
				statement.setLong(1, id);
				statement.setString(2, s.toString());
				statement.executeUpdate();
			}
		}catch(Exception e) {
			System.err.println("Error reported! Attempting to recover");
			e.printStackTrace();
			fixConnection();
		}
	}
	public void loadUserLevels(long gid, long id, UserLevelConfig uConf) throws Exception{
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `user_levels` WHERE `id`=\""+id+"\" AND `gid`=\""+gid+"\";");
			rs.next();
			if(rs.isAfterLast())
				return;
			uConf.setUserLevels(rs.getLong("gid"),rs.getLong("id"), rs.getInt("level"));
		}catch(Exception e) {
			System.err.println("Error reported! Attempting to recover");
			e.printStackTrace();
			fixConnection();
		}
	}
	public void saveUserLevels(long gid, long id, UserLevelConfig uConf) throws Exception{
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `user_levels` WHERE `id`=\""+id+"\" AND `gid`=\""+gid+"\";");
			rs.last();
			if(rs.getRow() == 0) {
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
		}catch(Exception e) {
			System.err.println("Error reported! Attempting to recover");
			e.printStackTrace();
			fixConnection();
		}
	}
	
	public void loadGroupPermissions(long gid, GroupIdentifier group, PermissionsConfig pConfig) throws Exception{
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `group_perms` WHERE `identifier`=\""+group.getIdentifier()+"\" AND `type`=\""+group.getType()+"\" AND `gid`=\""+gid+"\";");
			rs.next();
			List<Permission> perms = new ArrayList<Permission>();
			try {
				for(String s : rs.getString("permissions").split(":")) {
					perms.add(new Permission(s));
				}
			}catch(Exception e) {
				e.printStackTrace();
				return;
			}
			pConfig.setGroupPerms(rs.getLong("gid"), new GroupIdentifier(rs.getInt("type"), rs.getLong("identifier")), perms);
		}catch(Exception e) {
			System.err.println("Error reported! Attempting to recover");
			e.printStackTrace();
			fixConnection();
		}
	}
	public void saveGroupPermissions(long gid, GroupIdentifier group, PermissionsConfig pConfig) throws Exception{
		try {
			String result = "";
			List<Permission> perms = pConfig.getGroupPerms(gid,group);
			if(perms == null)
				return;
			for(int i = 0; i < perms.size(); i++) {
				if(i != 0)
					result += ":"+perms.get(i).toString();
				else
					result += perms.get(i).toString();
			}
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `group_perms` WHERE `identifier`=\""+group.getIdentifier()+"\" AND `type`=\""+group.getType()+"\" AND `gid`=\""+gid+"\";");
			rs.last();
			if(rs.getRow() == 0) {
				PreparedStatement pS = conn.prepareStatement("INSERT INTO `group_perms` (`gid`, `identifier`, `type`, `permissions`) VALUES(?,?,?,?);");
				pS.setLong(2, group.getIdentifier());
				pS.setInt(3, group.getType());
				pS.setLong(1, gid);
				pS.setString(4, result);
				pS.executeUpdate();
			}else {
				PreparedStatement pS = conn.prepareStatement("UPDATE `group_perms` SET `permissions`=? WHERE `identifier`=? AND `type`=? AND `gid`=?;");
				pS.setLong(2, group.getIdentifier());
				pS.setInt(3, group.getType());
				pS.setLong(4, gid);
				pS.setString(1, result);
				pS.executeUpdate();
			}
		}catch(Exception e)  {
			System.err.println("Error reported! Attempting to recover");
			e.printStackTrace();
			fixConnection();
		}
	}
}
