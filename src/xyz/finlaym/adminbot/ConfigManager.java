package xyz.finlaym.adminbot;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConfigManager {
	private static File INFO_FILE = new File("user.info");
	
	public HashMap<Long,UserInfo> infoMap;
	public Lock mapLock;
	public ConfigManager() throws Exception {
		infoMap = new HashMap<Long,UserInfo>();
		mapLock = new ReentrantLock();
		loadInfo();
	}
	public void loadInfo() throws Exception{
		if(!INFO_FILE.exists())
			return;
		mapLock.lock();
		infoMap.clear();
		Scanner in = new Scanner(INFO_FILE);
		while(in.hasNextLine()) {
			String line = in.nextLine();
			if(line.trim().isEmpty())
				continue;
			UserInfo info = new UserInfo(line);
			infoMap.put(info.getId(), info);
		}
		in.close();
		mapLock.unlock();
	}
	public void saveInfo() throws Exception{
		mapLock.lock();
		INFO_FILE.delete();
		INFO_FILE.createNewFile();
		PrintWriter out = new PrintWriter(new FileWriter(INFO_FILE,true));
		for(long l : infoMap.keySet()) {
			out.println(infoMap.get(l).toString());
		}
		out.close();
		mapLock.unlock();
	}
}
