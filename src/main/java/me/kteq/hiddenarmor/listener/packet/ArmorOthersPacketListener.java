package me.kteq.hiddenarmor.listener.packet;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.manager.HiddenArmorManager;
import me.kteq.hiddenarmor.util.ItemUtil;
import me.kteq.hiddenarmor.util.ProtocolUtil;

public class ArmorOthersPacketListener {
	private final FileConfiguration config;
	private final HiddenArmorManager hiddenArmorManager;

	public ArmorOthersPacketListener(HiddenArmor plugin, ProtocolManager manager) {
		this.config = plugin.getConfig();
		this.hiddenArmorManager = plugin.getHiddenArmorManager();
		manager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				final PacketContainer packet = event.getPacket();
				final Player player = event.getPlayer();

				final LivingEntity livingEntity = (LivingEntity) manager.getEntityFromID(player.getWorld(), packet.getIntegers().read(0));
				if(!(livingEntity instanceof Player)) return;
				final Player packetPlayer = (Player) livingEntity;

				if(!hiddenArmorManager.isArmorHidden(packetPlayer)) return;

				final List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairList = packet.getSlotStackPairLists().read(0);

				pairList.stream().filter(ProtocolUtil::isArmorSlot).forEach(slotPair -> {
					if(slotPair.getSecond().getType().equals(Material.ELYTRA) && ((packetPlayer.isGliding() || config.getBoolean("ignore.elytra")) && !packetPlayer.isInvisible())){
						slotPair.setSecond(new ItemStack(Material.ELYTRA));
					}
					else if(!ignore(slotPair.getSecond()))
						slotPair.setSecond(new ItemStack(Material.AIR));
				});
				packet.getSlotStackPairLists().write(0, pairList);
			}
		});
	}

	private boolean ignore(ItemStack itemStack) {
		return (config.getBoolean("ignore.leather-armor") && itemStack.getType().toString().startsWith("LEATHER")) ||
				(config.getBoolean("ignore.turtle-helmet") && itemStack.getType().equals(Material.TURTLE_HELMET)) ||
				(!ItemUtil.isArmor(itemStack) && !itemStack.getType().equals(Material.ELYTRA)) ||
				(itemStack.getType().equals(Material.ELYTRA) && config.getBoolean("ignore.elytra"));
	}
}
