package com.SnakeGame.PlayerTwo;

import com.SnakeGame.AbstractModels.AbstractSection;
import com.SnakeGame.DebrisEffects.DirtDisplacement;
import com.SnakeGame.DebrisEffects.SectionDisintegration;
import com.SnakeGame.FrameWork.GameManager;
import com.SnakeGame.FrameWork.GameSettings;
import com.SnakeGame.FrameWork.PlayerMovement;
import com.SnakeGame.IDEnums.GameObjectID;
import com.SnakeGame.IDEnums.GameStateID;
import com.SnakeGame.ImageBanks.GameImageBank;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class PlayerTwoSection extends AbstractSection {
	private double particleLife;
	private double particleSize;
	private double fadeValue = 1.0;
	private boolean fade = false;
	private boolean blowUp = true;
	private int dirtDelay = 10;
	private PlayerTwo playerTwo;
	private GameManager game;
	private Circle bones;
	private AbstractSection previousSection;
	private PlayerTwoSectionManager sectManager;

	public PlayerTwoSection(PlayerTwo snake, GameManager game, Pane layer, Node node, double x, double y, GameObjectID id,
			PlayerMovement Direction, int numericID) {
		super(game, layer, node, id);
		this.game = game;
		this.playerTwo = snake;
		this.numericID = numericID;
		this.sectManager = game.getSectManagerTwo();
		if (this.numericID <= 0) {
			if (Direction == PlayerMovement.MOVE_UP) {
				this.setLastDirection(Direction);
				this.y = y + this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
				this.x = x;
				this.r = snake.getR();
				this.velX = snake.getVelX();
				this.velY = snake.getVelY();
				snake.setNeighbor(this);
			} else if (Direction == PlayerMovement.MOVE_DOWN) {
				this.setLastDirection(Direction);
				this.y = y - this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
				this.x = x;
				this.r = snake.getR();
				this.velX = snake.getVelX();
				this.velY = snake.getVelY();
				snake.setNeighbor(this);
			} else if (Direction == PlayerMovement.MOVE_LEFT) {
				this.setLastDirection(Direction);
				this.x = x + this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
				this.y = y;
				this.r = snake.getR();
				this.velX = snake.getVelX();
				this.velY = snake.getVelY();
				snake.setNeighbor(this);
			} else if (Direction == PlayerMovement.MOVE_RIGHT) {
				this.setLastDirection(Direction);
				this.x = x - this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
				this.y = y;
				this.r = snake.getR();
				this.velX = snake.getVelX();
				this.velY = snake.getVelY();
				snake.setNeighbor(this);
			} else if (Direction == PlayerMovement.STANDING_STILL) {
				this.setLastDirection(Direction);
				this.x = x - this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
				this.y = y;
				this.r = snake.getR();
				this.velX = snake.getVelX();
				this.velY = snake.getVelY();
				snake.setNeighbor(this);
			}
		} else if (this.numericID > 0) {
			for (int i = sectManager.getSectionList().size() - 1; i >= 0; i--) {
				AbstractSection previousSect = sectManager.getSectionList().get(i);
				if (previousSect.getNumericID() == this.numericID - 1) {
					previousSection = previousSect;
					switch (previousSect.getLastDirection()) {
					case MOVE_UP:
						setLastDirection(PlayerMovement.MOVE_UP);
						this.y = previousSect.getY() + this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
						this.x = previousSect.getX();
						this.r = previousSect.getR();
						this.velX = previousSect.getVelX();
						this.velY = previousSect.getVelY();
						break;
					case MOVE_DOWN:
						setLastDirection(PlayerMovement.MOVE_DOWN);
						this.y = previousSect.getY() - this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
						this.x = previousSect.getX();
						this.r = previousSect.getR();
						this.velX = previousSect.getVelX();
						this.velY = previousSect.getVelY();
						break;
					case MOVE_LEFT:
						setLastDirection(PlayerMovement.MOVE_LEFT);
						this.x = previousSect.getX() + this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
						this.y = previousSect.getY();
						this.r = previousSect.getR();
						this.velX = previousSect.getVelX();
						this.velY = previousSect.getVelY();
						break;
					case MOVE_RIGHT:
						setLastDirection(PlayerMovement.MOVE_RIGHT);
						this.x = previousSect.getX() - this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
						this.y = previousSect.getY();
						this.r = previousSect.getR();
						this.velX = previousSect.getVelX();
						this.velY = previousSect.getVelY();
						break;
					case STANDING_STILL:
						setLastDirection(PlayerMovement.STANDING_STILL);
						this.x = previousSect.getX() - this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
						this.y = previousSect.getY();
						this.r = previousSect.getR();
						this.velX = previousSect.getVelX();
						this.velY = previousSect.getVelY();
						break;
					}
				}
			}
		}
	}

	public void move() {
		this.circle.setRadius(GameSettings.PLAYER_TWO_SIZE);
		checkBounds();
		disguiseLast();
		sectionAdjustment();
		if (PlayerTwo.DEAD == false && PlayerTwo.LEVEL_COMPLETED == false && PlayerTwo.KEEP_MOVING && game.getStateID()!=GameStateID.GAME_MENU)
			super.move();
		if (lastPosition.size() > 0) {
			if (x == lastPosition.get(0).getX() && y == lastPosition.get(0).getY()) {
				removeLatestLocation();
				if (lastDirection.get(0) == PlayerMovement.MOVE_UP) {
					setLastDirection(PlayerMovement.MOVE_UP);
					removeLatestDirection();
					velX = 0;
					velY = -GameSettings.SNAKE_TWO_SPEED;
					r = 180;
					sectManager.addNewCoordinates(new Point2D(x, y), PlayerMovement.MOVE_UP, this.numericID + 1);
				} else if (lastDirection.get(0) == PlayerMovement.MOVE_DOWN) {
					setLastDirection(PlayerMovement.MOVE_DOWN);
					removeLatestDirection();
					velX = 0;
					velY = GameSettings.SNAKE_TWO_SPEED;
					r = 0;
					sectManager.addNewCoordinates(new Point2D(x, y), PlayerMovement.MOVE_DOWN, this.numericID + 1);
				} else if (lastDirection.get(0) == PlayerMovement.MOVE_LEFT) {
					setLastDirection(PlayerMovement.MOVE_LEFT);
					removeLatestDirection();
					velY = 0;
					velX = -GameSettings.SNAKE_TWO_SPEED;
					r = 90;
					sectManager.addNewCoordinates(new Point2D(x, y), PlayerMovement.MOVE_LEFT, this.numericID + 1);
				} else if (lastDirection.get(0) == PlayerMovement.MOVE_RIGHT) {
					setLastDirection(PlayerMovement.MOVE_RIGHT);
					removeLatestDirection();
					velY = 0;
					velX = GameSettings.SNAKE_TWO_SPEED;
					r = -90;
					sectManager.addNewCoordinates(new Point2D(x, y), PlayerMovement.MOVE_RIGHT, this.numericID + 1);
				}
			}
		}
	}
	public void logicUpdate(){
		if(GameSettings.ALLOW_DIRT)
			updateDirt();
		if(playerTwo.getSpeedThrust())
			updateSpeedDirt();
	}
	public void updateDirt() {
		if ((this.numericID & 1) == 0) {
			dirtDelay--;
			if (dirtDelay <= 0) {
				if (PlayerTwo.KEEP_MOVING) {
					displaceDirt(x + width / 2, y + height / 2, 18, 18);
					dirtDelay = 10;
				}
			}
		}
	}
	public void updateSpeedDirt() {
		if ((this.numericID & 1) == 0) {
			dirtDelay--;
			if (dirtDelay <= 0) {
				if (PlayerTwo.KEEP_MOVING) {
					displaceSpeedDirt(x + width / 2, y + height / 2, 18, 18);
					dirtDelay = 10;
				}
			}
		}
	}
	public void displaceDirt(double x, double y, double low, double high) {
		if (direction != PlayerMovement.STANDING_STILL && !PlayerTwo.DEAD && !PlayerTwo.LEVEL_COMPLETED) {
			for (int i = 0; i <2; i++) {
				game.getDebrisManager().addDebris(new DirtDisplacement(game, GameImageBank.dirt,1.5, x, y,
						new Point2D((Math.random() * (8 - -8 + 1) + -8), Math.random() * (8 - -8 + 1) + -8)));
			}
		}
	}

	public void displaceSpeedDirt(double x, double y, double low, double high) {
		if (direction != PlayerMovement.STANDING_STILL && !PlayerTwo.DEAD && !PlayerTwo.LEVEL_COMPLETED) {
			for (int i = 0; i <6; i++) {
				game.getDebrisManager().addDebris(new DirtDisplacement(game, GameImageBank.sand_grain,1.5, x, y,
						new Point2D((Math.random() * (8 - -8 + 1) + -8), Math.random() * (13 - -13 + 1) + -13)));
			}
		}
	}
	public void hideLast() {
		if (this.numericID == PlayerTwo.NUMERIC_ID - 1) {
			this.circle.setVisible(false);
		} else if (this.numericID != PlayerTwo.NUMERIC_ID - 1) {
			this.circle.setVisible(true);
		}
	}

	public void disguiseLast() {
		if (!PlayerTwo.DEAD) {
			if (this.numericID == PlayerTwo.NUMERIC_ID - 1) {
				this.circle.setFill(GameImageBank.tailImage);
			}
			else if (this.numericID != PlayerTwo.NUMERIC_ID - 1) {
				this.circle.setFill(GameImageBank.snakeTwoBody);
			}
		}
		if (fade == true) {
			fadeValue -= 0.01;
			this.circle.setOpacity(fadeValue);
			if (fadeValue <= 0) {
				fadeValue = 0;
			}
		}
	}

	public void checkBounds() {
		if (x < 0 - radius) {
			x = (float) (GameSettings.WIDTH + radius);
		} else if (x > GameSettings.WIDTH + radius) {
			x = (float) (0 - radius);
		} else if (y < GameSettings.START_Y - radius) {
			y = (float) (GameSettings.HEIGHT + radius);
		} else if (y > GameSettings.HEIGHT + radius) {
			y = (float) (GameSettings.START_Y - radius);
		}
	}

	public void sectionAdjustment() {
		if (previousSection != null) {
			if (x > 0 + radius && x < GameSettings.WIDTH - radius && y > GameSettings.START_Y + radius
					&& y < GameSettings.HEIGHT - radius) {
				if (this.direction == PlayerMovement.MOVE_DOWN) {
					if (previousSection.getY() - y >= this.radius) {
						y = previousSection.getY() - this.radius;
						x = previousSection.getX();
					}
				}
				if (this.direction == PlayerMovement.MOVE_UP) {
					if (previousSection.getY() - y >= this.radius) {
						y = previousSection.getY() - this.radius;
						x = previousSection.getX();
					}
				}
				if (this.direction == PlayerMovement.MOVE_LEFT) {
					if (x - previousSection.getX() >= this.radius) {
						x = previousSection.getX() + this.radius;
						y = previousSection.getY();
					}
				}
				if (this.direction == PlayerMovement.MOVE_RIGHT) {
					if (previousSection.getX() - x >= this.radius) {
						x = previousSection.getX() - this.radius;
						y = previousSection.getY();
					}
				}
			}
		}
	}
	public void loadBones() {
		if (this.numericID == PlayerTwo.NUMERIC_ID - 1) {
			bones = new Circle(x, y, this.radius * 0.4, new ImagePattern(GameImageBank.snakeBones));
			game.getBaseLayer().getChildren().add(bones);
			bones.setRotate(r-90);
		}
		else if (this.numericID != PlayerTwo.NUMERIC_ID - 1) {
			bones = new Circle(x, y, this.radius * 0.8, new ImagePattern(GameImageBank.snakeBones));
			game.getBaseLayer().getChildren().add(bones);
			bones.setRotate(r-90);
		}


	}

	public void blowUp() {
		if (blowUp == true) {
			for (int i = 0; i < GameSettings.PARTICLE_LIMIT; i++) {
				if (GameSettings.ADD_VARIATION) {
					particleSize = Math.random() * (10 - 5 + 1) + 5;
					particleLife = Math.random() * (1.5 - 0.5 + 1) + 0.5;
				}
				game.getDebrisManager().addParticle(new SectionDisintegration(game, GameImageBank.snakeTwoDebris,
						particleLife, particleSize, (double) (x + this.radius / 2), (double) (y + this.radius / 2)));
			}
			blowUp = false;
		}
	}

	public void die() {
		loadBones();
		fade = true;
		blowUp();
	}
	public Rectangle2D getBounds() {

		return new Rectangle2D(x - radius / 2, y - radius / 2, radius, radius);
	}
}
