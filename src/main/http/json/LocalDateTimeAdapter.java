package http.json;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    @Override public JsonElement serialize(LocalDateTime src, Type t, JsonSerializationContext c) {
        return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()); // ISO-8601
    }
    @Override public LocalDateTime deserialize(JsonElement json, Type t, JsonDeserializationContext c)
            throws JsonParseException {
        return json == null || json.isJsonNull() ? null : LocalDateTime.parse(json.getAsString());
    }
}
