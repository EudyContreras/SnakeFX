package com.EudyContreras.Snake.Utilities;

import com.EudyContreras.Snake.Application.GameManager;
import com.EudyContreras.Snake.Application.GameSettings;
import com.EudyContreras.Snake.Identifiers.GameStateID;
import com.EudyContreras.Snake.PlayerOne.PlayerOne;
import com.EudyContreras.Snake.PlayerTwo.PlayerTwo;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.MotionBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ScreenEffectUtility {
	private Runnable script;
	private MotionBlur motionEffect = new MotionBlur(0, 50);
	private BoxBlur blurEffect = new BoxBlur(25, 25, 2);
	private GaussianBlur deathEffect = new GaussianBlur(0);
	private GaussianBlur gaussianEffect = new GaussianBlur(7);
	private BoxBlur clearLevelBlur = new BoxBlur();
	private GaussianBlur introBlurEffect = new GaussianBlur(0);
	private GaussianBlur stormBlur = new GaussianBlur(0);
	private Rectangle toneOverlay = new Rectangle(0, 0, GameSettings.WIDTH, GameSettings.HEIGHT);
	private Rectangle fadeScreen = new Rectangle(0, 0, GameSettings.WIDTH, GameSettings.HEIGHT);
	private Bloom bloomEffect = new Bloom();
	private Point2D originalPosition = new Point2D(0, 0);
	private Pane node;
	private Boolean instanceCheck = false;
	private Boolean setDistortion = false;
	private Boolean setBloom = false;
	private Boolean setSoftBlur = false;
	private Boolean setIntenseBlur = false;
	private Boolean setToneOverlay = false;
	private Boolean deathBlur = false;
	private Boolean blurLevel = false;
	private Boolean introBlur = false;
	private Boolean storm = false;
	private Boolean blurUp = true;
	private Boolean blurDown = false;
	private Boolean clearLevel = false;
	private Boolean setFadeOverlay = false;
	private Boolean screenShakeH = false;
	private Boolean screenShakeV = false;
	private Boolean nodeShake = false;
	private Double shakeDuration = 0.0;
	private Double nodeShakeDuration = 0.0;
	private Double shakeStrenght = 15.0;
	private Double shakeAmount = 0.0;
	private Double nodeShakeAmountX = 0.0;
	private Double nodeShakeAmountY = 0.0;
	private Double clearLevelBluring = 0.0;
	private Double stormBluring = 0.0;
	private Double softBlurLifetime = 0.0;
	private Double distortionLifetime = 0.0;
	private Double bloomLifetime = 0.0;
	private Double toneLifetime = 0.0;
	private Double intenseBlurLifetime = 0.0;
	private Double deathBlurLifetime = 0.0;
	private Double introBlurOff = 20.0;
	private Double speedDistortion;
	private Double speedTone;
	private Double speedBlur;
	private Double speedGaussian;
	private Double speedBloom;
	private Double shakeX = 0.0;
	private Double nodeShakeX = 0.0;
	private Double nodeShakeY = 0.0;
	private Double fade;
	private Double fadeSpeed;
	private Pane layer;
	private GameManager game;

	public ScreenEffectUtility(GameManager game, Pane layer) {
		this.game = game;
		this.layer = layer;
		toneOverlay.setFill(Color.TRANSPARENT);
	}

	/**
	 * Adds a distortion effect to the layer. max lifetime = 63. min lifetime =0.
	 *
	 * @param lifetime
	 */
	public synchronized void addDistortion(double lifetime, double speed) {
		if (!PlayerOne.LEVEL_COMPLETED && !PlayerTwo.LEVEL_COMPLETED) {
			layer.setEffect(motionEffect);
			motionEffect.setAngle(10);
			setDistortion = true;
			distortionLifetime = lifetime;
			speedDistortion = speed;
		}
	}

	public synchronized void addDistortion() {
		if (!PlayerOne.LEVEL_COMPLETED && !PlayerTwo.LEVEL_COMPLETED) {
			layer.setEffect(motionEffect);
			motionEffect.setAngle(10);
			motionEffect.setRadius(25);
		}
	}
	/**
	 * Adds a soft blur effect to the layer. max lifetime = 63. min lifetime =0.
	 *
	 * @param lifetime
	 */
	public synchronized void addSoftBlur(double lifetime, double speed) {
		if (!PlayerOne.LEVEL_COMPLETED && !PlayerTwo.LEVEL_COMPLETED) {
			layer.setEffect(null);
			softBlurLifetime = lifetime;
			speedGaussian = speed;
			layer.setEffect(gaussianEffect);
			setSoftBlur = true;
		}
	}

	/**
	 * Adds a intense blur effect to the layer. max lifetime = 255. min lifetime = 0.
	 *
	 * @param lifetime
	 */
	public synchronized void addIntenseBlur(double lifetime, double speed) {
		if (!PlayerOne.LEVEL_COMPLETED && !PlayerTwo.LEVEL_COMPLETED) {
			intenseBlurLifetime = lifetime;
			speedBlur = speed;
			blurEffect.setIterations(2);
			layer.setEffect(blurEffect);
			setIntenseBlur = true;
		}
	}

	/**
	 * Adds a bloom effect to the scene max lifetime = 10; min lifeTime = 0;
	 *
	 * @param lifetime
	 */
	public synchronized void addBloom(double lifetime, double speed) {
		if (!PlayerOne.LEVEL_COMPLETED && !PlayerTwo.LEVEL_COMPLETED) {
			bloomLifetime = lifetime / 10;
			speedBloom = speed / 10;
			layer.setEffect(bloomEffect);
			setBloom = true;
		}
	}

	/**
	 * Adds a tone overlay to the screen which will disappear after at a given
	 * rate max lifetime = 10; min lifetime = 0;
	 *
	 * @param tone
	 * @param lifetime
	 * @param speed
	 */
	public synchronized void addToneOverlay(Color tone, double lifetime, double speed) {
		if (!PlayerOne.LEVEL_COMPLETED && !PlayerTwo.LEVEL_COMPLETED) {
			layer.getChildren().remove(toneOverlay);
			toneLifetime = lifetime / 10;
			speedTone = speed / 10;
			toneOverlay.setFill(tone);
			toneOverlay.setOpacity(toneLifetime / 10);
			layer.getChildren().add(toneOverlay);
			setToneOverlay = true;
		}
	}

	/**
	 * Adds a blur to the screen after the snake dies
	 */
	public synchronized void addDeathBlur() {
		if (!PlayerOne.LEVEL_COMPLETED && !PlayerTwo.LEVEL_COMPLETED) {
			deathBlurLifetime = 0.0;
			layer.setEffect(null);
			layer.setEffect(deathEffect);
			deathBlur = true;
		}
	}

	/**
	 * Adds a blur to the screen after the level has been completed
	 */
	public synchronized void levelCompleteBlur() {
		clearLevelBluring = 0.0;
		clearLevelBlur.setIterations(2);
		clearLevelBlur.setWidth(clearLevelBluring);
		clearLevelBlur.setHeight(clearLevelBluring);
		layer.setEffect(null);
		layer.setEffect(clearLevelBlur);
		clearLevel = false;
		blurLevel = true;
	}

	public synchronized void levelCompleteBlurOff() {
		layer.setEffect(clearLevelBlur);
		blurLevel = false;
		clearLevel = true;
	}

	/**
	 * Adds random blurring during sand storms
	 */
	public synchronized void addStormBlur() {
		if (!PlayerOne.LEVEL_COMPLETED && !PlayerTwo.LEVEL_COMPLETED) {
			layer.setEffect(null);
			layer.setEffect(stormBlur);
			storm = true;
		}
	}

	public void addScreenShake(Pane screen, double duration, boolean horizontal, boolean vertical) {
		if(horizontal){
			shakeDuration = (double) (duration*30);
			shakeAmount = 15.0;
			screenShakeH = true;
		}
		if(vertical){
			shakeDuration = (double) (duration*30);
			shakeAmount = 15.0;
			screenShakeV = true;
		}
	}

	public void addScreenShake(Pane screen, double duration, double shakeForce, boolean horizontal, boolean vertical) {
		if(horizontal){
			shakeDuration = (double) (duration*30);
			shakeStrenght = shakeForce;
			shakeAmount = shakeStrenght;
			screenShakeH = true;
		}
		if(vertical){
			shakeDuration = (double) (duration*30);
			shakeStrenght = shakeForce;
			shakeAmount = shakeStrenght;
			screenShakeV = true;
		}
	}

	public void addNodeShake(Node node, double duration) {
		if(node instanceof Pane){
			this.node = (Pane) node;
			this.originalPosition = new Point2D(this.node.getTranslateX(), this.node.getTranslateY());
			nodeShakeDuration = (double) (duration * 30);
			nodeShakeAmountX = 10.0;
			nodeShakeAmountY = 10.0;
			nodeShake = true;
		}
	}

	/**
	 * Adds a fading screen to the game which leads to the main menu. The fade
	 * speed determines the speed of the fade.
	 *
	 * @param fadeSpeed: max 10, min 1;
	 */
	public void addFadeScreen(double speed, GameStateID stateID, Runnable script) {
		this.script = script;
		game.getEleventhLayer().getChildren().remove(fadeScreen);
		fade = 0.0;
		fadeScreen.setOpacity(fade);
		fadeScreen.setFill(Color.BLACK);
		fadeSpeed = speed / 1000;
		game.getEleventhLayer().getChildren().add(fadeScreen);
		setFadeOverlay = true;
	}

	public void setIntroEffect(){
		introBlurOff = 25.0;
		introBlurEffect.setRadius(introBlurOff);
		layer.setEffect(null);
		layer.setEffect(introBlurEffect);
	}

	public void addIntroEffect(){
		introBlurOff = 25.0;
		introBlurEffect.setRadius(introBlurOff);
		layer.setEffect(null);
		layer.setEffect(introBlurEffect);
		introBlur = true;
	}

	public void updateEffect() {
		if (setDistortion) {
			setDistortionModifier();
		}
		if (setSoftBlur) {
			setSoftBlurModifier();
		}
		if (setIntenseBlur) {
			setIntenseBlurModifier();
		}
		if (setBloom) {
			setBloomModifier();
		}
		if (setToneOverlay) {
			setToneModifier();
		}
		if (deathBlur) {
			setDeathBlur();
		}
		if (storm) {
			setStormBlur();
		}
		if (blurLevel || clearLevel) {
			setClearLevelBlur();
		}
		if (introBlur){
			setIntroBlur();
		}
		if (setFadeOverlay) {
			setFadeModifier();
		}
		if (screenShakeH){
			setHShakeModifier();
		}
		if (screenShakeV){
			setVShakeModifier();
		}
		if (nodeShake){
			setNodeShakeModifier();
		}
	}

	private void setDistortionModifier() {
		if (distortionLifetime >= 0) {
			distortionLifetime -= speedDistortion;
			motionEffect.setRadius(distortionLifetime);
		}
	}

	private void setSoftBlurModifier() {
		if (softBlurLifetime >= 0) {
			softBlurLifetime -= speedGaussian;
			gaussianEffect.setRadius(softBlurLifetime);
		}
	}

	private void setIntenseBlurModifier() {
		if (intenseBlurLifetime >= 0) {
			intenseBlurLifetime -= speedBlur;
			blurEffect.setWidth(intenseBlurLifetime);
			blurEffect.setHeight(intenseBlurLifetime);
		}
	}

	private void setBloomModifier() {
		bloomLifetime -= speedBloom;
		bloomEffect.setThreshold(bloomLifetime);
		if (bloomLifetime <= 0) {
			layer.setEffect(null);
			bloomLifetime = 0.0;
		}
	}

	private void setToneModifier() {
		toneLifetime -= speedTone;
		toneOverlay.setOpacity(toneLifetime);
		if (toneLifetime <= 0) {
			toneLifetime = 0.0;
			layer.getChildren().remove(toneOverlay);
		}
	}

	private void setFadeModifier() {
		fade += fadeSpeed;
		fadeScreen.setOpacity(fade);
		if (fade >= 1.0f) {
			fadeScreen.setOpacity(1);
		}
		if (fade >= 1.1f) {
			if(script!=null){
				script.run();
			}
			setFadeOverlay = false;
		}
	}

	private void setHShakeModifier(){
		if(screenShakeH){
			shakeX +=shakeAmount;
			shakeDuration--;
			game.getGameRoot().setTranslateX(shakeX);
			if(shakeX>shakeStrenght){
				shakeAmount = -shakeStrenght;
			}
			if(shakeX<-shakeStrenght){
				shakeAmount = shakeStrenght;
			}
			if(shakeDuration<=0){
				game.getGameRoot().setTranslateX(0);
				shakeAmount = 0.0;
				screenShakeH = false;
				shakeDuration = 0.0;
			}
		}
	}

	private void setVShakeModifier(){
		if(screenShakeV){
			shakeX +=shakeAmount;
			shakeDuration--;
			game.getGameRoot().setTranslateY(shakeX);
			if(shakeX>shakeStrenght){
				shakeAmount = -shakeStrenght;
			}
			if(shakeX<-shakeStrenght){
				shakeAmount = shakeStrenght;
			}
			if(shakeDuration<=0){
				game.getGameRoot().setTranslateY(0);
				shakeAmount = 0.0;
				screenShakeV = false;
				shakeDuration = 0.0;
			}
		}
	}

	private void setNodeShakeModifier(){
		if(nodeShake){
			nodeShakeX +=nodeShakeAmountX;
			nodeShakeY +=nodeShakeAmountY;
			nodeShakeDuration--;
			node.setTranslateX(originalPosition.getX() + nodeShakeX);
			node.setTranslateY(originalPosition.getY() + nodeShakeY);
			if(nodeShakeX>10){
				nodeShakeAmountX = -10.0;
			}
			if(nodeShakeX<-10){
				nodeShakeAmountX = 10.0;
			}
			if(nodeShakeY>10){
				nodeShakeAmountY = -10.0;
			}
			if(nodeShakeY<-10){
				nodeShakeAmountY = 10.0;
			}
			if(nodeShakeDuration<=0){
				node.setTranslateX(originalPosition.getX() + 0);
				node.setTranslateY(originalPosition.getY() + 0);
				nodeShakeAmountX = 0.0;
				nodeShakeAmountY = 0.0;
				nodeShake = false;
				nodeShakeDuration = 0.0;
				node = null;
				originalPosition = null;
			}
		}
	}

	private void setStormBlur() {
		if (!PlayerOne.LEVEL_COMPLETED && !PlayerTwo.LEVEL_COMPLETED) {
			if (blurUp) {
				stormBluring += 0.1;
				if (stormBluring >= 3) {
					blurDown = true;
					blurUp = false;
				}

			}
			if (blurDown) {
				stormBluring -= 0.1;
				if (stormBluring <= 0) {
					blurUp = true;
					blurDown = false;
					storm = false;
					layer.setEffect(null);
				}
			}
			layer.setEffect(stormBlur);
			stormBlur.setRadius(stormBluring);
		}
	}

	private void setDeathBlur() {
		deathBlurLifetime += 0.03;
		layer.setEffect(deathEffect);
		deathEffect.setRadius(deathBlurLifetime);
		if (deathBlurLifetime >= 40) {
			deathBlurLifetime = 40.0;
		}
		if (deathBlurLifetime >= 10) {
			if (!instanceCheck) {
				game.getGameOverScreen().fadeOut();
				instanceCheck = true;
			}
		}
	}

	private void setClearLevelBlur() {
		if (blurLevel) {
			clearLevelBluring += 0.3;
			if (clearLevelBluring >= 20) {
				clearLevelBluring = 20.0;
			}
		}
		if (clearLevel) {
			clearLevelBluring -= 1.2;
			if (clearLevelBluring <= 0) {
				clearLevelBluring = 0.0;
				clearLevel = false;
				layer.setEffect(null);
			}
		}
		layer.setEffect(clearLevelBlur);
		clearLevelBlur.setWidth(clearLevelBluring);
		clearLevelBlur.setHeight(clearLevelBluring);
	}

	public void setIntroBlur(){
		if (introBlur) {
			introBlurOff -= 0.6;
			if (introBlurOff <= 0) {
				introBlurOff = 0.0;
				introBlur = false;
			}
		}
		layer.setEffect(introBlurEffect);
		introBlurEffect.setRadius(introBlurOff);
	}

	public void removeBlur() {
		clearLevelBluring = 0.0;
		setDistortion = false;
		setBloom = false;
		setSoftBlur = false;
		setIntenseBlur = false;
		deathBlur = false;
		blurLevel = false;
		storm = false;
		blurUp = true;
		blurDown = false;
		clearLevel = false;
		layer.setEffect(null);
	}
}