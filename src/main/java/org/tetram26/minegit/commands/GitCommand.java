package org.tetram26.minegit.commands;

import com.google.j2objc.annotations.ObjectiveCName;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class GitCommand implements CommandExecutor, TabCompleter {

    private SubCommand rootCommand;
    public GitCommand() {
        this.rootCommand = new SubCommand("ep", 0) {
            @Override
            protected void run(CommandSender sender, String[] args) {
                if (args.length == 0) {
                    sender.sendMessage(Component.text("Usage: /git <pull> <force>"));
                } else {
                    this.executeSubCommand(sender,args);
                }

            }

            @Override
            protected List<String> setTabCompletions() {
                return List.of("pull","reload");
            }

        };
        rootCommand.addSubCommand(new PullCommand());
        rootCommand.addSubCommand(new ReloadCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        rootCommand.run(commandSender,strings);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return rootCommand.getTabCompletions(commandSender,strings);
    }
}
