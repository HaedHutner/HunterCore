package dev.haedhutner.skills.api.skill;

import org.spongepowered.api.text.Text;

import java.util.List;

public class CastResult {

    private final Text message;

    private CastResult(Text message) {
        this.message = message;
    }

    public static CastResult empty() {
        return new CastResult(Text.EMPTY);
    }

    public static CastResult custom(Text text) {
        return new CastResult(text);
    }

    public static CastResult success() {
        return new CastResult(Text.EMPTY);
    }

    public static CastResult concat(List<CastResult> multicastResult) {
        return new CastResult(
                multicastResult.stream().map(CastResult::getMessage).reduce(Text.EMPTY, Text::concat)
        );
    }

    public Text getMessage() {
        return message;
    }
}
