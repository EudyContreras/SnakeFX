package com.SnakeGame.HudElements;

import com.SnakeGame.FrameWork.Settings;
import com.SnakeGame.FrameWork.SnakeGame;
import com.SnakeGame.ImageBanks.GameImageBank;
import com.SnakeGame.PlayerOne.PlayerOne;
import com.SnakeGame.PlayerTwo.PlayerTwo;

import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * This class is used to simulate a simple health bar which will decrease under
 * certain conditions, and that will also self restore
 *
 * @author Eudy Contreras
 *
 */
public class HealthBarOne {


	private boolean setDelay = false;
	private boolean killPlayer = false;
	private boolean playerIsAlive = true;
	private double maxHealth = 100;
	private double width = 0;
	private double x;
	private double y;
	private double height;
	private int delay = 0;
	private SnakeGame game;
	private PlayerOne player;
	private Rectangle healthBar = new Rectangle();
	private Rectangle healthBarBorder = new Rectangle();
	private Rectangle playerHud = new Rectangle();
	private Circle playerHead = new Circle();

	public HealthBarOne(SnakeGame game, double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.player = game.getGameLoader().getPlayerOne();
		this.game = game;
		this.healthBar.setWidth(width);
		this.healthBar.setHeight(height);
		this.healthBar.setTranslateX(x);
		this.healthBar.setTranslateY(y);
		this.healthBarBorder.setWidth(width+4);
		this.healthBarBorder.setHeight(height+3);
		this.healthBarBorder.setTranslateX(x-1);
		this.healthBarBorder.setTranslateY(y-1);
		this.playerHud.setWidth(width/2);
		this.playerHud.setHeight(height*2);
		this.playerHud.setTranslateX(x+width);
		this.playerHud.setTranslateY(0);
		this.playerHead.setRadius(SnakeGame.ScaleX(30));
		this.playerHead.setCenterX(x+width+playerHead.getRadius()*1.5);
		this.playerHead.setCenterY(y+playerHead.getRadius());
		this.playerHead.setFill(new ImagePattern(GameImageBank.snakeEating));
		this.playerHud.setFill(new ImagePattern(GameImageBank.player_one_hud));
		this.healthBar.setFill(new ImagePattern(GameImageBank.health_bar_green_one));
		this.healthBarBorder.setFill(new ImagePattern(GameImageBank.health_bar_red_one));
		game.getNinthLayer().getChildren().add(healthBarBorder);
		game.getNinthLayer().getChildren().add(healthBar);
		game.getNinthLayer().getChildren().add(playerHud);
		game.getNinthLayer().getChildren().add(playerHead);
		this.maxHealth = width;
	}

	/**
	 * This method depletes the health by a specific percentage and under
	 * specific conditions
	 */
	public void depleteHealth() {

		if (player.isCollision() == true) {
			width -= Settings.DAMAGE_AMOUNT;
			setDelay = true;
			player.setCollision(false);
		}
		if (width <= 0 && playerIsAlive) {
			killPlayer = true;
			playerIsAlive = false;
		}
		this.healthBar.setWidth(width);
	}

	/**
	 * This method add a predetermined delay to health regeneration
	 */
	public void setDelay() {

		if (setDelay == true) {
			delay = 100;
			setDelay = false;
		}
	}

	/**
	 * This method regenerates the health over a given period of time.
	 */
	public void regerateHealth() {
		if (player.isDead() == false) {
			setDelay();
			if (delay >= 0) {
				delay--;
			}

			if (player.isCollision() == false) {
				if (width < maxHealth) {
					if (delay <= 0)
						width += Settings.HEALTH_REGENERATION_SPEED;
				}
			}
		}
		if (killPlayer == true) {
			player.die();
			killPlayer = false;
		}
	}

	public void hide() {
		if (PlayerOne.LEVEL_COMPLETED || PlayerTwo.LEVEL_COMPLETED) {
			healthBar.setVisible(false);
			healthBarBorder.setVisible(false);
		}
	}

	public void show() {
		healthBar.setVisible(true);
		healthBarBorder.setVisible(true);
	}

	public void refill() {
		this.setDelay = false;
		this.killPlayer = false;
		this.playerIsAlive = true;
		this.width = maxHealth;
		this.healthBar.setWidth(maxHealth);
	}

	public void drainAll() {
		this.width = 0;
		this.healthBar.setWidth(width);
	}

	public void setPlayer() {
		this.player = null;
		this.player = game.getGameLoader().getPlayerOne();
	}

	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getWidth(){
		return width;
	}
	public double getHeight(){
		return height;
	}
}
