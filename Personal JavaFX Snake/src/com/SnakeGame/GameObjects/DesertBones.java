
package com.SnakeGame.GameObjects;

import com.SnakeGame.AbstractModels.AbstractTile;
import com.SnakeGame.FrameWork.GameManager;
import com.SnakeGame.FrameWork.GameSettings;
import com.SnakeGame.IDEnums.GameLevelObjectID;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Every static object or esthetic object in the game such as walls, boxes etc
 * is considered a tile. This class is the main tile class and can be used for
 * creating any level object.
 *
 * @author Eudy Contreras
 *
 */
public class DesertBones extends AbstractTile {
	private Rectangle2D collisionBounds;
	private GameManager game;

	public DesertBones(GameManager game, float x, float y, float velX, float velY, Image image, GameLevelObjectID id) {
		super(x, y, image, id);
		this.game = game;
		this.velX = velX;
		this.velY = velY;
		this.view.setTranslateX(x);
		this.view.setTranslateY(y);
//		if(RandomGenerator.getRNG(1, 3) == 3){
//			this.view.setImage(GameLevelImage.desert_skull_alt);
//			this.setX(x -40);
//			this.setY(y +10);
//		}
		draw();
	}

	public DesertBones(GameManager game, float x, float y, float velX, float velY, Image image) {
		super(x, y, image);
		this.game = game;
		this.velX = velX;
		this.velY = velY;
		this.view.setTranslateX(x);
		this.view.setTranslateY(y);
		draw();
	}

	/**
	 * Moves this object
	 */
	public void move() {
		x = x + velX;
	}
	/**
	 * Draws a bounding box
	 */
	public void draw() {
		drawBoundingBox();
	}
	/**
	 * Draws the bounding box of this object for debugging purposes
	 */
	public void drawBoundingBox() {

		if (GameSettings.DEBUG_MODE) {
			Rectangle bounds = new Rectangle(x, y + 30, width - 30, height - 30);
			bounds.setStroke(Color.WHITE);
			bounds.setFill(Color.TRANSPARENT);
			bounds.setStrokeWidth(3);
			game.getSeventhLayer().getChildren().add(bounds);

		}
	}
	/**
	 * This methods will return specified bounds of this object
	 * based on coordinates and dimensions.
	 */
	public Rectangle2D getBounds() {
		return collisionBounds;
	}

	public Rectangle2D getBoundsTop() {
		return new Rectangle2D(x + 20, y, width - 40, height);
	}

	public Rectangle2D getBoundsRight() {
		return new Rectangle2D(x + width - 20, y + 10, 20, height - 10);
	}

	public Rectangle2D getBoundsLeft() {
		return new Rectangle2D(x, y + 10, 20, height - 10);
	}

	public Bounds getCollisionBounds() {
		return this.view.getBoundsInParent();
	}

}
