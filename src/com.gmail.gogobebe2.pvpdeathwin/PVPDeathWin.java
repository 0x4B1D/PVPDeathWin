package com.gmail.gogobebe2.pvpdeathwin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class PVPDeathWin extends JavaPlugin implements Listener {
	public List<Player> redPlayers = new ArrayList<>();
	public List<Player> bluePlayers = new ArrayList<>();
	private Scoreboard board;
	private Score scoreRed;
	private Score scoreBlue;
	private Objective objective;
	private ScoreboardManager manager;

	public void onEnable() {
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

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if (label.equalsIgnoreCase("pvp")) {
			if (sender.hasPermission("pvp.use")) {
				if (args.length == 0) {
					// ./pvp
					sender.sendMessage(ChatColor.GOLD + "[PVPDeathWin Help]");
					sender.sendMessage(ChatColor.DARK_PURPLE
							+ "/pvp changeteam <" + ChatColor.RED + "red"
							+ ChatColor.DARK_PURPLE + "|" + ChatColor.BLUE
							+ "blue" + ChatColor.DARK_PURPLE
							+ "> <player(optional)>");
					return true;
				} else if (args[0].equalsIgnoreCase("changeteam")) {
					// ./pvp changeteam <red|blue> <player(optional)>
					if (!(args.length >= 2)) {
						sender.sendMessage(ChatColor.RED
								+ "Incorrect usage! /pvp changeteam <"
								+ ChatColor.RED + "red" + ChatColor.RED + "|"
								+ ChatColor.BLUE + "blue" + ChatColor.RED
								+ "> <player(optional)>");
						return true;

					}
					if (!(args[1].equalsIgnoreCase("red") || args[1]
							.equalsIgnoreCase("blue"))) {
						sender.sendMessage(ChatColor.RED
								+ "Incorrect usage! /pvp changeteam <"
								+ ChatColor.RED + "red" + ChatColor.RED + "|"
								+ ChatColor.BLUE + "blue" + ChatColor.RED
								+ "> <player(optional)>");
						return true;
					}

					else if (args.length == 2) {
						if (sender instanceof Player) {
							Player player = (Player) sender;
							if (args[1].equalsIgnoreCase("blue")) {
								if (redPlayers.contains(player)) {
									redPlayers.remove(player);
								}
								bluePlayers.add(player);
								player.sendMessage(ChatColor.AQUA
										+ "You have been added to "
										+ ChatColor.BLUE + "blue"
										+ ChatColor.AQUA + " team!");
								return true;
							} else if (args[1].equalsIgnoreCase("red")) {
								if (bluePlayers.contains(player)) {
									bluePlayers.remove(player);
								}

								redPlayers.add(player);
								player.sendMessage(ChatColor.AQUA
										+ "You have been added to "
										+ ChatColor.RED + "red"
										+ ChatColor.AQUA + " team!");
								return true;
							}
						} else {
							sender.sendMessage(ChatColor.RED
									+ "You must be a player to use this command on yourself. To use it on players, type /pvp changeteam <"
									+ ChatColor.RED + "red" + ChatColor.RED
									+ "|" + ChatColor.BLUE + "blue"
									+ ChatColor.RED + "> <player(optional)>");
							return true;
						}
					}

					if (args[1].equalsIgnoreCase("blue")) {
						@SuppressWarnings("deprecation")
						Player target = Bukkit.getServer().getPlayer(args[2]);
						if (target == null) {
							sender.sendMessage(ChatColor.RED
									+ "Could not find player!");
							return true;
						}
						if (redPlayers.contains(target)) {
							redPlayers.remove(target);
						}

						bluePlayers.add(target);
						sender.sendMessage(ChatColor.AQUA + ""
								+ target.getDisplayName()
								+ " has been added to " + ChatColor.BLUE
								+ "blue" + ChatColor.AQUA + " team!");
						target.sendMessage(ChatColor.AQUA
								+ "You have been added to " + ChatColor.BLUE
								+ "blue" + ChatColor.AQUA + " team!");
						return true;
					} else if (args[1].equalsIgnoreCase("red")) {
						@SuppressWarnings("deprecation")
						Player target = Bukkit.getServer().getPlayer(args[2]);
						if (target == null) {
							sender.sendMessage(ChatColor.RED
									+ "Could not find player!");
							return true;
						}

						if (bluePlayers.contains(target)) {
							bluePlayers.remove(target);
						}
						redPlayers.add(target);
						sender.sendMessage(ChatColor.AQUA + ""
								+ target.getDisplayName()
								+ " has been added to " + ChatColor.RED + "red"
								+ ChatColor.AQUA + " team!");
						target.sendMessage(ChatColor.AQUA
								+ "You have been added to " + ChatColor.RED
								+ "red" + ChatColor.AQUA + " team!");
						return true;
					}

				}
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You do not have permission to use this command!");
				return true;
			}
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		for (Player online : Bukkit.getOnlinePlayers()) {
			online.setScoreboard(board);
		}
		if (redPlayers.isEmpty()) {
			if (bluePlayers.contains(event.getPlayer())) {
				bluePlayers.remove(event.getPlayer());
			}
			redPlayers.add(event.getPlayer());
			event.getPlayer().sendMessage(
					ChatColor.AQUA + "You are in " + ChatColor.RED + "red"
							+ ChatColor.AQUA + " team!");
		} else if (bluePlayers.isEmpty()) {
			if (redPlayers.contains(event.getPlayer())) {
				redPlayers.remove(event.getPlayer());
			}
			bluePlayers.add(event.getPlayer());
			event.getPlayer().sendMessage(
					ChatColor.AQUA + "You are in " + ChatColor.BLUE + "blue"
							+ ChatColor.AQUA + " team!");
		} else {
			if (bluePlayers.size() >= redPlayers.size()) {
				if (bluePlayers.contains(event.getPlayer())) {
					bluePlayers.remove(event.getPlayer());
				}
				redPlayers.add(event.getPlayer());
				event.getPlayer().sendMessage(
						ChatColor.AQUA + "You are in " + ChatColor.RED + "red"
								+ ChatColor.AQUA + " team!");
			} else {
				if (redPlayers.contains(event.getPlayer())) {
					redPlayers.remove(event.getPlayer());
				}
				bluePlayers.add(event.getPlayer());
				event.getPlayer().sendMessage(
						ChatColor.AQUA + "You are in " + ChatColor.BLUE
								+ "blue" + ChatColor.AQUA + " team!");
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
							+ " Has been killed on red team.");
			getConfig().set("redTeamDeaths",
					getConfig().getInt("redTeamDeaths") + 1);
			saveConfig();
		} else if (bluePlayers.contains(event.getEntity().getPlayer())) {
			Bukkit.getLogger().log(
					Level.INFO,
					"[PVPDeathWin] " + event.getEntity().getPlayer().getName()
							+ " Has been killed on blue team.");
			getConfig().set("blueTeamDeaths",
					getConfig().getInt("blueTeamDeaths") + 1);
			saveConfig();

		}
		setupScoreBoard();
		
	}
	
	@SuppressWarnings("deprecation")
	private void setupScoreBoard() {
		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		objective = board.registerNewObjective("test", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective
				.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SCORE");
		scoreRed = objective.getScore(ChatColor.DARK_RED + "Red deaths:");
		scoreBlue = objective.getScore(ChatColor.BLUE + "Blue deaths:");
		scoreRed.setScore(getConfig().getInt("redTeamDeaths"));
		scoreBlue.setScore(getConfig().getInt("blueTeamDeaths"));
		for (Player online : Bukkit.getOnlinePlayers()) {
			online.setScoreboard(board);
		}
	}
}
