package http.json;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * Gson adapter for serializing and deserializing {@link LocalDateTime}.
 * <p>
 * This adapter converts {@code java.time.LocalDateTime} objects
 * to and from JSON strings using the standard ISO-8601 format.
 * </p>
 *
 * <h3>Serialization</h3>
 * <ul>
 *     <li>{@link LocalDateTime} → JSON string in ISO-8601 (e.g. {@code "2025-09-26T15:30:00"})</li>
 *     <li>{@code null} → {@code JsonNull}</li>
 * </ul>
 *
 * <h3>Deserialization</h3>
 * <ul>
 *     <li>JSON string in ISO-8601 → {@link LocalDateTime#parse(CharSequence)}</li>
 *     <li>{@code JsonNull} → {@code null}</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * Register this adapter with {@link GsonBuilder}:
 * <pre>{@code
 * Gson gson = new GsonBuilder()
 *     .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
 *     .create();
 *
 * LocalDateTime now = LocalDateTime.now();
 * String json = gson.toJson(now); // e.g. "2025-09-26T15:30:00"
 * LocalDateTime restored = gson.fromJson(json, LocalDateTime.class);
 * }</pre>
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    /**
     * Serializes a {@link LocalDateTime} into an ISO-8601 JSON string.
     *
     * @param src       the source LocalDateTime (may be {@code null})
     * @param typeOfSrc the actual type of the source object
     * @param context   serialization context
     * @return a JSON primitive containing the ISO-8601 string, or {@code JsonNull} if src is {@code null}
     */
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString());
    }

    /**
     * Deserializes an ISO-8601 JSON string into a {@link LocalDateTime}.
     *
     * @param json     the JSON element (expected: ISO-8601 string or {@code null})
     * @param typeOfT  the target type for deserialization
     * @param context  deserialization context
     * @return a {@link LocalDateTime} parsed from the given string, or {@code null} if input is {@code null}
     * @throws JsonParseException if the JSON value is not a valid ISO-8601 string
     */
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return json == null || json.isJsonNull() ? null : LocalDateTime.parse(json.getAsString());
    }
}