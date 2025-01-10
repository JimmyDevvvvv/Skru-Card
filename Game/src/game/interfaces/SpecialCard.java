package game.interfaces;

import game.engine.Player;

public interface SpecialCard {

	public default void performSpecialAction(Player p , int cardIndex){
		p.getHand().get(cardIndex).setVisible(true);
	}
	
}
