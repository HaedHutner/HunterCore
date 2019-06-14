package com.atherys.core.command;

import com.atherys.core.command.annotation.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import static org.spongepowered.api.text.format.TextColors.*;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class CommandService {

    private static CommandService instance = new CommandService();

    public static CommandService getInstance() {
        return instance;
    }

    public <T extends CommandExecutor> void register(T executor, Object plugin)
            throws AnnotatedCommandException {
        Command command = buildCommandSpec(executor);
        Sponge.getCommandManager().register(plugin, command.getSpec(), command.getAliases());
    }

    public <T extends CommandExecutor> Command buildCommandSpec(T command)
            throws AnnotatedCommandException {

        Class<? extends CommandExecutor> commandClass = command.getClass();

        String[] aliases = getAliases(commandClass);

        CommandSpec.Builder spec = CommandSpec.builder();

        // set description
        if (commandClass.isAnnotationPresent(Description.class)) {
            String description = commandClass.getAnnotation(Description.class).value();
            spec.description(Text.of(description));
        }

        // set permission
        if (commandClass.isAnnotationPresent(Permission.class)) {
            String permission = commandClass.getAnnotation(Permission.class).value();
            spec.permission(permission);
        }

        List<Command> children = new ArrayList<>();
        // set children
        if (commandClass.isAnnotationPresent(Children.class)) {
            // if a parent command is also parameterized, throw exception
            if (command instanceof ParameterizedCommand) {
                throw AnnotatedCommandException.parentParameterizedCommand(commandClass);
            }

            Class<? extends CommandExecutor>[] childCommandClasses = commandClass
                    .getAnnotation(Children.class).value();

            for (Class<? extends CommandExecutor> child : childCommandClasses) {
                Command childSpec;
                // instantiate the child command
                try {
                    childSpec = buildCommandSpec(child.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw AnnotatedCommandException.childInstantiation(child);
                }
                spec.child(childSpec.getSpec(), childSpec.getAliases());
                children.add(childSpec);
            }
        }

        // if the command has parameters, cast it to a ParameterizedClass, get the arguments, and apply them to the spec
        if (command instanceof ParameterizedCommand) {
            ParameterizedCommand parameterizedCommand = (ParameterizedCommand) command;
            spec.arguments(parameterizedCommand.getArguments());
        }


        spec.executor(command);
        CommandSpec commandSpec = spec.build();

        Command com = new Command(commandSpec, children, aliases);

        if (commandClass.isAnnotationPresent(HelpCommand.class)) {
            CommandSpec helpCommand = createHelpCommand(
                    com,
                    commandClass.getAnnotation(HelpCommand.class));
            if (helpCommand != null) spec.child(helpCommand, "help");
            com.spec = spec.build();
        }

        return com;
    }



    private CommandSpec createHelpCommand(Command command, HelpCommand annotation) {
        CommandSpec.Builder helpSpec = CommandSpec.builder();
        if (command.children.size() == 0) return null;

        List<Text> help = command.children.stream()
                .map(child -> getHelpFor(child, annotation))
                .collect(Collectors.toList());

        PaginationList helpList = PaginationList.builder()
                .title(Text.of(GOLD, TextStyles.BOLD, annotation.title()))
                .padding(Text.of(DARK_GRAY, "="))
                .contents(help)
                .build();

        CommandExecutor helpExecutor = (src, args) -> {
            helpList.sendTo(src);
            return CommandResult.success();
        };
        helpSpec.executor(helpExecutor);
        return helpSpec.build();
    }

    private Text getHelpFor(Command command, HelpCommand annotation) {
        Text.Builder help = Text.builder();

        String base = command.getAliases()[0];
        if (!annotation.prefix().isEmpty()) {
            base = annotation.prefix();
        }

        help.append(Text.of(GOLD, "/", base, " ", command.aliases[0], " "));

        // Don't want to spam the message with a bunch of sub-commands
        if (command.getChildren().size() == 0) {
            help.append(command.getSpec().getUsage(console()));
        }

        command.getSpec().getShortDescription(console()).ifPresent(desc -> {
            help.append(Text.of(DARK_GREEN, " : ", desc));
        });
        return help.build();
    }

    private ConsoleSource console() {
        return Sponge.getServer().getConsole();
    }

    private String[] getAliases(Class<? extends CommandExecutor> commandClass)
            throws AnnotatedCommandException {
        // All command must have at least 1 non-empty alias
        if (!commandClass.isAnnotationPresent(Aliases.class)) {
            throw AnnotatedCommandException.noAliases(commandClass);
        }

        String[] aliases = commandClass.getAnnotation(Aliases.class).value();

        // check for empty aliases, they're not allowed
        for (String alias : aliases) {
            if (alias.isEmpty()) {
                throw AnnotatedCommandException.emptyAlias(commandClass);
            }
        }

        return aliases;
    }

    public static class Command {

        String[] aliases;
        CommandSpec spec;
        List<Command> children;

        Command(CommandSpec spec, List<Command> children, String... aliases) {
            this.children = children;
            this.aliases = aliases;
            this.spec = spec;
        }

        String[] getAliases() {
            return aliases;
        }

        CommandSpec getSpec() {
            return spec;
        }

        List<Command> getChildren() {
            return children;
        }
    }

    public static class AnnotatedCommandException extends Exception {

        private AnnotatedCommandException(String error) {
            super(error);
        }

        public static AnnotatedCommandException noAliases(Class<?> commandClass) {
            return new AnnotatedCommandException(
                    "The " + commandClass.getName() + " class is not annotated with Aliases.");
        }

        public static AnnotatedCommandException emptyAlias(Class<?> commandClass) {
            return new AnnotatedCommandException(
                    "The " + commandClass.getName() + " class is annotated with an empty alias.");
        }

        public static AnnotatedCommandException childInstantiation(Class<?> commandClass) {
            return new AnnotatedCommandException(
                    "Failed to instantiate the " + commandClass.getName()
                            + " class. Ensure this class has an accessible no-args constructor available.");
        }

        public static AnnotatedCommandException parentParameterizedCommand(Class<?> commandClass) {
            return new AnnotatedCommandException("The " + commandClass.getName()
                    + " command class is both a parent ( Annotated with @Children ) and a ParameterizedCommand. Parent command ought not to have parameters.");
        }
    }

}
