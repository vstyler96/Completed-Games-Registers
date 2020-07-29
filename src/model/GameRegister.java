package model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import util.Path;

/*
 * NOTA: Los JOptionPane y algunas las clausulas "try" con excepciones cachadas estaran en el controlador
 * Por ahora los dejare aqui (comentados) hasta que finalmente los mueva
 * */


public class GameRegister{
	private ArrayList<GameStat> gameStats;
	private boolean changeMade;
	
	/* Obviously it's necessary, but most importantly because if there wasn't a "savefile" to load, it will generate 
	 * a new an fresh-oh-yezz array list.
	 * */
	public GameRegister(){
		gameStats = new ArrayList<>();
		changeMade = false;
	}
	
	private ArrayList<HashMap<String,Object>> toHashMapArray(ArrayList<GameStat> stats){
		ArrayList<HashMap<String,Object>> array = new ArrayList<>();

		for(GameStat gs: stats)
			array.add(new HashMap<String,Object>(gs.exportStat().toMap()));

		return array;
	}

	private ArrayList<GameStat> fromHashMapArray(ArrayList<HashMap<String,Object>> array){
		ArrayList<GameStat> stats = new ArrayList<>();

		for(HashMap<String,Object> hashMap: array)
			stats.add(new GameStat(new JSONObject(hashMap)));

		return stats;
	}

	public void saveGameStats() throws IOException {
		Path.resolve(Path.dataPath);
		FileOutputStream f = new FileOutputStream(Path.saveFile);
		ObjectOutputStream o = new ObjectOutputStream(f);
		o.writeObject(toHashMapArray(gameStats));
		o.close();
		f.close();

		changeMade = true;
	}
	
	@SuppressWarnings("unchecked")
	public void loadGameStats() throws ClassNotFoundException, IOException {
		Path.resolve(Path.dataPath);
		FileInputStream f = new FileInputStream(Path.saveFile);
		ObjectInputStream o = new ObjectInputStream(f);
		gameStats = fromHashMapArray((ArrayList<HashMap<String,Object>>)o.readObject());
		o.close();
		f.close();
	}
	
	public String doBackup() throws IOException {
		Path.resolve(Path.backupPath);
		String fileName = Path.backupPath+"backup-"+(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")).format(new Date())+".dat";
		FileOutputStream f = new FileOutputStream(fileName);
		ObjectOutputStream o = new ObjectOutputStream(f);
		o.writeObject(gameStats);
		o.close();
		f.close();

		return fileName;
	}

	public String exportStats() throws IOException {
		Path.resolve(Path.exportPath);
		String filename = Path.exportPath+"export-"+(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")).format(new Date())+".json";

		JSONArray array = new JSONArray();
		for(GameStat gs: gameStats)
			array.put(gs.exportStat());
		JSONObject json = new JSONObject();
		json.put("registers",array);

		FileWriter f = new FileWriter(filename);
		f.append(json.toString());
		f.close();

		return filename;
	}
	
	public boolean addGameStat(GameStat gs){
		int iterator = 0;
		for(iterator = 0; iterator < gameStats.size(); iterator++){ // Add elements sorted by their title
			if(gs.getGame().compareToIgnoreCase(gameStats.get(iterator).getGame()) > 0) continue;
			if(gs.getGame().compareToIgnoreCase(gameStats.get(iterator).getGame()) == 0) return false;
			break;
		}
		gameStats.add(iterator, gs);

		return true;
	}

	public GameStat getGameStat(String name){
		for(GameStat gs: gameStats){
			if(gs.getGame().compareToIgnoreCase(name) == 0) return gs;
		}
		return null;
	}
	
	public boolean removeGameStat(GameStat gs){
		if(gs != null)
			return gameStats.remove(gs);

		return false;
	}
	
	public boolean changesMade(){
		return changeMade;
	}
	
	public ArrayList<GameStat> getGameStats(){return gameStats;}
	public ArrayList<GameStat> getGameStatsOccurrences(String title){
		ArrayList<GameStat> gss = new ArrayList<>();
		for(GameStat gs: gameStats)
			if(gs.getGame().toLowerCase().contains(title.toLowerCase())) gss.add(gs);
		return gss;
	}
	
	public void setGameStats(ArrayList<GameStat> gs){gameStats = gs;}
}
