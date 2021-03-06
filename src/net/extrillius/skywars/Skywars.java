package net.extrillius.skywars;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Created by mattbstanciu on 3/28/16.
 */
public class Skywars extends JavaPlugin implements Listener {

    WorldGuardPlugin worldGuardPlugin = new WorldGuardPlugin();
    WorldEditPlugin worldEditPlugin = new WorldEditPlugin();
    private ConfigAccessor mapsAccessor;
    private ConfigAccessor valueAccessor; // I don't even know if this file is necessary or not.
    private List<String> valueList; // I thought of making this List<Integer> but I already checked whether or not args[1] is a number.

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        this.mapsAccessor = new ConfigAccessor(this, "maps.yml");
        this.valueAccessor = new ConfigAccessor(this, "values.yml"); // I don't think addDefault is necessary (maybe)
        // valueAccessor can possibly be replaced with a simple ArrayList, but I'm not sure.
    }

    private WorldEditPlugin getWorldEdit() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");

        if (plugin instanceof WorldEditPlugin) return (WorldEditPlugin) plugin;
        else return null;
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        if (plugin instanceof WorldGuardPlugin) return (WorldGuardPlugin) plugin;
        else return null;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use commands!");
            return false;
        }
        Player p = (Player) sender;
        Selection s = getWorldEdit().getSelection(p); // why warning
        World world = p.getWorld();
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "Cannot find world!");
        }
        if (s == null) {
            sender.sendMessage(ChatColor.RED + "You haven't defined a selection yet.");
        }
        

        if (cmd.getName().equalsIgnoreCase("create")) {
            if (args.length == 1) {
                if (!(sender.hasPermission("skywars.create"))) {
                    sender.sendMessage(ChatColor.RED + "Creating maps is an admin-only command.");
                }
                this.mapsAccessor.getConfig().set("maps." + args[0], "");
                sender.sendMessage(ChatColor.GREEN + "Map " + ChatColor.DARK_AQUA + args[0] +
                        ChatColor.GREEN + " has been added.");
                sender.sendMessage(ChatColor.GREEN + "Now define a selection and type " +
                        ChatColor.AQUA + "/island <map> <value>");
                sender.sendMessage(ChatColor.DARK_RED + "Remember to select the middle island first!");
            }
            if (!(args.length == 1)) {
                sender.sendMessage(ChatColor.GRAY + "Usage: " + ChatColor.AQUA + "/create <map>");
            }
        }

        // All islands will be randomly relative to Island 1
        if (cmd.getName().equalsIgnoreCase("island")) {
            if (!(StringUtils.isNumeric(args[1]))) { // This is optional but easier. Can be removed if it doesn't work.
                    sender.sendMessage(ChatColor.RED + "Your second argument must be a number!");
                    sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.GRAY + "/island <map> <value>");
                if (valueList.contains(args[1])) {
                    sender.sendMessage(ChatColor.RED + "You've already defined this island.");
                }
            }
            if (!(args.length == 2)) {
                sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.GRAY + "/island <map> <value>");
            }
            s = getWorldEdit().getSelection(p);
            if (s == null) {
                sender.sendMessage(ChatColor.RED + "Define a WorldEdit selection before running the /island command.");
            }
            Location minPoint = s.getMinimumPoint();
            Location maxPoint = s.getMaximumPoint();
            if (!(s instanceof CuboidSelection)) {
                sender.sendMessage(ChatColor.RED + "All you have to do is select an island as a cuboid ;)");
            }
            this.mapsAccessor.getConfig().set("maps." + args[0] + ".loc1", minPoint);
            this.mapsAccessor.getConfig().set("maps." + args[0] + ".loc2", maxPoint);
            valueList.add(args[1]);
            this.valueAccessor.getConfig().set("values." + args[0], valueList); // iffy
        }

        if (cmd.getName().equalsIgnoreCase("delete")) {
            if (!(sender.hasPermission("skywars.delete"))) {
                sender.sendMessage(ChatColor.RED + "Deleting maps is an admin-only command.");
            }
            // the actual command
        }
        return true;
    }
}
