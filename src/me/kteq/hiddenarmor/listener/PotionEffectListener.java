package me.kteq.hiddenarmor.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.handler.ArmorPacketHandler;
import me.kteq.hiddenarmor.manager.HiddenArmorManager;
import me.kteq.hiddenarmor.util.EventUtil;

public class PotionEffectListener implements Listener {
	HiddenArmor plugin;
	HiddenArmorManager hiddenArmorManager;

	public PotionEffectListener(HiddenArmor plugin) {
		this.plugin = plugin;
		this.hiddenArmorManager = plugin.getHiddenArmorManager();
		EventUtil.register(this, plugin);
	}

	@EventHandler
	public void onPlayerInvisibleEffect(EntityPotionEffectEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		final Player player = (Player) event.getEntity();

		new BukkitRunnable(){
			@Override
			public void run() {
				ArmorPacketHandler.getInstance().updatePlayer(player);
			}
		}.runTaskLater(plugin, 2L);
	}
}
