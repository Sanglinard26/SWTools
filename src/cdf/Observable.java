package cdf;

import gui.Observer;

public interface Observable {

    public void addObserver(Observer obs);

    public void notifyObserver(String cdf, String rate);

}
