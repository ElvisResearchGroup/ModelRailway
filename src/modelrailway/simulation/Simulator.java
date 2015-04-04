package modelrailway.simulation;

import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Event.Listener;
import modelrailway.core.Route;
import modelrailway.core.Train;
/**
 * 
 * @author powleybenj
 *
 */
public class Simulator implements Controller{

	public Simulator(Track track){

	}
	@Override
	public void notify(Event e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void register(Listener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean start(int trainID, Route route) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stop(int trainID) {
		// TODO Auto-generated method stub

	}

	@Override
	public Train train(int trainID) {
		// TODO Auto-generated method stub
		return null;
	}

}
