package com.gmail.necnionch.myplugin.adhome.common;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


// version 2
public class Utils {
    public static boolean checkArgument(String[] args, String sub, int index) {
        return ((args.length > index) && (args[index].equalsIgnoreCase(sub)));
    }

    public static boolean allowedServerVersion(String... versions) {
        Matcher m = Pattern.compile("v(\\d*_\\d*)").matcher(Bukkit.getServer().getClass().getPackage().getName());
        if (m.find()) {
            return Arrays.asList(versions).contains(m.group(1).replace("_", "."));
        }
        return false;
    }


    public static List<String> generateTab(String arg, String... entries) {
        return Stream.of(entries)
                .filter(s -> s.startsWith(arg.toLowerCase()))
                .collect(Collectors.toList());
    }
}
