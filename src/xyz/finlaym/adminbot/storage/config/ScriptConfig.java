package xyz.finlaym.adminbot.storage.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.finlaym.adminbot.action.script.Script;
import xyz.finlaym.adminbot.storage.DBInterface;

public class ScriptConfig {
	private Map<Long,List<Script>> scripts;
	private DBInterface dbInterface;
	
	public ScriptConfig(DBInterface dbInterface) {
		this.scripts = new HashMap<Long,List<Script>>();
		this.dbInterface = dbInterface;
	}
	public void saveConfig(long gid) throws Exception {
		this.dbInterface.saveScriptConfig(gid, this);
	}
	public void loadConfig(long gid) throws Exception {
		this.dbInterface.getScriptConfig(gid, this);
	}
	public List<Script> getScripts(long gid){
		return scripts.get(gid);
	}
	public void setScripts(long gid, List<Script> script) {
		this.scripts.put(gid, script);
	}
}
