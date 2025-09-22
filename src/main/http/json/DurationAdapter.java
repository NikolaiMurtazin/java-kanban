package http.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;

public class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
    @Override
    public JsonElement serialize(Duration src, Type t, JsonSerializationContext c) {
        return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toMinutes()); // в минутах
    }

    @Override
    public Duration deserialize(JsonElement json, Type t, JsonDeserializationContext c)
            throws JsonParseException {
        if (json == null || json.isJsonNull()) return null;
        long minutes = json.getAsLong();
        return Duration.ofMinutes(minutes);
    }
}
