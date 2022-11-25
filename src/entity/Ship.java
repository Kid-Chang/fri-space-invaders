package entity;

import java.awt.Color;
import java.util.Set;

import engine.*;
import engine.DrawManager.SpriteType;
import screen.BattleScreen;
import sound.SoundPlay;
import sound.SoundType;
//import entity.Shield;

/**
 * Implements a ship, to be controlled by the player.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Ship extends Entity {
	/** Time spent inactive between hits. */
	public Cooldown destructionCooldown;

	private int shipShape, shipColor;

	public Shield shield = null;

	/**
	 * Constructor, establishes the ship's properties.
	 * 
	 * @param positionX
	 *            Initial position of the ship in the X axis.
	 * @param positionY
	 *            Initial position of the ship in the Y axis.
	 */

	public Ship(final int positionX, final int positionY) {
		super(positionX, positionY, 26, 16);
		shipShape = PermanentState.getInstance().getP_state(P_State.shipShape);
		shipColor = PermanentState.getInstance().getP_state(P_State.shipColor);
		this.positionX = positionX;
		this.positionY = positionY;
		this.width = 26;
		this.height = 16;
		if (shipColor == 0) this.color = Color.BLUE;
		if (shipColor == 1) this.color = Color.GRAY;
		if (shipColor == 2) this.color = Color.GREEN;

		if (shipShape == 0)
			this.spriteType = SpriteType.ShipA;
		if (shipShape == 1)
			this.spriteType = SpriteType.ShipB;
		if (shipShape == 2)
			this.spriteType = SpriteType.ShipC;

		destructionCooldown = Core.getCooldown(1000);
	}

	/**
	 	Moves the ship speed units, until the screen border is reached.
	 */
	public final void movePositionX(int delta)
	{
		if (this.destructionCooldown.checkFinished()) {
			positionX += delta;
			if (shield != null)
				shield.movePositionX();
		}
	}




	/**
	 * Updates status of the ship.
	 */
	public final void update() {
		if (!this.destructionCooldown.checkFinished())
			this.spriteType = SpriteType.ShipDestroyed;
		else {
			if (shipShape == 0)
				this.spriteType = SpriteType.ShipA;
			if (shipShape == 1)
				this.spriteType = SpriteType.ShipB;
			if (shipShape == 2)
				this.spriteType = SpriteType.ShipC;
		}
	}

	/**
	 * Switches the ship to its damaged state.
	 */
	public final void damaged() {
		this.destructionCooldown.reset();
	}

	/**
	 * Checks if the ship is damaged.
	 * 
	 * @return True if the ship is currently damaged.
	 */
	public final boolean isDamaged() {
		return !destructionCooldown.checkFinished();
	}

}
