package com.EudyContreras.Snake.PathFindingAI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import com.EudyContreras.Snake.PathFindingAI.AIPathFinder.DistressLevel;
import com.EudyContreras.Snake.PathFindingAI.CellNode.Direction;
import com.EudyContreras.Snake.PathFindingAI.GridNode.Neighbor;
import com.EudyContreras.Snake.PathFindingAI.LinkedPath.ConnectionType;
import com.EudyContreras.Snake.PlayerTwo.PlayerTwo;

public class SearchAlgorithm {

	private double heuristicScale = 2;

	private HeuristicType heuristicType;
	private PathType pathType;
	private TieBreaker tieBreaker;

	public SearchAlgorithm(){
		this.initialize();
	}

	public void initialize() {
		heuristicType = HeuristicType.MANHATHAN;
		pathType = PathType.SHORTEST_PATH;
		tieBreaker = TieBreaker.NONE;
	}

	public List<CellNode> GET_SHORTEST_LIST(List<List<CellNode>> paths) {

		List<CellNode> shortest = paths.get(0);

		int minSize = Integer.MAX_VALUE;

		for (int i = 0; i<paths.size(); i++) {

			if (paths.get(i).size() < minSize && !paths.get(i).isEmpty()) {

				minSize = paths.get(i).size();
				shortest = paths.get(i);
			}
		}
		return shortest;
	}

	public List<CellNode> GET_LONGEST_LIST(List<List<CellNode>> paths) {

		List<CellNode> longest = paths.get(0);

		int maxSize = Integer.MIN_VALUE;

		for (int i = 0; i<paths.size(); i++) {

			if (paths.get(i).size() > maxSize && !paths.get(i).isEmpty()) {

				maxSize = paths.get(i).size();
				longest = paths.get(i);
			}
		}
		return longest;
	}

	public CellNode GET_FARTHEST_CELL(PlayerTwo snakeAI, GridNode grid, CellNode from){
		CellNode farthestCell = null;

		List<CellNode> edges = grid.getEdges();

		double maxDistance = -1;

		for(CellNode edge: edges){

			if (edge == null) {
				continue;
			}

			if (edge.isOccupied()){
				continue;
			}

			double distance = edge.getDistanceFrom(from);

			if(distance > maxDistance){

				maxDistance = distance;

				farthestCell = edge;
			}
		}

		return farthestCell;
	}


	public void LABEL_DISTANCES(GridNode grid, CellNode from){

		List<CellNode> neighbors = grid.getNeighborCells(from, DistressLevel.LEVEL_THREE);

		for(CellNode neighbor : neighbors){
			if (neighbor == null) {
				continue;
			}

			if (neighbor.isVisited()){
				continue;
			}

			neighbor.setDistance(from.getDistance()+1);

			LABEL_DISTANCES(grid,neighbor);
		}
	}

	public List<CellNode> DEEP_NEIGHBOR_CHECK(PlayerTwo snakeAI,GridNode grid, CellNode cell, int depth, Neighbor neighbor){
		List<CellNode> neighbors = new LinkedList<>();

		CellNode tempCell = null;
		CellNode current = null;

		tempCell = grid.getNeighbor(cell, neighbor);

		tempCell.setParentNode(cell);

		if (tempCell.isTraversable() && !tempCell.isOccupied()) {
			neighbors.add(tempCell);
		}

		for(int i = 0; i<depth; i++){
			current = tempCell;

			tempCell = grid.getNeighbor(tempCell, neighbor);

			tempCell.setParentNode(current);

			if (tempCell.isTraversable() && !tempCell.isOccupied()) {
				neighbors.add(tempCell);
			}
		}

		return neighbors;
	}

	public List<CellNode> GET_BRUTE_PATH(PlayerTwo snakeAI, GridNode grid, CellNode current, int depth){

		List<CellNode> brutePath = null;

		List<List<CellNode>> paths = new LinkedList<>();

		Neighbor directionOne = null;
		Neighbor directionTwo = null;

		switch(snakeAI.getCurrentDirection()){
		case MOVE_UP:
			directionOne = Neighbor.EAST;
			directionTwo = Neighbor.WEST;
			break;
		case MOVE_DOWN:
			directionOne = Neighbor.EAST;
			directionTwo = Neighbor.WEST;
			break;
		case MOVE_LEFT:
			directionOne = Neighbor.NORTH;
			directionTwo = Neighbor.SOUTH;
			break;
		case MOVE_RIGHT:
			directionOne = Neighbor.NORTH;
			directionTwo = Neighbor.SOUTH;
			break;
		case STANDING_STILL:
			break;
		}

		paths.add(DEEP_NEIGHBOR_CHECK(snakeAI, grid, current, depth, directionOne));

		paths.add(DEEP_NEIGHBOR_CHECK(snakeAI, grid, current, depth, directionTwo));

		brutePath = GET_LONGEST_LIST(paths);

		return brutePath;
	}

	public boolean QUICK_PATH_SEARCH(GridNode grid, CellNode startingPoint, CellNode objective) {

		PriorityQueue<CellNode> openCollection = new PriorityQueue<CellNode>((grid.getRowCount() * grid.getColumnCount()), new CellComparator());

		CellNode current = null;

		boolean containsNeighbor;

		grid.resetCells(true);

		openCollection.add(startingPoint);

		startingPoint.setVisited(true);

		startingPoint.setMovementCost(0d);

		startingPoint.setTotalCost(startingPoint.getMovementCost() + heuristicCostEstimate(startingPoint, objective, 1.0, HeuristicType.MANHATHAN)); //The higher the scale the less the number of turn: scale from 1 to 2

		while (!openCollection.isEmpty()) {

			current = openCollection.poll();

			if (current.equals(objective)) {

				return true;
			}

			current.setVisited(true);

			for (CellNode neighbor : grid.getNeighborCells(current, DistressLevel.LEVEL_THREE)) {

				if (neighbor == null) {
					continue;
				}

				if (neighbor.isVisited()){
					continue;
				}

				double potentialGScore = current.getMovementCost() + heuristicCostEstimate(current, neighbor, 1.0, HeuristicType.MANHATHAN); //The higher the scale the less the number of turn: scale from 1 to 2

				if (!(containsNeighbor = openCollection.contains(neighbor)) || Double.compare(potentialGScore, neighbor.getMovementCost()) < 0) {

					if (!containsNeighbor) {

					neighbor.setParentNode(current);

					neighbor.setMovementCost(potentialGScore);

					neighbor.setHeuristic(heuristicCostEstimate(neighbor, objective, 1.0, HeuristicType.MANHATHAN));

					neighbor.setTotalCost(neighbor.getMovementCost() + neighbor.getHeuristic());

					openCollection.add(neighbor);

					}
				}
			}
		}
		return false;
	}

	public List<CellNode> GET_LONGEST_PATH_ALT(PlayerTwo snakeAI, GridNode grid, CellNode startingPoint, CellNode objective, DistressLevel distressLevel){

		Stack<CellNode> openCollection = new Stack<>();

		List<CellNode> path = null;

		CellNode current = null;

		int searchCount = 0;

		boolean containsNeighbor;

		grid.resetCells(true);

		switch(snakeAI.getCurrentDirection()){

		case MOVE_DOWN:
			startingPoint.setDirection(Direction.DOWN);
			break;
		case MOVE_LEFT:
			startingPoint.setDirection(Direction.LEFT);
			break;
		case MOVE_RIGHT:
			startingPoint.setDirection(Direction.RIGHT);
			break;
		case MOVE_UP:
			startingPoint.setDirection(Direction.UP);
			break;
		case STANDING_STILL:
			startingPoint.setDirection(Direction.DOWN);
			break;
		}

		LABEL_DISTANCES(grid,objective);

		startingPoint.setVisited(true);

		objective.setObjective(true);

		openCollection.push(startingPoint);

		while (!openCollection.isEmpty()) {

			current = openCollection.pop();

			searchCount++;

			if (current.equals(objective)) {

				return buildPath(CurrentGoal.OBJECTIVE, current, searchCount);
			}

			current.setVisited(true);

			for (CellNode neighbor : grid.getNeighborCells(current,distressLevel)) {

				if (neighbor == null) {
					continue;
				}

				if (neighbor.isVisited()) {
					continue;
				}

				if (!(containsNeighbor = openCollection.contains(neighbor))) {

					if (!containsNeighbor && neighbor.getDistance() > current.getDistance()) {

//						if(neighbor.getDirection())

						neighbor.setParentNode(current);

						neighbor.setDistance(current.getDistance()+1);

						if(neighbor.getIndex().getRow() > current.getIndex().getRow()){
							current.setDirection(Direction.RIGHT);
						}
						else if (neighbor.getIndex().getRow() < current.getIndex().getRow()) {
							current.setDirection(Direction.LEFT);
						}
						else if (neighbor.getIndex().getCol() > current.getIndex().getCol()) {
							current.setDirection(Direction.DOWN);
						}
						else if (neighbor.getIndex().getCol() < current.getIndex().getCol()) {
							current.setDirection(Direction.UP);
						}

						openCollection.add(neighbor);
					}
				}
			}
		}
		return path;
	}

	public List<CellNode> GET_LONGEST_PATH(int iteration, PlayerTwo snakeAI, GridNode grid, CellNode startingPoint, DistressLevel distressLevel, BorderPole pole) {

		List<CellNode> result = null;

		List<CellNode> borderCells = null;

		CellNode objective = null;

		grid.resetCells(true);

		if (iteration == 0) {
			switch (snakeAI.getCurrentDirection()) {
			case MOVE_DOWN:
				startingPoint.setDirection(Direction.DOWN);
				break;
			case MOVE_LEFT:
				startingPoint.setDirection(Direction.LEFT);
				break;
			case MOVE_RIGHT:
				startingPoint.setDirection(Direction.RIGHT);
				break;
			case MOVE_UP:
				startingPoint.setDirection(Direction.UP);
				break;
			case STANDING_STILL:
				startingPoint.setDirection(Direction.DOWN);
				break;
			}
		}

		switch (pole) {
		case NORTH:
			borderCells = grid.getTeleportZoneNorth();
			break;
		case SOUTH:
			borderCells = grid.getTeleportZoneSouth();
			break;
		case WEST:
			borderCells = grid.getTeleportZoneWest();
			break;
		case EAST:
			borderCells = grid.getTeleportZoneEast();
			break;
		}

		for(CellNode borderCell: borderCells){
			if (borderCell == null) {
				continue;
			}
			if (borderCell.isOccupied()){
				continue;
			}
			if(QUICK_PATH_SEARCH(grid, startingPoint, borderCell)){

				objective = borderCell;

				break;
			}
		}

		if (startingPoint.equals(objective)) {

			List<CellNode> path = new LinkedList<CellNode>();

			path.add(startingPoint);

			return path;
		}

		objective.setObjective(true);

		startingPoint.setDistance(0);

		startingPoint.setVisited(true);

		int maxLength = -1;

		for (CellNode neighbor : grid.getNeighborCells(startingPoint,distressLevel)) {

			if (neighbor == null) {
				continue;
			}

			if (neighbor.isVisited()){
				continue;
			}

			neighbor.setDistance(startingPoint.getDistance()+1);

			List<CellNode> path = GET_LONGEST_PATH(iteration+1, snakeAI, grid, neighbor, objective, distressLevel);

			if (path != null && path.size() > maxLength) {

				maxLength = path.size();

				path.add(0, startingPoint);

				result = path;
			}
		}

		startingPoint.setVisited(false);

		if (result == null || result.size() == 0){
			return null;
		}

		return result;
	}

	public List<CellNode> GET_LONGEST_PATH(int iteration, PlayerTwo snakeAI, GridNode grid, CellNode startingPoint, CellNode objective, DistressLevel distressLevel) {

		List<CellNode> result = null;

		grid.resetCells(true);

		if (iteration == 0) {
			switch (snakeAI.getCurrentDirection()) {

			case MOVE_DOWN:
				startingPoint.setDirection(Direction.DOWN);
				break;
			case MOVE_LEFT:
				startingPoint.setDirection(Direction.LEFT);
				break;
			case MOVE_RIGHT:
				startingPoint.setDirection(Direction.RIGHT);
				break;
			case MOVE_UP:
				startingPoint.setDirection(Direction.UP);
				break;
			case STANDING_STILL:
				startingPoint.setDirection(Direction.DOWN);
				break;
			}
		}

		if (startingPoint.equals(objective)) {

			List<CellNode> path = new LinkedList<CellNode>();

			path.add(startingPoint);

			return path;
		}

		objective.setObjective(true);

		startingPoint.setDistance(0);

		startingPoint.setVisited(true);

		int maxLength = -1;

		for (CellNode neighbor : grid.getNeighborCells(startingPoint,distressLevel)) {

			if (neighbor == null) {
				continue;
			}

			if (neighbor.isVisited()){
				continue;
			}

			neighbor.setDistance(startingPoint.getDistance()+1);

			List<CellNode> path = GET_LONGEST_PATH(iteration+1, snakeAI, grid, neighbor, objective, distressLevel);

			if (path != null && path.size() > maxLength) {

				maxLength = path.size();

				path.add(0, startingPoint);

				result = path;
			}
		}

		startingPoint.setVisited(false);

		if (result == null || result.size() == 0){
			return null;
		}

		return result;
	}

	public List<CellNode> GET_ASTAR_PATH(PlayerTwo snakeAI, GridNode grid, CellNode startingPoint, CellNode objective, DistressLevel distressLevel) {

		PriorityQueue<CellNode> openCollection = new PriorityQueue<CellNode>((grid.getRowCount() * grid.getColumnCount()), new CellComparator());

		CellNode current = null;

		int searchCount = 0;

		double turnPenalty = 0;

		boolean containsNeighbor;

		grid.resetCells(true);

		switch(snakeAI.getCurrentDirection()){
		case MOVE_DOWN:
			startingPoint.setDirection(Direction.DOWN);
			break;
		case MOVE_LEFT:
			startingPoint.setDirection(Direction.LEFT);
			break;
		case MOVE_RIGHT:
			startingPoint.setDirection(Direction.RIGHT);
			break;
		case MOVE_UP:
			startingPoint.setDirection(Direction.UP);
			break;
		case STANDING_STILL:
			startingPoint.setDirection(Direction.DOWN);
			break;
		}

		objective.setObjective(true);

		startingPoint.setVisited(true);

		startingPoint.setMovementCost(0d);

		startingPoint.setTotalCost(startingPoint.getMovementCost() + heuristicCostEstimate(startingPoint, objective,heuristicScale,heuristicType)); //The higher the scale the less the number of turn: scale from 1 to 2

		openCollection.add(startingPoint);

		while (!openCollection.isEmpty()) {

			current = openCollection.poll();
			searchCount++;

			if (current.equals(objective)) {

				return createCoordinates(objective,searchCount);
			}

			current.setVisited(true);

			for (CellNode neighbor : grid.getNeighborCells(current,distressLevel)) {

				if (neighbor == null) {
					continue;
				}

				if (neighbor.isVisited()){
					continue;
				}

				double potentialGScore = current.getMovementCost() + heuristicCostEstimate(current, neighbor,heuristicScale,heuristicType); //The higher the scale the less the number of turn: scale from 1 to 2

				if (!(containsNeighbor = openCollection.contains(neighbor)) || Double.compare(potentialGScore, neighbor.getMovementCost()) < 0) {

					if (!containsNeighbor) {

					neighbor.setParentNode(current);

					current.setChildNode(neighbor);

					neighbor.setMovementCost(potentialGScore);

					neighbor.setDistance(current.getDistance()+1);

					if (current.getParentNode() != null) {
						if (neighbor.getIndex().getRow() != current.getParentNode().getIndex().getRow()
						 || neighbor.getIndex().getCol() != current.getParentNode().getIndex().getCol()) {
							neighbor.setMovementCost(potentialGScore+turnPenalty);
						}
					}

					double heuristic = 0;

					heuristic = heuristicCostEstimate(neighbor, objective,2.0,heuristicType);

					switch (tieBreaker) {
					case CROSS:

						double dx1 = neighbor.getLocation().getX() - objective.getLocation().getX();
						double dy1 = neighbor.getLocation().getY() - objective.getLocation().getY();
						double dx2 = startingPoint.getLocation().getX() - objective.getLocation().getX();
						double dy2 = startingPoint.getLocation().getY() - objective.getLocation().getY();

						double cross = Math.abs((dx1 * dy2) - (dx2 * dy1));

						heuristic += cross * 0.001;
						heuristic *= heuristicScale;
						break;
					case PATH:

						double path = 10 / 1000;

						heuristic *= (1.0 + path);
						heuristic *= heuristicScale;
						break;
					case NONE:
						heuristic *= heuristicScale;
						break;
					}

					neighbor.setHeuristic(heuristic); // If used with scaled up heuristic it gives least number of turns!

					neighbor.setTotalCost(neighbor.getMovementCost() + neighbor.getHeuristic());

					openCollection.add(neighbor);

					}
				}
			}
		}
		return new LinkedList<>();
	}

	/**
	 * Find a path from start to goal using the Breadth first search algorithm
	 */
	public List<CellNode> GET_BFS_PATH(PlayerTwo snakeAI, GridNode grid, CellNode startingPoint, CellNode objective, DistressLevel distressLevel) {

		Queue<CellNode> openCollection = new LinkedList<>();

		CellNode current = null;

		int searchCount = 0;

		boolean containsNeighbor;

		grid.resetCells(true);

		switch(snakeAI.getCurrentDirection()){

		case MOVE_DOWN:
			startingPoint.setDirection(Direction.DOWN);
			break;
		case MOVE_LEFT:
			startingPoint.setDirection(Direction.LEFT);
			break;
		case MOVE_RIGHT:
			startingPoint.setDirection(Direction.RIGHT);
			break;
		case MOVE_UP:
			startingPoint.setDirection(Direction.UP);
			break;
		case STANDING_STILL:
			startingPoint.setDirection(Direction.DOWN);
			break;
		}

		objective.setObjective(true);

		startingPoint.setVisited(true);

		openCollection.add(startingPoint);


	    while (!openCollection.isEmpty()) {

	    	current = openCollection.poll();

			searchCount++;

			if (current.equals(objective)) {
				return buildPath(CurrentGoal.OBJECTIVE, objective, searchCount);
			}

			current.setVisited(true);

			for (CellNode neighbor : grid.getNeighborCells(current,distressLevel)) {

				if (neighbor == null) {
					continue;
				}

				if (neighbor.isVisited()) {
					continue;
				}

				if (!(containsNeighbor = openCollection.contains(neighbor))) {

					if(!containsNeighbor){

						neighbor.setParentNode(current);

						neighbor.setDistance(current.getDistance()+1);

						openCollection.add(neighbor);
					}
				}
			}
		}
	    return new ArrayList<>();
	}

	/**
	 * Find a path from start to goal using the depth first search algorithm
	 */

	public List<CellNode> GET_DFS_PATH(PlayerTwo snakeAI, GridNode grid, CellNode startingPoint, CellNode objective, DistressLevel distressLevel) {

		Stack<CellNode> openCollection = new Stack<CellNode>();

		CellNode current = null;

		int searchCount = 0;

		boolean containsNeighbor;

		grid.resetCells(true);

		switch(snakeAI.getCurrentDirection()){

		case MOVE_DOWN:
			startingPoint.setDirection(Direction.DOWN);
			break;
		case MOVE_LEFT:
			startingPoint.setDirection(Direction.LEFT);
			break;
		case MOVE_RIGHT:
			startingPoint.setDirection(Direction.RIGHT);
			break;
		case MOVE_UP:
			startingPoint.setDirection(Direction.UP);
			break;
		case STANDING_STILL:
			startingPoint.setDirection(Direction.DOWN);
			break;
		}

		objective.setObjective(true);

		startingPoint.setVisited(true);

		openCollection.push(startingPoint);


		while (!openCollection.isEmpty()) {

			current = openCollection.pop();

			searchCount++;

			if (current.equals(objective)) {

				return buildPath(CurrentGoal.OBJECTIVE, objective, searchCount);
			}

			current.setVisited(true);

			for (CellNode neighbor : grid.getNeighborCells(current,distressLevel)) {

				if (neighbor == null) {
					continue;
				}

				if (neighbor.isVisited()) {
					continue;
				}

				if (!(containsNeighbor = openCollection.contains(neighbor))) {

					if (!containsNeighbor) {

						neighbor.setParentNode(current);

						neighbor.setDistance(current.getDistance()+1);

						openCollection.add(neighbor);
					}
				}
			}
		}
		return new ArrayList<>();
	}

	public LinkedPath<CellNode> GET_SAFE_ASTAR_PATH(PlayerTwo snakeAI, GridNode grid, CellNode startingPoint, CellNode objective, CellNode tail, DistressLevel distressLevel) {
		LinkedPath<CellNode> safePath = new LinkedPath<CellNode>(ConnectionType.SAFE_PATH_CHECK);

		CellNode current = null;

		boolean containsNeighbor;

		double turnPenalty = 0;

		int searchCount = 0;

		int cellCount = grid.getRowCount() * grid.getColumnCount();

		PriorityQueue<CellNode> goalList = new PriorityQueue<CellNode>( cellCount, new CellComparator());

		PriorityQueue<CellNode> tailList = new PriorityQueue<CellNode>( cellCount, new CellComparator());

		grid.resetCells(true);

		switch(snakeAI.getCurrentDirection()){
		case MOVE_DOWN:
			startingPoint.setDirection(Direction.DOWN);
			break;
		case MOVE_LEFT:
			startingPoint.setDirection(Direction.LEFT);
			break;
		case MOVE_RIGHT:
			startingPoint.setDirection(Direction.RIGHT);
			break;
		case MOVE_UP:
			startingPoint.setDirection(Direction.UP);
			break;
		case STANDING_STILL:
			startingPoint.setDirection(Direction.DOWN);
			break;
		}

		containsNeighbor = false;

		current = null;

		searchCount = 0;

		objective.setObjective(true);

		startingPoint.setVisited(true);

		startingPoint.setMovementCost(0d);

		startingPoint.setTotalCost(startingPoint.getMovementCost() + heuristicCostEstimate(startingPoint, objective, heuristicScale, heuristicType));

		goalList.add(startingPoint);

		while( !goalList.isEmpty()) {

			current = goalList.poll();

			searchCount++;

			if( current == objective) {

				safePath.setPathOne(buildPath(CurrentGoal.OBJECTIVE, objective, searchCount));

				break;
			}

			current.setVisited(true);

			for( CellNode neighbor: grid.getNeighborCells(current, distressLevel)) {

				if( neighbor == null) {
					continue;
				}

				if( neighbor.isVisited()) {
					continue;
				}

				double potentialGScore = current.getMovementCost() + heuristicCostEstimate( current, neighbor, heuristicScale, heuristicType);

				if( !(containsNeighbor=goalList.contains( neighbor)) || Double.compare(potentialGScore, neighbor.getMovementCost()) < 0) {

					if( !containsNeighbor) {

					neighbor.setParentNode(current);

					current.setChildNode(neighbor);

					neighbor.setMovementCost(potentialGScore);

					neighbor.setDistance(current.getDistance()+1);

					if (current.getParentNode() != null) {
						if (neighbor.getIndex().getRow() != current.getParentNode().getIndex().getRow()
						 || neighbor.getIndex().getCol() != current.getParentNode().getIndex().getCol()) {
							neighbor.setMovementCost(potentialGScore+turnPenalty);
						}
					}

					double heuristic = 0;

					heuristic = heuristicCostEstimate(neighbor, objective,2.0,heuristicType);

					switch (tieBreaker) {
					case CROSS:

						double dx1 = neighbor.getLocation().getX() - objective.getLocation().getX();
						double dy1 = neighbor.getLocation().getY() - objective.getLocation().getY();
						double dx2 = startingPoint.getLocation().getX() - objective.getLocation().getX();
						double dy2 = startingPoint.getLocation().getY() - objective.getLocation().getY();

						double cross = Math.abs((dx1 * dy2) - (dx2 * dy1));

						heuristic += cross * 0.001;
						heuristic *= heuristicScale;
						break;
					case PATH:

						double path = 10 / 1000;

						heuristic *= (1.0 + path);
						heuristic *= heuristicScale;
						break;
					case NONE:
						heuristic *= heuristicScale;
						break;
					}

					neighbor.setHeuristic(heuristic); // If used with scaled up heuristic it gives least number of turns!

					neighbor.setTotalCost(neighbor.getMovementCost() + neighbor.getHeuristic());

					goalList.add( neighbor);

					}
				}
			}
		}

		containsNeighbor = false;

		current = null;

		turnPenalty = 0;

		searchCount = 0;

		grid.resetCellValues();

		objective.setMovementCost(0d);

		objective.setTotalCost(objective.getMovementCost() + heuristicCostEstimate(objective, tail, heuristicScale, heuristicType));

		tailList.add(objective);

		while(!tailList.isEmpty()) {

			current = tailList.poll();

			searchCount++;

			if( current == tail) {
				safePath.setPathTwo(buildPath(CurrentGoal.TAIL, tail, searchCount));
				break;
			}

			current.setVisited(true);

			for( CellNode neighbor: grid.getNeighborCells(current, DistressLevel.SAFETY_CHECK)) {

				if( neighbor == null) {
					continue;
				}

				if( neighbor.isVisited()) {
					continue;
				}

				double potentialGScore = current.getMovementCost() + heuristicCostEstimate( current, neighbor, heuristicScale, heuristicType);

				if( !(containsNeighbor=tailList.contains(neighbor)) || Double.compare(potentialGScore, neighbor.getMovementCost()) < 0) {

					if( !containsNeighbor) {

					neighbor.setParentNode(current);

					current.setChildNode(neighbor);

					neighbor.setMovementCost(potentialGScore);

					if (current.getParentNode() != null) {
						if (neighbor.getIndex().getRow() != current.getParentNode().getIndex().getRow()
						 || neighbor.getIndex().getCol() != current.getParentNode().getIndex().getCol()) {
							neighbor.setMovementCost(potentialGScore+turnPenalty);
						}
					}

					double heuristic = 0;

					heuristic = heuristicCostEstimate(neighbor, tail,2.0,heuristicType);

					switch (tieBreaker) {
					case CROSS:

						double dx1 = neighbor.getLocation().getX() - tail.getLocation().getX();
						double dy1 = neighbor.getLocation().getY() - tail.getLocation().getY();
						double dx2 = objective.getLocation().getX() - tail.getLocation().getX();
						double dy2 = objective.getLocation().getY() - tail.getLocation().getY();

						double cross = Math.abs((dx1 * dy2) - (dx2 * dy1));

						heuristic += cross * 0.001;
						heuristic *= heuristicScale;
						break;
					case PATH:

						double path = 10 / 1000;

						heuristic *= (1.0 + path);
						heuristic *= heuristicScale;
						break;
					case NONE:
						heuristic *= heuristicScale;
						break;
					}

					neighbor.setHeuristic(heuristic); // If used with scaled up heuristic it gives least number of turns!

					neighbor.setTotalCost(neighbor.getMovementCost() + neighbor.getHeuristic());

					tailList.add( neighbor);
					}
				}
			}
		}
		return safePath;
	}

	private List<CellNode> buildPath(CurrentGoal goal, CellNode current, int searchCount) {

		List<CellNode> totalPath = new LinkedList<>(); // arbitrary value, we'll most likely have more than 10 which is default for java

		switch(goal){
		case OBJECTIVE:
			totalPath.add(current);
			current.pathToGoal(true);

			while((current = current.getParentNode()) != null) {

				totalPath.add(current);
				current.pathToGoal(true);
			}

			break;
		case TAIL:
			totalPath.add( current);
			current.pathToTail(true);

			while((current = current.getParentNode()) != null) {
				totalPath.add(current);
				current.pathToTail(true);
			}

			break;
		}
		return totalPath;
	}

	private List<CellNode> createCoordinates(CellNode current, int searchCount) {

		List<CellNode> totalPath = new LinkedList<CellNode>();

		boolean createPath = true;

		int pathLength = 0;

		totalPath.add(current);
		current.pathToGoal(true);

		while (createPath) {

			pathLength++;

			if(current.getParentNode() != null){
				current = current.getParentNode();

				totalPath.add(current);
				current.pathToGoal(true);

				if(pathLength>=searchCount){
					createPath = false;
				}
			}
			else{
				createPath = false;
			}
		}
		return totalPath;
	}

	public double calculateDistance(double fromX, double toX, double fromY, double toY) {
		return Math.hypot(fromX - toX, fromY - toY);
	}

	public static double calculateManhathanDistance(double fromX, double toX, double fromY, double toY) {
		return Math.abs(fromX - toX) + Math.abs(fromY - toY);
	}

	public double calculateEuclidianDistance(double fromX, double toX, double fromY, double toY) {
		return Math.sqrt((fromX - toX) * (fromX - toX) + (fromY - toY) * (fromY - toY));
	}

	public double getCrossPolarDistance(CellNode start, CellNode closestRelativeCell, CellNode closestPolarCell, CellNode objective){
		double distanceOne = calculateManhathanDistance(start.getLocation().getX(),start.getLocation().getY(),closestRelativeCell.getLocation().getX(),closestRelativeCell.getLocation().getY());
		double distanceTwo = calculateManhathanDistance(closestPolarCell.getLocation().getX(),closestPolarCell.getLocation().getY(), objective.getLocation().getX(),objective.getLocation().getY());;
		return (distanceOne+distanceTwo);
	}

	@SuppressWarnings("unused")
	private double getHeuristicCost(CellNode start, CellNode end, Double scale) {
		double dx = Math.abs(start.getLocation().getX() - end.getLocation().getX());
		double dy = Math.abs(start.getLocation().getY() - end.getLocation().getY());
		return scale * (dx + dy);
	}

	private double heuristicCostEstimate(CellNode start, CellNode end, Double scale, HeuristicType cost) {
		double distance = 0;
		switch(cost){
		case CUSTOM_EUCLUDIAN:
			distance = scale*calculateDistance(start.getLocation().getX(), end.getLocation().getX(), start.getLocation().getY(),end.getLocation().getY());
			break;
		case EUCLIDIAN:
			distance = scale*calculateEuclidianDistance(start.getLocation().getX(), end.getLocation().getX(), start.getLocation().getY(),end.getLocation().getY());
			break;
		case MANHATHAN:
			distance = scale*calculateManhathanDistance(start.getLocation().getX(), end.getLocation().getX(), start.getLocation().getY(),end.getLocation().getY());
			break;
		}
		return distance;
	}

	public void setPathType(PathType pathType) {
		this.pathType = pathType;
	}

	public enum CurrentGoal{
		OBJECTIVE, TAIL,
	}

	public enum SearchType{
		CLOSEST_OBJECTIVE, SHORTEST_PATH;
	}

	public enum PathType{
		LONGEST_PATH, SHORTEST_PATH
	}

	public enum TieBreaker{
		PATH,CROSS, NONE
	}

	public enum HeuristicType{
		MANHATHAN, EUCLIDIAN, CUSTOM_EUCLUDIAN,
	}

	public enum BorderPole{
		NORTH, SOUTH, WEST, EAST
	}

	public class CellComparator implements Comparator<CellNode> {
		@Override
		public int compare(CellNode a, CellNode b) {
			if(pathType == PathType.SHORTEST_PATH){
				return Double.compare(a.getTotalCost(), b.getTotalCost());
			}
			else{
				return Double.compare(b.getTotalCost(), a.getTotalCost());
			}
		}
	}
}
