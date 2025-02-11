
package com.EudyContreras.Snake.Application;

import java.util.ArrayList;
import java.util.LinkedList;

import com.EudyContreras.Snake.AbstractModels.AbstractGameModel;
import com.EudyContreras.Snake.ClassicSnake.ClassicSnake;
import com.EudyContreras.Snake.ClassicSnake.ClassicSnakeManager;
import com.EudyContreras.Snake.ClassicSnake.ClassicSnakeSectionManager;
import com.EudyContreras.Snake.Controllers.FadeScreenController;
import com.EudyContreras.Snake.Controllers.GameDebrisController;
import com.EudyContreras.Snake.Controllers.GameObjectController;
import com.EudyContreras.Snake.EffectEmitter.RainEmitter;
import com.EudyContreras.Snake.EffectEmitter.SandEmitter;
import com.EudyContreras.Snake.FrameWork.CursorUtility;
import com.EudyContreras.Snake.FrameWork.CursorUtility.CursorID;
import com.EudyContreras.Snake.FrameWork.GameLoader;
import com.EudyContreras.Snake.FrameWork.ResizeHelper;
import com.EudyContreras.Snake.FrameWork.ResizeListener;
import com.EudyContreras.Snake.HudElements.CountDownScreen;
import com.EudyContreras.Snake.HudElements.EnergyBarOne;
import com.EudyContreras.Snake.HudElements.EnergyBarTwo;
import com.EudyContreras.Snake.HudElements.GameBorder;
import com.EudyContreras.Snake.HudElements.GameHud;
import com.EudyContreras.Snake.HudElements.GameOverScreen;
import com.EudyContreras.Snake.HudElements.HealthBarOne;
import com.EudyContreras.Snake.HudElements.HealthBarTwo;
import com.EudyContreras.Snake.HudElements.PauseMenu;
import com.EudyContreras.Snake.HudElements.ReadyNotification;
import com.EudyContreras.Snake.HudElements.ScoreBoard;
import com.EudyContreras.Snake.HudElements.ScoreKeeper;
import com.EudyContreras.Snake.HudElements.VictoryScreen;
import com.EudyContreras.Snake.Identifiers.GameObjectID;
import com.EudyContreras.Snake.ImageBanks.GameImageBank;
import com.EudyContreras.Snake.InputHandlers.KeyInputHandler;
import com.EudyContreras.Snake.InputHandlers.MouseInputHandler;
import com.EudyContreras.Snake.InputHandlers.TouchInputHandler;
import com.EudyContreras.Snake.PathFindingAI.AIController;
import com.EudyContreras.Snake.PlayerOne.PlayerOne;
import com.EudyContreras.Snake.PlayerOne.PlayerOneManager;
import com.EudyContreras.Snake.PlayerOne.PlayerOneSectionManager;
import com.EudyContreras.Snake.PlayerTwo.PlayerTwo;
import com.EudyContreras.Snake.PlayerTwo.PlayerTwoManager;
import com.EudyContreras.Snake.PlayerTwo.PlayerTwoSectionManager;
import com.EudyContreras.Snake.ThreadTools.TaskWrapper;
import com.EudyContreras.Snake.UserInterface.MenuManager;
import com.EudyContreras.Snake.Utilities.ScreenEffectUtility;
import com.EudyContreras.Snake.Utilities.SplashScreen;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * This class is the core of the game. Here is where every object connects with
 * every object. all classes used by the game will communicate with this class
 * and therefore with every other class. This class takes care of the main loop
 * of the game which is in charge of rendering and moving mobs from a to b as
 * you can see most of the rendering is being handled on a single thread, This
 * class also monitors the amount of objects used in the game,
 *
 * @author Eudy Contreras
 *
 */
public class GameManager extends AbstractGameModel {

    private LinkedList<TaskWrapper> guiUpdate = new LinkedList<>();
    private LinkedList<TaskWrapper> gameUpdates = new LinkedList<>();
    private static ArrayList<ShowListener> showListeners = new ArrayList<>();

    public void start(Stage primaryStage) {
        if(GameSettings.SHOW_SPLASHSCREEN){
            new SplashScreen(primaryStage,GameImageBank.splash_screen, ()->initialize(),()->showGame());
        }else{
            initialize();
            showGame();
        }
    }

    public void resizeListener(final Stage stage, final Scene scene, final Pane pane) {

        final double initWidth = GameSettings.WIDTH;
        final double initHeight = GameSettings.HEIGHT;

        final double ratio = initWidth / initHeight;

        sizeListener = new ResizeListener(stage, scene, ratio, initHeight, initWidth, pane);

        ResizeHelper.addSceneManipulation(this,stage,scene);

        scene.widthProperty().addListener(sizeListener);
        scene.heightProperty().addListener(sizeListener);

        loader.setScale(GameLoader.scaleFactor);
    }

    public void setNewRatio(boolean state){
        if(sizeListener!=null){
            if(state){
                final double initWidth = GameSettings.WIDTH;
                final double initHeight = GameSettings.HEIGHT;

                final double ratio = initWidth / initHeight;
                sizeListener.setNewRatio(ratio, initHeight, initWidth);

                for(Node node: sceneRoot.getChildren()){
                    node.setTranslateY(0);
                }
            }
            else{
                final double initWidth = GameSettings.SCREEN_WIDTH;
                final double initHeight = GameSettings.SCREEN_HEIGHT;

                final double ratio = initWidth / initHeight;
                sizeListener.setNewRatio(ratio, initHeight, initWidth);

                for(Node node: sceneRoot.getChildren()){
                    node.setTranslateY(15);
                }
            }
        }
    }

    public void setRoot(Pane root) {

        scene.setFill(Color.BLACK);
        if (!mainWindow.isFullScreen()) {
            root.setTranslateY(15);
        } else {
            root.setTranslateY(0);
        }
        sceneRoot.setCenter(root);
    }

    public void setWindowSize(double width, double height){
        mainWindow.setWidth(width);
        mainWindow.setHeight(height);
    }

    public void setWindowLocation(double x, double y){
        mainWindow.setX(x);
        mainWindow.setY(y);
    }

    public void allowMouseInput(boolean choice) {
        if (choice)
            mouseInput.processInput(this, getGameLoader().getPlayerOne(), getGameLoader().getPlayerTwo(), scene);
    }

    public static void maximize() {
        if (!mainWindow.isMaximized()) {
            mainWindow.setWidth(GameSettings.SCREEN_WIDTH);
            mainWindow.setHeight(GameSettings.SCREEN_HEIGHT);
            mainWindow.setX(0);
            mainWindow.setY(0);
            mainWindow.setMaximized(true);
        } else {
            if (mainWindow.getX() != 0 && mainWindow.getY() != 0) {
                mainWindow.setWidth(GameSettings.SCREEN_WIDTH);
                mainWindow.setHeight(GameSettings.SCREEN_HEIGHT);
                mainWindow.setX(0);
                mainWindow.setY(0);
            } else if (mainWindow.getX() == 0 && mainWindow.getY() == 0) {
                mainWindow.setMaximized(false);
                mainWindow.setWidth(GameSettings.SCREEN_WIDTH * .8);
                mainWindow.setHeight(GameSettings.SCREEN_HEIGHT * .8);
                mainWindow.setX(200);
                mainWindow.setY(85);
            }
        }
    }

    public static void minimize() {
        mainWindow.setIconified(true);
    }

    public void showGame() {
        fadeSplash = null;
        splashLayout = null;
        splash = null;
        splashScene = null;
        mainWindow = new Stage();
        gridLayer.setVisible(GameSettings.SHOW_ASTAR_GRAPH ? GameSettings.ALLOW_AI_CONTROL : false);

        addToGameRoot(gridLayer);
        addToGameRoot(debrisLayer);
        addToGameRoot(innerParticleLayer);
        addToGameRoot(snakeOneLayer);
        addToGameRoot(snakeTwoLayer);
        addToGameRoot(fruitLayer);
        addToGameRoot(dirtLayer);
        addToGameRoot(firstLayer);
        addToGameRoot(secondLayer);
        addToGameRoot(thirdLayer);
        addToGameRoot(fourthLayer);
        addToGameRoot(fithLayer);
        addToGameRoot(sixthLayer);
        addToGameRoot(seventhLayer);
        addToGameRoot(eighthLayer);
        addToGameRoot(ninthLayer);
        addToGameRoot(outerParticleLayer);

        mainRoot.getChildren().add(root);
        mainRoot.setMaxWidth(GameSettings.WIDTH);
        mainRoot.setMaxHeight(GameSettings.HEIGHT);
        scene.getStylesheets().add(GameManager.class.getResource("application.css").toExternalForm());

        processGameInput();
        processGestures();

        CursorUtility.setCursor(CursorID.NORMAL, scene);

        menuManager.setupMainMenu();
        mainWindow.setScene(scene);
        mainWindow.setResizable(true);
        mainWindow.setTitle(title);
        mainWindow.setFullScreenExitHint("Press Ctrl+Enter to exit fullscreen mode!");
        mainWindow.getIcons().add(GameImageBank.appIcon);
        mainWindow.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        mainWindow.setFullScreenExitHint("Press Ctrl+Enter to exit fullscreen mode!");
        mainWindow.setOnCloseRequest(e -> {
            closeGame();
        });

        mainWindow.setFullScreen(false);
        mainWindow.initStyle(StageStyle.UNDECORATED);
        mainWindow.show();

        if(!showListeners.isEmpty()){
        	for(int i = 0; i<showListeners.size(); i++){
        		showListeners.get(i).onShow();
        	}
        }
        showListeners.clear();

        resizeListener(mainWindow, scene, sceneRoot);
        setNewRatio(false);

        gameBorder.showBorders(true);

        ResizeHelper.baseWidth = ResizeHelper.baseWidth*.65;
        ResizeHelper.baseHeight = ResizeHelper.baseHeight*.65;

        animationWidth = ResizeHelper.baseWidth*.05;
        animationHeight = ResizeHelper.baseHeight*.05;
        allowStageScale = true;

        setWindowSize(ResizeHelper.baseWidth,ResizeHelper.baseHeight);
        setWindowLocation(Screen.getPrimary().getBounds().getWidth()/2-ResizeHelper.baseWidth/2, Screen.getPrimary().getBounds().getHeight()/2-ResizeHelper.baseHeight/2);

        Platform.setImplicitExit(false);
        translateObjects(mainRoot.getChildren());
        gameLoop();
    }

    private void addToGameRoot(Pane pane){
        pane.setFocusTraversable(true);
        root.getChildren().add(pane);
    }

    public Image copyBackground(Group content) {
        Image image = content.snapshot(new SnapshotParameters(), null);
        return image;
    }

    public Image copyBackground(Pane content) {
        Image image = content.snapshot(new SnapshotParameters(), null);
        return image;
    }

    private void initialize() {
        GameLoader.scaleResolution();
        frameGameLoop = new Timeline();
        mainRoot = new Pane();
        root = new Pane();
        sceneRoot = new BorderPane();
        menuManager = new MenuManager(this);
        rootContainer = new Group(sceneRoot);
        gameBorder = new GameBorder(this,rootContainer);
        scene = new Scene(rootContainer, Color.BLACK);
        gridLayer = new Pane();
        fruitLayer = new Pane();
        dirtLayer = new Pane();
        debrisLayer = new Pane();
        snakeOneLayer = new Pane();
        snakeTwoLayer = new Pane();
        firstLayer = new Pane();
        secondLayer = new Pane();
        thirdLayer = new Pane();
        fadeScreenLayer = new Pane();
        fourthLayer = new Pane();
        fithLayer = new Pane();
        sixthLayer = new Pane();
        seventhLayer = new Pane();
        eighthLayer = new Pane();
        ninthLayer = new Pane();
        tenthLayer = new Pane();
        eleventhLayer = new Pane();
        twelfthLayer = new Pane();
        thirTeenthLayer = new Pane();
        fourTeenthLayer = new Pane();
        innerParticleLayer = new Pane();
        outerParticleLayer = new Pane();
        levelLayer = new Pane();
        loader = new GameLoader(this);
        overlayEffect = new ScreenEffectUtility(this, getGameRoot());
        fadeHandler = new FadeScreenController(this);
        objectManager = new GameObjectController(this);
        playerOneManager = new PlayerOneManager(this);
        playerTwoManager = new PlayerTwoManager(this);
        classicSnakeManager = new ClassicSnakeManager(this);
        sectManagerOne = new PlayerOneSectionManager(this);
        sectManagerTwo = new PlayerTwoSectionManager(this);
        sectManagerThree = new ClassicSnakeSectionManager(this);
        aiController = new AIController(this);
        keyInput = new KeyInputHandler();
        gestures = new TouchInputHandler();
        mouseInput = new MouseInputHandler();
        debrisManager = new GameDebrisController(this);
        loadHUDElements();

        if (GameSettings.PARENT_CACHE) {
            cacheAllLayers();
        }

    }

    private void loadHUDElements() {
        rainEmitter = new RainEmitter(this, 0, -200, 75, 1, 1);
        sandEmitter = new SandEmitter(this, -200, 0, 1, 1);
        energyBarOne = new EnergyBarOne(this, 180, 45, 275, 35);
        energyBarTwo = new EnergyBarTwo(this, GameSettings.WIDTH - 458, 45, 275, 35);
        healthBarOne = new HealthBarOne(this);
        healthBarTwo = new HealthBarTwo(this);
        pauseMenu = new PauseMenu(this, 0, 0, GameSettings.WIDTH, 300);
        gameHud = new GameHud(this, -5, -25, GameSettings.WIDTH + 10, 115);
        scoreKeeper = new ScoreKeeper(this, GameSettings.APPLE_COUNT);
        scoreBoardOne = new ScoreBoard("", this, healthBarOne.getX() + 105, 55, Color.rgb(255, 150, 0),GameObjectID.PlayerOneHUD);
        scoreBoardTwo = new ScoreBoard("", this, healthBarTwo.getX() + healthBarTwo.getWidth() - 175, 55,Color.rgb(255, 150, 0), GameObjectID.PlayerTwoHUD);
        victoryScreen = new VictoryScreen(this, GameImageBank.level_complete_board, 950, 650);
        gameOverScreen = new GameOverScreen(this, GameImageBank.game_over_board, 950, 650);
        readyNotification = new ReadyNotification(this, GameImageBank.ready_notification.getWidth(),GameImageBank.ready_notification.getHeight(), fourTeenthLayer);
        countDownScreen = new CountDownScreen(this, 400, 600, getEleventhLayer());
        GameSettings.MIN_Y = (int) gameHud.getHudBottomY();
    }

    private void cacheAllLayers() {
        root.setCache(true);
        root.setCacheHint(CacheHint.SPEED);
    }

    public void resumeGame() {
        if (GameSettings.RENDER_GAME == true)
            return;
        GameSettings.RENDER_GAME = true;
    }

    public void pauseGame() {
        if (GameSettings.RENDER_GAME == false)
            return;
        GameSettings.RENDER_GAME = false;
    }

    public void pauseAndResume() {
        if (GameSettings.RENDER_GAME == false)
            GameSettings.RENDER_GAME = true;
        else if (GameSettings.RENDER_GAME == true)
            GameSettings.RENDER_GAME = false;
    }

    public void startThreads() {
        gameLoop.start();
    }

    public void stopThreads() {
        gameLoop.stop();
    }

    public void processGameInput() {
        keyInput.processInput(this, scene);
    }

    public void processGestures() {
        pauseMenu.processTouch();
        gestures.processGestures(this);
    }

    public void fullScreenOff() {
        mainWindow.setFullScreen(false);
    }

    public void setPerformance(Pane pane) {
        pane.setCache(true);
        pane.setCacheShape(true);
        pane.setCacheHint(CacheHint.SPEED);
    }

    public void performGameUpdate(TaskWrapper update){
        gameUpdates.add(update);
    }

    public void performGUIUpdate(TaskWrapper update){
    	guiUpdate.add(update);
    }

    public void closeGame() {
        pauseGame();
        logToConsole("Good bye!");
        Platform.exit();
    }

    private void logToConsole(String message) {
        System.out.println("GAME_MANAGER: " + message);
    }

    public void setBackgroundImage(Image image) {
        backgroundImage.setImage(image);
    }

    private void windowScaleAnimation(){
        animationWidth = animationWidth*1.008;
        animationHeight = animationHeight*1.008;

        setWindowSize(animationWidth, animationHeight);
        setWindowLocation(Screen.getPrimary().getBounds().getWidth()/2-animationWidth/2, Screen.getPrimary().getBounds().getHeight()/2-animationHeight/2);

        if(animationWidth >= ResizeHelper.baseWidth && animationHeight >= ResizeHelper.baseHeight){
            allowStageScale = false;
            setWindowSize(ResizeHelper.baseWidth,ResizeHelper.baseHeight);
            setWindowLocation(Screen.getPrimary().getBounds().getWidth()/2-ResizeHelper.baseWidth/2, Screen.getPrimary().getBounds().getHeight()/2-ResizeHelper.baseHeight/2);
            menuManager.setupMainMenu();
            task.cancel();
        }
    }
    /**
     * This is the game loop. virtually every object in the game is processed,
     * rendered, and moved here by calling the updates methods of every manager.
     * The animation timer is frame dynamic meaning it will capped the frame
     * rate according to the performance of the machine is running on. This
     * method also keeps track of the frame rate
     */
    public synchronized void gameLoop() {

        gameLoop = new AnimationTimer() {

            long startTime = System.currentTimeMillis();
            long cummulativeTime = startTime;
            long lastTime = System.nanoTime();
            long nanoSecond = 1000000000;
            long currentTime = 0;
            long lastUpdate = 0;
            double delta = 0;
            double FPS = 0;

            public void handle(long now) {
                timePassed = System.currentTimeMillis() - cummulativeTime;
                cummulativeTime += timePassed;
                FPS++;
                currentTime = now;
                delta += currentTime - lastTime;
                if (now - lastUpdate >= 10_000_000) {

                    if (!GameSettings.RENDER_GAME) {
                        menuManager.transition();

                        if (!guiUpdate.isEmpty()){
                            guiUpdate.stream().forEach(update-> update.doBackgroundWork());
                        }
                    }if (GameSettings.RENDER_GAME) {

                        if (!gameUpdates.isEmpty()){
                            gameUpdates.stream().forEach(update-> update.doBackgroundWork());
                        }

                        countDownScreen.update();

                        overlayEffect.updateEffect();

                        fadeHandler.updateFade();

                        pauseMenu.updateTouchPanel();

                        gameHud.updateHudBars();

                        victoryScreen.updateUI();

                        readyNotification.updateUI();

                        gameOverScreen.updateUI();

                        scoreKeeper.updateUI();

                        objectManager.updateAll(timePassed);

                        debrisManager.updateAll();

                        loader.updateLevelObjects();

                        if (getGameLoader().getPlayerOne() != null) {
                            playerOneManager.updateAllLogic(timePassed);
                            sectManagerOne.updateAllLogic(timePassed);
                            for (int speed = 0; speed < PlayerOne.SPEED; speed += 1) {
                                playerOneManager.updateAllMovement();
                                sectManagerOne.updateAllMovement(timePassed);
                            }
                        }

                        if (getGameLoader().getPlayerTwo() != null) {
                            playerTwoManager.updateAllLogic(timePassed);
                            sectManagerTwo.updateAllLogic(timePassed);
                            for (int speed = 0; speed < PlayerTwo.SPEED; speed += 1) {
                                playerTwoManager.updateAllMovementI();
                                sectManagerTwo.updateAllMovementI();
                                if (GameSettings.ALLOW_AI_CONTROL){
                                    aiController.update_AI_Simulation(timePassed);
                                }
                            }
                        }

                        if (getGameLoader().getClassicSnake() != null) {
                            classicSnakeManager.updateAllLogic(timePassed);
                            sectManagerThree.updateAllLogic(timePassed);
                            for (int speed = 0; speed < ClassicSnake.SPEED; speed += 1) {
                                classicSnakeManager.updateAllMovement();
                                sectManagerThree.updateAllMovement(timePassed);
                            }
                        }

                        if (GameSettings.SAND_STORM) {
                            sandEmitter.move();
                            sandEmitter.emit();
                        }

                        if (GameSettings.RAIN_STORM) {
                            rainEmitter.move();
                            rainEmitter.emit();
                        }

                        if (loader.getPlayerOne() != null && getHealthBarOne() != null && getEnergyBarOne() != null) {
                            getHealthBarOne().update();
                            getEnergyBarOne().update();
                            if (scoreBoardOne != null) {
                                scoreBoardOne.updateUI();
                            }
                        }

                        if (loader.getPlayerTwo() != null && getHealthBarTwo() != null && getEnergyBarTwo() != null) {
                            getHealthBarTwo().update();
                            getEnergyBarTwo().update();
                            if (scoreBoardTwo != null) {
                                scoreBoardTwo.updateUI();
                            }
                        }

                        if (!outerParticleLayer.getChildren().isEmpty()) {
                            if (outerParticleLayer.getChildren().size() >= GameSettings.PARTICLE_LIMIT) {
                                outerParticleLayer.getChildren().remove(0);
                            }
                        }
                    }
                    lastUpdate = now;
                }
                if (delta > nanoSecond) {
                    FPS_TEXT.setText(""+FPS);
                    if(FPS>=59){
                        FPS_TEXT.setFill(Color.LIME);
                    }
                    if(FPS<59 && FPS>=30){
                        FPS_TEXT.setFill(Color.ORANGE);
                    }
                    else if(FPS<30){
                        FPS_TEXT.setFill(Color.RED);
                    }
                    delta -= nanoSecond;
                    FPS = 0;
                }
                lastTime = currentTime;
                if (GameSettings.WINDOW_ALWAYS_VISIBLE && !mainWindow.isFullScreen()) {
                    if (mainWindow.getX() < 0) {
                        mainWindow.setX(0);
                    }
                    if (mainWindow.getY() < 0) {
                        mainWindow.setY(0);
                    }
                    if (mainWindow.getX() > GameSettings.WIDTH - mainWindow.getWidth()) {
                        mainWindow.setX(GameSettings.WIDTH - mainWindow.getWidth());
                    }
                    if (mainWindow.getY() > GameSettings.HEIGHT - mainWindow.getHeight()) {
                        mainWindow.setY(GameSettings.HEIGHT - mainWindow.getHeight());
                    }
                }
            }

        };
        gameLoop.start();
    }



    /**
     * Thread used for updating non graphical elements or any other elements
     * that do not involve x and y translations within nodes.
     *
     * @author Eudy Contreras
     *
     */
    private double amountOfUpadates = 60.0;
    private double ns = 1000000000 / amountOfUpadates;
    private double delta = 0;
    private double delta1 = 0;
    private double delta2 = 0;
    private double delta3 = 0;
    private double delta4 = 0;
    private double delta5 = 0;
    private double delta6 = 0;
    private double FPS = 0;
    private long startTime = System.currentTimeMillis();
    private long lastTime = System.nanoTime();
    private long cummulativeTime = startTime;
    private long nanoSecond = 1000000000;
    private long currentTime = 0;
    private long timePassed = 0;
    private long now = 0;

    @SuppressWarnings("unused")
    private void backgroundTaskThread() {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (true) {
                    Platform.runLater(() -> {
                        FPS++;
                        now = System.nanoTime();
                        currentTime = now;
                        delta += currentTime - lastTime;
                        timePassed = System.currentTimeMillis() - cummulativeTime;
                        cummulativeTime += timePassed;
                        delta1 += (now - lastTime) / ns / 12;
                        delta2 += (now - lastTime) / ns / 4;
                        delta3 += (now - lastTime) / ns / 2;
                        delta4 += (now - lastTime) / ns;
                        delta5 += (now - lastTime) / ns * 2;
                        delta6 += (now - lastTime) / ns * 4;
                        while (delta1 >= 1) {
                            updateAt5();
                            delta1--;
                        }
                        while (delta2 >= 1) {
                            updateAt15();
                            delta2--;
                        }
                        while (delta3 >= 1) {
                            updateAt30();
                            delta3--;
                        }
                        while (delta4 >= 1) {
                            updateAt60(timePassed);
                            updateAnimation(timePassed);
                            delta4--;
                        }
                        while (delta5 >= 1) {
                            updateAt120();
                            delta5--;
                        }
                        while (delta6 >= 1) {
                            updateAt240();
                            delta6--;
                        }
                        maxFrameUpdate();
                        if (delta > nanoSecond) {
                            FPS_TEXT.setText("FPS :" + FPS);
                            delta -= nanoSecond;
                            FPS = 0;
                        }
                        lastTime = currentTime;

                    });
                    Thread.sleep(16);
                }
            }

            private void updateAt5() {

            }

            private void updateAt15() {

            }

            private void updateAt30() {

            }

            private void updateAt60(long timePassed) {

            }

            private void updateAt120() {

            }

            private void updateAt240() {

            }

            private void maxFrameUpdate() {

            }

            private void updateAnimation(long timePassed) {

            }
        };
        mainThread = new Thread(task);
        mainThread.setDaemon(true);
        mainThread.start();
    }

    @SuppressWarnings("unused")
    private void backgroundWorkerTwo() {
        task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (true) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if(allowStageScale)
                            windowScaleAnimation();
                        }
                    });
                    Thread.sleep(1);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    /**
     * Thread which can be used to updates elements and values that are not part
     * of the JavaFX UI e.g: this thread may update the x variable used for
     * translating a and element from point a to be but it may not update the
     * element itself within its run method
     *
     * @throws: Not the JavaFX Thread!! if any JavaFX UI element is updated on
     *              this thread
     */
    @SuppressWarnings("unused")
    private void nonUIThread() {

        Thread physicsThread = new Thread(new Runnable() {

            double physicsFps = 1000f / 60f;

            @Override
            public void run() {

                long prevTime = System.currentTimeMillis();
                long currTime = System.currentTimeMillis();

                while (true) {

                    currTime = System.currentTimeMillis();

                    if ((currTime - prevTime) >= physicsFps) {

                        prevTime = currTime;
                    }
                }
            }
        });

        physicsThread.setDaemon(true);
        physicsThread.start();

    }

    /**
     * The level thread function is used for two things this thread is frame
     * controlled and can provide updates on the current amount of children
     * nodes being used by the layers used in game and the amount of objects in
     * each list This thread can also be used to procedurally create a level
     * which will then put objects on the level based on the set speed
     */
    public void objectChecker() {
        if (GameSettings.OBJECT_TRACKER) {
            Timeline levelUpdateLoop = new Timeline();
            levelUpdateLoop.setCycleCount(Timeline.INDEFINITE);

            KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.512), new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    System.out.println("Amount of objects in dirt layer: " + dirtLayer.getChildren().size());
                    System.out.println("Amount of objects in debris layer: " + debrisLayer.getChildren().size());
                    System.out.println("Amount of objects in lower particle layer: " + innerParticleLayer.getChildren().size());
                    System.out.println("Amount of objects in higher particle layer: " + outerParticleLayer.getChildren().size());
                    System.out.println("Amount of objects in all layers: " + getGameRoot().getChildren().size());
                    System.out.println();
                    System.out.println();
                    System.out.println("Amount of objects in particle manager: " + debrisManager.getParticleList().size());
                    System.out.println("Amount of objects in player manager: " + playerOneManager.getObjectList().size());
                    System.out.println("Amount of objects in object manager: " + objectManager.getObsFruitList().size());
                    System.out.println("Amount of objects in debris manager: " + debrisManager.getDebrisList().size());
                    System.out.println("Amount of objects in tile manager: " + loader.getTileManager().getTile().size());
                    System.out.println("Amount of objects in block manager: " + loader.getTileManager().getBlock().size());
                    System.out.println("Amount of objects in trap manager: " + loader.getTileManager().getTrap().size());
                    System.out.println();
                    System.out.println("---------------------------------------------------------------------------");
                    System.out.println();
                }
            });
            levelUpdateLoop.getKeyFrames().add(keyFrame);
            levelUpdateLoop.play();
        }
    }

    /**
     * Method used to translate objects on the screen nodes translated will stay
     * on the visual range of the screen useful for things like heads up
     * displays and and other nodes that need to be constantly visible by the
     * player
     */
    public void translateObjects(ObservableList<Node> rootPane) {
        HBox words = new HBox(0);
        words.setAlignment(Pos.CENTER);
        Text FPS = new Text("FPS : ");
        FPS_TEXT = new Text("60.0");
        FPS_TEXT.setFill(Color.LIME);
        FPS.setFill(Color.WHITE);
        FPS.setFont(Font.font("AERIAL", FontWeight.EXTRA_BOLD, 25));
        FPS_TEXT.setFont(Font.font("AERIAL", FontWeight.EXTRA_BOLD, 25));
        words.getChildren().addAll(FPS,FPS_TEXT);
        words.setTranslateX(-GameSettings.SCREEN_WIDTH/2 + 75);
//		words.setTranslateY(5);
        words.setOpacity(1);

        rootPane.add(fadeScreenLayer);
        rootPane.add(tenthLayer);
        rootPane.add(eleventhLayer);
        rootPane.add(twelfthLayer);
        rootPane.add(thirTeenthLayer);
        rootPane.add(fourTeenthLayer);

        getGameBorder().getUtilityBar().getChildren().add(words);
    }

    public void prepareGame() {
        resetGame();
        getFadeScreenHandler().prepareIntroScreen();
        loader.loadPixelMap();
        showCursor(false, getScene());
        processGameInput();
        processGestures();
    }

    public void restart() {
        resetGame();
        loader.loadPixelMap();
        showCursor(false, getScene());
        processGameInput();
        processGestures();
    }

    public void goToNext() {
        resetGame();
        loader.switcLevel();
        scoreKeeper.setCount(GameSettings.APPLE_COUNT); 
        loader.loadPixelMap();

        showCursor(false, getScene());
        processGameInput();
        processGestures();
    }

    public void quitToMain() {
        resetGame();
        loader.loadPixelMap();
        processGameInput();
        processGestures();
        getMainMenu().setMainMenu();
    }

    public void resetGame() {
        clearAll();
        GameSettings.FRAME_SCALE = 1.0f;
        GameSettings.ALLOW_DAMAGE_IMMUNITY = false;
        PlayerOne.NUMERIC_ID = 0;
        PlayerOne.DEAD = false;
        PlayerOne.MOUTH_CLOSE = true;
        PlayerOne.KEEP_MOVING = true;
        PlayerTwo.NUMERIC_ID = 0;
        PlayerTwo.DEAD = false;
        PlayerTwo.MOUTH_CLOSE = true;
        PlayerTwo.KEEP_MOVING = true;
        PlayerTwo.AI_CONTROLLED = GameSettings.ALLOW_AI_CONTROL;
        ClassicSnake.NUMERIC_ID = 0;
        ClassicSnake.DEAD = false;
        ClassicSnake.MOUTH_CLOSE = true;
        ClassicSnake.KEEP_MOVING = true;
        victoryScreen.removeBlur();
        victoryScreen.removeBoard();
        gameOverScreen.removeBlur();
        gameOverScreen.removeBoard();
        scoreBoardOne.resetScore();
        scoreBoardTwo.resetScore();
        scoreKeeper.resetCount();
        scoreKeeper.resetTimer();
        scoreBoardOne.show(true);
        scoreBoardTwo.show(true);
        gameHud.show();
        loader.killPlayerOne();
        loader.killPlayerTwo();
        loader.killClassicSnake();
        getGameRoot().setEffect(null);
        getHealthBarOne().show();
        getHealthBarTwo().show();
        PlayerOne.LEVEL_COMPLETED = false;
        PlayerTwo.LEVEL_COMPLETED = false;
        ClassicSnake.LEVEL_COMPLETED = false;
    }

    public void clearAll() {
        GameSettings.PLAYER_ONE_SIZE = GameSettings.PLAYER_SIZE;
        GameSettings.PLAYER_TWO_SIZE = GameSettings.PLAYER_SIZE;
        GameSettings.CLASSIC_SNAKE_SIZE = 30;
        PlayerOne.SPEED = GameSettings.PLAYER_ONE_SPEED;
        PlayerTwo.SPEED = GameSettings.PLAYER_TWO_SPEED;
        ClassicSnake.SPEED = GameSettings.CLASSIC_SNAKE_SPEED;

//		gridLayer.getChildren().clear();
        fruitLayer.getChildren().clear();
        dirtLayer.getChildren().clear();
        debrisLayer.getChildren().clear();
        innerParticleLayer.getChildren().clear();
        outerParticleLayer.getChildren().clear();
        snakeOneLayer.getChildren().clear();
        snakeTwoLayer.getChildren().clear();
        firstLayer.getChildren().clear();
        secondLayer.getChildren().clear();
        thirdLayer.getChildren().clear();
        fourthLayer.getChildren().clear();
        fithLayer.getChildren().clear();
        sixthLayer.getChildren().clear();
        seventhLayer.getChildren().clear();
        eighthLayer.getChildren().clear();
        ninthLayer.getChildren().clear();
        levelLayer.getChildren().clear();
        playerOneManager.clearAll();
        playerTwoManager.clearAll();
        classicSnakeManager.clearAll();
        debrisManager.clearAll();
        objectManager.clearAll();
        sectManagerOne.clearAll();
        sectManagerTwo.clearAll();
        sectManagerThree.clearAll();
        loader.clearTiles();

    }

    public void assignPlayer() {
        getAIController().getPathFindingAI().setPlayer();
        getHealthBarOne().setPlayer();
        getHealthBarTwo().setPlayer();
        getEnergyBarOne().setPlayer();
        getEnergyBarTwo().setPlayer();
        getHealthBarOne().refill();
        getHealthBarTwo().refill();
        getEnergyBarOne().refill();
        getEnergyBarTwo().refill();
        getKeyInput().setPlayerOne(getGameLoader().getPlayerOne());
        getKeyInput().setPlayerTwo(getGameLoader().getPlayerTwo());
        getKeyInput().setClassicSnake(getGameLoader().getClassicSnake());
        getAIController().updateGrid();
    }

    public void removePlayers() {
        fithLayer.getChildren().clear();
        fourthLayer.getChildren().clear();
        playerOneManager.clearAll();
        playerTwoManager.clearAll();
        classicSnakeManager.clearAll();
        objectManager.clearAll();
        sectManagerOne.clearAll();
        sectManagerTwo.clearAll();
        sectManagerThree.clearAll();
    }

    public void setPlayerInfoVisibility(boolean state) {
        getHealthBarOne().setVisible(state);
        getHealthBarTwo().setVisible(state);
        getEnergyBarOne().setVisible(state);
        getEnergyBarTwo().setVisible(state);
        
        getScoreBoardOne().show(state);
        getScoreBoardTwo().show(state);
    }

    public static void exit() {
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }

	public static void addShowListener(ShowListener listener) {
		showListeners.add(listener);
	}
}
