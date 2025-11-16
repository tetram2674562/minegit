package org.tetram26.minegit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.tetram26.minegit.commands.GitCommand;
import org.tetram26.minegit.git.GitPoule;

import java.io.IOException;
import java.net.URISyntaxException;

public final class Minegit extends JavaPlugin {

    public static Minegit getInstance() {
        return getPlugin(Minegit.class);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        // Register command
        getCommand("git").setExecutor(new GitCommand());
        getCommand("git").setTabCompleter(new GitCommand());
        // Init !
        GitPoule.init();
        // Pull the changes
        try {
            GitPoule.INSTANCE.pull();
        } catch (GitAPIException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(Minegit.getInstance(),() -> {
            try {
                if (GitPoule.INSTANCE.remoteHasChanged()) {
                    GitPoule.INSTANCE.pull();
                }
            } catch (IOException | GitAPIException | URISyntaxException e) {
                e.printStackTrace();
            }
        }, 0L, 20L * 30);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
