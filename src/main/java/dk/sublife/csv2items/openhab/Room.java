package dk.sublife.csv2items.openhab;

import dk.sublife.csv2items.ets.SupportedLine;
import lombok.Data;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Data
public class Room {
	private final String name;
	private final Map<String,Item> items = new TreeMap<>();

	public Room(String name) {
		this.name = name;
	}

	public Room addLine(SupportedLine line){
		final Reflections reflections = new Reflections("dk.sublife.csv2items.openhab");
		Class<? extends Item> itemClass = null;
		for (Class<? extends Item> aClass : reflections.getSubTypesOf(Item.class)) {
			try {
				aClass.getMethod("addLine", line.getClass());
				itemClass = aClass;
				break;
			} catch (Exception ignored) {
			}
		}
		if(itemClass == null) {
			throw new RuntimeException("No supported types found: " + line.getClass().getName());
		}

		Class<? extends Item> finalItemClass = itemClass;
		final Item item = items.computeIfAbsent(line.getItemName() + itemClass.getSimpleName(), k -> {
			try {
				return finalItemClass.getConstructor(SupportedLine.class).newInstance(line);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		try {
			Method addLine = item.getClass().getMethod("addLine", line.getClass());
			addLine.invoke(item, line);
		} catch (Exception ignored) {
			throw new RuntimeException(ignored);
		}
		return this;
	}




	public String filename(){
		return name.replaceAll(" ", "");
	}

	public String openhabItemConfig(){
		StringBuilder sb = new StringBuilder("// AUTOGENERATED ROOM CONFIG. DO NOT EDIT!").append(name).append("\n");
		sb.append(String.format("Group %s \"%s\"\n", getName().replaceAll("\\s", ""), getName()));
		return sb.append(items.entrySet().stream().map(o -> o.getValue().openhab()).collect(Collectors.joining("\n"))).toString();
	}

	public String openhabSitemapConfig(){
		StringBuilder sb = new StringBuilder("\n\n\tGroup label=\"").append(name).append("\"");
		sb.append(" item=");

		Optional<Map.Entry<String, Item>> first = items.entrySet()
				.stream()
				.filter(i -> Temperature.class.isAssignableFrom(i.getValue().getClass()))
				.findFirst();
		if(!first.isPresent()){
			first = items.entrySet()
					.stream()
					.findFirst();
		}

		sb.append(
				first
				.get()
				.getValue()
				.getName().replaceAll("\\s|/|-|_", ""));
		sb.append(" { \n");
		return sb.append(items.entrySet().stream().map(o -> o.getValue().openhabSitemap()).collect(Collectors.joining("\n"))).append("\n\t}\n").toString();
	}
}
