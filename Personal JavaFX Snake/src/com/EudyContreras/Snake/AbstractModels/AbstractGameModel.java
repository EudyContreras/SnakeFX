package com.EudyContreras.Snake.AbstractModels;

import java.util.concurrent.CountDownLatch;

import com.EudyContreras.Snake.DebrisEffects.RainEmitter;
import com.EudyContreras.Snake.DebrisEffects.SandEmitter;
import com.EudyContreras.Snake.EnumIDs.GameStateID;
import com.EudyContreras.Snake.FrameWork.FadeScreenHandler;
import com.EudyContreras.Snake.FrameWork.GameDebrisManager;
import com.EudyContreras.Snake.FrameWork.GameLoader;
import com.EudyContreras.Snake.FrameWork.LogicThread;
import com.EudyContreras.Snake.FrameWork.ObjectManager;
import com.EudyContreras.Snake.HUDElements.EnergyBarOne;
import com.EudyContreras.Snake.HUDElements.EnergyBarTwo;
import com.EudyContreras.Snake.HUDElements.GameHud;
import com.EudyContreras.Snake.HUDElements.GameOverScreen;
import com.EudyContreras.Snake.HUDElements.HealthBarOne;
import com.EudyContreras.Snake.HUDElements.HealthBarTwo;
import com.EudyContreras.Snake.HUDElements.PauseMenu;
import com.EudyContreras.Snake.HUDElements.ScoreBoard;
import com.EudyContreras.Snake.HUDElements.ScoreKeeper;
import com.EudyContreras.Snake.HUDElements.VictoryScreen;
import com.EudyContreras.Snake.ImageBanks.GameImageBank;
import com.EudyContreras.Snake.ImageBanks.GameLevelImage;
import com.EudyContreras.Snake.InputHandlers.InputManagerGestures;
import com.EudyContreras.Snake.InputHandlers.InputManagerKey;
import com.EudyContreras.Snake.InputHandlers.InputManagerMouse;
import com.EudyContreras.Snake.PlayerOne.PlayerOneManager;
import com.EudyContreras.Snake.PlayerOne.PlayerOneSectionManager;
import com.EudyContreras.Snake.PlayerTwo.PlayerTwoManager;
import com.EudyContreras.Snake.PlayerTwo.PlayerTwoSectionManager;
import com.EudyContreras.Snake.SlitherSnake.SlitherManager;
import com.EudyContreras.Snake.SlitherSnake.SlitherSectionManager;
import com.EudyContreras.Snake.UserInterface.MenuMain;
import com.EudyContreras.Snake.Utilities.ScreenEffectUtility;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public abstract class AbstractGameModel extends Application {

	protected GameStateID stateID;
	protected GameLoader loader;
	protected Timeline frameGameLoop;
	protected AnimationTimer playerMovementLoop;
	protected InputManagerKey keyInput;
	protected InputManagerMouse mouseInput;
	protected InputManagerGestures gestures;
	protected GraphicsContext gc;
	protected ScreenEffectUtility overlayEffect;
	protected ObjectManager objectManager;
	protected SlitherManager slitherManager;
	protected PlayerOneManager playerOneManager;
	protected PlayerTwoManager playerTwoManager;
	protected GameDebrisManager debrisManager;
	protected PlayerOneSectionManager sectManagerOne;
	protected PlayerTwoSectionManager sectManagerTwo;;
	protected SlitherSectionManager slitherSectManager;
	protected FadeTransition fadeSplash;
	protected LogicThread thread;
	protected MenuMain mainMenu;
	protected Scene scene;
	protected Scene splashScene;
	protected Group mainRoot;
	protected Stage mainWindow;
	protected Canvas canvas;
	protected Pane root;
	protected Pane splashLayout;
	protected Pane levelLayer;
	protected Pane snakeOneLayer;
	protected Pane snakeTwoLayer;
	protected Pane baseLayer;
	protected Pane dirtLayer;
	protected Pane innerParticleLayer;
	protected Pane outerParticleLayer;
	protected Pane debrisLayer;
	protected Pane firstLayer;
	protected Pane secondLayer;
	protected Pane thirdLayer;
	protected Pane fourthLayer;
	protected Pane fithLayer;
	protected Pane sixthLayer;
	protected Pane seventhLayer;
	protected Pane eighthLayer;
	protected Pane ninthLayer;
	protected Pane tenthLayer;
	protected Pane eleventhLayer;
	protected Pane twelfthLayer;
	protected Pane thirTeenthLayer;
	protected Pane fourTeenthLayer;
	protected Pane fadeScreenLayer;
	protected Text TextFPS;
	protected PauseMenu pauseMenu;
	protected GameImageBank imageBank;
	protected GameLevelImage levelImageBank;
	protected HealthBarOne healthBarOne;
	protected HealthBarTwo healthBarTwo;
	protected ScoreBoard scoreBoardOne;
	protected ScoreBoard scoreBoardTwo;
	protected EnergyBarOne energyBarOne;
	protected EnergyBarTwo energyBarTwo;
	protected SandEmitter sandEmitter;
	protected RainEmitter rainEmitter;
	protected VictoryScreen victoryScreen;
	protected AnimationTimer gameLoop;
	protected AnimationTimer animationLoop;
	protected AnimationTimer particleLoop;
	protected GameOverScreen gameOverScreen;
	protected FadeScreenHandler fadeHandler;
	protected ScoreKeeper scoreKeeper;
	protected ImageView backgroundImage;
	protected ImageView splash;
	protected Rectangle2D bounds;
	protected GameHud gameHud;
	protected String title = "SNAKE";
	protected boolean isRunning = true;
	protected boolean gameRunning = false;
	protected boolean goToNext;
	protected boolean goToMain;
	protected int splashWidth;
	protected int splashHeight;
	protected int levelLenght;
	protected double splashFadeDuration;
	protected double splashFadeDelay;
	public static double ScaleX = GameLoader.ResolutionScaleX;
	public static double ScaleY = GameLoader.ResolutionScaleY;
	public static double ScaleX_ScaleY = (GameLoader.ResolutionScaleX+GameLoader.ResolutionScaleY)/2;

	public HealthBarOne getHealthBarOne() {
		return healthBarOne;
	}

	public HealthBarTwo getHealthBarTwo() {
		return healthBarTwo;
	}

	public PlayerOneManager getPlayerOneManager() {
		return playerOneManager;
	}

	public void setPlayerOneManager(PlayerOneManager playerManager) {
		this.playerOneManager = playerManager;
	}

	public PlayerTwoManager getPlayerTwoManager() {
		return playerTwoManager;
	}

	public void setPlayerTwoManager(PlayerTwoManager playerManager) {
		this.playerTwoManager = playerManager;
	}

	public void setHealthBarOne(HealthBarOne healthBarOne) {
		this.healthBarOne = healthBarOne;
	}

	public void setHealthBarTwo(HealthBarTwo healthBarTwo) {
		this.healthBarTwo = healthBarTwo;
	}

	public static void writeToLog(String text) {
		System.out.println(text + "\n");
	}

	public VictoryScreen getVictoryScreen() {
		return victoryScreen;
	}

	public void setVictoryScreen(VictoryScreen victoryScreen) {
		this.victoryScreen = victoryScreen;
	}

	public GameOverScreen getGameOverScreen() {
		return gameOverScreen;
	}

	public void setGameOverScreen(GameOverScreen gameOverScreen) {
		this.gameOverScreen = gameOverScreen;
	}

	public EnergyBarOne getEnergyBarOne() {
		return energyBarOne;
	}

	public void setEnergyBarOne(EnergyBarOne energyBarOne) {
		this.energyBarOne = energyBarOne;
	}

	public EnergyBarTwo getEnergyBarTwo() {
		return energyBarTwo;
	}

	public void setEnergyBarTwo(EnergyBarTwo energyBarTwo) {
		this.energyBarTwo = energyBarTwo;
	}

	public PauseMenu getPauseMenu(){
		return pauseMenu;
	}
	public MenuMain getMainMenu() {
		return mainMenu;
	}

	public void setMainMenu(MenuMain mainMenu) {
		this.mainMenu = mainMenu;
	}

	public Stage getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(Stage mainWindow) {
		this.mainWindow = mainWindow;
	}

	public GameLoader getGameLoader() {
		return loader;
	}

	public void setGameLoader(GameLoader loader) {
		this.loader = loader;
	}

	public Pane getGameRoot() {
		return root;
	}

	public void setRoot(Parent root) {
		scene.setFill(Color.BLACK);
		scene.setRoot(root);
	}

	public Pane getSnakeOneLayer() {
		return snakeOneLayer;
	}

	public void setSnakeOneLayer(Pane snakeOneLayer) {
		this.snakeOneLayer = snakeOneLayer;
	}

	public Pane getSnakeTwoLayer() {
		return snakeTwoLayer;
	}

	public void setSnakeTwoLayer(Pane snakeTwoLayer) {
		this.snakeTwoLayer = snakeTwoLayer;
	}

	public Pane getLevelLayer() {
		return levelLayer;
	}

	public ObjectManager getObjectManager() {
		return objectManager;
	}

	public void setObjectManager(ObjectManager objectManager) {
		this.objectManager = objectManager;
	}

	public SlitherManager getSlitherManager() {
		return slitherManager;
	}

	public GameDebrisManager getDebrisManager() {
		return debrisManager;
	}

	public PlayerOneSectionManager getSectManagerOne() {
		return sectManagerOne;
	}

	public PlayerTwoSectionManager getSectManagerTwo() {
		return sectManagerTwo;
	}

	public SlitherSectionManager getSlitherSectManager() {
		return slitherSectManager;
	}

	public void setDebrisManager(GameDebrisManager debrisManager) {
		this.debrisManager = debrisManager;
	}

	public FadeScreenHandler getFadeScreenHandler(){
		return fadeHandler;
	}

	public ScoreBoard getScoreBoardOne() {
		return scoreBoardOne;
	}

	public ScoreBoard getScoreBoardTwo() {
		return scoreBoardTwo;
	}

	public GameHud getGameHud() {
		return gameHud;
	}

	public Group getMainRoot() {
		return mainRoot;
	}

	public void setScoreBoardOne(ScoreBoard scoreBoard) {
		this.scoreBoardOne = scoreBoard;
	}

	public void setScoreBoardTwo(ScoreBoard scoreBoard) {
		this.scoreBoardTwo = scoreBoard;
	}

	public InputManagerKey getKeyInput() {
		return keyInput;
	}

	public void setKeyInput(InputManagerKey keyInput) {
		this.keyInput = keyInput;
	}

	public Pane getBaseLayer() {
		return baseLayer;
	}

	public Pane getDirtLayer() {
		return dirtLayer;
	}

	public void setDirtLayer(Pane dirtLayer) {
		this.dirtLayer = dirtLayer;
	}

	public Pane getInnerParticleLayer() {
		return innerParticleLayer;
	}

	public void setInnerParticleLayer(Pane particleLayer) {
		this.innerParticleLayer = particleLayer;
	}

	public Pane getOuterParticleLayer() {
		return outerParticleLayer;
	}

	public void setOuterParticleLayer(Pane outerParticleLayer) {
		this.outerParticleLayer = outerParticleLayer;
	}

	public Pane getDebrisLayer() {
		return debrisLayer;
	}

	public void setDebrisLayer(Pane debrisLayer) {
		this.debrisLayer = debrisLayer;
	}

	public void setBaseLayer(Pane baseLayer) {
		this.baseLayer = baseLayer;
	}

	public Pane getFirstLayer() {
		return firstLayer;
	}

	public void setFirstLayer(Pane debrisLayer) {
		this.firstLayer = debrisLayer;
	}

	public Pane getSecondLayer() {
		return secondLayer;
	}

	public void setSecondLayer(Pane bottomLayer) {
		this.secondLayer = bottomLayer;
	}

	public Pane getThirdLayer() {
		return thirdLayer;
	}

	public void setThirdLayer(Pane playfieldLayer) {
		this.thirdLayer = playfieldLayer;
	}

	public Pane getFourthLayer() {
		return fourthLayer;
	}

	public Pane getFithLayer() {
		return fithLayer;
	}

	public Pane getSixthLayer() {
		return sixthLayer;
	}

	public Pane getSeventhLayer() {
		return seventhLayer;
	}

	public void setSeventhLayer(Pane radarLayer) {
		this.seventhLayer = radarLayer;
	}

	public Pane getEighthLayer() {
		return eighthLayer;
	}

	public void setEighthLayer(Pane eighthLayer) {
		this.eighthLayer = eighthLayer;
	}

	public Pane getNinthLayer() {
		return ninthLayer;
	}

	public void setNinthLayer(Pane ninthLayer) {
		this.ninthLayer = ninthLayer;
	}

	public Pane getTenthLayer() {
		return tenthLayer;
	}

	public void setTenthLayer(Pane tenthLayer) {
		this.tenthLayer = tenthLayer;
	}

	public Pane getEleventhLayer() {
		return eleventhLayer;
	}

	public void setEleventhLayer(Pane eleventhLayer) {
		this.eleventhLayer = eleventhLayer;
	}

	public Pane getTwelfthLayer() {
		return twelfthLayer;
	}

	public void setTwelfthLayer(Pane eighthLayer) {
		this.twelfthLayer = eighthLayer;
	}

	public Pane getThirTeenthLayer() {
		return thirTeenthLayer;
	}

	public void setThirTeenthLayer(Pane ninthLayer) {
		this.thirTeenthLayer = ninthLayer;
	}

	public Pane getFourTeenthLayer() {
		return fourTeenthLayer;
	}

	public void setFourTeenthLayer(Pane tenthLayer) {
		this.fourTeenthLayer = tenthLayer;
	}

	public Pane getFadeScreenLayer() {
		return fadeScreenLayer;
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public ScoreKeeper getScoreKeeper() {
		return scoreKeeper;
	}

	public GameStateID getStateID() {
		return stateID;
	}

	public void setStateID(GameStateID stateID) {
		this.stateID = stateID;
	}

	public ScreenEffectUtility getOverlayEffect(){
		return overlayEffect;
	}

	public void showCursor(boolean choice, Scene scene) {
		if (!choice)
			scene.setCursor(Cursor.NONE);

		else if (choice)
			scene.setCursor(Cursor.DEFAULT);
	}

	public int getLevelLenght() {
		return levelLenght;
	}

	public Timeline getGameLoop(){
		return frameGameLoop;
	}

	public void setLevelLenght(int levelLenght) {
		this.levelLenght = levelLenght;
	}

	public void backgroundWorker(){
		Service<Void> service = new Service<Void>() {
	        @Override
	        protected Task<Void> createTask() {
	            return new Task<Void>() {
	                @Override
	                protected Void call() throws Exception {
	                    //Background work
	                    final CountDownLatch latch = new CountDownLatch(1);
	                    Platform.runLater(new Runnable() {
	                        @Override
	                        public void run() {
	                            try{
	                                //FX Stuff done here
	                            }finally{
	                                latch.countDown();
	                            }
	                        }
	                    });
	                    latch.await();
	                    //Keep with the background work
	                    return null;
	                }
	            };
	        }
	    };
	    service.start();
	}

}