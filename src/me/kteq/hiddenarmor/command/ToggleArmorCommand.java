package me.kteq.hiddenarmor.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.handler.MessageHandler;
import me.kteq.hiddenarmor.manager.HiddenArmorManager;
import me.kteq.hiddenarmor.util.CommandUtil;

public class ToggleArmorCommand {

	HiddenArmor plugin;
	HiddenArmorManager hiddenArmorManager;

	public ToggleArmorCommand(HiddenArmor plugin){
		this.plugin = plugin;
		this.hiddenArmorManager = plugin.getHiddenArmorManager();
		final FileConfiguration config = plugin.getConfig();
		new CommandUtil(this.plugin,"togglearmor", 0,1, false, config.getBoolean("default-permissions.toggle")){

			@Override
			public void sendUsage(CommandSender sender) {
				final MessageHandler messageHandler = MessageHandler.getInstance();
				final Map<String, String> placeholderMap = new HashMap<>();
				if(sender instanceof Player) {
					final String usage = "/togglearmor" + (canUseArg(sender, "other") ? " [%player%]" : "");
					placeholderMap.put("usage", usage);
				} else {
					placeholderMap.put("usage", "/togglearmor <%player%>");
				}
				messageHandler.message(sender, "%correct-usage%", false, placeholderMap);
			}

			@Override
			public boolean onCommand(CommandSender sender, String[] arguments) {
				Player player;
				final MessageHandler messageHandler = MessageHandler.getInstance();
				if(arguments.length == 1) {
					if(!canUseArg(sender, "other") && !config.getBoolean("default-permissions.toggle-other")) return false;
					final String playerName = arguments[0];
					player = Bukkit.getPlayer(playerName);

					if(player == null){
						messageHandler.message(sender, "%player-not-found%");
						return true;
					}
				}else {
					if (sender instanceof ConsoleCommandSender) {
						messageHandler.message(sender, "%console-togglearmor-warning%");
						sendUsage(sender);
						return true;
					} else {
						player = (Player) sender;
					}
				}

				hiddenArmorManager.togglePlayer(player, true);

				if(!player.equals(sender)) {
					final Map<String, String> placeholderMap = new HashMap<>();
					placeholderMap.put("visibility", hiddenArmorManager.isEnabled(player) ? "%visibility-hidden%" : "%visibility-shown%");
					placeholderMap.put("player", player.getName());
					messageHandler.message(sender, "%armor-visibility-other%", false, placeholderMap);
				}
				return true;
			}
		}.setCPermission("toggle").setUsage("/togglearmor").setDescription("Toggle armor visibility");
	}
}
