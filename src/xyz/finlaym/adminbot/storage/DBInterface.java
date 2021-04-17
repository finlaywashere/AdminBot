package xyz.finlaym.adminbot.storage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.Bot;
import xyz.finlaym.adminbot.action.message.response.CustomResponse;
import xyz.finlaym.adminbot.action.message.swear.SwearWord;
import xyz.finlaym.adminbot.action.permission.GroupIdentifier;
import xyz.finlaym.adminbot.action.permission.Permission;
import xyz.finlaym.adminbot.action.reserve.PermissionState;
import xyz.finlaym.adminbot.action.reserve.ReservationState;
import xyz.finlaym.adminbot.storage.config.CurrencyConfig;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;
import xyz.finlaym.adminbot.storage.config.ReservationConfig;
import xyz.finlaym.adminbot.storage.config.ServerConfig;
import xyz.finlaym.adminbot.storage.config.SwearsConfig;

public class DBInterface {
	
	private static final Logger logger = LoggerFactory.getLogger(DBInterface.class);
	
	private Connection conn;
	
	private String dbName,username,password;
	
	public void init(String dbName, String username, String password) throws Exception{
		this.dbName = dbName;
		this.username = username;
		this.password = password;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbName, username, password);
		}catch(Exception e) {
			logger.error("UwU DBInterface go not brrrr", e);
			throw e;
		}
	}
	public void fixConnection() throws Exception{
		conn.close();
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbName, username, password);
	}
	public void getServerConfig(long id, ServerConfig sConfig) throws Exception{
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `server_config` WHERE `id`=\""+id+"\";");
			rs.next();
			try {
				sConfig.setFlags(rs.getLong("id"),rs.getLong("flags"));
				sConfig.setResponses(rs.getLong("id"), CustomResponse.fromString(rs.getString("customResponses")));
				sConfig.setLoggingChannel(rs.getLong("id"), rs.getLong("logging_channel"));
				sConfig.setCurrencySuffix(rs.getLong("id"), rs.getString("currency_suffix"));
			}catch(SQLException e) {
				logger.debug("SQL exception encountered, likely null row");
				logger.trace("SQL exception encountered, likely null row", e);
			}
			rs.close();
		}catch(Exception e) {
			logger.error("Error reported! Attempting to recover", e);
			fixConnection();
		}
	}
	public void saveServerConfig(long id, ServerConfig sConfig) throws Exception{
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `server_config` WHERE `id`=\""+id+"\";");
			rs.last();
			String responses = "";
			if(sConfig.getResponses(id) != null) {
				for(CustomResponse r : sConfig.getResponses(id)) {
					responses += ":"+r.toString();
				}
			}
			if(responses.length() > 0)
				responses = responses.substring(1);
			long loggingChannel = 0;
			TextChannel channel = sConfig.getLoggingChannel(id);
			if(channel != null)
				loggingChannel = channel.getIdLong();
			if(rs.getRow() == 0) {
				PreparedStatement pS = conn.prepareStatement("INSERT INTO `server_config` (`id`, `flags`, `customResponses`, `logging_channel`, `currency_suffix`) VALUES(?,?,?,?,?);");
				pS.setLong(1, id);
				pS.setLong(2, sConfig.getFlags(id));
				pS.setString(3, responses);
				pS.setLong(4, loggingChannel);
				pS.setString(5, sConfig.getCurrencySuffix(id));
				pS.executeUpdate();
			}else {
				PreparedStatement pS = conn.prepareStatement("UPDATE `server_config` SET `flags` = ?, `customResponses` = ?, `logging_channel` = ?, `currency_suffix` = ? WHERE `id` = ?");
				pS.setLong(5, id);
				pS.setLong(1, sConfig.getFlags(id));
				pS.setString(2, responses);
				pS.setLong(3, loggingChannel);
				pS.setString(4, sConfig.getCurrencySuffix(id));
				pS.executeUpdate();
			}
		}catch(Exception e) {
			logger.error("Error reported! Attempting to recover", e);
			fixConnection();
		}
	}
	public void getReservationConfig(ReservationConfig rConfig, Bot bot) throws Exception{
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `server_reservations`;");
			try {
				while(rs.next()) {
					String[] state = rs.getString("state").split("\\?");
					PermissionState[] array = new PermissionState[state.length];
					for(int i = 0; i < array.length; i++) {
						array[i] = PermissionState.fromString(state[i], bot.getJDA());
					}
					rConfig.addReservationDB(rs.getLong("gid"), rs.getLong("vid"), rs.getLong("cid"), array);
				}
			}catch(SQLException e) {
				logger.debug("SQL exception encountered, likely null row");
				logger.trace("SQL exception encountered, likely null row", e);
			}
			rs.close();
		}catch(Exception e) {
			logger.error("Error reported! Attempting to recover", e);
			fixConnection();
		}
	}
	public void deleteReservations() throws Exception{
		try {
			Statement statement = conn.createStatement();
			statement.executeUpdate("DELETE FROM `server_reservations` WHERE 1;"); // Delete all reservations
		}catch(Exception e) {
			logger.error("Error reported! Attempting to recover", e);
			fixConnection();
		}
	}
	public void removeReservation(long gid, long vid) throws Exception{
		try {
			PreparedStatement statement = conn.prepareStatement("DELETE FROM `server_reservations` WHERE `gid`=? AND `vid`=?;");
			statement.setLong(1, gid);
			statement.setLong(2, vid);
			statement.executeUpdate();
		}catch(Exception e) {
			logger.error("Error reported! Attempting to recover", e);
			fixConnection();
		}
	}
	public void addReservation(ReservationState state) throws Exception{
		try {
			PreparedStatement statement = conn.prepareStatement("INSERT INTO `server_reservations` (`gid`, `vid`, `cid`, `state`) VALUES (?,?,?,?);");
			statement.setLong(1, state.getGid());
			statement.setLong(2, state.getVid());
			statement.setLong(3, state.getCid());
			String string = "";
			for(int i = 0; i < state.getState().length; i++) {
				if(string.length() == 0) {
					string += state.getState()[i].toString();
				}else {
					string += "?"+state.getState()[i].toString();
				}
			}
			statement.setString(4, string);
			statement.executeUpdate();
		}catch(Exception e) {
			logger.error("Error reported! Attempting to recover", e);
			fixConnection();
		}
	}
	public void getSwears(long id, SwearsConfig sConf) throws Exception{
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `server_swears` WHERE `id`=\""+id+"\";");
			try {
				while(rs.next()) {
					sConf.addSwear(SwearWord.fromString(rs.getString("word")), rs.getLong("id"));
				}
			}catch(SQLException e) {
				logger.debug("SQL exception encountered, likely null row");
				logger.trace("SQL exception encountered, likely null row", e);
			}
			rs.close();
		}catch(Exception e) {
			logger.error("Error reported! Attempting to recover", e);
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
			logger.error("Error reported! Attempting to recover", e);
			fixConnection();
		}
	}
	public void loadCurrency(long gid, long id, CurrencyConfig uConf) throws Exception{
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `user_currency` WHERE `id`=\""+id+"\" AND `gid`=\""+gid+"\";");
			try {
				rs.next();
			}catch(SQLException e) {
				logger.debug("SQL exception encountered, likely null row");
				logger.trace("SQL exception encountered, likely null row", e);
			}
			uConf.setCurrency(rs.getLong("gid"),rs.getLong("id"), rs.getInt("amount"));
		}catch(Exception e) {
			logger.error("Error reported! Attempting to recover", e);
			fixConnection();
		}
	}
	public void saveCurrency(long gid, long id, CurrencyConfig uConf) throws Exception{
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `user_currency` WHERE `id`=\""+id+"\" AND `gid`=\""+gid+"\";");
			rs.last();
			if(rs.getRow() == 0) {
				PreparedStatement pS = conn.prepareStatement("INSERT INTO `user_currency` (`gid`, `id`, `amount`) VALUES(?, ?, ?);");
				pS.setLong(1, gid);
				pS.setLong(2, id);
				pS.setInt(3, uConf.getCurrency(gid,id));
				pS.executeUpdate();
			}else {
				PreparedStatement pS = conn.prepareStatement("UPDATE `user_currency` SET `amount`=? WHERE `id`=? AND `gid`=?;");
				pS.setLong(2, id);
				pS.setLong(3, gid);
				pS.setInt(1, uConf.getCurrency(gid,id));
				pS.executeUpdate();
			}
		}catch(Exception e) {
			logger.error("Error reported! Attempting to recover", e);
			fixConnection();
		}
	}
	
	public void loadGroupPermissions(long gid, GroupIdentifier group, PermissionsConfig pConfig) throws Exception{
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `group_perms` WHERE `identifier`=\""+group.getIdentifier()+"\" AND `type`=\""+group.getType()+"\" AND `gid`=\""+gid+"\";");
			try {
				rs.next();
			}catch(SQLException e) {
				logger.debug("SQL exception encountered, likely null row");
				logger.trace("SQL exception encountered, likely null row", e);
			}
			List<Permission> perms = new ArrayList<Permission>();
			try {
				String permissions = rs.getString("permissions");
				if(permissions != null) {
					for(String s : permissions.split(":")) {
						perms.add(new Permission(s));
					}
				}
			}catch (SQLException e) {
				logger.debug("SQL exception encountered, likely null row");
				logger.trace("SQL exception encountered, likely null row", e);
				return;
			}catch(Exception e) {
				logger.error("Database error", e);
				return;
			}
			pConfig.setGroupPerms(rs.getLong("gid"), new GroupIdentifier(rs.getInt("type"), rs.getLong("identifier")), perms);
		}catch(Exception e) {
			logger.error("Error reported! Attempting to recover", e);
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
			logger.error("Error reported! Attempting to recover", e);
			fixConnection();
		}
	}
	public void logError(long gid, Throwable trace, String extra) {
		try {
			Date date = new Date(System.currentTimeMillis());
			PreparedStatement statement = conn.prepareStatement("INSERT INTO `error_logging` (`gid`, `date`, `trace`, `extra`) VALUES (?,?,?,?);");
			statement.setLong(1, gid);
			statement.setDate(2, date);
			String message = "";
			if(trace != null)
				message = trace.getMessage();
			statement.setString(3, message);
			statement.setString(4, extra);
			statement.executeUpdate();
		}catch(Exception e) {
			logger.error("Error reported! Attempting to recover", e);
			try {
				fixConnection();
			}catch(Exception e1) {
				logger.error("Failed to fix database connection", e1);
			}
		}
	}
	public void logAccess(long gid, long uid, String info) {
		try {
			Date date = new Date(System.currentTimeMillis());
			PreparedStatement statement = conn.prepareStatement("INSERT INTO `access_logging` (`gid`, `date`, `uid`, `info`) VALUES (?,?,?,?);");
			statement.setLong(1, gid);
			statement.setDate(2, date);
			statement.setLong(3, uid);
			statement.setString(4, info);
			statement.executeUpdate();
		}catch(Exception e) {
			logger.error("Error reported! Attempting to recover", e);
			try {
				fixConnection();
			}catch(Exception e1) {
				logger.error("Failed to fix database connection", e1);
			}
		}
	}
}
