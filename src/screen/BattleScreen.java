package screen;

import engine.*;
import entity.*;
import engine.I_State;
import sound.SoundPlay;
import sound.SoundType;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static engine.Collider.checkCollision;

/**
 * Implements the game screen, where the action happens.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class BattleScreen extends Screen {

	/** Milliseconds until the screen accepts user input. */
	private static final int INPUT_DELAY = 6000;
	/** Bonus score for each life remaining at the end of the level. */
	private static final int LIFE_SCORE = 100;

	/** Time from finishing the level to screen change. */
	private static final int SCREEN_CHANGE_INTERVAL = 1500;
	/** Height of the interface separation line. */
	public static final int SEPARATION_LINE_HEIGHT = 50;
	/** Milliseconds during the screen display the item info. */
	private static final int ITEM_DISPLAY_TIME = 2000;

	private static final Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());

	/** Current game difficulty settings. */
	private GameSettings gameSettings;


	/** Time from finishing the level to screen change. */
	private Cooldown screenFinishedCooldown;
	/** */
	public Cooldown itemInfoCooldown;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Checks if the level is finished. */
	private boolean levelFinished;
	/** Checks if a bonus life is received. */
	private boolean bonusLife;

	private int past_countdown=4;

	BattleState battleState;
	BattleManager battleManager;

	/**
	 * Constructor, establishes the properties of the screen.
	 *
	 * @param battleState
	 *            Current game state.
	 * @param gameSettings
	 *            Current game settings.
	 * @param bonusLife
	 *            Checks if a bonus life is awarded this level.
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public BattleScreen(final BattleState battleState,
                        final GameSettings gameSettings, final boolean bonusLife,
                        final int width, final int height, final int fps) {
		super(width, height, fps);
		this.battleState = battleState;
		this.gameSettings = gameSettings;
		this.bonusLife = bonusLife;
		if (this.bonusLife)
			this.battleState.gainB_state(C_State.livesRemaining, 1);
		if(battleState.getB_state(C_State.livesRemaining) == 0){
			SoundPlay.getInstance().stopBgm();
		}
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	public final void initialize() {
		super.initialize();

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				SoundPlay.getInstance().play(SoundType.inGameBGM);
			}
		};
		Timer timer = new Timer("Timer");
		long delay = 5800L;
		timer.schedule(task, delay);



		///////////////////////////////////
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);

		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();

		this.itemInfoCooldown = Core.getCooldown(ITEM_DISPLAY_TIME);

		this.battleManager = new BattleManager(this, gameSettings);
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		SoundPlay.getInstance().stopBgm();
		battleState.gainB_state(C_State.score,
				LIFE_SCORE * (battleState.getB_state(C_State.livesRemaining) - 1));
		this.logger.info("Screen cleared with a score of " + battleState.getB_state(C_State.score)); // 정상 출력
		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		if (this.inputDelay.checkFinished() && !this.levelFinished) {
			if (!battleManager.getShip().isDamaged()) { // ship control.
				if (inputManager.isKeyDown(KeyEvent.VK_LEFT) || inputManager.isKeyDown(KeyEvent.VK_A))
					battleManager.shipMove(-battleState.getB_state(C_State.SPEED));
				if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) || inputManager.isKeyDown(KeyEvent.VK_D))
					battleManager.shipMove(battleState.getB_state(C_State.SPEED));
				if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
					if (battleManager.shipShoot()) {
						battleState.gainB_state(C_State.bulletsShot, 1);
						SoundPlay.getInstance().play(SoundType.shoot);
					}
				}
			}
			battleManager.update();
		}

		draw();

		if ((battleManager.getEnemyShipFormation().isEmpty() || battleState.getB_state(C_State.livesRemaining) == 0)
				&& !this.levelFinished) {
			this.levelFinished = true;
			this.screenFinishedCooldown.reset();
		}

		if (this.levelFinished && this.screenFinishedCooldown.checkFinished())
			this.isRunning = false;

		if(battleManager.getEnemyShipFormation().isEmpty()){
			SoundPlay.getInstance().play(SoundType.roundClear);
			this.isRunning = false;
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);


		for (InGameItem inGameItem : battleManager.getItemManager().getItems())
			drawManager.drawEntity(inGameItem);


		drawManager.drawEntity(battleManager.getShip());
		if (battleManager.getShip().shield != null){
			drawManager.drawEntity(battleManager.getShip().shield);}


		if (battleManager.getEnemyShipSpecial() != null)
			drawManager.drawEntity(battleManager.getEnemyShipSpecial());
		if (battleManager.getEnemyShipDangerous() != null)
			drawManager.drawEntity(battleManager.getEnemyShipDangerous());
		battleManager.getEnemyShipFormation().draw();


		for (Bullet bullet : battleManager.getBulletManager().getBullets())
			drawManager.drawEntity(bullet);

		// Interface.
		drawManager.drawLevels(this, battleState.getB_state(C_State.difficulty) + 1);
		drawManager.drawScore(this, battleState.getB_state(C_State.score));
		drawManager.drawCoin(this, battleState.getB_state(C_State.coin));
		drawManager.drawLives(this, battleState.getB_state(C_State.livesRemaining));
		drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT);

		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY
					- (System.currentTimeMillis()
							- this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, battleState.getB_state(C_State.difficulty) + 1, countdown,
					this.bonusLife);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height
					/ 12);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height
					/ 12);
			if ((past_countdown > countdown)) {
				ExecutorService executorService = Executors.newCachedThreadPool();
				executorService.submit(() -> {
					past_countdown = countdown;
					if(countdown==0) SoundPlay.getInstance().play(SoundType.roundStart);
					if(countdown>0) SoundPlay.getInstance().play(SoundType.roundCounting);
				});
				executorService.shutdown();
			}
		}

		if(!itemInfoCooldown.checkFinished()){
			drawManager.drawItemInfo(this, battleManager.getItemManager().getCurItem());
		}

		drawManager.completeDrawing(this);
	}

	/**
	 * Cleans bullets that go off screen.
	 */


	/**
	 * Ma

	/**
	 * Returns a GameState object representing the status of the game.
	 * 
	 * @return Current game state.
	 */
	public final BattleState getBattleState() {
		return battleState;
	}
}
