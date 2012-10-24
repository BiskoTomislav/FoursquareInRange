package hr.tomislav.algorithms;

import java.util.ArrayList;

import android.util.Log;

public class GreedyAlgorithm implements IShortestRouteAlgorithm {

	public ArrayList<Integer> calculateShortestRoute(Coordinate start,
			ArrayList<Coordinate> points) {

		ArrayList<Integer> Indicies = new ArrayList<Integer>();
		ArrayList<Coordinate> pointsTemp = new ArrayList<Coordinate>(points);
		
		double minDistance = Double.MAX_VALUE;
		int indexOfNextPoint = 0;
		int indexToRemove = 0;
		Coordinate currentPoint = start;
		
		while(points.size() > 0) {
			for (int i = 0; i < points.size(); i++) {
				double currentDistance = haversianDistanceCalculator(currentPoint, points.get(i));
				if( currentDistance <= minDistance ) {
					minDistance = currentDistance;
					indexOfNextPoint = pointsTemp.indexOf(points.get(i));
					indexToRemove = i;
				}
			}
			Log.d("Index: ", "BROJ: " + indexOfNextPoint);
			currentPoint = pointsTemp.get(indexOfNextPoint);
			Indicies.add(indexOfNextPoint);
			points.remove(indexToRemove);
			minDistance = Double.MAX_VALUE;
		}
		
		return Indicies;
	}

	private double haversianDistanceCalculator(Coordinate start, Coordinate end) {
		final int R = 6371; // Radious of the earth
		Double lat1 = start.x;
		Double lon1 = start.y;
		Double lat2 = end.x;
		Double lon2 = end.y;
		
		Double latDistance = toRad(lat2-lat1);
		Double lonDistance = toRad(lon2-lon1);
		
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
		Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
		Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		Double distance = R * c;
		
		return distance;
	}
	
	private static Double toRad(Double value) {
		return value * Math.PI / 180;
	}
}
