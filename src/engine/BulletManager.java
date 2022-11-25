package engine;

import entity.*;
import screen.BattleScreen;
import sound.SoundPlay;
import sound.SoundType;

import java.util.*;

/**
 * Manages assigning items to enemyship.
 *
 */
public class BulletManager {
    /** Set of all bullets fired on screen */
    private Set<Bullet> bullets;

    BattleManager battleManager;
    BattleState battleState;

    public BulletManager(BattleManager battleManager) {
        this.battleManager = battleManager;
        battleState = battleManager.getBattleState();
        bullets = new HashSet<Bullet>();
    }

    public void update(){
        for (Bullet bullet : bullets){
            bullet.update();
            manageHitBullet(bullet);
        }
        cleanBullets();
    }

    public void addBullet(Bullet bullet){
        bullets.add(bullet);
    }

    private void manageHitBullet(Bullet bullet) {
        if (bullet.getSpeed() > 0) { // Enemy's Bullet
            if (Collider.checkCollision(bullet, battleManager.getShip())) {
                bullet.setIsDestroy();

                if (!battleManager.getShip().isDamaged()) {
                    SoundPlay.getInstance().play(SoundType.hit);
                    if (battleManager.getShip().shield == null) {
                        battleManager.getShip().damaged();
                        battleState.gainB_state(C_State.livesRemaining, -1);
                        Core.getLogger().info("Hit on player ship, " + battleState.getB_state(C_State.livesRemaining)
                                + " lives remaining.");
                        battleManager.getItemManager().clearItemBuff();
                    } else {
                        battleManager.getShip().shield = null;
                    }
                }
            }
        }
        else { // Player's Bullet
            for (EnemyShip enemyShip : battleManager.getEnemyShipFormation()) {
                if (!enemyShip.isDestroyed() && Collider.checkCollision(bullet, enemyShip)) {
                    bullet.setIsDestroy();

                    SoundPlay.getInstance().play(SoundType.enemyKill);
                    battleState.gainB_state(C_State.score, enemyShip.getPointValue());
                    battleState.gainB_state(C_State.shipsDestroyed, 1);
                    battleManager.getEnemyShipFormation().destroy(enemyShip);
                }
            }
            collideEnemy(battleManager.getEnemyShipSpecial(), bullet);
            collideEnemy(battleManager.getEnemyShipDangerous(), bullet);
        }
    }

    public void collideEnemy (EnemyShip enemyShip, Bullet bullet) {
        if (enemyShip != null && !enemyShip.isDestroyed() && Collider.checkCollision(bullet, enemyShip)) {
            SoundPlay.getInstance().play(SoundType.bonusEnemyKill);
            battleState.gainB_state(C_State.score, enemyShip.getPointValue());
            battleState.gainB_state(C_State.shipsDestroyed, 1);

            battleManager.getItemManager().addItem(new InGameItem(
                    enemyShip.getPositionX(), enemyShip.getPositionY(),
                    2, I_State.values()[new Random().nextInt(I_State.values().length)]));
            enemyShip.destroy();
        }
    }

    private void cleanBullets() {
        Set<Bullet> recyclable = new HashSet<Bullet>();
        for (Bullet bullet : this.bullets) {
            if (bullet.getIsDestroy() || bullet.getPositionY() < BattleScreen.SEPARATION_LINE_HEIGHT
                    || bullet.getPositionY() > battleManager.getScreen().getHeight())
                recyclable.add(bullet);
        }
        bullets.removeAll(recyclable);
    }

    public Set<Bullet> getBullets(){
        return bullets;
    }
}
