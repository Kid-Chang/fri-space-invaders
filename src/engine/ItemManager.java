package engine;

import entity.EnemyShip;
import entity.EnemyShipFormation;
import entity.InGameItem;
import entity.Shield;
import screen.BattleScreen;
import sound.SoundPlay;

import java.util.*;

/**
 * Manages assigning items to enemyship.
 *
 */
public class ItemManager {
    private Set<InGameItem> items;

    private int curItem;

    BattleManager battleManager;
    BattleState battleState;

    /**
     * constructor.
     * randomly shuffling items
     */
    public ItemManager(BattleManager battleManager) {
        items = new HashSet<InGameItem>();

        this.battleManager = battleManager;
        battleState = battleManager.getBattleState();
    }

    public void update(){
        for (InGameItem inGameItem : items){
            inGameItem.update();
            manageGetItem(inGameItem);
        }
        cleanItems();
    }

    public void addItem(InGameItem item){
        items.add(item);
    }

    private void manageGetItem(InGameItem inGameItem) {
        if (Collider.checkCollision(inGameItem, battleManager.getShip())) {
            inGameItem.setIsDestroy();

            if (inGameItem.getItemType() == I_State.BulletSpeedItem) {
                Core.getLogger().info("Obtained BulletSpeed Item");
                clearItemBuff();
                curItem = 0;
                battleManager.getScreen().itemInfoCooldown.reset();

                battleState.setB_state(C_State.BULLET_SPEED, battleState.getB_state(C_State.BULLET_SPEED) * 2);
            }
            else if (inGameItem.getItemType() == I_State.PointUpItem) {
                Core.getLogger().info("Obtained PointUp Item");
                clearItemBuff();
                curItem = 1;
                battleManager.getScreen().itemInfoCooldown.reset();

                for (EnemyShip enemyShip : battleManager.getEnemyShipFormation())
                    enemyShip.setPointValue(2 * enemyShip.getPointValue());
            }
            else if (inGameItem.getItemType() == I_State.MachineGun) {
                Core.getLogger().info("Obtained MachineGun");
                clearItemBuff();
                curItem = 2;
                battleManager.getScreen().itemInfoCooldown.reset();

                battleState.setB_state(C_State.SHOOTING_INTERVAL, battleState.getB_state(C_State.SHOOTING_INTERVAL) / 10);
            }
            else if (inGameItem.getItemType() == I_State.ShieldItem) {
                Core.getLogger().warning("Obtained Shield Item");
                clearItemBuff();
                curItem = 3;
                battleManager.getScreen().itemInfoCooldown.reset();

                battleManager.getShip().shield = new Shield(battleManager.getShip().getPositionX(), battleManager.getShip().getPositionY() - 3, battleManager.getShip());
            }
            else if (inGameItem.getItemType() == I_State.SpeedUpItem) {
                Core.getLogger().warning("Obtained SpeedUp Item");
                clearItemBuff();
                curItem = 4;
                battleManager.getScreen().itemInfoCooldown.reset();

                battleState.setB_state(C_State.SPEED, battleState.getB_state(C_State.SPEED) * 2);
            }
            else if (inGameItem.getItemType() == I_State.ExtraLifeItem) {
                clearItemBuff();
                if (battleState.getB_state(C_State.livesRemaining) < 4) {
                    Core.getLogger().warning("Obtained ExtraLife Item");
                    curItem = 5;
                    battleManager.getScreen().itemInfoCooldown.reset();

                    battleState.gainB_state(C_State.livesRemaining, 1);
                }
                else
                    Core.getLogger().warning("생명 4개 초과");
            }
        }
    }

    private void cleanItems() {
        Set<InGameItem> recyclable = new HashSet<InGameItem>();
        for (InGameItem inGameItem : items) {
            if (inGameItem.getIsDestroy() || inGameItem.getPositionY() > battleManager.getScreen().getHeight())
                recyclable.add(inGameItem);
        }
        items.removeAll(recyclable);
    }

    public void clearItemBuff(){
        if (curItem == I_State.PointUpItem.ordinal()) {
            for (EnemyShip enemyShip : battleManager.getEnemyShipFormation())
                enemyShip.setInitPointValue();
        }
        battleManager.getBattleState().setInitState();
        battleManager.getShip().shield = null;
    }

    public Set<InGameItem> getItems(){
        return items;
    }

    public int getCurItem(){
        return curItem;
    }
}
