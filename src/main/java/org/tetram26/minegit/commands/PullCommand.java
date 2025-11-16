package org.tetram26.minegit.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.tetram26.minegit.Minegit;
import org.tetram26.minegit.git.GitPoule;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class PullCommand extends SubCommand{

    public PullCommand() {
        super("pull", 0);
    }

    @Override
    protected void run(CommandSender sender, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(Minegit.getInstance(),() -> {
            try {
                GitPoule.INSTANCE.pull();
            } catch (GitAPIException | IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        Minegit.getInstance().getComponentLogger().info("Les fichiers ont bien été récupérés.");
        sender.sendMessage(Component.text("Les fichiers ont bien été récupérés.", NamedTextColor.BLUE));

        Minegit.getInstance().getComponentLogger().error("Erreur lors de la récupération des fichiers");
        sender.sendMessage(Component.text("Erreur lors de la récupération des fichiers", NamedTextColor.DARK_RED));
    }

    @Override
    protected List<String> setTabCompletions() {
        return List.of("");
    }


}
