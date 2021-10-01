package com.gmail.necnionch.myplugin.adhome.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class MyHome {
    private String name;
    private Double x;
    private Double y;
    private Double z;
    private Float yaw;
    private String worldName;

    public MyHome(String name, Location loc) {
        if ((name != null) && (name.isEmpty())) name = null;
        this.name = name;
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        //noinspection ConstantConditions
        this.worldName = loc.getWorld().getName();
    }

    public MyHome(String name, Double x, Double y, Double z, Float yaw, String worldName) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.worldName = worldName;
    }

    public String serialize() {
        String x = String.format("%.3f", this.x);
        String y = String.format("%.3f", this.y);
        String z = String.format("%.3f", this.z);
        String yaw = String.format("%.3f", this.yaw);

//        String x = Double.toString(Math.floor(1000 * this.x) / 1000);
//        String y = Double.toString(Math.floor(1000 * this.y) / 1000);
//        String z = Double.toString(Math.floor(1000 * this.z) / 1000);
//        String yaw = Float.toString((float) (Math.floor(100 * this.yaw) / 100));
        return x + "," + y + "," + z + "," + yaw + "," + this.worldName;
    }

    public static MyHome deserialize(String name, String s) throws DeserializeException {
        String[] split = s.split(",", 5);
        if (split.length == 5) {
            double x, y, z;
            float yaw;

            try {
                x = Double.parseDouble(split[0]);
                y = Double.parseDouble(split[1]);
                z = Double.parseDouble(split[2]);
                yaw = Float.parseFloat(split[3]);

            } catch (NumberFormatException e) {
                throw new DeserializeException();
            }
            String worldName = split[4];
            return new MyHome(name, x, y, z, yaw, worldName);
        }
        throw new DeserializeException();
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world != null)
            return new Location(world, x, y, z, yaw, 0);
        return null;
    }

    public String getName() {
        return this.name;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    public String getWorldName() {
        return worldName;
    }
}

class DeserializeException extends Exception {}