package modelrailway.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelrailway.ModelRailway;
import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Section;
import modelrailway.core.Train;
import modelrailway.core.Event.Listener;
import modelrailway.simulation.Track;

public class TrainController extends ControlerCollision implements Controller, Listener {
	private static SimulationTrack track = new SimulationTrack();

	public TrainController(Train[] trains, ModelRailway railway) {
		super(getTrains(trains), getSections(), getHead(), railway);




	}

	private static Track getHead() {
		// TODO Auto-generated method stub
		return track.getHead();
	}

	private static Map<Integer, Section> getSections() {
		// TODO Auto-generated method stub
		return track.getSections();
	}

	private static Map<Integer, Train> getTrains(Train[] trains) {
		// TODO Auto-generated method stub
		Map<Integer,Train> trainMap = new HashMap<Integer,Train>();
		for(int x = 0; x < trains.length; x++){
			trainMap.put(x, trains[x]);

		}
		return trainMap;
	}


}
