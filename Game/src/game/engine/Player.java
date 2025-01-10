package game.engine;

import game.cards.ActionCard;
import game.cards.Card;
import game.cards.NumberCard;
import game.cards.SkruCard;
import game.interfaces.SpecialCard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Player {

	// milestone 2

	private int turnCount = 0;
	private boolean hasPlayed;
	private boolean hasDrawn;
     
	private String name;
	private ArrayList<Card> hand;
	private int currenrTurnTotal;

	public boolean isHasDrawn() {
		return hasDrawn;
	}

	public void setHasDrawn(boolean hasDrawn) {
		this.hasDrawn = hasDrawn;
	}

	public int getTurnCount() {
		return turnCount;
	}

	public void setTurnCount(int turnCount) {
		this.turnCount = turnCount;
	}

	public boolean isHasPlayed() {
		return hasPlayed;
	}

	public void setHasPlayed(boolean hasPlayed) {
		this.hasPlayed = hasPlayed;
	}

	public int getCurrenrTurnTotal() {
		return currenrTurnTotal;
	}

	public void setCurrenrTurnTotal(int currenrTurnTotal) {
		this.currenrTurnTotal = currenrTurnTotal;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Card> getHand() {
		return hand;
	}

	public Player(String name) {
		this.name = name;
		this.hand = new ArrayList<Card>();
 
	}

	public int updateTotal() {
		int total = 0;
		for (Card card : hand) {
			if (card instanceof ActionCard)
				total += 10;

			else if (card instanceof NumberCard)
				total += ((NumberCard) card).getNumber();

			else if (card instanceof SkruCard)
				total += ((SkruCard) card).getValue();
		}
		return total;
	}

	public void discardCard(Card c, ArrayList<Card> d) {
		c.setVisible(true);
		int index = hand.indexOf(c);
		d.add(hand.remove(index));
	}

}
