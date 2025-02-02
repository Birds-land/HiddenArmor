package me.kteq.hiddenarmor.util;

import org.bukkit.inventory.ItemStack;

public class ItemUtil {

	public static boolean isArmor(ItemStack itemStack) {
		if (itemStack == null) return false;
		final String type = itemStack.getType().name();
		return type.endsWith("_HELMET")
				|| type.endsWith("_CHESTPLATE")
				|| type.endsWith("_LEGGINGS")
				|| type.endsWith("_BOOTS");
	}

	public static int getDurabilityPercentage(ItemStack itemStack){
		if(itemStack.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable){
			final org.bukkit.inventory.meta.Damageable meta = (org.bukkit.inventory.meta.Damageable) itemStack.getItemMeta();
			final int maxDurability = itemStack.getType().getMaxDurability();
			if(maxDurability==0) return -1;
			final int percentage = 100-((meta.getDamage()*100)/maxDurability);
			return percentage;
		}
		return -1;
	}

}
