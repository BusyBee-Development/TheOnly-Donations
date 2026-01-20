package me.aglerr.donations.commands;

import com.muhammaddaffa.mdlib.commands.commands.SimpleCommandSpec;
import com.muhammaddaffa.mdlib.utils.Placeholder;
import me.aglerr.donations.DonationPlugin;
import me.aglerr.donations.managers.ProductManager;
import me.aglerr.donations.managers.QueueManager;
import me.aglerr.donations.objects.Product;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public class MainCommand implements SimpleCommandSpec {

    @Override
    public String name() {
        return "donations";
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("donation", "don");
    }

    @Override
    public String permission() {
        return "donations.admin";
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("donations.admin")) {
            DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.noPermission");
            return;
        }

        if (args.length == 0) {
            DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.help");
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "send":
                handleSendCommand(sender, args);
                break;
            case "reload":
                handleReloadCommand(sender);
                break;
            case "reset":
                handleResetCommand(sender);
                break;
            default:
                DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.help");
                break;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (!sender.hasPermission("donations.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Arrays.asList("send", "reload", "reset").stream()
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("send")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(p -> p.getName())
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("send")) {
            ProductManager productManager = DonationPlugin.getInstance().getProductManager();
            return productManager.getListOfProductName().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private void handleSendCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /donations send <player> <product>");
            return;
        }

        String playerName = args[1];
        String productName = args[2];

        // Get the product manager
        ProductManager productManager = DonationPlugin.getInstance().getProductManager();

        // Get the target player
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);

        // Get the product from the command argument
        Product product = productManager.getProduct(productName);

        // Return if there is no product with that name
        if (product == null) {
            DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.invalidProduct");
            return;
        }

        // If the product exists, add the donation to the queue
        QueueManager queueManager = DonationPlugin.getInstance().getQueueManager();
        queueManager.addQueue(target, product);

        // Send a success message
        DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.performDonation",
                new Placeholder().add("{player}", target.getName()));
    }

    private void handleReloadCommand(CommandSender sender) {
        DonationPlugin.getInstance().reloadEverything();
        DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.reload");
    }

    private void handleResetCommand(CommandSender sender) {
        DonationPlugin.getInstance().resetDonation();
        DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.reset");
    }
}
