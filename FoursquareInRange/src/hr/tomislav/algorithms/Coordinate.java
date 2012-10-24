package hr.tomislav.algorithms;

import java.io.Serializable;

public class Coordinate implements Serializable {

	private static final long serialVersionUID = -8229037113995372994L;
	public double x;
	public double y;
	
	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
