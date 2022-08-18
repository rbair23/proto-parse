package sample.target.model;

public enum Suit {
	ACES, SPADES, CLUBS, DIAMONDS;

	public static Suit fromOrdinal(final int ordinal) {
		return switch (ordinal) {
			case 0 -> ACES;
			case 1 -> SPADES;
			case 2 -> CLUBS;
			case 3 -> DIAMONDS;
			default -> throw new AssertionError("Test error, cannot happen");
		};
	}
}
