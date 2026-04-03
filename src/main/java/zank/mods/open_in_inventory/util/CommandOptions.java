package zank.mods.open_in_inventory.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author ZZZank
 */
public class CommandOptions {
    public final Map<String, CommandOption> byName = new TreeMap<>();
    public final Map<String, CommandOption> byShorthand = new TreeMap<>();

    public CommandOptions(CommandOption... options) {
        for (var option : options) {
            add(option);
        }
    }

    public CommandOption add(CommandOption option) {
        byName.put(option.name, option);
        if (option.hasShorthand()) {
            byShorthand.put(option.shorthand, option);
        }
        return option;
    }

    public CommandOption add(String name) {
        return add(new CommandOption(name));
    }

    public CommandOption add(String name, String shorthand) {
        return add(new CommandOption(name, shorthand));
    }

    public Set<CommandOption> parse(String args) {
        var result = new HashSet<CommandOption>();
        for (var arg : args.split(" ")) {
            CommandOption option;
            if (arg.startsWith("--")) {
                // long option
                option = byName.get(arg.substring("--".length()));
            } else if (arg.startsWith("-")) {
                // short option
                option = byShorthand.get(arg.substring("-".length()));
            } else {
                option = null;
            }
            if (option != null) {
                result.add(option);
            }
        }
        return result;
    }

    public Collection<CommandOption> suggestNext(String args) {
        var parsed = parse(args);

        var result = new ArrayList<CommandOption>();
        for (var option : byName.values()) {
            if (!parsed.contains(option)) {
                result.add(option);
            }
        }
        return result;
    }

    public record CommandOption(String name, String shorthand) implements Comparable<CommandOption> {
        public CommandOption(String name) {
            this(name, null);
        }

        public CommandOption {
            Objects.requireNonNull(name, "String name == null");
        }

        public boolean hasShorthand() {
            return shorthand != null;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public int compareTo(@NotNull CommandOptions.CommandOption o) {
            return name.compareTo(o.name);
        }
    }
}
