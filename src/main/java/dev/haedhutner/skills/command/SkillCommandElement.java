package dev.haedhutner.skills.command;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import dev.haedhutner.skills.api.skill.Castable;
import dev.haedhutner.skills.service.SkillService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillCommandElement extends CommandElement {

    private static final TextTemplate exception = TextTemplate.of("No skill called ", TextTemplate.arg("skill"), " found.");

    @Inject
    SkillService skillService;

    public SkillCommandElement(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Castable parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String skill = args.next();

        if (skill.isEmpty()) {
            throw exception(skill, args);
        }

        return skillService.getSkillById(skill.toLowerCase()).orElseThrow(() -> exception(skill, args));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return skillService.getAllSkills().entrySet().stream()
                .filter(entry -> src.hasPermission(entry.getValue().getPermission()))
                .filter(entry -> args.nextIfPresent().map(arg -> entry.getKey().startsWith(arg.toLowerCase())).orElse(true))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private static ArgumentParseException exception(String skill, CommandArgs args) {
        return args.createError(exception.apply(ImmutableMap.of("skill", skill)).build());
    }
}
