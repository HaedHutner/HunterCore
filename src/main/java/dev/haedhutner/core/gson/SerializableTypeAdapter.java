package dev.haedhutner.core.gson;

import dev.haedhutner.core.HunterCore;
import com.google.gson.*;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataFormats;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

public class SerializableTypeAdapter<T extends DataSerializable> extends AbstractTypeAdapter<T> {

    private JsonParser parser = new JsonParser();

    protected SerializableTypeAdapter(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        try {
            DataView view = DataFormats.JSON.read(json.toString());
            Optional<T> object = view.getSerializable(DataQuery.of(), clazz);
            return object.orElse(null);
        } catch (IOException e) {
            HunterCore.getInstance().getLogger().info("Error deserializing type {}", typeOfT); // DEBUG
        }
        return null;
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        try {
            String json = DataFormats.JSON.write(src.toContainer());
            return parser.parse(json);
        } catch (IOException e) {
            HunterCore.getInstance().getLogger().info("Error serializing type {}", typeOfSrc); // DEBUG
        }
        return null;
    }
}
