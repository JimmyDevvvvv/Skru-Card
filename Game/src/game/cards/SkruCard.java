package game.cards;

import game.enums.SkruType;

public class SkruCard extends Card {
	private final SkruType type;

	public SkruType getType() {
		return type;
	}

	public SkruCard(SkruType type) {
		this.type = type;
	}

	public int getValue() {
		if (type == SkruType.RED)
			return 25;
		else
			return -1;
	}

	@Override
	public String toString() {
		if (type == SkruType.RED)

			return "REDSkru";
		else
			return "GREENSkru";
	}

}
