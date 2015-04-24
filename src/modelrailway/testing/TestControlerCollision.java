package modelrailway.testing;

import java.util.Map;

import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Route;
import modelrailway.simulation.Section;
import modelrailway.simulation.Track;
import modelrailway.simulation.Train;

public class TestControlerCollision extends TestController implements Controller{

	public TestControlerCollision(Map<Integer, Train> trains,
			Map<Integer, modelrailway.core.Train> orientations, Track head,
			Controller trackController) {
		super(trains, orientations, head, trackController);
		// TODO Auto-generated constructor stub
	}

	public void notify(Event e){
		super.notify(tryLocking(e));
	}
	/**
	 * Try to obtain the lock for the section ahead of where we are traveling into if we are traveling into a section.
	 * @param e
	 * @return
	 */
	private Event tryLocking(Event e){
		if((e instanceof Event.SectionChanged) && ((Event.SectionChanged) e).getInto()){ // when we are moving into a section

			Map.Entry<modelrailway.simulation.Train, Route> entry = super.getRoute(((Event.SectionChanged)e).getSection());
			Integer sec = entry.getValue().nextSection(((Event.SectionChanged) e).getSection());
			Train train = entry.getKey();
			Route trainRoute = entry.getValue();

		}
		return e;
	}

	private Section findNextSection(Train train, Integer sectionID){
		if(train.isFowards()){
			Track front = train.getFront();
		}else{
			Track back = train.getBack();

		}
		return null;
	}


}
