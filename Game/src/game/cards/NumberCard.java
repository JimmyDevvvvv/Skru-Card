package game.cards;

public class NumberCard extends Card {

	private final int number;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ""+number;
	}

	public NumberCard(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}
	


}
