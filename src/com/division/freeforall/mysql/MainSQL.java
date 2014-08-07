package com.division.freeforall.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.division.freeforall.core.FreeForAll;

public class MainSQL implements DataInterface {

	protected Connection conn;

	public MainSQL(FreeForAll instance) {
	}
	/*
	 *
	 * Start MySQL set functions.
	 *
	 */
	@Override
	public void createPlayerAccount(String player_name) {
		PreparedStatement pst = null;
		int player_id = getPlayerId(player_name);
		if (player_id == 0) {
			try {
				pst = conn.prepareStatement("INSERT INTO ffa_leaderboards(player_name) VALUES (?)");
				pst.setString(1, player_name);
				pst.executeUpdate();
			} catch (SQLException ex) {
			} finally {
				if (pst != null) {
					try {
						pst.close();
					} catch (SQLException ex) {
					}
				}
			}
		}
	}

	@Override
	public int getDeathCount(int player_id) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("SELECT deaths FROM ffa_leaderboards WHERE player_id=?");
			pst.setInt(1, player_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				return rs.getInt("deaths");
			}
		} catch (SQLException ex) {
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException ex) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
				}
			}
		}
		return 0;
	}

	@Override
	public int getKillCount(int player_id) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("SELECT killcount FROM ffa_leaderboards WHERE player_id=?");
			pst.setInt(1, player_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				return rs.getInt("killcount");
			}
		} catch (SQLException ex) {
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException ex) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
				}
			}
		}
		return 0;
	}

	@Override
	public int getKillStreak(int player_id) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("SELECT killstreak FROM ffa_leaderboards WHERE player_id=?");
			pst.setInt(1, player_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				return rs.getInt("killstreak");
			}
		} catch (SQLException ex) {
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException ex) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
				}
			}
		}
		return 0;
	}

	public int getPlayCount(int player_id) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("SELECT times_played FROM ffa_leaderboards WHERE player_id=?");
			pst.setInt(1, player_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				return rs.getInt("times_played");
			}
		} catch (SQLException ex) {
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException ex) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
				}
			}
		}
		return 0;
	}

	@Override
	public int getPlayerId(String player_name) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("SELECT player_id FROM ffa_leaderboards WHERE player_name=?");
			pst.setString(1, player_name);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("player_id") != 0) {
					return rs.getInt("player_id");
				}
			}
		} catch (SQLException ex) {
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException ex) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
				}
			}
		}
		return 0;
	}

	@Override
	public ArrayList<String> getTopKills() {
		ArrayList<String> top_Players = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("SELECT player_name FROM `ffa_leaderboards` ORDER BY `killcount` DESC");
			rs = pst.executeQuery();
			int count = 0;
			while (rs.next() & count < 10) {
				if (!rs.getString("player_name").equals("")) {
					top_Players.add(rs.getString("player_name"));
				}
				count++;
			}
		} catch (SQLException ex) {
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException ex) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
				}
			}
		}
		return top_Players;
	}

	@Override
	public ArrayList<String> getTopStreak() {
		ArrayList<String> top_Players = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("SELECT player_name FROM `ffa_leaderboards` ORDER BY `killstreak` DESC");
			rs = pst.executeQuery();
			int count = 0;
			while (rs.next() & count < 10) {
				if (!rs.getString("player_name").equals("")) {
					top_Players.add(rs.getString("player_name"));
				}
				count++;
			}
		} catch (SQLException ex) {
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException ex) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
				}
			}
		}
		return top_Players;
	}
	/*
	 *
	 * End MySQL get functions.
	 *
	 */

	@Override
	public void incrementDeathCount(String player_name) {
		PreparedStatement pst = null;
		int player_id = getPlayerId(player_name);
		int deathcount = getDeathCount(player_id);
		try {
			pst = conn.prepareStatement("UPDATE ffa_leaderboards SET deaths=? WHERE player_id=?");
			pst.setInt(1, deathcount + 1/2);
			pst.setInt(2, player_id);
			pst.executeUpdate();
		} catch (SQLException ex) {
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException ex) {
				}
			}
		}
	}

	@Override
	public void incrementKillCount(String player_name) {
		PreparedStatement pst = null;
		int player_id = getPlayerId(player_name);
		int killcount = getKillCount(player_id);
		try {
			pst = conn.prepareStatement("UPDATE ffa_leaderboards SET killcount=? WHERE player_id=?");
			pst.setInt(1, killcount + 1);
			pst.setInt(2, player_id);
			pst.executeUpdate();
		} catch (SQLException ex) {
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException ex) {
				}
			}
		}
	}

	@Override
	public void incrementPlayCount(String player_name) {
		PreparedStatement pst = null;
		int player_id = getPlayerId(player_name);
		int playcount = getPlayCount(player_id);
		try {
			pst = conn.prepareStatement("UPDATE ffa_leaderboards SET times_played=? WHERE player_id=?");
			pst.setInt(1, playcount + 1);
			pst.setInt(2, player_id);
			pst.executeUpdate();
		} catch (SQLException ex) {
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException ex) {
				}
			}
		}
	}

	@Override
	public void updateKillStreak(String player_name, int curKillStreak) {
		PreparedStatement pst = null;
		int player_id = getPlayerId(player_name);
		int killstreak = getKillStreak(player_id);
		if (curKillStreak > killstreak) {
			try {
				pst = conn.prepareStatement("UPDATE ffa_leaderboards SET killstreak=? WHERE player_id=?");
				pst.setInt(1, curKillStreak);
				pst.setInt(2, player_id);
				pst.executeUpdate();
			} catch (SQLException ex) {
			} finally {
				if (pst != null) {
					try {
						pst.close();
					} catch (SQLException ex) {
					}
				}
			}
		}
	}
}
