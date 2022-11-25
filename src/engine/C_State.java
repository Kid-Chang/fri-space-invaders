package engine;

public enum C_State {
    chapter,
    difficulty,
    score,
    coin,
    livesRemaining,
    bulletsShot,
    shipsDestroyed,

    /** Time between shots. */
    SHOOTING_INTERVAL, INIT_SHOOTING_INTERVAL,
    /** Speed of the bullets shot by the ship. */
    BULLET_SPEED, INIT_BULLET_SPEED,
    /** Movement of the ship for each unit of time. */
    SPEED, INIT_SPEED
};