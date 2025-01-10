# Skru Card Game

## Project Overview
This project is an implementation of the popular Egyptian card game **Skru** using Java, showcasing advanced object-oriented programming (OOP) concepts. The game supports four players and integrates features like custom rules, special cards, and dynamic gameplay mechanics, offering an engaging and strategic experience.

---

## Features

### 1. Core Gameplay
- **Game Setup:**
  - Supports 4 players with a standard deck of cards.
  - Players receive 4 face-down cards, with the two rightmost cards visible at the start of each round.
- **Objective:**
  - Players aim to minimize their card values to achieve the lowest score by the end of 5 rounds.
- **Actions Per Turn:**
  - **Replace a Card:** Draw from the deck or discard pile to replace a face-down card.
  - **Replicate a Card:** Discard a matching card to reduce the hand size, with penalties for mismatched attempts.
  - **Use Special Cards:** Perform unique actions such as peeking, swapping, or giving cards.

### 2. Special Cards
- **7 and 8:** Peek at one of your own cards.
- **9 and 10:** Look at an opponent's card.
- **Eye Master Card:** View a card from each player, including yourself.
- **Swap Card:** Swap one of your cards with an opponent's card.
- **Replica Card:** Discard any card.
- **Give Card:** Transfer a card to another player.

### 3. Gameplay Flow
- **Declaring "Skru":**
  - Players can declare "Skru" after three turns to signal the round's end. If the declarer doesn’t have the lowest score, their score is doubled.
- **Scoring:**
  - Action cards add 10 points, red Skru cards add 25 points, and green Skru cards subtract 1 point.
  - The player with the lowest score in a round scores zero.
- **Winning:**
  - The winner is the player with the lowest total score after 5 rounds.

---

## Technology Stack
- **Programming Language:** Java
- **Object-Oriented Principles:**
  - Encapsulation, Inheritance, Polymorphism, and Abstraction.
- **Custom Exception Handling:** Includes specialized exceptions like `CannotPlayException` and `CannotSkruException`.
- **Design Patterns:** Interface-based special card actions.

---

## Key Components

### Classes and Methods
- **Player Class:**
  - Attributes to track turn count, played status, and card draws.
  - Methods for scoring, card management, and turn handling.
- **Game Class:**
  - Ensures gameplay adheres to rules, manages rounds, and tracks scores.
  - Implements methods for drawing cards, using special cards, and declaring "Skru."
- **SpecialCard Interface:**
  - Defines actions for special cards, including swapping, peeking, and transferring cards.
- **Custom Exceptions:**
  - `CannotPlayException`: Handles invalid plays.
  - `CannotSkruException`: Handles invalid "Skru" declarations.

---

## Setup Instructions

1. **Clone the Repository:**
   ```bash
   git clone [repository URL]
   ```
2. **Compile the Code:**
   ```bash
   javac -d bin src/*.java
   ```
3. **Run the Game:**
   ```bash
   java -cp bin Main
   ```

---

## How to Play
1. Start the game and initialize 4 players.
2. Each player takes turns performing one of the allowed actions:
   - Replace, replicate, or use a special card.
3. After three turns, players can declare "Skru."
4. At the end of a round, scores are calculated, and the game progresses to the next round.
5. The game ends after 5 rounds, and the player with the lowest total score wins.

---

## Future Enhancements
- Add a graphical user interface (GUI) for improved interactivity.
- Implement an online multiplayer mode.
- Introduce additional card actions and variations to enhance gameplay.

---

## Contributors
- **Jimmy** (Team Lead)
- **[Add other contributors with their roles]**

---

## License
This project is licensed under [Your License Here].

