package me.kteq.hiddenarmor.listener;

import org.aslstd.core.OpenLib;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.handler.ArmorPacketHandler;
import me.kteq.hiddenarmor.manager.HiddenArmorManager;
import me.kteq.hiddenarmor.util.EventUtil;

public class InventoryShiftClickListener implements Listener {
	HiddenArmor plugin;
	HiddenArmorManager hiddenArmorManager;

	public InventoryShiftClickListener(HiddenArmor plugin){
		this.plugin = plugin;
		this.hiddenArmorManager = plugin.getHiddenArmorManager();
		EventUtil.register(this, plugin);
	}

	@EventHandler
	public void onShiftClickArmor(InventoryClickEvent event){
		if(!hiddenArmorManager.isArmorHidden((Player) event.getWhoClicked())) return;
		if(!(event.getClickedInventory() instanceof PlayerInventory)) return;
		if(!event.isShiftClick()) return;

		final Player player = (Player) event.getWhoClicked();
		final PlayerInventory inv = player.getInventory();
		final ItemStack armor = event.getCurrentItem();

		if(armor == null) return;

		if((armor.getType().toString().endsWith("_HELMET") && inv.getHelmet() == null) ||
				((armor.getType().toString().endsWith("_CHESTPLATE") || armor.getType().equals(Material.ELYTRA)) && inv.getChestplate() == null) ||
				(armor.getType().toString().endsWith("_LEGGINGS") && inv.getLeggings() == null) ||
				(armor.getType().toString().endsWith("_BOOTS") && inv.getBoots() == null)) {
			OpenLib.scheduler().scheduleLater(plugin, player, () -> ArmorPacketHandler.getInstance().updateSelf(player), 1L);
		}
	}
}
