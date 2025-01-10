package game.cards;

import game.engine.Player;
import game.interfaces.SpecialCard;

public class SelfRevealerCard extends NumberCard implements SpecialCard {
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "("+super.toString()+" seeYours)";
	}

	public SelfRevealerCard(int number) {
		super(number);
		// TODO Auto-generated constructor stub
	}



}
