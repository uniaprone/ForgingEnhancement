package org.zzq.forgingEnhancement.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.zzq.forgingEnhancement.ForgingEnhancement;
import org.zzq.forgingEnhancement.domain.valueobject.ForgingStone;
import org.zzq.forgingEnhancement.managers.PlayerSettingManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ForgingEnhancementCommand implements CommandExecutor, TabCompleter {

    private final ForgingEnhancement plugin;
    private final List<String> subCommands = Arrays.asList("reload", "give", "info", "toggle");
    private final List<String> qualities = Arrays.asList("BROKEN","COMMON", "UNCOMMON", "EPIC", "LEGENDARY","MYTHIC");

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
//                if (sender.hasPermission("forgingenhancement.admin")) {
//                    plugin.getFileManager().reloadFile();
//                    plugin.reloadConfig();
//                    sender.sendMessage(ChatColor.GREEN + "锻造增强插件配置已重载!");
//                    plugin.getLogger().info("配置已通过命令重载");
//                } else {
//                    sender.sendMessage(ChatColor.RED + "你没有权限执行此命令!");
//                }
                break;

            case "give":
                if (sender.hasPermission("forgingenhancement.admin")) {
                    handleGiveCommand(sender, args);
                } else {
                    sender.sendMessage(ChatColor.RED + "你没有权限执行此命令!");
                }
                break;

            case "info":
//                if (sender instanceof Player) {
//                    Player player = (Player) sender;
//                    sendPluginInfo(player);
//                } else {
//                    sender.sendMessage(ChatColor.RED + "只有玩家可以执行此命令!");
//                }
                break;

            case "toggle":
                if(sender instanceof Player){
                    handelToggleCommand(sender);
                }else {
                    sender.sendMessage(ChatColor.RED + "只有玩家可以执行此命令!");
                }
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // 第一个参数：子命令补全
            return StringUtil.copyPartialMatches(args[0], subCommands, new ArrayList<>());
        }

        switch (args[0].toLowerCase()) {
            case "give":
                return handleGiveTabComplete(sender, args);

            case "reload":
            case "info":
                // 这些命令不需要更多参数
                break;
        }

        return completions;
    }

    private List<String> handleGiveTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("forgingenhancement.admin")) {
            return completions;
        }

        switch (args.length) {
            case 2:
                // 第二个参数：玩家名补全
                return StringUtil.copyPartialMatches(args[1],
                        Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .collect(Collectors.toList()),
                        new ArrayList<>());

            case 3:
                // 第三个参数：品质补全
                List<String> extendedQualities = new ArrayList<>(qualities);
                extendedQualities.add("engravestone"); // 添加新字符串
                return StringUtil.copyPartialMatches(args[2], extendedQualities, new ArrayList<>());

            case 4:
                // 第四个参数：数量补全（提供一些常用数值）
                List<String> amounts = Arrays.asList("1", "8", "16", "32", "64");
                return StringUtil.copyPartialMatches(args[3], amounts, new ArrayList<>());
        }

        return completions;
    }


    private void handelToggleCommand(CommandSender sender){
        Player player = (Player) sender;

        boolean isEnable = plugin.getTogglePlayerSettingService().togglePlayerSetting(player);
        if(isEnable){
            sender.sendMessage(ChatColor.GREEN + "工作台锻造模式已开启");
        }else{
            sender.sendMessage(ChatColor.RED + "工作台锻造模式已关闭");
        }
    }
    private void handleGiveCommand(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "用法: /fe give <玩家> <品质> <数量>");
            sender.sendMessage(ChatColor.YELLOW + "可用品质: common, uncommon, epic, legendary");
            return;
        }

        String playerName = args[1];

        int amount;

        try {
            amount = Integer.parseInt(args[3]);
            if (amount <= 0 || amount > 64) {
                sender.sendMessage(ChatColor.RED + "数量必须在1-64之间");
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "无效的数量: " + args[3]);
            return;
        }

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "玩家 " + playerName + " 不存在或不在线!");
            return;
        }
        String quality = args[2].toUpperCase();
        // 验证品质是否有效
        if (ForgingStone.isMatchForgingStone(quality)) {

            // 创建锻造石并给予玩家
            ItemStack forgingStone = plugin.getGiveService().give(quality, amount, 1);
            target.getInventory().addItem(forgingStone);

            sender.sendMessage(ChatColor.GREEN + "已给予 " + target.getName() + " " + amount + " 个" + quality + "锻造石");
            if (!sender.equals(target)) {
                target.sendMessage(ChatColor.GREEN + "你获得了 " + amount + " 个" + quality + "锻造石");
            }
        } else if (quality.equalsIgnoreCase("engravestone")) {
            // 创建锻造石并给予玩家
            ItemStack engraveStone = plugin.getGiveService().give(quality, amount, 2);
            target.getInventory().addItem(engraveStone);

            sender.sendMessage(ChatColor.GREEN + "已给予 " + target.getName() + " " + amount + " 个" + "铭刻石");
            if (!sender.equals(target)) {
                target.sendMessage(ChatColor.GREEN + "你获得了 " + amount + " 个" + "铭刻石");
            }
        }else{
            sender.sendMessage(ChatColor.RED + "无效的品质! 可用品质: common, uncommon, epic, legendary");
        }

    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== 锻造增强插件帮助 ===");
        sender.sendMessage(ChatColor.YELLOW + "使用铁砧进行锻造:");
        sender.sendMessage(ChatColor.WHITE + "1. 将需要强化的装备放在铁砧第一个格子");
        sender.sendMessage(ChatColor.WHITE + "2. 将锻造石放在第二个格子");
        sender.sendMessage(ChatColor.WHITE + "3. 即可为装备添加随机词条");

        if (sender.hasPermission("forgingenhancement.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/fe reload - 重载插件配置");
            sender.sendMessage(ChatColor.YELLOW + "/fe give <玩家> <品质> <数量> - 给予锻造石");
        }
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.YELLOW + "/fe info - 查看插件信息");
        }
    }

//    private void sendPluginInfo(Player player) {
//        player.sendMessage(ChatColor.GOLD + "=== 锻造增强插件信息 ===");
//        player.sendMessage(ChatColor.GREEN + "✓ 支持全部装备类型的基础属性");
//        player.sendMessage(ChatColor.GREEN + "✓ 属性按原装备槽位生效");
//        player.sendMessage(ChatColor.GREEN + "✓ 支持6种品质等级");
//        player.sendMessage(ChatColor.GREEN + "✓ 可配置的属性池和数值范围");
//
//        // 显示可强化的装备类型数量
//        int equipmentTypes = plugin.getFileManager().getConfigManager().getEnhanceableEquipmentSuffixes().size();
//        player.sendMessage(ChatColor.AQUA + "可强化装备类型: " + equipmentTypes + "种");
//
//        // 显示支持的属性数量
//        int attributeCount = plugin.getFileManager().getConfigManager().getAllAttributes().size();
//        player.sendMessage(ChatColor.AQUA + "支持属性数量: " + attributeCount + "种");
//
//        // 显示可用的锻造石类型
//        player.sendMessage(ChatColor.AQUA + "锻造石类型: 普通, 优秀, 史诗, 传说");
//    }
}