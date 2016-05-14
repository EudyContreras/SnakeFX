package com.SnakeGame.PlayerOne;

import com.SnakeGame.FrameWork.OrgGameObject;
import com.SnakeGame.FrameWork.PlayerMovement;
import com.SnakeGame.FrameWork.Settings;
import com.SnakeGame.FrameWork.SnakeGame;
import com.SnakeGame.GameObjects.Tile;
import com.SnakeGame.ObjectIDs.GameObjectID;
import com.SnakeGame.ObjectIDs.LevelObjectID;

import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class OrgSnakeFangs extends OrgGameObject {
	int index;
	int counter = 0;
	boolean stop = false;
	float offsetX = 0;
	float offsetY = 0;
	PlayerOneEatTrigger trigger;
	SnakeGame game;
	OrgPlayer snake;
	OrgGameSectionManager sectManager;
	OrgSnakeHead snakeHead;
	OrgGameObjectManager gom;

	public OrgSnakeFangs(OrgSnakeHead snakeHead, OrgPlayer snake, SnakeGame game, Pane layer, Circle node, float x, float y, GameObjectID id,
			PlayerMovement Direction) {
		super(game, layer, node, y, y, id);
		this.snakeHead = snakeHead;
		this.snake = snake;
		this.game = game;
		this.gom = game.getOrgObjectManager();
		this.sectManager = game.getOrgSectManager();
		if (Direction == PlayerMovement.MOVE_UP) {
			this.y = (float) (y - this.circle.getRadius() * 3);
			this.x = x;
			this.velX = snake.getVelX();
			this.velY = snake.getVelY();
		} else if (Direction == PlayerMovement.MOVE_DOWN) {
			this.y = (float) (y + this.circle.getRadius() * 3);
			this.x = x;
			this.velX = snake.getVelX();
			this.velY = snake.getVelY();
		} else if (Direction == PlayerMovement.MOVE_LEFT) {
			this.x = (float) (x - this.circle.getRadius() * 3);
			this.y = y;
			this.velX = snake.getVelX();
			this.velY = snake.getVelY();
		} else if (Direction == PlayerMovement.MOVE_RIGHT) {
			this.x = (float) (x + this.circle.getRadius() * 3);
			this.y = y;
			this.velX = snake.getVelX();
			this.velY = snake.getVelY();
		} else if (Direction == PlayerMovement.STANDING_STILL) {
			this.y = (float) (y + this.circle.getRadius() * 3);
			this.x = x;
			this.velX = snake.getVelX();
			this.velY = snake.getVelY();
		}
		if (Settings.DEBUG_MODE) {
			this.circle.setStroke(Color.WHITE);
			this.circle.setStrokeWidth(3);
		}
		this.game.getloader().spawnSnakeFood();
		this.game.getloader().spawnSnakeFood();
	}

	public void move() {
		if (OrgPlayer.DEAD == false) {
			this.index = sectManager.getSectionList().size() - 1;
		}
		checkOffset();
		x = (float) (snakeHead.getX() + offsetX);
		y = (float) (snakeHead.getY() + offsetY);

	}
//	public void move() {
//		if (OrgPlayer.DEAD == false) {
//			this.index = sectManager.getSectionList().size() - 1;
//		}
//		super.move();
//		if (snake.getCurrentDirection() == PlayerMovement.MOVE_UP) {
//			this.y = (float) (snake.getY() - this.circle.getRadius() * 3);
//			this.x = snake.getX();
//		} else if (snake.getCurrentDirection() == PlayerMovement.MOVE_DOWN) {
//			this.y = (float) (snake.getY() + this.circle.getRadius() * 3);
//			this.x = snake.getX();
//		} else if (snake.getCurrentDirection() == PlayerMovement.MOVE_LEFT) {
//			this.x = (float) (snake.getX() - this.circle.getRadius() * 3);
//			this.y = snake.getY();
//		} else if (snake.getCurrentDirection() == PlayerMovement.MOVE_RIGHT) {
//			this.x = (float) (snake.getX() + this.circle.getRadius() * 3);
//			this.y = snake.getY();
//		}
//	}
	public void checkOffset(){
		if(snake.direction == PlayerMovement.MOVE_UP){
			this.offsetY = -20;
			this.offsetX = 0;
		}
		else if(snake.direction == PlayerMovement.MOVE_DOWN){
			this.offsetY = 20;
			this.offsetX = 0;
		}
		else if(snake.direction == PlayerMovement.MOVE_LEFT){
			this.offsetX = -20;
			this.offsetY = 0;
		}
		else if(snake.direction == PlayerMovement.MOVE_RIGHT){
			this.offsetX = 20;
			this.offsetY = 0;
		}
	}
	public boolean isApproximate(float tail_X, double sect_X, float tail_Y, double sect_Y) {
		double distance = Math.sqrt((tail_X - sect_X) * (tail_X - sect_X) + (tail_Y - sect_Y) * (tail_Y - sect_Y));
		if (distance > 10) {
			return true;
		}
		return false;
	}

	public void checkRemovability() {
		killTheSnake();
	}

	public void checkCollision() {
		for (int i = 0; i < sectManager.getSectionList().size(); i++) {
			OrgSectionMain tempObject = sectManager.getSectionList().get(i);
			if (tempObject.getId() == GameObjectID.SnakeSection) {
				if (tempObject.getNumericID() > 1) {
					if (getRadialBounds().intersects(tempObject.getRadialBounds())) {
						if (tempObject.numericID != 0 && tempObject.numericID != 1 && tempObject.numericID != 2) {
							snake.die();
						}
					}
				}
			}
		}
		for (int i = 0; i < game.getloader().tileManager.tile.size(); i++) {
			Tile tempTile = game.getloader().tileManager.tile.get(i);
			if (tempTile.getId() == LevelObjectID.fence) {
				if (!Settings.ALLOW_TELEPORT) {
					if (getCollisionBounds().intersects(tempTile.getCollisionBounds())) {
						snake.die();
					}
				}
			}
		}
	}

	public void killTheSnake() {
		if (OrgPlayer.DEAD == true) {
			counter++;
			if (sectManager.getSectionList().size() > 0) {
				if (counter == 5) {
					OrgSectionMain sectToKill = sectManager.getSectionList().get(index);
					sectToKill.die();
					counter = 0;
					index--;
					if (index <= 0) {
						index = 0;
						if (!stop) {
							snake.showTheSkull = true;
							snake.addBones();
							stop = true;
						}
					}
				}
			} else {
				if (!stop) {
					index = 0;
					snake.showTheSkull = true;
					snake.addBones();
					stop = true;
				}
			}
		}
	}

	public Bounds getCollisionBounds() {
		return this.circle.getBoundsInParent();
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;

	}
	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;

	}
}