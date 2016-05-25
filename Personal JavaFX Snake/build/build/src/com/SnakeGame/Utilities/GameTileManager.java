package com.SnakeGame.Utilities;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.SnakeGame.AbstractModels.AbstractTile;
import com.SnakeGame.FrameWork.SnakeGame;

/**
 * This manager class is responsible for managing, updating moving, displaying,
 * adding and removing different level objects each level object should have its
 * own separate list according to the amount of objects that will be rendered in
 * the game.
 *
 * @author Eudy Contreras
 *
 */
public class GameTileManager {

	private List<AbstractTile> tile;
	private List<AbstractTile> block;
	private List<AbstractTile> trap;
	private AbstractTile tempTile;
	private AbstractTile tempTrap;
	private AbstractTile tempBlock;
	private SnakeGame game;

	public GameTileManager(SnakeGame game) {
		this.game = game;
		this.tile = new LinkedList<AbstractTile>();
		this.block = new LinkedList<AbstractTile>();
		this.trap = new LinkedList<AbstractTile>();
	}

	public void addTile(AbstractTile tile) {
		this.tile.add(tile);
	}

	public void addBlock(AbstractTile tile) {
		this.block.add(tile);
	}

	public void addTrap(AbstractTile tile) {
		this.trap.add(tile);
	}
	public List<AbstractTile> getTile() {
		return tile;
	}

	public List<AbstractTile> getBlock() {
		return block;
	}

	public List<AbstractTile> getTrap() {
		return trap;
	}
	public void addObjectAsArray(AbstractTile... tl) {
		if (tl.length > 1) {
			tile.addAll(Arrays.asList(tl));
		} else {
			tile.add(tl[0]);
		}
	}

	/**
	 * This method updates the objects using and iterator which only iterates
	 * through every object once. faster in some cases but it makes it so the
	 * list can only be modified through this method or else an exception will
	 * be thrown
	 */
	public void updateAll() {
		for (Iterator<AbstractTile> tileList = tile.iterator(); tileList.hasNext();) {
			AbstractTile tempTile = tileList.next();
			tempTile.updateUI();
			tempTile.move();
			tempTile.checkCollision();
			if (tempTile.isBehindCharacter() || !tempTile.isAlive()) {
				game.getGameRoot().getChildren().remove(tempTile.getView());
				game.getLevelLayer().getChildren().remove(tempTile.getView());
				game.getSecondLayer().getChildren().remove(tempTile.getView());
				tileList.remove();
				continue;
			}

		}
	}

	/**
	 * Individually updates the tiles
	 */
	public void updateTiles() {
		for (int i = 0; i < tile.size(); i++) {
			tempTile = tile.get(i);
			tempTile.updateUI();
			tempTile.move();
			tempTile.checkCollision();
		}
	}

	/**
	 * Individually updates the blocks
	 */
	public void updateBlock() {
		for (int i = 0; i < block.size(); i++) {
			tempBlock = block.get(i);
			tempBlock.updateUI();
			tempBlock.move();
			tempTile.checkCollision();
		}
	}

	/**
	 * Individually updates the traps
	 */
	public void updateTrap() {
		for (int i = 0; i < trap.size(); i++) {
			tempTrap = trap.get(i);
			tempTrap.updateUI();
			tempTrap.move();
			tempTile.checkCollision();
		}
	}

	/**
	 * This method loops through all the items and checks which item is ready to
	 * be removed and removes it.
	 */
	public void checkIfRemovable() {
		for (int i = 0; i < tile.size(); i++) {
			tempTile = tile.get(i);
			if (tempTile.isBehindCharacter() || !tempTile.isAlive()) {
				game.getGameRoot().getChildren().remove(tempTile.getView());
				game.getSecondLayer().getChildren().remove(tempTile.getView());
				tile.remove(i);
			}
		}
		for (int i = 0; i < trap.size(); i++) {
			tempTrap = trap.get(i);
			if (tempTrap.isBehindCharacter() || !tempTrap.isAlive()) {
				game.getGameRoot().getChildren().remove(tempTrap.getView());
				game.getSecondLayer().getChildren().remove(tempTrap.getView());
				trap.remove(i);
			}
		}
		for (int i = 0; i < block.size(); i++) {
			tempBlock = block.get(i);
			if (tempBlock.isBehindCharacter() || !tempBlock.isAlive()) {
				game.getGameRoot().getChildren().remove(tempBlock.getView());
				game.getSecondLayer().getChildren().remove(tempBlock.getView());
				block.remove(i);
			}
		}
	}

	public void clearAll() {
		tile.clear();
		block.clear();
		trap.clear();
	}

}