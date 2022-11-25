package entity;

import engine.DrawManager;
import engine.I_State;

import java.awt.*;
/**
 * Implements a item.
 */
public class InGameItem extends Entity {
    /** Item drop speed */
    private int speed;
    /** Item types. */

    private I_State i_state;
    private boolean isDestroy = false;

    /**
     * Constructor, establishes the item's properties.
     *
     * @param positionX
     *            Initial position of the item in the X axis.
     * @param positionY
     *            Initial position of the item in the Y axis.
     * @param speed
     *            Initial set of the item's drop-speed
     * @param i_state
     *            set the item's type.
     */
    public InGameItem(final int positionX, final int positionY, final int speed, final I_State i_state) {
        super(positionX, positionY, 9 * 2, 9 * 2, Color.ORANGE);
        spriteType = DrawManager.SpriteType.ItemDrop;
        this.setPositionX(positionX -this.getWidth()/2);
        this.speed = speed;
        this.i_state = i_state;
    }

    /**
     * Updates the item's position.
     */
    public void update () {
        this.positionY += this.speed;
    }


    /**
     * decision to player's obtaining item.
     */
    public void setIsDestroy(){
        isDestroy = true;
        spriteType = DrawManager.SpriteType.ItemGet;
    }

    /**
     * get information that isget.
     */
    public boolean getIsDestroy(){
        return isDestroy;
    }

    /**
     * Returns item's itemtype
     *
     * @return itemtype that dropped item has.
     */
    public I_State getItemType(){
        return this.i_state;
    }
}
