package testJava;

import java.util.*;

class Card {
	final String value; // 2-10, A, J, K, Q
	final String face; // four kinds - heart, spade, club, diamond
	private int score;

	public Card(String value, String face) {
		this.value = value;
		this.face = face;
		parseScore();
	}

	public Card(String value) {
		this.value = value;
		this.face = null;
		parseScore();
	}

	protected void parseScore() {
		switch (value) {
		case "A":
			this.score = 1;
			break;
		case "J":
			this.score = 11;
			break;
		case "Q":
			this.score = 12;
			break;
		case "K":
			this.score = 13;
			break;
		case "10":
			this.score = 10;
			break;
		default:
			this.score = Integer.parseInt(value);
		}
	}

	public String getValue() {
		return this.value;
	}

	public int getScore() {
		return this.score;
	}

	public String getFace() {
		return this.face;
	}
}

class BlackJackCard extends Card {
	private int score;
	
	public BlackJackCard(String value) {
		super(value);
	}

	@Override
	protected void parseScore() {
		switch (value) {
		case "A":
			this.score = 1;
			break;
		case "J":
		case "K":
		case "Q":
		case "10":
			this.score = 10;
			break;
		default:
			this.score = Integer.parseInt(value);
		}
	}
	
	@Override
	public int getScore() {
		return this.score;
	}
}

class DeckOfBlackJackCards {
	// how many deck of card, every deck should has 52 cards - 4 * 13
	public int numberOfDeck;
	public List<Card> cards;

	public DeckOfBlackJackCards(int numberOfDeck) {
		this.numberOfDeck = numberOfDeck;
		this.cards = new ArrayList<Card>();
		initialCards();
		shuffleCards();
	}

	private void initialCards() {
		String[] allKindCard = new String[] { "A", "1", "2", "3", "4", "5",
				"6", "7", "8", "9", "10", "J", "Q", "K" };
		if (numberOfDeck <= 0) {
			return;
		}
		for (int i = 0; i < numberOfDeck; i++) {
			for (int j = 1; j <= 4; j++) { // every deck of cards has 4
											// kinds
				for (String oneCard : allKindCard) {
					cards.add(new BlackJackCard(oneCard));
				}
			}
		}
	}

	private void shuffleCards() {
		Random rand = new Random();
		for (int i = 0; i < cards.size(); i++) {
			swap(i, rand.nextInt(cards.size()));
		}
	}

	private void swap(int i, int j) {
		Card tempCard = cards.get(j);
		cards.set(j, cards.get(i));
		cards.set(i, tempCard);
	}

	public List<Card> getDeckOfBlackJackCards() {
		return this.cards;
	}

	public Card getOneCard() {
		if (cards.isEmpty())
			return null;
		Card res = cards.get(cards.size() - 1);
		cards.remove(cards.size() - 1);
		return res;
	}
}

class CardsOnHand {
	public List<Card> cards;
	public List<Integer> possibleScores;
	public boolean allBust;
	public boolean blackJack;

	public CardsOnHand() {
		this.cards = new ArrayList<Card>();
		this.possibleScores = new ArrayList<Integer>();
		this.allBust = false;
		this.blackJack = false;
	}

	public void addCard(Card card) {
		cards.add(card);
		updateScore(card);
	}
	
	public int getScore() {
		int maxValue = Integer.MIN_VALUE;
		int minValue = 0;
		for (int i = 0; i < possibleScores.size(); i++) {
			int temp = possibleScores.get(i);
			if (temp > 21 && temp < maxValue) {
				maxValue = temp;
			} else if (temp <= 21 && temp > minValue) {
				minValue = temp;
			}
		}
		if (minValue == 21)
			this.blackJack = true;
		return minValue == 0 ? maxValue : minValue;
	}

	private void updateScore(Card card) {
		boolean allbust = true;
		if (possibleScores.isEmpty()) {
			if (card.getValue().equals("A")) {
				possibleScores.add(1);
				possibleScores.add(11);
			} else {
				possibleScores.add(card.getScore());
			}
			allbust = false;
		} else {
			final int possibleScoresLengthSoFar = possibleScores.size();
			if (card.getValue().equals("A")) {
				for (int i = 0; i < possibleScoresLengthSoFar; i++) {
					Integer score = possibleScores.get(i);
					if (!isBust(score + 11)) {
						possibleScores.set(i, score + 11);
						possibleScores.add(i, score + 1);
						allbust = false;
					} else if (isBust(score + 1)) {
						possibleScores.set(i, score + 1);
						allbust = false;
					}
				}
			} else {
				for (int i = 0; i < possibleScoresLengthSoFar; i++) {
					Integer score = possibleScores.get(i);
					if (!isBust(score + card.getScore())) {
						possibleScores.set(i, score + card.getScore());
						allbust = false;
					}
				}
			}
		}
		this.allBust = allbust;
	}

	private boolean isBust(Integer score) {
		return score > 21;
	}

	public boolean isAllBust() {
		return this.allBust;
	}

	public boolean isBlackJack() {
		return this.blackJack;
	}

	public String getCards() {
		StringBuilder res = new StringBuilder();
		for (Card card : cards) {
			res.append(card.getValue() + ",");
		}
		return res.toString();
	}
}

class BlackJackGameDemo {
	public int round;
	private DeckOfBlackJackCards oneDeckOfBlackJackCards;
	private CardsOnHand player1;
	private CardsOnHand player2;

	public BlackJackGameDemo(int round, int deckNumber) {
		this.round = round;
		this.oneDeckOfBlackJackCards = new DeckOfBlackJackCards(deckNumber);
		this.player1 = new CardsOnHand();
		this.player2 = new CardsOnHand();
	}

	public void start() {
		Scanner in = new Scanner(System.in);
		System.out.println("Round " + round);
		while (round > 0) {
			int number = 0;
			while (number < 4) {
				if (number < 2) {
					player1.addCard(oneDeckOfBlackJackCards.getOneCard());
					player2.addCard(oneDeckOfBlackJackCards.getOneCard());
				} else {
					System.out.println("Player1 call a card? Y / N");
					String condition = in.next();
					if (condition.equals("Y")) {
						player1.addCard(oneDeckOfBlackJackCards
								.getOneCard());
					}
					System.out.println("Player2 call a card? Y / N");
					condition = in.next();
					if (condition.equals("Y")) {
						player2.addCard(oneDeckOfBlackJackCards
								.getOneCard());
					}
				}
				System.out.println("player1 get " + player1.getCards());
				System.out.println("player2 get " + player2.getCards());
				if (player1.isAllBust() && player2.isAllBust()) {
					System.out.println("All lost");
					break;
				} else if (player1.isAllBust()) {
					System.out.println("player2 win");
					break;
				} else if (player2.isAllBust()) {
					System.out.println("player1 win");
					break;
				}
				if (player1.isBlackJack() && player2.isBlackJack()) {
					System.out.println("All win");
					break;
				} else if (player1.isBlackJack()) {
					System.out.println("player1 win");
					break;
				} else if (player2.isBlackJack()) {
					System.out.println("player2 win");
					break;
				}
				number++;
			}
			System.out
					.println("4 cards have been all distruibted, player1 score: "
							+ player1.getScore()
							+ "  player2 score:"
							+ player2.getScore());
			round--;
		}
	}
}

public class GameOfBlackJack {
	// simulate the game
	
	public static void main(String[] args) {
		BlackJackGameDemo gameDemo = new BlackJackGameDemo(1, 1); // 1 round, 1
																	// deck of
																	// card
		gameDemo.start();
	}
}
