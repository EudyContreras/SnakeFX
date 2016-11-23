package com.EudyContreras.Snake.PathFindingAI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Stack;

import com.EudyContreras.Snake.AbstractModels.AbstractObject;
import com.EudyContreras.Snake.Application.GameManager;
import com.EudyContreras.Snake.Application.GameSettings;
import com.EudyContreras.Snake.FrameWork.PlayerMovement;
import com.EudyContreras.Snake.Identifiers.GameModeID;
import com.EudyContreras.Snake.Identifiers.GameStateID;
import com.EudyContreras.Snake.PathFindingAI.CellNode.Direction;
import com.EudyContreras.Snake.PlayerTwo.PlayerTwo;

import javafx.geometry.Rectangle2D;

/**
 *
 * @author Eudy Contreras
 * TODO: Trigger PATH finding AI only when a collision happens !!!
 *
 * Make it so that if the snake is close to an obstacle it will activate
 * path finding else it will deactivate it!
 *
 * TODO: Make the snake go for the closest only if the path is available
 * if the path is not available use a hierarchy system. If the path is close
 * check if the closest border is opened and if open check if the farthest border
 * is open! if both borders are open teleport!
 *
 * TODO: Do not allow tracking apples which are next to the snakes body. if the closest
 * apple is one cell away from snakes body. Check next closest!
 */
public class AIPathFinder2 {

	private AIController controller;
	private GameManager game;
	private PlayerTwo snakeAI;
	private Random rand;

	private boolean logDirections = false;
	private boolean teleporting = false;
	private boolean searching = false;
	private boolean allowTrace = false;
	private boolean trackingTail = false;

	private double checkTimer = 100;
	private double heuristicScale = 2;

	private int cellCount = 0;
	private int randomBoost = 200;

	private ObjectivePosition location;
	private DistressLevel distressLevel;
	private HeuristicType heuristicType;
	private PathType pathType;
	private SearchType searchType;
	private TieBreaker tieBreaker;
	private ActionType state;
	private Direction lastStep;

	private Objective[] objectives;
	private LinkedPath pathCoordinates;


	public AIPathFinder2(GameManager game, PlayerTwo snakeAI) {
		this.game = game;
		this.snakeAI = snakeAI;
		this.initialize();
	}

	public AIPathFinder2(GameManager game, AIController controller, PlayerTwo snakeAI, LinkedList<CollideNode> possibleColliders) {
		this.game = game;
		this.controller = controller;
		this.snakeAI = snakeAI;
		this.initialize();
	}

	public void initialize() {
		rand = new Random();
		cellCount = controller.getGrid().getRowCount() * controller.getGrid().getColumnCount();
		heuristicType = HeuristicType.MANHATHAN;
		distressLevel = DistressLevel.LEVEL_TWO;
		searchType = SearchType.CLOSEST_OBJECTIVE;
		tieBreaker = TieBreaker.PATH;
		pathType = PathType.SHORTEST_PATH;
		state = ActionType.FIND_PATH;
		lastStep = Direction.NONE;
	}

	public void findObjective() {
		switch (state) {
		case DODGE_OBSTACLES:
			break;
		case FIND_PATH:
			break;
		case FREE_MODE:
			if (game.getModeID() == GameModeID.LocalMultiplayer && GameSettings.ALLOW_AI_CONTROLL)
				createPath();
			break;
		default:
			break;

		}
	}

	/*
	 * Gets called when the game begins in order to initiate the simulation
	 */
	public void startSimulation() {
		if (game.getModeID() == GameModeID.LocalMultiplayer && GameSettings.ALLOW_AI_CONTROLL){
			snakeAI.setDirectCoordinates(PlayerMovement.MOVE_DOWN);
			computeClosestPath(5,5);
		}
	}

	/*
	 * this method gets called from the game loop and it is called at 60fps. The
	 * method update and keeps track of things
	 */
	public void updateSimulation() {
		if (game.getModeID() == GameModeID.LocalMultiplayer) {
			if (game.getStateID() == GameStateID.GAMEPLAY) {

				performLocationBasedAction();
				addRandomBoost(true);

				checkTimer --;

				if(checkTimer<=0){

					computeClosestPath(0,0);

					if(!trackingTail){
						checkTimer = 200;
					}else{
						checkTimer = 2000;
					}
				}
			}
		}
	}

	/**
	 * Find a path from start to goal using the A* algorithm
	 */

	public List<CellNode> GET_ASTAR_PATH(GridNode grid, CellNode startingPoint, CellNode objective) {

		PriorityQueue<CellNode>openCollection = new PriorityQueue<CellNode>(cellCount, new CellComparator());

		HashSet<CellNode> closedCollection = new HashSet<>(cellCount);

		CellNode current = null;

		int searchCount = 0;

		double turnPenalty = 0;

		boolean containsNeighbor;

		openCollection.add(startingPoint);

		//startingPoint.setOccupied(false);

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

		startingPoint.setMovementCost(0d);

		startingPoint.setTotalCost(startingPoint.getMovementCost() + heuristicCostEstimate(startingPoint, objective,heuristicScale,heuristicType)); //The higher the scale the less the number of turn: scale from 1 to 2

		searching = true;

		while (!openCollection.isEmpty() && searching) {

			current = openCollection.poll();
			searchCount++;

			if (current.equals(objective)) {

				return createCoordinates(objective,searchCount);

			}
			closedCollection.add(current);

			for (CellNode neighbor : grid.getNeighborCells(current,distressLevel)) {

				if (neighbor == null) {
					continue;
				}

				if (closedCollection.contains(neighbor)) {
					continue;
				}

				double potentialGScore = current.getMovementCost() + heuristicCostEstimate(current, neighbor,heuristicScale,heuristicType); //The higher the scale the less the number of turn: scale from 1 to 2

				if (!(containsNeighbor = openCollection.contains(neighbor)) || Double.compare(potentialGScore, neighbor.getMovementCost()) < 0) {

					neighbor.setParentNode(current);

					neighbor.setChildNode(neighbor);

					neighbor.setMovementCost(potentialGScore);

					if (current.getParentNode() != null) {
						if (neighbor.getIndex().getRow() != current.getParentNode().getIndex().getRow()
						 || neighbor.getIndex().getCol() != current.getParentNode().getIndex().getCol()) {
							neighbor.setMovementCost(potentialGScore+turnPenalty);
						}
					}

					double heuristic = 0;

					double path = 10 / 1000;
					double dx1 = neighbor.getLocation().getX() - objective.getLocation().getX();
					double dy1 = neighbor.getLocation().getY() - objective.getLocation().getY();
					double dx2 = startingPoint.getLocation().getX() - objective.getLocation().getX();
					double dy2 = startingPoint.getLocation().getY() - objective.getLocation().getY();

					double cross = Math.abs(dx1 * dy2 - dx2 * dy1);

					heuristic = heuristicCostEstimate(neighbor, objective,2.0,heuristicType);

					switch (tieBreaker) {

					case CROSS:
						heuristic += cross * 0.001;
						heuristic *= heuristicScale;
						break;
					case PATH:
						heuristic *= (1.0 + path);
						heuristic *= heuristicScale;
						break;
					case NONE:
						heuristic *= heuristicScale;
						break;
					}

					neighbor.setHeuristic(heuristic); // If used with scaled up heuristic it gives least number of turns!

					neighbor.setTotalCost(neighbor.getMovementCost() + neighbor.getHeuristic());

					if (!containsNeighbor) {

						openCollection.add(neighbor);
					}
				}
			}
		}

		endPathSearch();

		return new LinkedList<>();
	}

	/**
	 * Find a path from start to goal using the depth first search algorithm
	 */

	public List<CellNode> GET_DFS_PATH(GridNode grid, CellNode startingPoint, CellNode objective) {

		Stack<CellNode> openCollection = new Stack<CellNode>();

		ArrayList<CellNode> closedCollection = new ArrayList<>(cellCount);

		CellNode current = null;

		int searchCount = 0;

		boolean containsNeighbor;

		openCollection.push(startingPoint);

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

		searching = true;

		while (!openCollection.isEmpty() && searching) {

			current = openCollection.pop();

			searchCount++;

			if (current.equals(objective)) {

				return createCoordinates(objective,searchCount);
			}

			closedCollection.add(current);

			for (CellNode neighbor : grid.getNeighborCells(current,distressLevel)) {

				if (neighbor == null) {
					continue;
				}

				if (closedCollection.contains(neighbor)) {
					continue;
				}

				if (!(containsNeighbor = openCollection.contains(neighbor))) {

					neighbor.setParentNode(current);


					if (!containsNeighbor) {

						openCollection.add(neighbor);
					}
				}
			}
		}

		endPathSearch();

		return new ArrayList<>();
	}

	public List<CellNode> getShortestPath2( GridNode grid, CellNode startingPoint, CellNode objective) {

		CellNode current = null;

		boolean containsNeighbor;

		int searchCount = 0;

		int cellCount = grid.getRowCount() * grid.getColumnCount();

		HashSet<CellNode> closedSet = new HashSet<>(cellCount);

		PriorityQueue<CellNode> openSet = new PriorityQueue<CellNode>( cellCount, new CellComparator());

		openSet.add( startingPoint);

		startingPoint.setMovementCost(0d);

		startingPoint.setTotalCost(startingPoint.getMovementCost() + heuristicCostEstimate(startingPoint, objective, heuristicScale, heuristicType));


		while( !openSet.isEmpty()) {

			current = openSet.poll();

			searchCount++;

			if( current == objective) {
				return createCoordinates( objective, searchCount);
			}

			closedSet.add( current);

			for( CellNode neighbor: grid.getNeighborCells(current, distressLevel)) {

				if( neighbor == null) {
					continue;
				}

				if( closedSet.contains( neighbor)) {
					continue;
				}

				double tentativeScoreG = current.getMovementCost() + heuristicCostEstimate( current, neighbor, heuristicScale, heuristicType);

				if( !(containsNeighbor=openSet.contains( neighbor)) || Double.compare(tentativeScoreG, neighbor.getMovementCost()) < 0) {

					neighbor.setParentNode(current);

					neighbor.setMovementCost(tentativeScoreG);

					neighbor.setTotalCost(heuristicCostEstimate(neighbor, objective, heuristicScale, heuristicType));

					neighbor.setTotalCost( neighbor.getMovementCost() + neighbor.getHeuristic());

					if( !containsNeighbor) {
						openSet.add( neighbor);
					}
				}
			}
		}
		return new ArrayList<>();
	}


	private void endPathSearch(){
		searching = false;
		allowTrace = false;
	}

	/**
	 * Create final path of the A* algorithm. The path is from goal to start.
	 * TODO: Find out the reason why the loop will never break sometimes.
	 */
	private synchronized List<CellNode> createCoordinates(CellNode current, int searchCount) {
		endPathSearch();

		List<CellNode> totalPath = new LinkedList<CellNode>();

		boolean createPath = true;

		int pathLength = 0;

		totalPath.add(current);

		while (createPath) {

			pathLength++;

			if(current.getParentNode() != null){
				current = current.getParentNode();
				totalPath.add(current);

				if(pathLength>searchCount){
					createPath = false;
					log("path lenght exceded");
				}
			}
			else{
				createPath = false;
			}
		}
		return totalPath;
	}

	@SuppressWarnings("unused")
	private void findClosestObjective(){
		PriorityQueue<Distance>  distances = new PriorityQueue<Distance>(4, new DistanceComparator());

		for (int i = 0; i < getObjectiveCount(); i++) {
			distances.add(new Distance(calculateManhathanDistance(
					snakeAI.getX(), game.getGameObjectController().getObsFruitList().get(i).getX(),
					snakeAI.getY(), game.getGameObjectController().getObsFruitList().get(i).getY()),
					game.getGameObjectController().getObsFruitList().get(i)));
		}

		if(distances.poll().getObject().getNumericCode()!=objectives[0].getObject().getNumericCode()){
			computeClosestPath(0,0);
		}
	}

	public synchronized void computeClosestPath(int row, int col){
		teleporting = false;
		objectives = new Objective[getObjectiveCount()];

		for (int i = 0; i < objectives.length; i++) {
			AbstractObject objective = game.getGameObjectController().getObsFruitList().get(i);
			objectives[i] = new Objective(snakeAI, objective);
		}

		controller.getGrid().resetCells();

		CellNode start = controller.getHeadCell(snakeAI, 0, 0);
		CellNode tail = null;
		CellNode goal = null;

		LinkedPath path = new LinkedPath();

		if (searchType == SearchType.SHORTEST_PATH) {

			List<List<CellNode>> paths = new LinkedList<>();

			if (start != null) {
				for (int i = 0; i < getObjectiveCount(); i++) {
					paths.add(GET_ASTAR_PATH(controller.getGrid(), start, objectives[i].getCell()));
				}

				path = new LinkedPath(getShortestPath(paths), new ArrayList<>());
			}
		} else if (searchType == SearchType.CLOSEST_OBJECTIVE) {

			Arrays.sort(objectives);

			if (objectives.length > 0) {
				if (objectives[0] != null && GameSettings.SHOW_ASTAR_GRAPH) {
					objectives[0].getObject().blowUpAlt();
				}

				if (start != null) {

					if (!start.isDangerZone()) {
						distressLevel = DistressLevel.LEVEL_TWO;
					}
					path = checkObjectiveReach(start, goal, 0, objectives);
				}
			}

			if (!path.getPathOne().isEmpty()) {

				trackingTail = false;
				pathType = PathType.SHORTEST_PATH;
				showPathToObjective(path);

			} else {
				if (objectives.length > 0) {

					path = checkObjectiveReach(start, goal, objectives.length > 1 ? 1 : 0, objectives);
					if (!path.getPathOne().isEmpty()) {
						trackingTail = false;
						pathType = PathType.SHORTEST_PATH;
						showPathToObjective(path);

					} else {

						path = checkObjectiveReach(start, goal, objectives.length > 2 ? 2 : 0, objectives);
						if (!path.getPathOne().isEmpty()) {
							trackingTail = false;
							pathType = PathType.SHORTEST_PATH;
							showPathToObjective(path);
						} else {

							path = checkObjectiveReach(start, goal, objectives.length > 3 ? 3 : 0, objectives);
							if (!path.getPathOne().isEmpty()) {
								trackingTail = false;
								pathType = PathType.SHORTEST_PATH;
								showPathToObjective(path);
							} else {

								tail = controller.getGrid().getTailCell(snakeAI);
								if (start != null && tail != null) {
									if (!start.isDangerZone()) {
										distressLevel = DistressLevel.LEVEL_TWO;
										path = new LinkedPath(GET_ASTAR_PATH(controller.getGrid(), start, tail),new ArrayList<>());
									}

								}
								if (!path.getPathOne().isEmpty()) {
									trackingTail = true;
									pathType = PathType.LONGEST_PATH;
									showPathToObjective(path);
								} else {

									distressLevel = DistressLevel.LEVEL_THREE;
									path = new LinkedPath(GET_ASTAR_PATH(controller.getGrid(), start, tail),new ArrayList<>());

									if (!path.getPathOne().isEmpty()) {
										trackingTail = true;
										pathType = PathType.LONGEST_PATH;
										showPathToObjective(path);
									}
									 else {
										log("Emergency path to tail empty!");

										distressLevel = DistressLevel.LEVEL_THREE;
										path = emergencyTeleport(controller.getGrid(), start, tail);

										if (!path.getPathOne().isEmpty()) {

											trackingTail = true;
											pathType = PathType.LONGEST_PATH;
											showPathToObjective(path);

										} else {
											// TODO: Stall until path is found or die!
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public LinkedPath checkObjectiveReach(CellNode start, CellNode goal, int index, Objective[] objectives){
		if(start!=null){
//
//			if((objectives[index>objectives.length-1 ? objectives.length-1 : index].getDistance()) > objectives[objectives.length-1].getInterpolarDistance(start.getLocation().getX(),start.getLocation().getY())){
//				return computeInterpolarDirection(controller.getGrid(),start,objectives,index);
//			}
//			else if((objectives[index].getXDistance(start.getLocation().getX())>GameSettings.WIDTH*.4) && objectives[index].getYDistance(start.getLocation().getY())<GameSettings.HEIGHT*.4){
//				return computeInterpolarDirection(controller.getGrid(),start,objectives,index);
//			}
//			else if(objectives[index].getYDistance(start.getLocation().getY())>GameSettings.HEIGHT*.5 && objectives[index].getXDistance(start.getLocation().getX())<GameSettings.WIDTH*.5){
//				return computeInterpolarDirection(controller.getGrid(),start,objectives,index);
//			}
//			else if(objectives[index].getXDistance(start.getLocation().getX())>GameSettings.WIDTH*.5 && objectives[index].getYDistance(start.getLocation().getY())>GameSettings.HEIGHT*.5){
//				return  computeInterpolarDirection(controller.getGrid(),start,objectives,index);
//			}
//			//TODO: Find additional conditions that may qualify for interpolation
//
//			else{

			CellNode newGoal = objectives[index].getCell(); //TODO: Newly marked out. Dont know why im creating a new goal based on index!! may be unsafe to mark out

			if(start!=null && newGoal!=null){
				if(!start.isDangerZone()){
					distressLevel = DistressLevel.LEVEL_TWO;
				}

				LinkedPath path = new LinkedPath(GET_ASTAR_PATH(controller.getGrid(),start ,newGoal),new ArrayList<>());

				if(!path.getPathOne().isEmpty()){
					return path;
				}
				}
			}
//		}
		return new LinkedPath();
	}
	/*
	 * Unsafe amd unchecked teleport method which triggers a teleportation when a the distance
	 * between the snake and the objective is above a certain threshold. The calculations are made
	 * based on relational distance planes.
	 */

	private LinkedPath computeInterpolarDirection(GridNode grid, CellNode start, Objective[] objectives, int index) {
		LinkedPath path;
		CellNode portalIn;
		CellNode portalOut;

		if((objectives[index].getXDistance(start.getLocation().getX())>GameSettings.WIDTH*.45) && objectives[index].getYDistance(start.getLocation().getY())<GameSettings.HEIGHT*.45){

			if(start.getLocation().getX() > GameSettings.WIDTH*.65){

				if(!grid.getCell(grid.getRowCount()-1, start.getIndex().getCol()).isOccupied()){
					portalIn = grid.getCell(grid.getRowCount()-1, start.getIndex().getCol());
					if(!grid.getCell(grid.getMinRow(), start.getIndex().getCol()).isOccupied()){
						portalOut = grid.getCell(grid.getMinRow(), start.getIndex().getCol());
						path = findPortalCell(grid, portalIn, portalOut, start, objectives);
						if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
							lastStep = Direction.RIGHT;
							teleporting = true;
							return path;
						}
					}
				}else{
					if(!grid.getCell(grid.getRowCount()-1, start.getIndex().getCol() + (start.getIndex().getCol()+2)<grid.getColumnCount()-1 ? 2 : 0).isOccupied()){
						portalIn = grid.getCell(grid.getRowCount()-1, start.getIndex().getCol() + 2);
						if(!grid.getCell(grid.getMinRow(), start.getIndex().getCol() + 2).isOccupied()){
							portalOut = grid.getCell(grid.getMinRow(), start.getIndex().getCol() + 2);
							path = findPortalCell(grid, portalIn, portalOut, start, objectives);
							if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
								lastStep = Direction.RIGHT;
								teleporting = true;
								return path;
							}
						}
					}else{
						if(!grid.getCell(grid.getRowCount()-1, start.getIndex().getCol() - (start.getIndex().getCol()+2)>grid.getMinCol() ? 2 : 0).isOccupied()){
							portalIn = grid.getCell(grid.getRowCount()-1, start.getIndex().getCol() - 2);
							if(!grid.getCell(grid.getMinRow(), start.getIndex().getCol() - 2).isOccupied()){
								portalOut = grid.getCell(grid.getMinRow(), start.getIndex().getCol() - 2);
								path = findPortalCell(grid, portalIn, portalOut, start, objectives);
								if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
									lastStep = Direction.RIGHT;
									teleporting = true;
									return path;
								}
							}
						}else{
							List<CellNode> eastBorder =  grid.getTeleportZoneEast();
							for (CellNode cell : eastBorder) {
								if (!cell.isOccupied() && !grid.getCell(grid.getMinRow(), cell.getIndex().getCol()).isOccupied()) {
									if(cell.getLocation().getY()>=start.getLocation().getY()-100){
										distressLevel = DistressLevel.LEVEL_THREE;
										path = findPortalCell(grid, cell, grid.getCell(grid.getMinRow(), cell.getIndex().getCol()), start, objectives);
										if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
											lastStep = Direction.RIGHT;
											teleporting = true;
											return path;

										}
										else {
											continue; //TODO: Marked out needs reasesstment
										}
									}
								}
							}
						}
					}
				}
			}
			if(start.getLocation().getX() < GameSettings.WIDTH*.35){

				if(!grid.getCell(grid.getMinRow(),start.getIndex().getCol()).isOccupied()){
					portalIn = grid.getCell(grid.getMinRow(),start.getIndex().getCol());
					if(!grid.getCell(grid.getRowCount()-1,start.getIndex().getCol()).isOccupied()){
						portalOut = grid.getCell(grid.getRowCount()-1,start.getIndex().getCol());
						path = findPortalCell(grid, portalIn, portalOut, start, objectives);
						if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
							lastStep = Direction.LEFT;
							teleporting = true;
							return path;
						}
					}
				}else{

					/*
					 *TODO: Find out why I get a null pointer at the line below!!!!!!!!!!!!!!!!!!!!
					 */
					int thereshold = (start.getIndex().getCol()+2)<(grid.getColumnCount()-1) ? 2 : 0;
					if(!grid.getCell(grid.getMinRow(),start.getIndex().getCol()+(thereshold)).isOccupied()){
						portalIn = grid.getCell(grid.getMinRow(),start.getIndex().getCol() + thereshold);
						if(!grid.getCell(grid.getRowCount()-1,start.getIndex().getCol() + thereshold).isOccupied()){
							portalOut = grid.getCell(grid.getRowCount()-1,start.getIndex().getCol() + thereshold);
							path = findPortalCell(grid, portalIn, portalOut, start, objectives);
							if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
								log("HALLELOYOU!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
								lastStep = Direction.LEFT;
								teleporting = true;
								return path;
							}
						}
					}else{
						if(!grid.getCell(grid.getMinRow(),start.getIndex().getCol()-(start.getIndex().getCol()+2)>grid.getMinCol() ? 2 : 0).isOccupied()){
							portalIn = grid.getCell(grid.getMinRow(),start.getIndex().getCol() - 2);
							if(!grid.getCell(grid.getRowCount()-1,start.getIndex().getCol() - 2).isOccupied()){
								portalOut = grid.getCell(grid.getRowCount()-1,start.getIndex().getCol() - 2);
								path = findPortalCell(grid, portalIn, portalOut, start, objectives);
								if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
									lastStep = Direction.LEFT;
									teleporting = true;
									return path;
								}
							}
						}else{
							List<CellNode> westBorder =  grid.getTeleportZoneWest();
							for (CellNode cell : westBorder) {
								if (!cell.isOccupied() && !grid.getCell(grid.getRowCount()-1,cell.getIndex().getCol()).isOccupied()) {
									if(cell.getLocation().getY()>=start.getLocation().getY()-100){
										distressLevel = DistressLevel.LEVEL_THREE;
										path = findPortalCell(grid, cell, grid.getCell(grid.getRowCount()-1,cell.getIndex().getCol()), start, objectives);
										if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
											lastStep = Direction.LEFT;
											teleporting = true;
											return path;
										}
										else {
											continue; //TODO: needs reassestment
										}
									}
								}
							}
						}
					}
				}
			}
		}
		else if(objectives[index].getYDistance(start.getLocation().getY())>GameSettings.HEIGHT*.45  && objectives[index].getXDistance(start.getLocation().getX())<GameSettings.WIDTH*.45){

			//TODO: Incomplete! Implement the rest of the conditions!!
			if(start.getLocation().getY() > GameSettings.HEIGHT*.65){
				List<CellNode> southBorder =  grid.getTeleportZoneSouth();
				for (CellNode cell : southBorder) {
					if (!cell.isOccupied() && cell.getLocation().getX()>=start.getLocation().getX() && !grid.getCell(cell.getIndex().getRow(),grid.getMinCol()).isOccupied()) {
						distressLevel = DistressLevel.LEVEL_THREE;
						path = findPortalCell(grid, cell, grid.getCell(cell.getIndex().getRow(),grid.getMinCol()), start, objectives);
						if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
							lastStep = Direction.DOWN;
							teleporting = true;
							return path;

						} else {
							continue;
						}
					}
				}
			}
			if(start.getLocation().getY() < GameSettings.HEIGHT*.35){
				List<CellNode> northBorder =  grid.getTeleportZoneNorth();
				for (CellNode cell : northBorder) {
					if (!cell.isOccupied() && cell.getLocation().getX()>=start.getLocation().getX() && !grid.getCell(cell.getIndex().getRow(),grid.getColumnCount()-1).isOccupied()) {
						distressLevel = DistressLevel.LEVEL_THREE;
						path = findPortalCell(grid, cell, grid.getCell(cell.getIndex().getRow(),grid.getColumnCount()-1), start, objectives);
						if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
							lastStep = Direction.UP;
							teleporting = true;
							return path;

						} else {
							continue;
						}
					}
				}
			}
		}
		else if(objectives[index].getXDistance(start.getLocation().getX())>GameSettings.WIDTH*.45 && objectives[index].getYDistance(start.getLocation().getY())>GameSettings.HEIGHT*.45){
			new LinkedPath(GET_ASTAR_PATH(controller.getGrid(), start, objectives[0].getCell()),new ArrayList<>());
		}
		return new LinkedPath(GET_ASTAR_PATH(controller.getGrid(), start, objectives[0].getCell()),new ArrayList<>());
	}

	public LinkedPath emergencyTeleport(GridNode grid, CellNode start, CellNode  end) {
		LinkedPath path;

		if(start.getLocation().getX() > GameSettings.WIDTH*.65 && start.getLocation().getY() > GameSettings.HEIGHT*.35 && start.getLocation().getY() < GameSettings.HEIGHT*.65){
			List<CellNode> eastBorder =  grid.getTeleportZoneEast();
			for (CellNode cell : eastBorder) {
				if (!cell.isOccupied() && !grid.getCell(grid.getMinRow(), cell.getIndex().getCol()).isOccupied()) {
					distressLevel = DistressLevel.LEVEL_THREE;
					path = findPortalCell(grid, cell, grid.getCell(grid.getMinRow(), cell.getIndex().getCol()), start, end);
					if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
						lastStep = Direction.RIGHT;
						teleporting = true;
						return path;

					} else {
						continue;
					}
				}
			}
		}
		if(start.getLocation().getX() < GameSettings.WIDTH*.35 && start.getLocation().getY() > GameSettings.HEIGHT*.35 && start.getLocation().getY() < GameSettings.HEIGHT*.65){
			List<CellNode> westBorder =  grid.getTeleportZoneWest();
			for (CellNode cell : westBorder) {
				if (!cell.isOccupied() && !grid.getCell(grid.getRowCount()-1,cell.getIndex().getCol()).isOccupied()) {
					distressLevel = DistressLevel.LEVEL_THREE;
					path = findPortalCell(grid, cell, grid.getCell(grid.getRowCount()-1,cell.getIndex().getCol()), start, end);
					if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
						lastStep = Direction.LEFT;
						teleporting = true;
						return path;

					} else {
						continue;
					}
				}
			}
		}

		if(start.getLocation().getY() > GameSettings.HEIGHT*.65 && start.getLocation().getX() > GameSettings.WIDTH && start.getLocation().getX() < GameSettings.WIDTH*.65){
			List<CellNode> southBorder =  grid.getTeleportZoneSouth();
			for (CellNode cell : southBorder) {
				if (!cell.isOccupied() && !grid.getCell(cell.getIndex().getRow(),grid.getMinCol()).isOccupied()) {
					distressLevel = DistressLevel.LEVEL_THREE;
					path = findPortalCell(grid, cell, grid.getCell(cell.getIndex().getRow(),grid.getMinCol()), start, end);
					if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
						lastStep = Direction.DOWN;
						teleporting = true;
						return path;

					} else {
						continue;
					}
				}
			}
		}
		if(start.getLocation().getY() < GameSettings.HEIGHT*.35 && start.getLocation().getX() > GameSettings.WIDTH && start.getLocation().getX() < GameSettings.WIDTH*.65){
			List<CellNode> northBorder =  grid.getTeleportZoneNorth();
			for (CellNode cell : northBorder) {
				if (!cell.isOccupied() && !grid.getCell(cell.getIndex().getRow(),grid.getColumnCount()-1)  .isOccupied()) {
					distressLevel = DistressLevel.LEVEL_THREE;
					path = findPortalCell(grid, cell, grid.getCell(cell.getIndex().getRow(),grid.getColumnCount()-1), start, end);
					if (!path.getPathOne().isEmpty() && !path.getPathTwo().isEmpty()) {
						lastStep = Direction.UP;
						teleporting = true;
						return path;

					} else {
						continue;
					}
				}
			}
		}

		return new LinkedPath(GET_ASTAR_PATH(controller.getGrid(), start, objectives[0].getCell()),new ArrayList<>());
	}

	private LinkedPath findPortalCell(GridNode grid, CellNode portalIn, CellNode portalOut, CellNode start, Objective[] objectives){

		List<CellNode> pathToPortal = null;
		List<CellNode> pathFromPortal = null;

		PriorityQueue<Distance>  distances = new PriorityQueue<Distance>(objectives.length, new DistanceComparator());

		for (int i = 0; i < getObjectiveCount(); i++) {
			distances.add(new Distance(portalOut,game.getGameObjectController().getObsFruitList().get(i)));
		}

		distressLevel = DistressLevel.LEVEL_THREE;

		if(!portalOut.isOccupied()){

//			for (int i = 0; i < distances.size(); i++) {
//				if (distances.peek() != null) {
//					pathToPortal = GET_ASTAR_PATH(controller.getGrid(), start, portalIn);
//					if (!pathToPortal.isEmpty()) {
//						pathFromPortal = GET_ASTAR_PATH(controller.getGrid(), portalOut,distances.poll().getObject().getCell());
//						return new LinkedPath(pathToPortal, pathFromPortal);
//					}
//				}
//			}
			pathToPortal = GET_ASTAR_PATH(controller.getGrid(), start, portalIn);

			if (!pathToPortal.isEmpty()) {
				pathFromPortal = GET_ASTAR_PATH(controller.getGrid(), portalOut, distances.poll().getObject().getCell());

				if(!pathFromPortal.isEmpty()){
					return new LinkedPath(pathToPortal,pathFromPortal);
				}
				else{
					pathFromPortal = GET_ASTAR_PATH(controller.getGrid(), portalOut, distances.poll().getObject().getCell());

					if(!pathFromPortal.isEmpty()){
						return new LinkedPath(pathToPortal,pathFromPortal);
					}
					else{
						pathFromPortal = GET_ASTAR_PATH(controller.getGrid(), portalOut, distances.poll().getObject().getCell());

						if(!pathFromPortal.isEmpty()){
							return new LinkedPath(pathToPortal,pathFromPortal);
						}
						else{
							pathFromPortal = GET_ASTAR_PATH(controller.getGrid(), portalOut, distances.poll().getObject().getCell());

							if(!pathFromPortal.isEmpty()){
								return new LinkedPath(pathToPortal,pathFromPortal);
							}
						}
					}
				}
			}
		}
		return new LinkedPath(pathToPortal, pathFromPortal);
	}

	private LinkedPath findPortalCell(GridNode grid, CellNode portalIn, CellNode portalOut, CellNode start, CellNode objective){

		List<CellNode> pathToPortal = null;
		List<CellNode> pathFromPortal = null;

		distressLevel = DistressLevel.LEVEL_THREE;

		if(!portalOut.isOccupied()){

			pathToPortal = GET_ASTAR_PATH(controller.getGrid(), start, portalIn);

			if (!pathToPortal.isEmpty()) {
				pathFromPortal = GET_ASTAR_PATH(controller.getGrid(), portalOut, objective);

				if(!pathFromPortal.isEmpty()){
					return new LinkedPath(pathToPortal,pathFromPortal);
				}
				else{

					PriorityQueue<Distance>  distances = new PriorityQueue<Distance>(getObjectiveCount(), new DistanceComparator());

					for (int i = 0; i < getObjectiveCount(); i++) {
						distances.add(new Distance(portalOut,game.getGameObjectController().getObsFruitList().get(i)));
					}

//					for (int i = 0; i < distances.size(); i++) {
//						if (distances.peek() != null) {
//							if (!pathToPortal.isEmpty()) {
//								pathFromPortal = GET_ASTAR_PATH(controller.getGrid(), portalOut, distances.poll().getObject().getCell());
//								return new LinkedPath(pathToPortal, pathFromPortal);
//							}
//						}
//					}
					pathFromPortal = GET_ASTAR_PATH(controller.getGrid(), portalOut, distances.poll().getObject().getCell());

					if (!pathFromPortal.isEmpty()) {
						return new LinkedPath(pathToPortal, pathFromPortal);
					} else {
						pathFromPortal = GET_ASTAR_PATH(controller.getGrid(), portalOut,distances.poll().getObject().getCell());

						if (!pathFromPortal.isEmpty()) {
							return new LinkedPath(pathToPortal, pathFromPortal);
						} else {
							pathFromPortal = GET_ASTAR_PATH(controller.getGrid(), portalOut,distances.poll().getObject().getCell());

							if (!pathFromPortal.isEmpty()) {
								return new LinkedPath(pathToPortal, pathFromPortal);
							} else {
								pathFromPortal = GET_ASTAR_PATH(controller.getGrid(), portalOut,distances.poll().getObject().getCell());

								if (!pathFromPortal.isEmpty()) {
									return new LinkedPath(pathToPortal, pathFromPortal);
								}
							}
						}
					}
				}
			}
		}
		return new LinkedPath(pathToPortal,pathFromPortal);
	}



	/**
	 * TODO: Perform a check to determine if the computed path to the objective is a safe path
	 * by computing a path from the start to the objective and from the objective to the tail of the snake.
	 * The path must be computed as a special path which considers the path to the objective to be an obstacle.
	 * Create a special path made out of special obstacles from the start to goal. The path must be an abstract path. Once
	 * that path is created then compute a path from the goal to the tail of the snake, the path must ignore nodes that
	 * belong to the path from start to goal. If a path can be created from goal to tail then the given a path can
	 * be consider somewhat "Safe"!!. If the path is safe allow the snake to go for the apple. but if the path isnt safe
	 *
	 * @param start
	 * @param goal
	 * @param tail
	 * @return
	 */
	public boolean isPathSafe(CellNode start, CellNode goal, CellNode tail){

		return true;
	}
	/**
	 * Condition which checks the relative location of the player and the calculates which edge
	 * the player is closest too. Once the closest edge is determined we check which cells on that edge
	 * can be reached! if a cell is found the function then checks if the opposite cell at the same row/column
	 * is also accessible if yes a path is created and the player teleports to the opposite side of the screen, at
	 * the specified cell. If not the search will continue until a cell has been found. If no cell the is found
	 * the player will stall until a path is open or until it dies!
	 *
	 * TODO: Make sure a path can be found from the cross polar cell to the cross polar objective!
	 * @param start
	 * @return
	 */


	/**
	 * TODO: Perform a check to determine if the computed path to the objective is a safe path
	 * by computing a path from the start to the objective and from the objective to the tail of the snake.
	 * The path must be computed as a special path which considers the path to the objective to be an obstacle.
	 * Create a special path made out of special obstacles from the start to goal. The path must be an abstract path. Once
	 * that path is created then compute a path from the goal to the tail of the snake, the path must ignore nodes that
	 * belong to the path from start to goal. If a path can be created from goal to tail then the given a path can
	 * be consider somewhat "Safe"!!. If the path is safe allow the snake to go for the apple. but if the path isnt safe
	 *
	 * @param start
	 * @param goal
	 * @param tail
	 * @return
	 */
	private List<CellNode> fetchSafePath(GridNode grid, CellNode start, CellNode goal, CellNode tail,DistressLevel normal) {

		List<CellNode> pathToGoal = GET_ASTAR_PATH(controller.getGrid(),start,goal);

		for(CellNode cell: pathToGoal){
			cell.setCheckBlock(true);
		}

		List<CellNode> pathToTail = GET_ASTAR_PATH(controller.getGrid(),goal,tail);

		for(CellNode cell: pathToGoal){
			cell.setCheckBlock(false);
		}

		if(!pathToGoal.isEmpty() && !pathToTail.isEmpty()){

			return pathToGoal;
		}
		else{

			log("Normal path to goal is empty");

			pathToGoal = GET_ASTAR_PATH(controller.getGrid(),start,goal);

			for(CellNode cell: pathToGoal){
				cell.setCheckBlock(true);
			}

			pathToTail = GET_ASTAR_PATH(controller.getGrid(),goal,tail);

			for(CellNode cell: pathToGoal){
				cell.setCheckBlock(false);
			}
			if(!pathToGoal.isEmpty() && !pathToTail.isEmpty()){

				return pathToGoal;
			}
			else{
				for(CellNode cell: pathToTail){
					cell.setCheckBlock(false);
				}
				return new ArrayList<>();
			}
		}
	}

	public List<CellNode> getShortestPath(List<List<CellNode>> paths) {

		List<CellNode> smallest = paths.get(0);

		int minSize = Integer.MAX_VALUE;

		for (int i = paths.size() - 1; i >= 0; i--) {

			if (paths.get(i).size() < minSize && !paths.get(i).isEmpty()) {

				minSize = paths.get(i).size();
				smallest = paths.get(i);
			}
		}
		return smallest;
	}

	private void showPathToObjective(LinkedPath cells){
		setPathCoordinates(calculateDirection(cells));
		setAllowTrace(true);
		if (logDirections) {
			for (int i = cells.getPathOne().size() - 1; i >= 0; i--) {
				log("Direction: " + cells.getPathOne().get(i).getDirection().toString());
			}
			log("");
			for (int i = cells.getPathTwo().size() - 1; i >= 0; i--) {
				log("Direction: " + cells.getPathTwo().get(i).getDirection().toString());
			}
			log("");
		}
	}

	private void setAllowTrace(boolean state) {
		this.allowTrace = state;
	}

	private LinkedPath calculateDirection(LinkedPath paths) {
		for (CellNode node : paths.getPathOne()) {
			node.setPathCell(true);
			if (node.getParentNode() != null) {
				if (node.getLocation().getX() > node.getParentNode().getLocation().getX()) {
					node.getParentNode().setDirection(Direction.RIGHT);
				} else if (node.getLocation().getX() < node.getParentNode().getLocation().getX()) {
					node.getParentNode().setDirection(Direction.LEFT);
				} else if (node.getLocation().getY() > node.getParentNode().getLocation().getY()) {
					node.getParentNode().setDirection(Direction.DOWN);
				} else if (node.getLocation().getY() < node.getParentNode().getLocation().getY()) {
					node.getParentNode().setDirection(Direction.UP);
				}
			}
		}
		if (!paths.getPathTwo().isEmpty()){
			for (CellNode node : paths.getPathTwo()) {
				node.setPathCell(true);
				if (node.getParentNode() != null) {
					if (node.getLocation().getX() > node.getParentNode().getLocation().getX()) {
						node.getParentNode().setDirection(Direction.RIGHT);
					} else if (node.getLocation().getX() < node.getParentNode().getLocation().getX()) {
						node.getParentNode().setDirection(Direction.LEFT);
					} else if (node.getLocation().getY() > node.getParentNode().getLocation().getY()) {
						node.getParentNode().setDirection(Direction.DOWN);
					} else if (node.getLocation().getY() < node.getParentNode().getLocation().getY()) {
						node.getParentNode().setDirection(Direction.UP);
					}
				}
			}
		}
		paths.getPathOne().get(0).setDirection(lastStep);
		lastStep = Direction.NONE;
		return paths;
	}
	/**
	 * TODO: Build a list containing coordinates and directions.
	 * make the snake move towards the first direction on the list
	 * if the snake moves reaches the coordinate on the list make the
	 * snake take the next turn and so forth:....
	 */
	public void steerAI() {
		CellNode cell = null;
		if (pathCoordinates != null && allowTrace) {
			for (int index = 0; index < pathCoordinates.getPathOne().size(); index++) {
				cell = pathCoordinates.getPathOne().get(index);
				if (cell.getBoundsCheck().contains(snakeAI.getBounds())) {
					switch (cell.getDirection()) {
					case DOWN:
						game.getGameLoader().getPlayerTwo().setDirectCoordinates(PlayerMovement.MOVE_DOWN);
						cell.setPathCell(false);
						break;
					case LEFT:
						game.getGameLoader().getPlayerTwo().setDirectCoordinates(PlayerMovement.MOVE_LEFT);
						cell.setPathCell(false);
						break;
					case RIGHT:
						game.getGameLoader().getPlayerTwo().setDirectCoordinates(PlayerMovement.MOVE_RIGHT);
						cell.setPathCell(false);
						break;
					case UP:
						game.getGameLoader().getPlayerTwo().setDirectCoordinates(PlayerMovement.MOVE_UP);
						cell.setPathCell(false);
						break;
					case NONE:
						break;
					}
					break;
				}
			}
			if(!pathCoordinates.getPathTwo().isEmpty()){
				for (int index = 0; index < pathCoordinates.getPathTwo().size(); index++) {
					cell = pathCoordinates.getPathTwo().get(index);
					if (cell.getBoundsCheck().contains(snakeAI.getBounds())) {
						switch (cell.getDirection()) {
						case DOWN:
							game.getGameLoader().getPlayerTwo().setDirectCoordinates(PlayerMovement.MOVE_DOWN);
							cell.setPathCell(false);
							break;
						case LEFT:
							game.getGameLoader().getPlayerTwo().setDirectCoordinates(PlayerMovement.MOVE_LEFT);
							cell.setPathCell(false);
							break;
						case RIGHT:
							game.getGameLoader().getPlayerTwo().setDirectCoordinates(PlayerMovement.MOVE_RIGHT);
							cell.setPathCell(false);
							break;
						case UP:
							game.getGameLoader().getPlayerTwo().setDirectCoordinates(PlayerMovement.MOVE_UP);
							cell.setPathCell(false);
							break;
						case NONE:
							break;
						}
						break;
					}
				}
			}
		}
	}

	/**
	 * Method which under certain conditions will activate the speed boost of
	 * the snake
	 *
	 * @param random
	 */
	public void addRandomBoost(boolean random) {
		if (!trackingTail) {
			if (state == ActionType.FREE_MODE) {
				if (random && rand.nextInt(randomBoost) != 0) {
					return;
				}
				if (snakeAI != null) {
					applyThrust();
				}
			} else if (state == ActionType.FIND_PATH) {
				if (random && rand.nextInt(randomBoost) != 0) {
					return;
				}
				if (snakeAI != null) {
					applyThrust();
				}
			}
		}
	}

	private void log(String str) {
		System.out.println(str+"\n");
	}

	/*
	 * Method which attempts to determine the best course of action in order to
	 * move towards the objective! The method will first check if the x distance
	 * is less or greater than the y distance and based on that it will decide
	 * to perform a horizontal or vertical move. if the method to be perform is
	 * a vertical move the method will check if the objective is above or below
	 * and then perform a move based on the objectives coordinates!
	 */
	private void createPath() {
		switch (state) {
		case DODGE_OBSTACLES:
			break;
		case FIND_PATH:
			break;
		case FREE_MODE:
			break;
		default:
			break;
		}
	}

	/**
	 * Method which performs actions based on the current location of the snake
	 * and the objective! if the snake is within a predetermined threshold the
	 * snake will perform the appropriate turn in order to collect the
	 * objective!
	 */
	private void performLocationBasedAction() {
		switch (state) {
		case DODGE_OBSTACLES:
			break;
		case FIND_PATH:
			steerAI();
			break;
		case FREE_MODE:
			break;
		default:
			break;
		}
	}

	private void applyThrust() {
		if (game.getEnergyBarTwo().getEnergyLevel() >150) {
			if (snakeAI.isAllowThrust() && !snakeAI.getSpeedThrust()) {
				snakeAI.setSpeedThrust(true);
			}
		}
		if (game.getEnergyBarTwo().getEnergyLevel() < 50) {
			snakeAI.setSpeedThrust(false);
		}
	}

	/**
	 * Class which holds the distance and the nearest object and the object!
	 *
	 * @author Eudy Contreras
	 *
	 */
	private class Distance {

		private Double distance;
		private AbstractObject object;

		public Distance(double distance, AbstractObject object) {
			this.distance = distance;
			this.object = object;
		}
		public Distance(CellNode start, AbstractObject objective){
			this.distance = calculateManhathanDistance(start.getLocation().getX(), objective.getX(),start.getLocation().getY(),objective.getY());
			this.object = objective;
		}
		public double getDistance() {
			return distance;
		}
		public AbstractObject getObject() {
			return object;
		}
	}

	public class Objective implements Comparable<Objective>{

		private double x;
		private double y;
		private double normalDistance;
		private CellNode cell;
		private PlayerTwo snakeAI;
		private AbstractObject object;

		public Objective(PlayerTwo snake, AbstractObject object) {
			this.x = object.getX();
			this.y = object.getY();
			this.snakeAI = snake;
			this.object = object;
			this.cell = object.getCell();
			this.computeDistances();
		}

		public double getX(){
			return x;
		}

		public double getY(){
			return y;
		}

		private void computeDistances(){
			this.normalDistance = calculateManhathanDistance(snakeAI.getX(), object.getX(), snakeAI.getY(), object.getY());
		}

		public double getXDistance(double x){
			return Math.abs(x-this.x);
		}

		public double getYDistance(double y){
			return Math.abs(y-this.y);
		}

		public double getDistance(double x, double y){
			return Math.abs(x - this.x)+Math.abs(y - this.y);
		}

		public double getInterpolarXDistance(double x){
			double xDistance;

			if(this.x > x){
				xDistance = x + (GameSettings.WIDTH-this.x);
			}
			else{
				xDistance =this.x + (GameSettings.WIDTH-x);
			}
			return xDistance;
		}

		public double getInterpolarYDistance(double y){
			double yDistance;

			if(this.y > y){
				yDistance = y + (GameSettings.HEIGHT-this.y);
			}
			else{
				yDistance = this.y +(GameSettings.HEIGHT-y);
			}
			return yDistance;
		}

		public double getInterpolarDistance(double x, double y){
			double dX = getInterpolarXDistance(x);
			double dY = getInterpolarYDistance(y);

			return getDistance(dX,dY);
		}

		public ObjectivePosition getRelativeLocation(CellNode start){
			if(x > start.getLocation().getX()){
				if(y > start.getLocation().getY()){
					return ObjectivePosition.SOUTH_EAST;
				}
				else if (y < start.getLocation().getY()){
					return ObjectivePosition.NORTH_EAST;
				}
				else{
					return ObjectivePosition.EAST;
				}
			}
			else if(x < start.getLocation().getX()){
				if(y > start.getLocation().getY()){
					return ObjectivePosition.SOUTH_WEST;
				}
				else if (y <  start.getLocation().getY()){
					return ObjectivePosition.NORTH_WEST;
				}
				else{
					return ObjectivePosition.WEST;
				}
			}
			else if(x == start.getLocation().getX()){
				if(y > start.getLocation().getY()){
					return ObjectivePosition.SOUTH;
				}
				else{
					return ObjectivePosition.NORTH;
				}
			}
			return location;
		}

		public double getDistance() {
			return normalDistance;
		}

		public CellNode getCell() {
			return cell;
		}

		public AbstractObject getObject(){
			return object;
		}

		@Override
		public String toString(){
			return normalDistance+"";
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Objective other = (Objective) obj;
			if (this.object.getX() != other.object.getX() && this.object.getY() != other.object.getY()) {
				return false;
			}
			if (this.normalDistance != other.normalDistance) {
				return false;
			}
			return true;
		}

		@Override
		public int compareTo(Objective distance) {
			return Double.compare(this.getDistance(),distance.getDistance());
		}
	}
	private class LinkedPath{

		private List<CellNode> pathOne;
		private List<CellNode> pathTwo;

		public LinkedPath() {
			super();
			this.pathOne = new LinkedList<>();
			this.pathTwo = new LinkedList<>();
		}

		public LinkedPath(List<CellNode> pathOne, List<CellNode> pathTwo) {
			super();
			if (pathOne != null && pathTwo != null) {
				this.pathOne = !pathOne.isEmpty() ? pathOne : new LinkedList<>();
				this.pathTwo = !pathTwo.isEmpty() ? pathTwo : new LinkedList<>();
			}
			else{
				this.pathOne = new LinkedList<>();
				this.pathTwo = new LinkedList<>();
			}
		}

		public List<CellNode> getPathOne() {
			return pathOne;
		}

		public List<CellNode> getPathTwo() {
			return pathTwo;
		}

	}
	/**
	 * A program which takes a starting cell and the cell to flee from!
	 * The program computes the which cell is farthest away from the cell it wishes to flee from
	 * and computes a path to that cell!
	 * @author Eudy
	 *
	 */
	public class AFleePathFinding{

	}
	public double getX() {
		return snakeAI.getX();
	}

	public double getY() {
		return snakeAI.getY();
	}

	public double getWidth() {
		return snakeAI.getAIBounds().getWidth();
	}

	public double getHeight() {
		return snakeAI.getAIBounds().getHeight();
	}

	public void setPlayer() {
		this.snakeAI = null;
		this.snakeAI = game.getGameLoader().getPlayerTwo();
	}

	private void setPathCoordinates(LinkedPath coordinates){
		this.pathCoordinates = coordinates;
	}
	public PlayerMovement getDirection() {
		return snakeAI.getCurrentDirection();
	}

	public ObjectivePosition getLocation() {
		return location;
	}

	public Rectangle2D getCollisionBounds() {
		return snakeAI.getAIBounds();
	}

	public void setLocation(ObjectivePosition location) {
		this.location = location;
	}
	private enum SearchType{
		CLOSEST_OBJECTIVE, SHORTEST_PATH;
	}
	private enum PathType{
		LONGEST_PATH,SHORTEST_PATH
	}
	private enum TieBreaker{
		PATH,CROSS, NONE
	}

	private enum ObjectivePosition {
		NORTH, SOUTH, WEST, EAST, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST
	}

	public enum ActionType {
		FREE_MODE, STALL, FIND_PATH, DODGE_OBSTACLES
	}

	private enum HeuristicType{
		MANHATHAN,EUCLIDIAN,CUSTOM_EUCLUDIAN,
	}

	public enum DistressLevel{
		LEVEL_ONE,LEVEL_TWO,LEVEL_THREE,SAFETY_CHECK,CAUTIOUS_CHECK_EMERGENCY
	}

	public int getObjectiveCount(){
		return game.getGameObjectController().getObsFruitList().size();
	}

	public double calculateDistance(double fromX, double toX, double fromY, double toY) {
		return Math.hypot(fromX - toX, fromY - toY);
	}

	public double calculateManhathanDistance(double fromX, double toX, double fromY, double toY) {
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
	private class DistanceComparator implements Comparator<Distance>{
		@Override
		public int compare(Distance a, Distance b){
			return Double.compare(a.getDistance(), b.getDistance());
		}
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
	public class ObjectiveComparator implements Comparator<Objective> {
		@Override
		public int compare(Objective a, Objective b) {
			if(pathType == PathType.SHORTEST_PATH){
				return Double.compare(a.getDistance(), b.getDistance());
			}
			else{
				return Double.compare(b.getDistance(), a.getDistance());
			}
		}
	}
}
