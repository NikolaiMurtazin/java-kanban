package http.json;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Duration;

/**
 * Gson adapter for serializing and deserializing {@link Duration}.
 * <p>
 * This adapter converts {@code java.time.Duration} objects
 * to their JSON representation as a number of minutes, and vice versa.
 * </p>
 *
 * <h3>Serialization</h3>
 * <ul>
 *     <li>{@link Duration} → JSON long (total minutes)</li>
 *     <li>{@code null} → {@code JsonNull}</li>
 * </ul>
 *
 * <h3>Deserialization</h3>
 * <ul>
 *     <li>JSON number → {@link Duration#ofMinutes(long)}</li>
 *     <li>{@code JsonNull} → {@code null}</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * Register this adapter with {@link GsonBuilder}:
 * <pre>{@code
 * Gson gson = new GsonBuilder()
 *     .registerTypeAdapter(Duration.class, new DurationAdapter())
 *     .create();
 * }</pre>
 *
 * Example:
 * <pre>{@code
 * Duration d = Duration.ofMinutes(90);
 * String json = gson.toJson(d); // "90"
 * Duration restored = gson.fromJson("90", Duration.class);
 * }</pre>
 */
public class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {

    /**
     * Serializes a {@link Duration} to its JSON representation (minutes).
     *
     * @param src the source duration (may be {@code null})
     * @param typeOfSrc the actual type of the source object
     * @param context serialization context
     * @return a JSON primitive with total minutes, or {@code JsonNull} if src is {@code null}
     */
    @Override
    public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toMinutes());
    }

    /**
     * Deserializes a JSON element into a {@link Duration}.
     *
     * @param json the JSON element (expected: number of minutes or {@code null})
     * @param typeOfT the target type of the deserialization
     * @param context deserialization context
     * @return a {@link Duration} created from the given number of minutes, or {@code null} if input is {@code null}
     * @throws JsonParseException if the JSON value cannot be parsed as a long
     */
    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || json.isJsonNull()) return null;
        long minutes = json.getAsLong();
        return Duration.ofMinutes(minutes);
    }
}