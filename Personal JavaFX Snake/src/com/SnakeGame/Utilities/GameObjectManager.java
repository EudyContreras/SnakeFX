package com.SnakeGame.Utilities;

import java.util.Iterator;
import java.util.LinkedList;

import com.SnakeGame.FrameWork.GameObject;
import com.SnakeGame.FrameWork.SnakeGame;
import com.SnakeGame.ObjectIDs.GameObjectID;

import javafx.scene.canvas.GraphicsContext;

/**
 * This manager class is the core of every game object and is responsible for
 * updating, drawing, adding physics, animating, removing, moving every object
 * and checking whether the objects is alive or not meaning no longer used. the
 * objects updated by this class are mob objects meaning objects that move,
 * interact and collide.
 *
 * @author Eudy Contreras
 *
 */
public class GameObjectManager {

	private LinkedList<GameObject> object;
	private GameObject tempObject;
	private SnakeGame game;

	public GameObjectManager(SnakeGame gameJavaFX) {
		this.game = gameJavaFX;
		initialize();
	}

	public void initialize() {
		this.object = new LinkedList<GameObject>();
	}

	/**
	 * This method updates the objects using and iterator which only iterates
	 * through every object once. faster in some cases but it makes it so the
	 * list can only be modified through this method or else an exception will
	 * be thrown
	 */
	public void update(GraphicsContext gc, long timePassed) {
		Iterator<? extends GameObject> spriteIter = object.iterator();
		while (spriteIter.hasNext()) {
			GameObject sprite = spriteIter.next();
			sprite.updateUI();
			sprite.addPhysics();
			sprite.draw(gc);
			sprite.move();
			sprite.checkRemovability();
			if (sprite.isRemovable() || !sprite.isAlive()) {
				sprite.removeFromLayer();
				spriteIter.remove();
				continue;
			}
		}
	}

	/**
	 * Method used to update every object in the game. this method uses a
	 * conventional for loop and allows the list to be modified from an outside
	 * source without provoking a break.
	 */
	public void updateAll(GraphicsContext gc, long timePassed) {

		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);
			tempObject.updateUI();
			tempObject.addPhysics();
			tempObject.draw(gc);
			tempObject.move();
			tempObject.updateAnimation(timePassed);
			tempObject.checkRemovability();
			if (tempObject.isRemovable() || !tempObject.isAlive()) {
				tempObject.removeFromLayer();
				object.remove(i);
			}
		}
	}

	/**
	 * Method used to explicitly update the graphics
	 */
	public void updateUI() {

		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);
			tempObject.updateUI();
		}
	}

	/**
	 * Method used to explicitly update animations
	 */
	public void updateAnimation(long timePassed) {

		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);
			tempObject.updateAnimation(timePassed);
		}
	}

	/**
	 * Method used to explicitly move the objects
	 */
	public void move() {

		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);
			tempObject.move();
		}
	}

	/**
	 * Method used to explicitly draw the graphics
	 */
	public void draw(GraphicsContext gc) {

		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);
			tempObject.draw(gc);
		}
	}

	/**
	 * Method used to explicitly add physics to the objects
	 */
	public void addPhysics() {

		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);
			tempObject.addPhysics();
		}
	}

	/**
	 * Method used to check if the object should be removed
	 */
	public void checkIfRemoveable() {
		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);
			tempObject.checkRemovability();
		}
	}

	/**
	 * Method used to check if the object has collied with another object
	 */
	public void checkCollisions() {
		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);
			tempObject.checkCollision();
		}
	}

	/**
	 * Check the status of the parent node
	 */
	public void checkStatus() {
		if (game.getPlayfieldLayer().getChildren().size() > 50) {
			game.getPlayfieldLayer().getChildren().remove(2, 35);
		}
	}

	/**
	 * Procedurally places the objects in the level
	 */
	public void procedurallyCreateLevel() {
		Iterator<? extends GameObject> spriteIter = object.iterator();
		while (spriteIter.hasNext()) {
			GameObject sprite = spriteIter.next();
			sprite.createLevel();
		}
	}

	public LinkedList<GameObject> getObjectList() {
		return object;
	}

	public void addObject(GameObject object) {
		this.object.add(object);
	}

	public void removeObject(GameObject object) {
		this.object.remove(object);
	}

	public void clearAll() {
		this.object.clear();
	}

	public GameObject findObject(GameObjectID id) {
		for (GameObject go : object) {
			if (go.getId() == id) {
				return go;
			}
		}
		return null;
	}

}