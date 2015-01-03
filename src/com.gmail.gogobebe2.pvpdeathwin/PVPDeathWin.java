package com.gmail.gogobebe2.pvpdeathwin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PVPDeathWin extends JavaPlugin implements Listener {
	public List<Player> redPlayers = new ArrayList<>();
	public List<Player> bluePlayers = new ArrayList<>();
	private int deathLimit;
	private Scoreboard board;
	private Score scoreRed;
	private Score scoreBlue;
	private Score scoreDLimit;
	private Objective objective;
	private ScoreboardManager manager;


	public void onEnable() {
		deathLimit = getConfig().getInt("deathLimit");
		this.saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		this.getConfig().options().copyDefaults(true);
		saveConfig();
		setupScoreBoard();
	}

	public void onDisable() {
		for (Player player : redPlayers) {
			player.kickPlayer(ChatColor.RED
					+ "Server reloading, please rejoin.");
		}
		for (Player player : bluePlayers) {
			player.kickPlayer(ChatColor.RED
					+ "Server reloading, please rejoin.");
		}
		saveConfig();
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if(label.equalsIgnoreCase("pvp")) {
			// typed /pvp
			if (sender.hasPermission("pvp.use")) {
				if (sender instanceof Player) {
					// sender is play not console
					if (!(args.length > 0)) {
						sender.sendMessage(ChatColor.GOLD + "[PVPDeathWin Help]");
						sender.sendMessage(ChatColor.RESET + "/pvp changeteam <" + ChatColor.RED + "red"
								+ ChatColor.RESET + "|" + ChatColor.BLUE
								+ "blue" + ChatColor.RESET
								+ "> <player(optional)>");
						sender.sendMessage("/pvp teams");
						sender.sendMessage("/pvp dlimit");
						sender.sendMessage("/pvp reset");
						return true;
					}

					if (args.length > 0) {
						if (args[0].equalsIgnoreCase("teams")) {
							// typed /pvp teams
							sender.sendMessage(ChatColor.RED + "RED TEAM PLAYERS:");
							for (int x = 0; x < redPlayers.size(); x++) {
								sender.sendMessage(ChatColor.RED + "   - " + redPlayers.get(x).getDisplayName());
							}
							sender.sendMessage(ChatColor.BLUE + "BLUE TEAM PLAYERS:");
							for (int x = 0; x < bluePlayers.size(); x++) {
								sender.sendMessage(ChatColor.BLUE + "   - " + bluePlayers.get(x).getDisplayName());
							}
						} else if (args[0].equalsIgnoreCase("cteam")) {
							// typed /pvp cteam
							if (args.length > 1) {
									if (args[1].equalsIgnoreCase("red")) {
										addToTeam((Player)sender, "red");
									}
									if (args[1].equalsIgnoreCase("blue")) {
										addToTeam((Player)sender, "blue");
									}
								} else {
									sender.sendMessage(ChatColor.RED
											+ "Usage: /pvp cteam <"
											+ ChatColor.RED + "red" + ChatColor.RESET + "|"
											+ ChatColor.BLUE + "blue" + ChatColor.RESET
											+ "> <player(optional)>");
									return true;
								}
							}else if (args[0].equalsIgnoreCase("dlimit")) {
							// typed /pvp dlimit
								try {
									int i = Integer.parseInt(args[1]);
									if (i > 1 && i < 99999) {
										sender.sendMessage("Death Limit set to " + i);
										getConfig().set("deathLimit", Integer.parseInt(args[1]));
										saveConfig();
										deathLimit = getConfig().getInt("deathLimit");
										setupScoreBoard();
									} else {
										sender.sendMessage("Invalid input! 2 - 99998");
									}
								}catch (Exception e){
									sender.sendMessage("Input null or not a number!");
								}
							}else if (args[0].equalsIgnoreCase("reset")) {
								// typed /pvp cteam
								getConfig().set("redTeamDeaths", 0);
								getConfig().set("blueTeamDeaths", 0);
								getConfig().set("redTeamPlayers", null);
								getConfig().set("blueTeamPlayers", null);
								saveConfig();
								setupScoreBoard();
							}
						}else {
								sender.sendMessage(ChatColor.RED
										+ "Usage: /pvp cteam <"
										+ ChatColor.RED + "red" + ChatColor.RESET + "|"
										+ ChatColor.BLUE + "blue" + ChatColor.RESET
										+ "> <player(optional)>");
								return true;
							}

						}else {
						addToTeam(getServer().getPlayer(args[2]),args[1]);
						}
					}else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
				}
		}else {
			Bukkit.getLogger().log(Level.INFO, "Console Command: pvp cteam TEAM PLAYER");
		}
		return true;
	}

	public void addToTeam(Player playerName, String team){
		switch (team) {
			case "red":
				if (bluePlayers.contains(playerName)) {
					bluePlayers.remove(playerName);
					getConfig().set("blueTeamPlayers." + playerName.getDisplayName(), null);
				}
				if (redPlayers.contains(playerName)) {
					playerName.sendMessage(playerName.getDisplayName()
							+ " is already on the "
							+ ChatColor.RED + "Red Team!");
				} else {
					redPlayers.add(playerName);
					Bukkit.broadcastMessage(playerName.getDisplayName()
							+ " added to the "
							+ ChatColor.RED + "Red Team!");

					List<String> names = getConfig().getStringList("redTeamPlayers");
					if (!names.contains(playerName.getName()))
						names.add(playerName.getName());
					getConfig().set("redTeamPlayers", names);
					saveConfig();
				}
				break;
			case "blue":
				if(redPlayers.contains(playerName)){
					redPlayers.remove(playerName);
					getConfig().set("redTeamPlayers." + playerName.getDisplayName(), null);
				}
				if(bluePlayers.contains(playerName)){
					playerName.sendMessage(playerName.getDisplayName()
							+ " is already on the "
							+ ChatColor.BLUE + "Blue Team!");
				}else {
					bluePlayers.add(playerName);
					Bukkit.broadcastMessage(playerName.getDisplayName()
							+ " added to the "
							+ ChatColor.BLUE + "Blue Team!");
				}

				List<String> names = getConfig().getStringList("blueTeamPlayers");
				if (!names.contains(playerName.getName()))
					names.add(playerName.getName());
				getConfig().set("blueTeamPlayers", names);
				saveConfig();
				break;
			default:
				playerName.sendMessage("Bad Input! Only teams are "
						+ ChatColor.RED + "Red "
						+ ChatColor.RESET + "or "
						+ ChatColor.BLUE + "Blue");
				break;
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		for (Player online : Bukkit.getOnlinePlayers()) {
			online.setScoreboard(board);

			List<String> redNames = getConfig().getStringList("redTeamPlayers");
			List<String> blueNames = getConfig().getStringList("blueTeamPlayers");
			if (redNames.contains(event.getPlayer().getName())) {
				redPlayers.remove(event.getPlayer());
				redPlayers.add(event.getPlayer());
				Bukkit.broadcastMessage(event.getPlayer().getDisplayName()
						+ " added to the "
						+ ChatColor.RED + "Red Team!");
				return;
			}else  if(blueNames.contains(event.getPlayer().getName())){
				bluePlayers.remove(event.getPlayer());
				bluePlayers.add(event.getPlayer());
				Bukkit.broadcastMessage(event.getPlayer().getDisplayName()
						+ " added to the "
						+ ChatColor.BLUE + "Blue Team!");
				return;
			}else {
				event.getPlayer().sendMessage("Please wait until you are put on a team!");
			}

		}
		return;
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		if (redPlayers.contains(event.getPlayer())) {
			redPlayers.remove(event.getPlayer());
			Bukkit.getLogger()
					.log(Level.INFO,
							"[PVPDeathWin] "
									+ event.getPlayer().getName()
									+ " has been removed from red team to free up memory.");
		} else if (bluePlayers.contains(event.getPlayer())) {
			bluePlayers.remove(event.getPlayer());
			Bukkit.getLogger()
					.log(Level.INFO,
							"[PVPDeathWin] "
									+ event.getPlayer().getName()
									+ " has been removed from blue team to free up memory.");
		}

	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {

		if (redPlayers.contains(event.getEntity().getPlayer())) {
			Bukkit.getLogger().log(
					Level.INFO,
					"[PVPDeathWin] " + event.getEntity().getPlayer().getName()
							+ " has been killed on red team.");
			getConfig().set("redTeamDeaths",
					getConfig().getInt("redTeamDeaths") + 1);
			saveConfig();
			Bukkit.getLogger().log(Level.INFO, Integer.toString(getConfig().getInt("redTeamDeaths")));
			if(getConfig().getInt("redTeamDeaths") == deathLimit){
				Bukkit.broadcastMessage(ChatColor.BLUE + "Blue Team"
						+ ChatColor.RESET
						+ " has won the game!");
			}
		} else if (bluePlayers.contains(event.getEntity().getPlayer())) {
			Bukkit.getLogger().log(
					Level.INFO,
					"[PVPDeathWin] " + event.getEntity().getPlayer().getName()
							+ " Has been killed on blue team.");
			getConfig().set("blueTeamDeaths",
					getConfig().getInt("blueTeamDeaths") + 1);
			saveConfig();
			Bukkit.getLogger().log(Level.INFO, Integer.toString(getConfig().getInt("redTeamDeaths")));
			if(getConfig().getInt("blueTeamDeaths") == deathLimit){
				Bukkit.broadcastMessage(ChatColor.RED + "Red Team"
						+ ChatColor.RESET
						+ " has won the game!");
			}
		}
		setupScoreBoard();
	}
	
	@SuppressWarnings("deprecation")
	private void setupScoreBoard() {
		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		objective = board.registerNewObjective("test", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SCORE");
		scoreRed = objective.getScore(ChatColor.RED + "Red deaths:");
		scoreBlue = objective.getScore(ChatColor.BLUE + "Blue deaths:");
		scoreDLimit = objective.getScore(ChatColor.ITALIC + "Death Limit:");
		scoreRed.setScore(getConfig().getInt("redTeamDeaths"));
		scoreBlue.setScore(getConfig().getInt("blueTeamDeaths"));
		scoreDLimit.setScore(getConfig().getInt("deathLimit"));
		for (Player online : Bukkit.getOnlinePlayers()) {
			online.setScoreboard(board);
		}
	}
}
