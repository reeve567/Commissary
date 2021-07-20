package dev.reeve.commissary;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class GsonUtility {
	public static JsonSerializer<Location> locationJsonSerializer = (src, typeOfSrc, context) -> {
		JsonObject object = new JsonObject();
		
		object.addProperty("x", src.getX());
		object.addProperty("y", src.getY());
		object.addProperty("z", src.getZ());
		object.addProperty("pitch", src.getPitch());
		object.addProperty("yaw", src.getYaw());
		object.addProperty("world", src.getWorld().getName());
		
		return object;
	};
	
	public static JsonDeserializer<Location> locationJsonDeserializer = (json, typeOfT, context) -> {
		JsonObject object = json.getAsJsonObject();
		return new Location(
				Bukkit.getWorld(object.get("world").getAsString()),
				object.get("x").getAsDouble(),
				object.get("y").getAsDouble(),
				object.get("z").getAsDouble(),
				object.get("pitch").getAsFloat(),
				object.get("yaw").getAsFloat()
		);
	};
}
