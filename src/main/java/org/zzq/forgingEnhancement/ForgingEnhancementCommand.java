package org.zzq.forgingEnhancement;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForgingEnhancementCommand implements CommandExecutor {

    private final ForgingEnhancement plugin;

    public ForgingEnhancementCommand(ForgingEnhancement plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (sender.hasPermission("forgingenhancement.admin")) {
                    // 重载配置管理器
                    plugin.getFileManager().reloadFile();
                    // 重载插件配置
                    plugin.reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "锻造增强插件配置已重载!");
                    plugin.getLogger().info("配置已通过命令重载");
                } else {
                    sender.sendMessage(ChatColor.RED + "你没有权限执行此命令!");
                }
                break;

            case "info":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    sendPluginInfo(player);
                } else {
                    sender.sendMessage(ChatColor.RED + "只有玩家可以执行此命令!");
                }
                break;

            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== 锻造增强插件帮助 ===");
        sender.sendMessage(ChatColor.YELLOW + "使用铁砧进行锻造:");
        sender.sendMessage(ChatColor.WHITE + "1. 将需要强化的装备放在铁砧第一个格子");
        sender.sendMessage(ChatColor.WHITE + "2. 将锻造石放在第二个格子");
        sender.sendMessage(ChatColor.WHITE + "3. 即可为装备添加随机词条");

        if (sender.hasPermission("forgingenhancement.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/fe reload - 重载插件配置");
        }
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.YELLOW + "/fe info - 查看插件信息");
        }
    }

    private void sendPluginInfo(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== 锻造增强插件信息 ===");
        player.sendMessage(ChatColor.GREEN + "✓ 支持全部装备类型的基础属性");
        player.sendMessage(ChatColor.GREEN + "✓ 属性按原装备槽位生效");
        player.sendMessage(ChatColor.GREEN + "✓ 支持6种品质等级");
        player.sendMessage(ChatColor.GREEN + "✓ 可配置的属性池和数值范围");

        // 显示可强化的装备类型数量
        int equipmentTypes = plugin.getFileManager().getConfigManager().getEnhanceableEquipmentSuffixes().size();
        player.sendMessage(ChatColor.AQUA + "可强化装备类型: " + equipmentTypes + "种");

        // 显示支持的属性数量
        int attributeCount = plugin.getFileManager().getConfigManager().getAllAttributes().size();
        player.sendMessage(ChatColor.AQUA + "支持属性数量: " + attributeCount + "种");
    }
}