package engine;

import entity.*;
import screen.BattleScreen;
import screen.Screen;
import sound.SoundPlay;
import sound.SoundType;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class BattleManager {
    /** Minimum time between bonus ship's appearances. */
    private static final int BONUS_SHIP_INTERVAL = 20000;
    /** Maximum variance in the time between bonus ship's appearances. */
    private static final int BONUS_SHIP_VARIANCE = 10000;
    /** Time until bonus ship explosion disappears. */
    private static final int BONUS_SHIP_EXPLOSION = 500;

    /** Minimum time between bonus ship appearances. */
    private Cooldown enemyShipSpecialCooldown;
    /** Minimum time between dangerous ship appearances. */
    private Cooldown enemyShipdangerousCooldown;
    /** Time until bonus ship explosion disappears. */
    private Cooldown enemyShipSpecialExplosionCooldown;
    /** Time until bangerous ship explosion disappears. */
    private Cooldown enemyShipdangerousExplosionCooldown;
    /** Minimum time between shots. */
    private Cooldown shootingCooldown;


    BattleScreen screen;
    /** Player's ship. */
    private Ship ship;

    /** Formation of enemy ships. */

    /** Bonus enemy ship that appears sometimes. */
    private EnemyShip enemyShipSpecial;
    /** Dangerous enemy ship tahat appears sometimes. */
    private EnemyShip enemyShipDangerous;
    private EnemyShipFormation enemyShipFormation;
    private ItemManager itemManager;
    private BattleState battleState;
    private BulletManager bulletManager;



    public BattleManager(final BattleScreen screen, final GameSettings gameSettings){
        this.screen = screen;
        ship = new Ship(screen.getWidth() / 2, screen.getHeight() - 30);
        enemyShipFormation = new EnemyShipFormation(gameSettings);
        battleState = screen.getBattleState();
        itemManager = new ItemManager(this);
        bulletManager = new BulletManager(this);

        enemyShipFormation.attach(screen);

        // Appears each 10-30 seconds.
        enemyShipSpecialCooldown = Core.getVariableCooldown(BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
        enemyShipSpecialCooldown.reset();
        enemyShipSpecialExplosionCooldown = Core.getCooldown(BONUS_SHIP_EXPLOSION);
        //add dangerous Ship
        enemyShipdangerousCooldown = Core.getVariableCooldown(BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
        enemyShipdangerousCooldown.reset();
        enemyShipdangerousExplosionCooldown = Core.getCooldown(BONUS_SHIP_EXPLOSION);
        shootingCooldown = Core.getCooldown(battleState.getB_state(C_State.SHOOTING_INTERVAL));
    }

    public void update(){
        ship.update();
        enemyShipFormation.update(); // 적 함선 갱신
        enemyShipFormation.shoot(bulletManager);
        
        // 랜덤 출현 적 함선 생성
        checkEnemyShipSpecial();
        checkEnemyShipDangerous();

        itemManager.update(); // Manage item collisions and movements
        bulletManager.update(); // Manage bullet collisions and movements
    }

    public void checkEnemyShipSpecial() {
        if (enemyShipSpecial != null) {
            if (enemyShipSpecial.getPositionX() > screen.getWidth()) {
                enemyShipSpecial = null;
                Core.getLogger().info("The special ship has escaped");
            } else {
                if (!enemyShipSpecial.isDestroyed())
                    enemyShipSpecial.move(2, 0);
                else if (enemyShipSpecialExplosionCooldown.checkFinished())
                    enemyShipSpecial = null;
            }
        } else {
            if (enemyShipSpecialCooldown.checkFinished()) {
                enemyShipSpecial = new EnemyShip();
                enemyShipSpecialCooldown.reset();
                Core.getLogger().info("A special ship appears");
            }
        }
    }

    public void checkEnemyShipDangerous() {
        if (enemyShipDangerous != null) {
            if (enemyShipDangerous.getPositionX() > screen.getWidth()) {
                battleState.gainB_state(C_State.livesRemaining, -1);
                enemyShipDangerous = null;
                Core.getLogger().info("The dangerous ship has escaped and you has lost lives");
            } else {
                if (!enemyShipDangerous.isDestroyed())
                    enemyShipDangerous.move(1, 0);
                else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
                    enemyShipDangerous = null;
            }
        } else {
            if (enemyShipdangerousCooldown.checkFinished()) {
                enemyShipDangerous = new EnemyShip(Color.BLUE);
                enemyShipdangerousCooldown.reset();
                Core.getLogger().info("A dangerous ship appears");
            }
        }
    }

    public void shipMove(int delta){
        if (delta < 0) {
            if (0 < ship.getPositionX() + delta)
                ship.movePositionX(delta);
        }
        else {
            if (ship.getPositionX() + ship.getWidth() + delta < screen.getWidth())
                ship.movePositionX(delta);
        }
    }

    /**
     * Shoots a bullet upwards.
     *
     * @return Checks if the bullet was shot correctly.
     */
    public boolean shipShoot() {
        if (shootingCooldown.checkFinished()) {
            shootingCooldown = Core.getCooldown(battleState.getB_state(C_State.SHOOTING_INTERVAL));
            shootingCooldown.reset();
            Core.getLogger().info(battleState.getB_state(C_State.BULLET_SPEED) + "");
            bulletManager.addBullet(new Bullet(ship.getPositionX(),
                    ship.getPositionY(), battleState.getB_state(C_State.BULLET_SPEED)));
            return true;
        }
        return false;
    }

    public Ship getShip(){
        return ship;
    }

    public BattleScreen getScreen(){
        return screen;
    }

    public EnemyShipFormation getEnemyShipFormation(){
        return enemyShipFormation;
    }
    public BattleState getBattleState(){
        return battleState;
    }
    public ItemManager getItemManager(){
        return itemManager;
    }
    public BulletManager getBulletManager(){
        return bulletManager;
    }

    public EnemyShip getEnemyShipSpecial(){
        return enemyShipSpecial;
    }
    public EnemyShip getEnemyShipDangerous(){
        return enemyShipDangerous;
    }
}
