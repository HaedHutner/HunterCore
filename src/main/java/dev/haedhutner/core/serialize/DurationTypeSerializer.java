package dev.haedhutner.core.serialize;

import com.google.common.reflect.TypeToken;
import dev.haedhutner.core.utils.CoreUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;

public class DurationTypeSerializer implements TypeSerializer<Duration> {
    @Override
    public Duration deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String val = value.getString();

        if (StringUtils.isEmpty(val)) {
            throw new ObjectMappingException("Cannot parse Duration: Is either null or empty string.");
        }

        return CoreUtils.convertStringToDuration(val);
    }

    @Override
    public void serialize(TypeToken<?> type, Duration obj, ConfigurationNode value) throws ObjectMappingException {
        value.setValue(CoreUtils.convertDurationToString(obj));
    }
}
