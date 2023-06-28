package me.kteq.hiddenarmor.util;

import java.util.ArrayList;
import java.util.List;

import org.aslstd.api.bukkit.message.Texts;

import com.google.common.collect.ImmutableList;

public class StrUtil {

	public static List<String> color(List<String> stringList) {
		final List<String> colored = new ArrayList<>();

		ImmutableList.copyOf(stringList).forEach(s -> colored.add(Texts.c(s)));

		return colored;
	}

}
