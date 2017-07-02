package cdf;

import visu.Observer;

public interface Observable {
	
	public void addObserver(Observer obs);
	
	public void notifyObserver(String cdf, String variable, int nbLabel, int nLabel);

}
