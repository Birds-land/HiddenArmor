package me.kteq.hiddenarmor.handler;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aslstd.api.bukkit.message.Texts;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.kteq.hiddenarmor.util.ConfigUtil;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;

public class MessageHandler {
	private static MessageHandler instance;

	private Plugin plugin;
	private String defaultLocale;
	private String prefix = "";
	private Map<String, FileConfiguration> localeMap;

	public static MessageHandler getInstance() {
		if (instance == null) {
			instance = new MessageHandler();
		}
		return instance;
	}

	public void setup(Plugin plugin, String prefix) {
		this.plugin = plugin;
		setPrefix(prefix);
		reloadLocales();
	}

	public void reloadLocales() {
		setDefaultLocale(plugin.getConfig().getString("locale.default-locale", "en_us").replaceAll("-", "_"));

		final Set<String> includedLocales = new HashSet<>();
		includedLocales.add("en_us");
		includedLocales.add("pt_br");

		for (final String locale : includedLocales) {
			final String path = "locale/" + locale + ".yml";
			if (!new File(plugin.getDataFolder().getAbsolutePath() + "/" + path).exists()) {
				plugin.saveResource(path, false);
			}
		}



		localeMap = new HashMap<>();
		final File localeFolder = new File(plugin.getDataFolder().getAbsolutePath() + "/locale");
		for (final File file : localeFolder.listFiles()) {
			final FileConfiguration localeYaml = ConfigUtil.getYamlConfiguration(file);
			localeMap.put(file.getName().replaceAll(".yml", ""), localeYaml);
		}
	}

	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void message(CommandSender sender, String message) {
		message(ChatMessageType.CHAT, sender, message, false);
	}

	public void message(CommandSender sender, String message, boolean prefix) {
		message(ChatMessageType.CHAT, sender, message, prefix);
	}

	public void message(CommandSender sender, String message, boolean prefix, Map<String, String> placeholderMap) {
		message(ChatMessageType.CHAT, sender, message, prefix, placeholderMap);
	}

	public void message(ChatMessageType messageType, CommandSender sender, String message, boolean prefix) {
		message(messageType, sender, message, prefix, new HashMap<>());
	}

	public void message(ChatMessageType messageType, CommandSender sender, String message, boolean prefix, Map<String, String> placeholderMap) {
		message = replaceHoldersFromConfig(sender, message);

		for (final String placeholder : placeholderMap.keySet()) {
			String value = placeholderMap.get(placeholder);
			value = replaceHoldersFromConfig(sender, value);

			message = message.replaceAll("%" + placeholder + "%", value);
		}

		if (prefix) {
			message = this.prefix + message;
		}

		if (sender instanceof Player) {
			final Player player = (Player) sender;
			player.sendMessage(Component.text(Texts.s(message)));
		} else if (sender instanceof ConsoleCommandSender && messageType.equals(ChatMessageType.CHAT)) {
			sender.sendMessage(Texts.c(message));
		}
	}

	private String replaceHoldersFromConfig(CommandSender sender, String message) {
		for (final String string : message.split("%")) {
			if (string.contains(" ")) continue;
			final String localizedMessage = getLocalizedMessage(sender, string);

			if (localizedMessage != null) {
				message = message.replaceAll("%" + string + "%", localizedMessage);
			}
		}

		return message;
	}

	@SuppressWarnings("deprecation")
	private String getLocalizedMessage(CommandSender sender, String messageKey) {
		String locale;
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			locale = player.getLocale();
		} else {
			locale = defaultLocale;
		}
		FileConfiguration localeYaml = localeMap.get(locale);
		if (localeYaml == null) {
			localeYaml = localeMap.get(defaultLocale);
			if (localeYaml == null) {
				localeYaml = getDefaultResourceLocale();
			}
		}

		return localeYaml.getString(messageKey, getDefaultResourceLocale().getString(messageKey));
	}

	private FileConfiguration getDefaultResourceLocale() {
		final InputStream inputStream = plugin.getResource("locale/en_us.yml");
		final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		return YamlConfiguration.loadConfiguration(inputStreamReader);
	}
}
