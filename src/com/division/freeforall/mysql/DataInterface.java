package com.division.freeforall.mysql;

import java.util.ArrayList;

public interface DataInterface {

	public void createPlayerAccount(String player_name);

	public int getDeathCount(int player_id);

	public int getKillCount(int player_id);

	public int getKillStreak(int player_id);

	public int getPlayerId(String player_name);

	public ArrayList<String> getTopKills();

	public ArrayList<String> getTopStreak();

	public void incrementDeathCount(String player_name);

	public void incrementKillCount(String player_name);

	public void incrementPlayCount(String player_name);

	public void updateKillStreak(String player_name, int curKillStreak);
}
