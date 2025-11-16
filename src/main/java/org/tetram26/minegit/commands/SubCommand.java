package org.tetram26.minegit.commands;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SubCommand {
    protected final Set<SubCommand> subCommands = Collections.synchronizedSet(new HashSet<>());
    protected String name;
    protected int requiredArguments;

    public SubCommand(String name, int requiredArguments) {
        this.name = name;
        this.requiredArguments = requiredArguments;
    }

    /**
     * Get the name of the subcommand
     *
     * @return The name of the subcommand
     */
    public String getName() {
        return this.name;
    }

    public void addSubCommand(SubCommand subCommand) {
        subCommands.add(subCommand);
    }

    /**
     * Try to find the corresponding called subcommand and execute corresponding code.
     *
     * @param sender The executor of this command
     * @param args   The arguments.
     */
    public void executeSubCommand(CommandSender sender, String[] args) {
        if (args.length == 0 || subCommands.isEmpty()) {
            // No further subcommands, or no args: try running this one
            if (args.length >= requiredArguments) {
                this.run(sender, args);
            } else {
                sender.sendMessage(Component.text("Invalid arguments."));
            }
            return;
        }

        // Try to find matching subcommand
        for (SubCommand sub : subCommands) {
            if (sub.getName().equalsIgnoreCase(args[0])) {
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                sub.executeSubCommand(sender, newArgs);
                return;
            }
        }

        // No subcommand matched, maybe this is a leaf?
        if (args.length >= requiredArguments) {
            this.run(sender, args);
        } else {
            sender.sendMessage(Component.text("Unknown subcommand or invalid usage."));
        }
    }

    /**
     * Run the corresponding task.
     *
     * @param sender The executor of this command
     * @param args   The arguments.
     */
    protected abstract void run(CommandSender sender, String[] args);

    protected abstract List<String> setTabCompletions();

    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return setTabCompletions(); // provide options at this level
        }

        if (subCommands.isEmpty()) {
            return Collections.emptyList(); // no further completions
        }

        for (SubCommand sub : subCommands) {
            if (sub.getName().equalsIgnoreCase(args[0])) {
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                return sub.getTabCompletions(sender, newArgs);
            }
        }

        // No exact match, suggest matching subcommand names
        return subCommands.stream()
                .map(SubCommand::getName)
                .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
    }
}
