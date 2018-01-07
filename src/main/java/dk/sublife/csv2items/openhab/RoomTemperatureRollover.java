package dk.sublife.csv2items.openhab;

import dk.sublife.csv2items.ets.SupportedLine;
import lombok.Data;

@SuppressWarnings("unused")
public class RoomTemperatureRollover extends Number {

	public RoomTemperatureRollover(SupportedLine supportedLine) {
		super(supportedLine);
		setDpt("5.010");
		setIcon("heating");
	}

	public void addLine(dk.sublife.csv2items.ets.types.RoomTemperatureRollover roomTemperatureRollover){
		setValue(roomTemperatureRollover);
	}

	public void addLine(dk.sublife.csv2items.ets.types.RoomTemperatureRolloverFeedback roomTemperatureRolloverFeedback){
		setValueFeedback(roomTemperatureRolloverFeedback);
	}

	@Override
	public String getLabel() {
		return super.getLabel()+" [MAP(rtr.map):%s]";
	}
}
