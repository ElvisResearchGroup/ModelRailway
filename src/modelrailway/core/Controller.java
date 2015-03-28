package modelrailway.core;

/**
 * A generic interface for representing a railway controller, which listens to
 * events and generates events.
 * 
 * @author David J. Pearce
 *
 */
public interface Controller extends Event.Listener {
	/**
	 * Register an event listener with this controller. The controller will
	 * direct events that it generates into all registered listeners.
	 * 
	 * @param listener
	 */
	public void register(Event.Listener listener);
	
	/**
	 * Start a given train on a given route.
	 * 
	 * @param trainID
	 * @param route
	 * @return True if the train was propertly started, false otherwise.
	 */
	public boolean start(int trainID, Route route);
	
	/**
	 * Stop the train and clear it's current route.
	 * @param trainID
	 */
	public void stop(int trainID);
	
	/**
	 * Get the train record for a given train.
	 * 
	 * @param trainID
	 * @return
	 */
	public Train train(int trainID);
}
