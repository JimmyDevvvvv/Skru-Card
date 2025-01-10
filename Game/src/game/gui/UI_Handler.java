package game.gui;

import game.engine.Game;
import game.engine.Player;
import game.exceptions.CannotPlayException;
import game.exceptions.CannotSkruException;
import game.exceptions.CardActionException;
import game.cards.ActionCard;
import game.cards.Card; // Assuming you have a Card class
import game.cards.GiveCard;
import game.cards.MasterEyeCard;
import game.cards.NumberCard;
import game.cards.OthersRevealerCard;
import game.cards.ReplicaCard;
import game.cards.SelfRevealerCard;
import game.cards.SkruCard;
import game.cards.SwapCard;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UI_Handler extends Application {
    private final Color[] playerColors = {Color.BLUE, Color.GREEN, Color.ORANGE, Color.PURPLE};
    private final TextField[] playerNameFields = new TextField[4];
    private final Button startButton = new Button("Start Game");
    private final Button drawFromDeckButton = new Button("Draw from Deck");
    private final Button drawFromDiscardButton = new Button("Draw from Discard Deck");
    private final Button endTurnButton = new Button("End Turn");
    private Stage primaryStage;
    private Scene gameScene;
    private BorderPane mainLayout;
    private final ArrayList<Player> players = new ArrayList<>();
    private Game game; // Game instance
    private int currentPlayerIndex = 0; // Index of the current player
    private Label currentPlayerLabel = new Label("Current Turn: Player 1"); // Initialize with default player
    private Label currentPlayerHandTotalLabel = new Label("Current Player's Hand Total: 0");
    private Player currentPlayer;
    private Card currentCard;
    private Label mainDeckLabel = new Label("Main Deck: ");
    private Label discardDeckLabel = new Label("Discard Deck: ");
    private Button useSpecialButton = new Button("Use Special Card");
    
    private int current_Player_Index;
    private int enemy_Player_Index;
     private ComboBox<Player> enemyPlayerComboBox = new ComboBox<>();
    private Button setEnemyButton = new Button("Set Enemy Player");
    private Player selectedEnemyPlayer;
    private ComboBox<Card> enemyCardComboBox = new ComboBox<>();
    private Button selectEnemyCardButton = new Button("Select an enemy Card");
    private Card SelectedCurrentPlayerCard;
    private Card Enemy_Selected_Card;
    private ComboBox<Card> CurrentPlayerComboBox = new ComboBox<>();
    private Button SelectCurrentPlayerButton = new Button("Select a player Card");
    private Label topDiscardCardLabel = new Label("Top Discard Card: None");
    
    private final Button playCard = new Button("PlayCard"); 

  
    private boolean isSkruDeclared = false; // Track whether "Skru" has been declared
 
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Game Start Menu");

        // Set up the scene and stage
        Scene startMenuScene = new Scene(createStartMenuLayout(), 300, 250);
        primaryStage.setScene(startMenuScene);
        primaryStage.show();
    }

    private GridPane createStartMenuLayout() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Create labels and text fields for player names
        for (int i = 0; i < 4; i++) {
            grid.add(new Label("Player " + (i + 1) + " Name:"), 0, i);
            playerNameFields[i] = new TextField();
            grid.add(playerNameFields[i], 1, i);
        }

        // Configure and add the start button
        startButton.setOnAction(e -> startGame());
        grid.add(startButton, 1, 4);

        return grid;
    }
    

    private void startGame() {
        players.clear(); // Clear any existing players
        boolean allNamesEntered = true;

        // Collect player names
        for (TextField tf : playerNameFields) {
            String name = tf.getText().trim();
            if (name.isEmpty()) {
                allNamesEntered = false;
                break;
            }
            players.add(new Player(name));
        }

        if (allNamesEntered) {
            game = new Game(players);
            currentPlayer = players.get(currentPlayerIndex); // Initialize currentPlayer
            showGameScreen(); // Show game screen with player names and cards
        } else {
            showCustomAlert("Error", "Please enter names for all four players.");
        }
    }
    
    

    private void showGameScreen() {
    	 if (mainLayout == null) {
    	        mainLayout = new BorderPane();

    	        ScrollPane scrollPane = new ScrollPane(mainLayout);
    	        scrollPane.setFitToWidth(true);

    	        VBox playersVBox = new VBox(10);
    	        playersVBox.setAlignment(Pos.CENTER);
    	        playersVBox.setPadding(new javafx.geometry.Insets(10));

    	        int colorIndex = 0;
    	        for (Player player : players) {
    	            VBox playerBox = new VBox();
    	            playerBox.setAlignment(Pos.CENTER);
    	            playerBox.setSpacing(5);
    	            playerBox.setStyle("-fx-border-color: " + toHexString(playerColors[colorIndex]) + "; -fx-padding: 10;");

    	            Label playerNameLabel = new Label("Player: " + player.getName());
    	            playerBox.getChildren().add(playerNameLabel);

    	            // Reveal 2 random cards for the current player
    	            List<Card> handCards = player.getHand();
    	            boolean[] revealed = new boolean[handCards.size()];
    	            
    	            Random random = new Random();
    	            int revealedCardCount = 0;

    	            while (revealedCardCount < 2) {
    	                int indexToReveal = random.nextInt(handCards.size());
    	                if (!revealed[indexToReveal]) {
    	                    revealed[indexToReveal] = true;
    	                    revealedCardCount++;
    	                }
    	            }

    	            // Display each card as a button
    	            for (int cardIndex = 0; cardIndex < handCards.size(); cardIndex++) {
    	                Card card = handCards.get(cardIndex);
    	                boolean isRevealed = revealed[cardIndex];
    	                Button cardButton = createCardButton(card, cardIndex, player, enemy_Player_Index, isRevealed);
    	                playerBox.getChildren().add(cardButton);
    	            }

    	            playersVBox.getChildren().add(playerBox);
    	            colorIndex++;
    	        }


            // Top section with Player turn info
            VBox topVBox = new VBox(10);
            topVBox.setAlignment(Pos.CENTER);
            topVBox.getChildren().addAll(
                currentPlayerLabel,
                currentPlayerHandTotalLabel,
                playersVBox
            );

            // Other components (enemy player selection, current player's card selection...
            HBox enemySelectionHBox = createEnemySelectionHBox();
            topVBox.getChildren().add(enemySelectionHBox);

            // Add the Declare Skru button
            Button declareSkruButton = new Button("Declare Skru");
            declareSkruButton.setOnAction(e -> showSkruDeclarationDialog());
            topVBox.getChildren().add(declareSkruButton);

            HBox currentCardSelectionHBox = createCurrentPlayerCardSelectionHBox();
            topVBox.getChildren().add(currentCardSelectionHBox);

            HBox enemyCardSelectionHBox = createEnemyCardSelectionHBox();
            topVBox.getChildren().add(enemyCardSelectionHBox);

            mainLayout.setTop(topVBox);

            // Center section: Deck Info and Action buttons
            HBox deckActionsHBox = createDeckActionsHBox();
            VBox centerVBox = new VBox(10, deckActionsHBox, createDeckInfoVBox());
            centerVBox.setAlignment(Pos.CENTER);
            mainLayout.setCenter(centerVBox);

            // Bottom section: End Turn, Special Card, and Play Card buttons
            HBox endTurnHBox = createEndTurnHBox();
            endTurnHBox.getChildren().add(playCard); // Add playCard button to the bottom HBox

            // Set up playCard button action
            playCard.setOnAction(event -> HandlePlayCardMethod());

            mainLayout.setBottom(endTurnHBox);

            populateCurrentPlayerCardComboBox();  // Populate the initial card selections
            
            // Reveal two random cards for each player
            
            gameScene = new Scene(scrollPane, 800, 600); // Changed the size to 800x600
            primaryStage.setScene(gameScene);

        } else {
            updateUI(); // Update the UI if the game screen already exists
        }
    }

    
    private void updateUI() {
        // Refresh the UI based on the current game state
        currentPlayerHandTotalLabel.setText("Current Player's Hand Total: " + currentPlayer.getHand().size());
        mainDeckLabel.setText("Main Deck: " + game.getDeck().size() + " cards");
        discardDeckLabel.setText("Discard Deck: " + game.getDiscardDeck().size() + " cards");

        // Update the top card label
        if (!game.getDiscardDeck().isEmpty()) {
            Card topDiscardCard = game.getDiscardDeck().get(game.getDiscardDeck().size() - 1);
            topDiscardCardLabel.setText("Top Discard Card: " + topDiscardCard.toString());
        } else {
            topDiscardCardLabel.setText("Top Discard Card: None");
        }

        // Update player cards display
        VBox playersVBox = (VBox) ((VBox) mainLayout.getTop()).getChildren().get(2);
        playersVBox.getChildren().clear();

        int colorIndex = 0;

        // Assume you already have a mechanism to track revealed cards
        Map<Player, List<Integer>> revealedCardsMap = new HashMap<>(); // Track revealed card indices
        for (Player player : players) {
            revealedCardsMap.put(player, new ArrayList<>()); // Initialize entry for the player

            // For this example, let's assume you implement the logic to reveal cards here
            List<Card> handCards = player.getHand();
            Random random = new Random();
            int revealedCardCount = 0;
            
            while (revealedCardCount < 2 && handCards.size() > revealedCardCount) { // Ensure we do not try to reveal more than we have 
                int indexToReveal = random.nextInt(handCards.size());
                if (!revealedCardsMap.get(player).contains(indexToReveal)) {
                    revealedCardsMap.get(player).add(indexToReveal);
                    revealedCardCount++;
                }
            }
        }

        // Iterate through players and create button representations
        for (Player player : players) {
            Label playerNameLabel = new Label("Player: " + player.getName());
            VBox playerBox = new VBox(playerNameLabel);

            // Retrieve revealed card indices for the current player
            List<Integer> revealedIndices = revealedCardsMap.get(player);

            int cardIndex = 0;
            for (Card card : player.getHand()) {
                boolean isRevealed = revealedIndices.contains(cardIndex);
                Button cardButton = createCardButton(card, cardIndex, player, enemy_Player_Index, isRevealed); // Use the revealed state
                playerBox.getChildren().add(cardButton);
                cardIndex++;
            }

            playerBox.setAlignment(Pos.CENTER);
            playerBox.setStyle("-fx-border-color: " + toHexString(playerColors[colorIndex]) + "; -fx-padding: 10;");
            playersVBox.getChildren().add(playerBox);
            colorIndex++;
        }

        // Update the current player's card ComboBox
        populateCurrentPlayerCardComboBox();
        
        
        
        currentPlayer.setCurrenrTurnTotal(currentPlayer.getTurnCount());
        System.out.println("Current Turn Total: " + currentPlayer.getCurrenrTurnTotal()); // Make sure a getter exists
    }

    
    
    private void showSkruDeclarationDialog() {
        Stage skruDialogStage = new Stage();
        skruDialogStage.initModality(Modality.APPLICATION_MODAL);
        skruDialogStage.setTitle("Declare Skru");

        VBox dialogLayout = new VBox(10);
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.setPadding(new javafx.geometry.Insets(10));

        dialogLayout.getChildren().add(new Label("Select player to declare Skru:"));

        ComboBox<Player> playerComboBox = new ComboBox<>();
        playerComboBox.getItems().addAll(players); // Add all players to the ComboBox
        dialogLayout.getChildren().add(playerComboBox);

        Button declareSkruButton = new Button("Declare Skru");
        declareSkruButton.setOnAction(e -> {
            Player selectedPlayer = playerComboBox.getValue();
            if (selectedPlayer != null) {
                if (selectedPlayer.getTurnCount() % 3 == 0) { // Check if it's the right turn count
                    try {
                        game.declareSkru(); // Attempt to declare Skru
                        skruDialogStage.close();
                        showCustomAlert("Ay haga", "Ay aga");

                        // Update UI to show the current player's hand total
                        updateUIBecauseOfSkru(); // Update for current player's hand total
                        showAllPlayersTotal(); // Display total scores for all players

                         Player winner = game.getWinner();
                        showCustomAlert("Winner!", "The winner is: " + winner.getName()); // Display winner
                        
                    } catch (CannotSkruException cannotSkruException) {
                        showCustomAlert("Error", "You cannot declare Skru before your 3rd turn.");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    showCustomAlert("Error", "You can't declare Skru unless the current round is a multiple of 4.");
                }
            } else {
                showCustomAlert("Error", "Please select a player to declare Skru.");
            }
        });
        dialogLayout.getChildren().add(declareSkruButton);

        Button declineButton = new Button("Decline");
        declineButton.setOnAction(e -> skruDialogStage.close());
        dialogLayout.getChildren().add(declineButton);

        Scene dialogScene = new Scene(dialogLayout, 300, 200);
        skruDialogStage.setScene(dialogScene);
        skruDialogStage.showAndWait();

        updateUI(); // Update UI after dialog is closed
    }

 
    // New method to show total for all players
    private void showAllPlayersTotal() {
        StringBuilder totalsMessage = new StringBuilder("Player Totals:\n");
        for (Player player : players) {
            int total = player.updateTotal(); // Assuming updateTotal() calculates the score
            totalsMessage.append(player.getName()).append(": ").append(total).append("\n"); // Get player's name for display
        }
        showCustomAlert("Players' Total Scores", totalsMessage.toString()); // Show totals in an alert
    }


    // New method to update the hand total specifically after declaring skru
    private void updateUIBecauseOfSkru() {
        int currentTotal = currentPlayer.updateTotal(); // Calculate the total score of the current player's hand
        currentPlayerHandTotalLabel.setText("Current Player's Hand Total: " + currentTotal); // Update the label with the total
    }

    


    private HBox createEnemySelectionHBox() {
        HBox enemySelectionHBox = new HBox(10);
        enemySelectionHBox.setAlignment(Pos.CENTER);
        enemyPlayerComboBox.getItems().addAll(players);
        enemyPlayerComboBox.setPromptText("Select Enemy Player");
        setEnemyButton.setOnAction(e -> setEnemyPlayer());
        enemySelectionHBox.getChildren().addAll(enemyPlayerComboBox, setEnemyButton);
        return enemySelectionHBox;
    }

    private HBox createCurrentPlayerCardSelectionHBox() {
        HBox currentPlayerCardSelectionHBox = new HBox(10);
        currentPlayerCardSelectionHBox.setAlignment(Pos.CENTER);
        CurrentPlayerComboBox.setPromptText("Select one of your cards");
        SelectCurrentPlayerButton.setOnAction(e -> selectCurrentPlayerCardFromCurrentPlayerHand());
        currentPlayerCardSelectionHBox.getChildren().addAll(CurrentPlayerComboBox, SelectCurrentPlayerButton);
        return currentPlayerCardSelectionHBox;
    }

    private HBox createEnemyCardSelectionHBox() {
        HBox enemyCardSelectionHBox = new HBox(10);
        enemyCardSelectionHBox.setAlignment(Pos.CENTER);
        enemyCardComboBox.setPromptText("Select an enemy card");
        selectEnemyCardButton.setOnAction(e -> selectCardFromEnemyHand());
        enemyCardSelectionHBox.getChildren().addAll(enemyCardComboBox, selectEnemyCardButton);
        return enemyCardSelectionHBox;
    }

    private HBox createDeckActionsHBox() {
        HBox deckActionsHBox = new HBox(10);
        deckActionsHBox.setAlignment(Pos.CENTER);
        drawFromDeckButton.setOnAction(e -> drawFromDeck());
        drawFromDiscardButton.setOnAction(e -> drawFromDiscardDeck());
        deckActionsHBox.getChildren().addAll(drawFromDeckButton, drawFromDiscardButton);
        return deckActionsHBox;
    }

    private VBox createDeckInfoVBox() {
        VBox deckInfoVBox = new VBox(5);
        deckInfoVBox.setAlignment(Pos.CENTER);
        mainDeckLabel.setText("Main Deck: " + game.getDeck().size() + " cards");
        discardDeckLabel.setText("Discard Deck: " + game.getDiscardDeck().size() + " cards");
        deckInfoVBox.getChildren().addAll(mainDeckLabel, discardDeckLabel);
        return deckInfoVBox;
    }

    private HBox createEndTurnHBox() {
        HBox endTurnHBox = new HBox(10);
        endTurnHBox.setAlignment(Pos.CENTER);
        endTurnButton.setOnAction(e -> endTurn());
        useSpecialButton.setOnAction(e -> useSpecialCard());
        endTurnHBox.getChildren().addAll(endTurnButton, useSpecialButton);
        return endTurnHBox;
    }
    private void setEnemyPlayer() {
        selectedEnemyPlayer = enemyPlayerComboBox.getValue();
        if (selectedEnemyPlayer != null) {
            enemy_Player_Index = players.indexOf(selectedEnemyPlayer); // Set enemy player index
            System.out.println("Selected enemy player: " + selectedEnemyPlayer.getName());
            populateEnemyCardComboBox(); // Populate the ComboBox with enemy player's cards
        } else {
            showCustomAlert("Error", "Please select an enemy player.");
        }
    }

 
    

    // Method to handle the "Use Special Card" button
    private void useSpecialCard() {
        if (currentPlayer != null && currentCard != null) {
            if (isSpecialCard(currentCard)) {
                // Implement logic to use the special card
                handleSpecialCard(currentCard);
                System.out.println(currentPlayer.getName() + " used a special card: " + currentCard);
                updateUI();
            } else {
                showCustomAlert("Error", "Selected card is not a special card.");
            }
        } else {
            showCustomAlert("Error", "No current player or card to use.");
        }
    }
    private boolean isSpecialCard(Card card) {
        return card instanceof MasterEyeCard || card instanceof SwapCard || card instanceof GiveCard ||
               card instanceof ActionCard || card instanceof OthersRevealerCard || card instanceof ReplicaCard ||
               card instanceof SelfRevealerCard || card instanceof SkruCard || card instanceof NumberCard;
    }

    private void handleSpecialCard(Card card) {
        System.out.println("Selected card: " + card);  // Only need to print this once
        if (card instanceof MasterEyeCard) {
            handlePeekCard((MasterEyeCard) card);
        } else if (card instanceof SwapCard) {
            handleSwapCard((SwapCard) card);
            
          
        } else if (card instanceof GiveCard) {
            handleGiveCard((GiveCard) card);
        } else if (card instanceof ActionCard) {
            handleActionCard((ActionCard) card);
            updateUI();
            
        } else if (card instanceof OthersRevealerCard) {
            handleOthersRevealerCard((OthersRevealerCard) card);
        } else if (card instanceof ReplicaCard) {
            handleReplicaCard((ReplicaCard) card);
            updateUI();

        } else if (card instanceof SelfRevealerCard) {
            handleSelfRevealerCard((SelfRevealerCard) card);
        } else if (card instanceof SkruCard) {
            handleSkruCard((SkruCard) card);
        } else if (card instanceof NumberCard) {
            handleNumberCard((NumberCard) card);
        } else {
            showCustomAlert("Error", "Unknown special card type.");
        }
    }


    private void handleNumberCard(NumberCard card) {
    	
    
         System.out.println("Number card action executed.");
    }
    private void handlePeekCard(MasterEyeCard card) {
    	 Random random = new Random();
    	    
    	    // Assume each player has a hand and the hand is a list of cards.
    	    for (int i = 0; i < players.size(); i++) {
    	        int handSize = players.get(i).getHand().size();
    	        
    	        // Get a random index for the current player's hand
    	        if (handSize > 0) {  // Ensure the player has at least one card
    	            int randomIndex = random.nextInt(handSize);
    	            
    	            // Fetch the card content
    	            String cardContent = players.get(i).getHand().get(randomIndex).toString(); // Assuming there is a getContent() method
            
    	            showCustomAlert("Card Information", cardContent);
    	        } else {
    	            System.out.println("Player " + (i + 1) + " has no cards.");
    	        }
    	    }
    	    
     	    int i1 = random.nextInt(players.get(0).getHand().size());
    	    int i2 = random.nextInt(players.get(1).getHand().size());
    	    int i3 = random.nextInt(players.get(2).getHand().size());
    	    int i4 = random.nextInt(players.get(3).getHand().size());

    	    game.useSpecialCard(i1, i2, i3, i4);
    

    }


    private void handleSwapCard(SwapCard card) {
        // Check if players are selected
        if (currentPlayer == null) {
            System.out.println("Current player is not set.");
            return;
        }

        if (selectedEnemyPlayer == null) {
            System.out.println("Selected enemy player is not set.");
            return;
        }

        // Check if card indices are within bounds
        if (current_Player_Index < 0 || current_Player_Index >= currentPlayer.getHand().size()) {
            System.out.println("Invalid index for current player's hand.");
            return;
        }

        if (enemy_Player_Index < 0 || enemy_Player_Index >= selectedEnemyPlayer.getHand().size()) {
            System.out.println("Invalid index for enemy player's hand.");
            return;
        }

        // Call the method to perform the card swap
        game.useSpecialCard(selectedEnemyPlayer, current_Player_Index, enemy_Player_Index);

        // Update UI or perform other actions
        System.out.println("Swap card action executed.");
        updateUI();
    }



    private void handleGiveCard(GiveCard card) {
    	
    	game.useSpecialCard(selectedEnemyPlayer,SelectedCurrentPlayerCard);
         System.out.println("Give card action executed.");
         updateUI();
    }

    private void handleActionCard(ActionCard card) {
       game.replicate(SelectedCurrentPlayerCard);
        System.out.println("Action card action executed.");
        updateUI();
    }

    private void handleOthersRevealerCard(OthersRevealerCard card) {
 
    Player p = selectedEnemyPlayer;	
    	
    if(p.equals(selectedEnemyPlayer)){	
    try {
		game.performActionCard(selectedEnemyPlayer, Enemy_Selected_Card, enemy_Player_Index);
	    String Message  = Enemy_Selected_Card.toString();
	    showCustomAlert("Card Information", Message);
	} catch (CardActionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
    }
    else{
    	System.out.println("you cant view your own cards");
    }
    updateUI();
    
    }

    private void handleReplicaCard(ReplicaCard card) {
    	if(SelectedCurrentPlayerCard!= null){
    	game.replicate(SelectedCurrentPlayerCard);
           System.out.println("Replica card action executed.");
    	}
    	else{
    		System.out.println("its null");
    	}
    	
          updateUI();
    }

    private void handleSelfRevealerCard(SelfRevealerCard card) {
        try {
            // Perform the action associated with the card
            game.performActionCard(currentPlayer, SelectedCurrentPlayerCard, current_Player_Index);
            
            
            
            // Create the message to display in the alert
           String message = SelectedCurrentPlayerCard.toString();
            
            // Show the custom alert with the card information
            showCustomAlert("Card Information", message);

        } catch (CardActionException e) {
            e.printStackTrace();
        }

        System.out.println("Peek card action executed.");
        updateUI();
    }

    private void handleSkruCard(SkruCard card) {
        // Implement skru logic
        System.out.println("Skru card action executed.");
    }
    private void drawFromDeck() {
        try {
            if (currentPlayer != null) {
                game.DrawCardFromDeck(); // Ensure this method is correct
                System.out.println(currentPlayer.getName() + " drew a card from the deck.");
                updateUI();
            } else {
                showCustomAlert("Error", "No current player to draw for.");
            }
        } catch (CannotPlayException e) {
            showCustomAlert("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawFromDiscardDeck() {
        try {
            if (currentPlayer != null) {
                game.DrawCardFromDiscardDeck(); // Ensure this method is correct
                System.out.println(currentPlayer.getName() + " drew a card from the discard deck.");
                updateUI();
            } else {
                showCustomAlert("Error", "No current player to draw for.");
            }
        } catch (CannotPlayException e) {
            showCustomAlert("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void endTurn() {
        try {
            // Proceed with normal end turn logic
            game.endTurn();
            
             
                switchPlayerTurn();
                System.out.println("Turn ended for " + currentPlayer.getName() + ".");
                updateUI();
          
        } catch (CannotPlayException e) {
            showCustomAlert("Error", e.getMessage());
            e.printStackTrace();
        }
        currentPlayer.setCurrenrTurnTotal(currentPlayer.getTurnCount() +1);
        
        
        updateUI();
    }

    
    public void endRound() {
             if (isSkruDeclared) {
                // Show Skru declaration dialog
                showSkruDeclarationDialog(); // Call the function here to display the dialog
             }
         updateUI();
    }


 
    
    private void switchPlayerTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
        currentPlayerLabel.setText("Current Turn: " + currentPlayer.getName());
        populateCurrentPlayerCardComboBox(); // Update the ComboBox with the new current player's cards
    }

 
    
    
    
    
    
    private void populateEnemyCardComboBox() {
        if (selectedEnemyPlayer != null) {
            enemyCardComboBox.getItems().clear();
            enemyCardComboBox.getItems().addAll(selectedEnemyPlayer.getHand());
            enemyCardComboBox.setPromptText("Select a card");
        }
    }
    
    
    
    
    
    private void populateCurrentPlayerCardComboBox() {
        CurrentPlayerComboBox.getItems().clear(); // Clear previous items
        if (currentPlayer != null) {
            CurrentPlayerComboBox.getItems().addAll(currentPlayer.getHand());
        }
    }

    private void selectCardFromEnemyHand() {
        Card selectedCard = enemyCardComboBox.getValue();
        if (selectedCard != null) {
            // Assuming you have a list or array of enemy cards used to populate the ComboBox
            List<Card> enemyCards = selectedEnemyPlayer.getHand(); // Replace with your method to get enemy cards
            int index = enemyCards.indexOf(selectedCard);

            if (index != -1) {
                enemy_Player_Index = index; // Store the index of the selected card
                Enemy_Selected_Card = selectedCard;
                System.out.println("Selected card index: " + enemy_Player_Index); // Print the index for debugging
            } else {
                showCustomAlert("Error", "The selected card is not found in the list.");
            }
        } else {
            showCustomAlert("Error", "Please select a card from the enemy's hand.");
        }
    }
    
    
    private void selectCurrentPlayerCardFromCurrentPlayerHand() {
        Card selectedCard = CurrentPlayerComboBox.getValue();
        if (selectedCard != null) {
            int index = currentPlayer.getHand().indexOf(selectedCard);
            if (index != -1) {
                current_Player_Index = index; // Store the index of the selected card
                SelectedCurrentPlayerCard = selectedCard;
                
                System.out.println("Selected current player card index: " + current_Player_Index); // Print the index for debugging
            } else {
                showCustomAlert("Error", "The selected card is not found in your hand.");
            }
        } else {
            showCustomAlert("Error", "Please select a card from your hand.");
        }
    }


    private void HandlePlayCardMethod() {
        try {
            // Attempt to play the selected card
            game.PlayCard(SelectedCurrentPlayerCard);
            System.out.println("Play Card button clicked!");
        } catch (CannotPlayException e) {
            // Print stack trace for debugging
            e.printStackTrace();

            // Display a user-friendly error message
            showCustomAlert("Error", "Unable to play the selected card. " +
                "Please ensure that the card is valid for play and try again.");
        }
        updateUI();
    }




    private Button createCardButton(Card card, int cardIndex, Player player, int enemyIndex, boolean isRevealed) {
         String buttonLabel = isRevealed ? card.toString() : "?";
        Button button = new Button(buttonLabel);
        
        // Set action for the button
        button.setOnAction(e -> {
            if (isRevealed && isSpecialCard(card)) {
                handleSpecialCard(card);
            } else if (!isRevealed) {
                showCustomAlert("Error", "Please reveal the card first.");
            } else {
                showCustomAlert("Error", "Selected card is not a special card or cannot be played.");
            }
        });
        return button;
    }

    
    
   
    
    
    
    private void showCustomAlert(String title, String message) {
        Stage alertStage = new Stage();
        alertStage.initModality(Modality.APPLICATION_MODAL);
        alertStage.setTitle(title);

        VBox alertVBox = new VBox(10);
        alertVBox.setAlignment(Pos.CENTER);
        alertVBox.getChildren().add(new Label(message));

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> alertStage.close());
        alertVBox.getChildren().add(closeButton);

        Scene alertScene = new Scene(alertVBox, 200, 100);
        alertStage.setScene(alertScene);
        alertStage.showAndWait();
    }

    private String toHexString(Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
