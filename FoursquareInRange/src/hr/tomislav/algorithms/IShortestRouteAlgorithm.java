package hr.tomislav.algorithms;

import java.util.ArrayList;


public interface IShortestRouteAlgorithm {

	public ArrayList<Integer> calculateShortestRoute(Coordinate start, ArrayList<Coordinate> points);
	
}
