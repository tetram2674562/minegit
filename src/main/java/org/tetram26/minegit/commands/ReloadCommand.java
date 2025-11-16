package org.tetram26.minegit.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.tetram26.minegit.Minegit;
import org.tetram26.minegit.git.GitPoule;

import java.util.List;

public class ReloadCommand extends SubCommand{
    public ReloadCommand() {
        super("reload", 0 );
    }

    @Override
    protected void run(CommandSender sender, String[] args) {
        GitPoule.INSTANCE.reload(Minegit.getInstance());
        sender.sendMessage(Component.text("Configuration rechargée", NamedTextColor.GREEN));
    }

    @Override
    protected List<String> setTabCompletions() {
        return List.of();
    }
}
