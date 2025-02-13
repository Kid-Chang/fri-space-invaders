package engine;

import java.util.ArrayList;

import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import screen.*;


/**
 * Implements core game logic.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class Core {

	/** Width of current screen. */
	private static final int WIDTH = 448;
	/** Height of current screen. */
	private static final int HEIGHT = 520;
	/** Max fps of current screen. */
	private static final int FPS = 60;

	/** Max lives. */
	private static final int MAX_LIVES = 3;
	/** Levels between extra life. */
	private static final int EXTRA_LIFE_FRECUENCY = 3;
	/** Total number of levels. */
	private static final int NUM_LEVELS = 8;
	/** Check click on Save & Exit button */
	private static boolean GO_MAIN;
	/** Difficulty settings for level 1. */
	private static final GameSettings SETTINGS_LEVEL_1 =

			new GameSettings(1, 5, 4, 60, 2000);
	/** Difficulty settings for level 2. */
	private static final GameSettings SETTINGS_LEVEL_2 =
			new GameSettings(2, 5, 5, 50, 2500);
	/** Difficulty settings for level 3. */
	private static final GameSettings SETTINGS_LEVEL_3 =
			new GameSettings(3, 6, 5, 40, 1500);
	/** Difficulty settings for level 4. */
	private static final GameSettings SETTINGS_LEVEL_4 =
			new GameSettings(4, 6, 6, 30, 1500);
	/** Difficulty settings for level 5. */
	private static final GameSettings SETTINGS_LEVEL_5 =
			new GameSettings(5, 7, 6, 20, 1000);
	/** Difficulty settings for level 6. */
	private static final GameSettings SETTINGS_LEVEL_6 =
			new GameSettings(6, 7, 7, 10, 1000);
	/** Difficulty settings for level 7. */
	private static final GameSettings SETTINGS_LEVEL_7 =
			new GameSettings(7,7, 8, 5, 500);

	/** add boss stage **/
	private static final GameSettings SETTINGS_Boss_Stage =
			new GameSettings(8, 3,3, 0, 200);

	/** Frame to draw the screen on. */
	private static Frame frame;
	/** Screen currently shown. */
	private static Screen currentScreen;
	/** Difficulty settings list. */
	private static List<GameSettings> gameSettings;
	/** Application logger. */
	private static final Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());
	/** Logger handler for printing to disk. */
	private static Handler fileHandler;
	/** Logger handler for printing to console. */
	private static ConsoleHandler consoleHandler;


	/**
	 * Test implementation.
	 *
	 * @param args
	 *            Program args, ignored.
	 */
	public static void main(final String[] args) {
		try {
			LOGGER.setUseParentHandlers(false);

			fileHandler = new FileHandler("log");
			fileHandler.setFormatter(new MinimalFormatter());

			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new MinimalFormatter());

			LOGGER.addHandler(fileHandler);
			LOGGER.addHandler(consoleHandler);
			LOGGER.setLevel(Level.ALL);

		} catch (Exception e) {
			// TODO handle exception
			e.printStackTrace();
		}

		frame = new Frame(WIDTH, HEIGHT);
		DrawManager.getInstance().setFrame(frame);
		int width = frame.getWidth();
		int height = frame.getHeight();


		gameSettings = new ArrayList<GameSettings>();
		gameSettings.add(SETTINGS_LEVEL_1);
		gameSettings.add(SETTINGS_LEVEL_2);
		gameSettings.add(SETTINGS_LEVEL_3);
		gameSettings.add(SETTINGS_LEVEL_4);
		gameSettings.add(SETTINGS_LEVEL_5);
		gameSettings.add(SETTINGS_LEVEL_6);
		gameSettings.add(SETTINGS_LEVEL_7);
		gameSettings.add(SETTINGS_Boss_Stage);

		ChapterState chapterState = null;
		PermanentState permanentState = PermanentState.getInstance();
		ItemState itemState = ItemState.getInstance();



		int returnCode = 1;
		do {
			switch (returnCode) {
				case 1:
					// Main menu.
					currentScreen = new TitleScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " title screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing title screen.");

					break;

				case 2:
					// Game & score.
					if (chapterState == null){
						chapterState = new ChapterState(4);
					}
					currentScreen = new MapScreen(chapterState, width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " map screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing map screen.");
					break;

				case 3:
					// High scores.
					currentScreen = new HighScoreScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " high score screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing high score screen.");
					break;

				case 4:
					// Store
					currentScreen = new StoreScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " store screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing store screen.");
					break;

				case 5:
					// Load
					String save_info [] = getFileManager().loadInfo();
					returnCode = 2;
					break;

				case 6:
					// Setting.
					currentScreen = new SettingScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " setting screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing setting screen.");
					break;

				case 7: //Help
					currentScreen = new HelpScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " setting screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing help screen.");
					break;

				case 8: //Map testing


				case 9: //Volume //mainmenu 1014
					currentScreen = new VolumeScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " setting screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing help screen.");
					break;

				case 100:
					BattleState battleState = new BattleState(chapterState.getC_state());
					currentScreen = new BattleScreen(battleState,
							gameSettings.get(chapterState.getC_state(C_State.difficulty)),
							false, width, height, FPS); // bonus life is false...? need condition
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " battle screen at " + FPS + " fps.");
					frame.setScreen(currentScreen);
					LOGGER.info("Closing battle screen.");
					battleState = ((BattleScreen)currentScreen).getBattleState();
					chapterState.setC_state(battleState.getB_state());

					if(battleState.getB_state(C_State.livesRemaining) > 0){
						//저장
						//중간결과
						currentScreen = new BattleResultScreen(width, height, FPS, battleState);
						LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
								+ " battle result screen at " + FPS + " fps.");
						returnCode = frame.setScreen(currentScreen); // is 2.
						LOGGER.info("Closing battle result screen.");
					}
					else{
						//최종결과
						currentScreen = new ResultScreen(width, height, FPS, chapterState);
						LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
								+ " result screen at " + FPS + " fps.");
						returnCode = frame.setScreen(currentScreen); // is 1.
						LOGGER.info("Closing result screen.");
						chapterState = null; // current chapterstate delete
						returnCode = 1;
					}
					break;

				case 200:
					BattleState bossState = new BattleState(chapterState.getC_state());
					currentScreen = new BattleScreen(bossState, gameSettings.get(7),
							false, width, height, FPS); // bonus life is false...? need condition
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " battle screen at " + FPS + " fps.");
					frame.setScreen(currentScreen);
					LOGGER.info("Closing battle screen.");
					bossState = ((BattleScreen)currentScreen).getBattleState();
					chapterState.setC_state(bossState.getB_state());

					if (bossState.getB_state(C_State.livesRemaining) > 0){
						currentScreen = new BattleResultScreen(width, height, FPS, bossState);
						LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
								+ " battle result screen at " + FPS + " fps.");
						returnCode = frame.setScreen(currentScreen); // is 8.
						LOGGER.info("Closing battle result screen.");
					}
					else {
						currentScreen = new ResultScreen(width, height, FPS, chapterState);
						LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
								+ " result screen at " + FPS + " fps.");
						returnCode = frame.setScreen(currentScreen); // is 1.
						LOGGER.info("Closing result screen.");
						chapterState = null; // current chapterstate delete
						returnCode = 1;
					}
					break;

				default:
					break;
			}

		} while (returnCode != 0);

		fileHandler.flush();
		fileHandler.close();
		System.exit(0);
	}

	/**
	 * Constructor, not called.
	 */
	private Core() {

	}

	/**
	 * Controls access to the logger.
	 *
	 * @return Application logger.
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Controls access to the drawing manager.
	 *
	 * @return Application draw manager.
	 */
	public static DrawManager getDrawManager() {
		return DrawManager.getInstance();
	}

	/**
	 * Controls access to the input manager.
	 *
	 * @return Application input manager.
	 */
	public static InputManager getInputManager() {
		return InputManager.getInstance();
	}

	/**
	 * Controls access to the file manager.
	 *
	 * @return Application file manager.
	 */
	public static FileManager getFileManager() {
		return FileManager.getInstance();
	}

	/**
	 * Controls creation of new cooldowns.
	 *
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @return A new cooldown.
	 */
	public static Cooldown getCooldown(final int milliseconds) {
		return new Cooldown(milliseconds);
	}

	/**
	 * Controls creation of new cooldowns with variance.
	 *
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @param variance
	 *            Variation in the cooldown duration.
	 * @return A new cooldown with variance.
	 */
	public static Cooldown getVariableCooldown(final int milliseconds,
											   final int variance) {
		return new Cooldown(milliseconds, variance);
	}
}