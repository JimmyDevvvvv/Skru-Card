package game.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import game.engine.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class M2PublicTest {
	// ///////////////////////Helper methods//////////////////////////////////

	private void testInstanceVariableIsFinal(Class aClass, String varName) {
		boolean thrown = false;
		Field f = null;
		try {
			f = aClass.getDeclaredField(varName);
		} catch (NoSuchFieldException e) {
			thrown = true;
		}
		if (!thrown) {
			boolean isFinal = Modifier.isFinal(f.getModifiers());
			assertTrue(varName + " should be final", isFinal);
		} else
			assertTrue("you should have" + varName + " as a final variable",
					false);
	}

	private void testIsEnum(Class eClass) {
		assertTrue(eClass.getSimpleName() + " should be an Enum",
				eClass.isEnum());
	}

	private void testEnumValues(String path, String name, String[] values) {
		try {
			Class eClass = Class.forName(path);
			for (int i = 0; i < values.length; i++) {
				try {
					Enum.valueOf((Class<Enum>) eClass, values[i]);
				} catch (IllegalArgumentException e) {
					fail(eClass.getSimpleName() + " enum can be " + values[i]);
				}
			}
		} catch (ClassNotFoundException e) {
			fail("There should be an enum called " + name + "in package "
					+ path);
		}

	}

	@SuppressWarnings("rawtypes")
	private void testClassIsSubClass(Class subClass, Class superClass) {
		assertEquals(subClass.getName() + " class should inherit from "
				+ superClass.getName() + ".", superClass,
				subClass.getSuperclass());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void testGetterMethodExistsInClass(Class aClass, String methodName,
			Class returnedType) {
		Method m = null;
		boolean found = true;
		try {
			m = aClass.getDeclaredMethod(methodName);
		} catch (NoSuchMethodException e) {
			found = false;
		}
		String varName = methodName.substring(3).toLowerCase();
		assertTrue(
				"The " + varName + " instance variable in class "
						+ aClass.getName() + " is a READ variable.", found);
		assertTrue("incorrect return type for " + methodName + " method in "
				+ aClass.getName() + " class.", m.getReturnType()
				.isAssignableFrom(returnedType));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void testSetterMethodExistsInClass(Class aClass, String methodName,
			Class inputType, boolean writeVariable) {

		Method[] methods = aClass.getDeclaredMethods();
		String varName = methodName.substring(3).toLowerCase();
		if (writeVariable) {
			assertTrue("The " + varName + " instance variable in class "
					+ aClass.getName() + " is a WRITE variable.",
					containsMethodName(methods, methodName));
		} else {
			assertFalse("The " + varName + " instance variable in class "
					+ aClass.getName() + " is a READ ONLY variable.",
					containsMethodName(methods, methodName));
			return;
		}
		Method m = null;
		boolean found = true;
		try {
			m = aClass.getDeclaredMethod(methodName, inputType);
		} catch (NoSuchMethodException e) {
			found = false;
		}

		assertTrue(aClass.getName() + " class should have " + methodName
				+ " method that takes one " + inputType.getSimpleName()
				+ " parameter", found);

		assertTrue("incorrect return type for " + methodName + " method in "
				+ aClass.getName() + ".", m.getReturnType().equals(Void.TYPE));

	}

	private void testInstanceVariableIsStatic(Class aClass, String varName) {
		boolean thrown = false;
		Field f = null;
		try {
			f = aClass.getDeclaredField(varName);
		} catch (NoSuchFieldException e) {
			thrown = true;
		}
		if (!thrown) {
			boolean isStatic = Modifier.isStatic(f.getModifiers());
			assertTrue(varName + " should be static", isStatic);
		} else
			assertTrue("you should have" + varName + " as a static variable",
					false);
	}

	private void testClassIsAbstract(Class aClass) {
		assertTrue(aClass.getSimpleName() + " should be an abtract class.",
				Modifier.isAbstract(aClass.getModifiers()));
	}

	private void testClassImplementsInterface(Class aClass, Class iClass) {
		assertTrue(
				aClass.getSimpleName() + " should implement "
						+ iClass.getSimpleName(),
				iClass.isAssignableFrom(aClass));
	}

	private void testIsInterface(Class iClass) {
		assertTrue(iClass.getSimpleName() + " should be interface",
				iClass.isInterface());
	};

	private void testConstructorExists(Class aClass, Class[] inputs) {
		boolean thrown = false;
		try {
			aClass.getConstructor(inputs);
		} catch (NoSuchMethodException e) {
			thrown = true;
		}

		if (inputs.length > 0) {
			String msg = "";
			int i = 0;
			do {
				msg += inputs[i].getSimpleName() + " and ";
				i++;
			} while (i < inputs.length);

			msg = msg.substring(0, msg.length() - 4);

			assertFalse(
					"Missing constructor with " + msg + " parameter"
							+ (inputs.length > 1 ? "s" : "") + " in "
							+ aClass.getSimpleName() + " class.",

					thrown);
		} else
			assertFalse(
					"Missing constructor with zero parameters in "
							+ aClass.getSimpleName() + " class.",

					thrown);

	}

	private void testConstructorInitialization(Object createdObject,
			String[] names, Object[] values) throws NoSuchMethodException,
			SecurityException, IllegalArgumentException, IllegalAccessException {

		for (int i = 0; i < names.length; i++) {

			Field f = null;
			Class curr = createdObject.getClass();
			String currName = names[i];
			Object currValue = values[i];

			while (f == null) {

				if (curr == Object.class)
					fail("Class " + createdObject.getClass().getSimpleName()
							+ " should have the instance variable \""
							+ currName + "\".");
				try {
					f = curr.getDeclaredField(currName);
				} catch (NoSuchFieldException e) {
					curr = curr.getSuperclass();
				}
			}
			f.setAccessible(true);

			assertEquals("The constructor of the "
					+ createdObject.getClass().getSimpleName()
					+ " class should initialize the instance variable \""
					+ currName + "\" correctly.", currValue,
					f.get(createdObject));

		}

	}

	@SuppressWarnings("rawtypes")
	private void testInstanceVariablesArePresent(Class aClass, String varName,
			boolean implementedVar) throws SecurityException {

		boolean thrown = false;
		try {
			aClass.getDeclaredField(varName);
		} catch (NoSuchFieldException e) {
			thrown = true;
		}
		if (implementedVar) {
			assertFalse("There should be " + varName
					+ " instance variable in class " + aClass.getName(), thrown);
		} else {
			assertTrue("There should not be " + varName
					+ " instance variable in class " + aClass.getName()
					+ ", it should be inherited from the super class", thrown);
		}
	}

	@SuppressWarnings("rawtypes")
	private void testInstanceVariablesArePrivate(Class aClass, String varName)
			throws NoSuchFieldException, SecurityException {
		Field f = aClass.getDeclaredField(varName);
		assertEquals(
				varName + " instance variable in class " + aClass.getName()
						+ " should not be accessed outside that class", 2,
				f.getModifiers());
	}

	private void testInstanceVariableIsPrivate(Class aClass, String varName)
			throws NoSuchFieldException, SecurityException {
		boolean thrown = false;
		Field f = null;
		try {
			f = aClass.getDeclaredField(varName);
		} catch (NoSuchFieldException e) {
			thrown = true;
		}
		if (!thrown) {
			boolean isPrivate = (Modifier.isPrivate(f.getModifiers()));
			assertTrue("The \"" + varName + "\" instance variable in class "
					+ aClass.getSimpleName()
					+ " should not be accessed outside that class.", isPrivate);

		} else {
			assertFalse("There should be \"" + varName
					+ "\" instance variable in class " + aClass.getSimpleName()
					+ ".", thrown);
		}

	}

	private static boolean containsMethodName(Method[] methods, String name) {
		for (Method method : methods) {
			if (method.getName().equals(name))
				return true;
		}
		return false;
	}

	private void testInterfaceMethod(Class iClass, String methodName,
			Class returnType, Class[] parameters) {
		Method[] methods = iClass.getDeclaredMethods();
		assertTrue(iClass.getSimpleName() + " interface should have "
				+ methodName + "method",
				containsMethodName(methods, methodName));

		Method m = null;
		boolean thrown = false;
		try {
			m = iClass.getDeclaredMethod(methodName, parameters);
		} catch (NoSuchMethodException e) {
			thrown = true;
		}

		assertTrue(
				"Method " + methodName
						+ " should have the following set of parameters : "
						+ Arrays.toString(parameters), !thrown);
		assertTrue("wrong return type", m.getReturnType().equals(returnType));

	}

	private void testMethodAbsence(Class aClass, String methodName) {
		Method[] methods = aClass.getDeclaredMethods();
		assertFalse(aClass.getSimpleName() + " class should not override "
				+ methodName + " method",
				containsMethodName(methods, methodName));
	}

	private void testInterfaceMethodIsDefault(Class iClass, String methodName,
			Class[] parameters) {
		Method m = null;
		boolean thrown = false;
		try {
			m = iClass.getDeclaredMethod(methodName, parameters);
		} catch (NoSuchMethodException e) {
			thrown = true;
		}
		assertTrue("Method " + methodName + " with the following parameters :"
				+ Arrays.toString(parameters) + " should exist in interface"
				+ iClass.getName(), !thrown);
		assertTrue("Method " + methodName + " should be a default method",
				m.isDefault());
	}

	private void testMethodExistence(Class aClass, String methodName,
			Class returnType, Class[] parameters) {
		Method[] methods = aClass.getDeclaredMethods();
		assertTrue(aClass.getSimpleName() + " class should have " + methodName
				+ "method", containsMethodName(methods, methodName));

		Method m = null;
		boolean thrown = false;
		try {
			m = aClass.getDeclaredMethod(methodName, parameters);
		} catch (NoSuchMethodException e) {
			thrown = true;
		}

		assertTrue(
				"Method " + methodName
						+ " should have the following set of parameters : "
						+ Arrays.toString(parameters), !thrown);
		assertTrue("wrong return type the method (" + methodName
				+ ") should return type : " + returnType, m.getReturnType()
				.equals(returnType));
	}

	// ///////////////all pathes

	String specialCarInterfacePath = "game.interfaces.SpecialCard";
	String skruTypePath = "game.enums.SkruType";
	String cardPath = "game.cards.Card";
	String skruCardPath = "game.cards.SkruCard";
	String ActionCardPath = "game.cards.ActionCard";
	String giveCardPath = "game.cards.GiveCard";
	String replicaCardPath = "game.cards.ReplicaCard";
	String masterEyeCardPath = "game.cards.MasterEyeCard";
	String swapCardPath = "game.cards.SwapCard";
	String numberCardPath = "game.cards.NumberCard";
	String selfRevealerCardPath = "game.cards.SelfRevealerCard";
	String othersRevealerCardPath = "game.cards.OthersRevealerCard";
	String cannotRevealExceptionPath = "game.exceptions.CannotRevealException";
	String cannotSkruExceptionPath = "game.exceptions.CannotSkruException";
	String cardActionExceptionPath = "game.exceptions.CardActionException";
	String cannotPlayExceptionPath = "game.exceptions.CannotPlayException";
	String playerPath = "game.engine.Player";
	String gamePath = "game.engine.Game";

	// ////////////////////////////test exception////////////////
	@Test(timeout = 1000)
	public void testCannotPlayExceptionIsSubclassOfExceptionClass()
			throws ClassNotFoundException {
		testClassIsSubClass(Class.forName(cannotPlayExceptionPath),
				Class.forName("java.lang.Exception"));
	}

	@Test(timeout = 1000)
	public void testConstructorCannotPlayExceptionEmpty()
			throws ClassNotFoundException {
		Class[] inputs = {};
		testConstructorExists(Class.forName(cannotPlayExceptionPath), inputs);
	}

	@Test(timeout = 1000)
	public void testConstructorCannotPlaylExceptionWithString()
			throws ClassNotFoundException {
		Class[] inputs = { String.class };
		testConstructorExists(Class.forName(cannotPlayExceptionPath), inputs);
	}

	// //////////// player updates ////////////

	@Test(timeout = 1000)
	public void testPlayerInstanceVariables() throws NoSuchFieldException,
			SecurityException, ClassNotFoundException {

		testInstanceVariablesArePresent(Class.forName(playerPath), "hasPlayed",
				true);
		testInstanceVariablesArePresent(Class.forName(playerPath), "hasDrawn",
				true);
		testInstanceVariablesArePresent(Class.forName(playerPath),
				"currenrTurnTotal", true);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableTurnCountInPlayerGetter() throws Exception {
		testGetterMethodExistsInClass(Class.forName(playerPath),
				"getTurnCount", int.class);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableTurnCountInPlayerSetter() throws Exception {
		testSetterMethodExistsInClass(Class.forName(playerPath),
				"setTurnCount", int.class, true);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableHasPlayedInPlayerGetter() throws Exception {
		testGetterMethodExistsInClass(Class.forName(playerPath), "isHasPlayed",
				boolean.class);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableHasPlayedInPlayerSetter() throws Exception {
		testSetterMethodExistsInClass(Class.forName(playerPath),
				"setHasPlayed", boolean.class, true);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableHasDrawnInPlayerGetter() throws Exception {
		testGetterMethodExistsInClass(Class.forName(playerPath), "isHasDrawn",
				boolean.class);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableHasDrawnInPlayerSetter() throws Exception {
		testSetterMethodExistsInClass(Class.forName(playerPath), "setHasDrawn",
				boolean.class, true);
	}

	@Test(timeout = 1000)
	public void testPlayerInstanceVariableHasDrawnIsPrivate()
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		testInstanceVariableIsPrivate(Class.forName(playerPath), "hasDrawn");
	}

	@Test(timeout = 1000)
	public void testPlayerInstanceVariableHasPlayedIsPrivate()
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		testInstanceVariableIsPrivate(Class.forName(playerPath), "hasPlayed");
	}

	@Test(timeout = 1000)
	public void testPlayerInstanceVariableTurnCountIsPrivate()
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		testInstanceVariableIsPrivate(Class.forName(playerPath), "turnCount");
	}

	// // game class updates////

	@Test(timeout = 1000)
	public void testGamedInstanceVariables() throws NoSuchFieldException,
			SecurityException, ClassNotFoundException {

		testInstanceVariablesArePresent(Class.forName(gamePath),
				"declaredSkru", true);
	}

	@Test(timeout = 1000)
	public void testGameInstanceVariableDeclaredSkruIsPrivate()
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		testInstanceVariableIsPrivate(Class.forName(gamePath), "declaredSkru");
	}

	public void testInstanceVariableDeclaredSkruInGameGetter() throws Exception {
		testGetterMethodExistsInClass(Class.forName(gamePath),
				"getDeclaredSkru", Class.forName(playerPath));
	}

	@Test(timeout = 1000)
	public void testInstanceVariableDeclaredSkruInGameSetter() throws Exception {
		testSetterMethodExistsInClass(Class.forName(gamePath),
				"setDeclaredSkru", Class.forName(playerPath), true);
	}

	// ////////////////////test Methods //////////

	// ////////test interface special card/////////////
	@Test(timeout = 1000)
	public void testSpecialCardIsAnInterface() throws ClassNotFoundException {
		testIsInterface(Class.forName(specialCarInterfacePath));
	}

	@Test(timeout = 1000)
	public void testPerformSpecialActionInSpecialCardInterface()
			throws ClassNotFoundException {
		Class[] prmtrs = { Class.forName(playerPath), int.class };
		testInterfaceMethod(Class.forName(specialCarInterfacePath),
				"performSpecialAction", void.class, prmtrs);
	}

	@Test(timeout = 1000)
	public void testSelfRevealerClassImplementsSpecialCardInterface() {
		try {
			testClassImplementsInterface(Class.forName(selfRevealerCardPath),
					Class.forName(specialCarInterfacePath));
		} catch (ClassNotFoundException e) {
			assertTrue(e.getClass().getName() + " occurred: " + e.getMessage(),
					false);
		}
	}

	@Test(timeout = 1000)
	public void testOthersRevealerClassImplementsSpecialCardInterface() {
		try {
			testClassImplementsInterface(Class.forName(othersRevealerCardPath),
					Class.forName(specialCarInterfacePath));
		} catch (ClassNotFoundException e) {
			assertTrue(e.getClass().getName() + " occurred: " + e.getMessage(),
					false);
		}
	}

	@Test(timeout = 1000)
	public void testPerformSpecialActionIsDefault()
			throws ClassNotFoundException {
		Class[] prmtrs = { Class.forName(playerPath), int.class };

		try {
			testInterfaceMethodIsDefault(
					Class.forName(specialCarInterfacePath),
					"performSpecialAction", prmtrs);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}
	}

	@Test(timeout = 1000)
	public void testPerformSpecialActionInSpecialCardInterfaceLogic()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Object player;
		ArrayList<Object> hand;
		Object card;

		// Use reflection to create a Card instance
		Class<?> cardClass = Class.forName(replicaCardPath);
		Constructor<?> cardConstructor = cardClass.getDeclaredConstructor();
		card = cardConstructor.newInstance();

		// Use reflection to set the visible attribute to false
		Method setVisibleMethod = cardClass.getMethod("setVisible",
				boolean.class);
		setVisibleMethod.invoke(card, false);

		// Create the player's hand and add the card to it
		hand = new ArrayList<>();
		hand.add(card);

		// Use reflection to create a Player instance
		Class<?> playerClass = Class.forName(playerPath);
		Constructor<?> playerConstructor = playerClass
				.getDeclaredConstructor(String.class);
		player = playerConstructor.newInstance("P1");

		// Use reflection to access the hand field of the player
		Field handField = playerClass.getDeclaredField("hand");
		handField.setAccessible(true);

		// Set the hand field to the ArrayList containing the card
		handField.set(player, hand);

		// Use reflection to get the performSpecialAction method

		Class<?> selfRevealerCardClass = Class.forName(selfRevealerCardPath);
		Constructor<?> selfRevealerCardConstructor = selfRevealerCardClass
				.getDeclaredConstructor(int.class);
		Object selfRevealerCard = selfRevealerCardConstructor.newInstance(7);

		// Use reflection to get the performSpecialAction method
		Method performSpecialActionMethod = selfRevealerCardClass.getMethod(
				"performSpecialAction", Class.forName(playerPath), int.class);

		// Invoke the performSpecialAction method using reflection
		performSpecialActionMethod.invoke(selfRevealerCard, player, 0);

		// Use reflection to get the isVisible method of Card
		Method isVisibleMethod = card.getClass().getMethod("isVisible");
		assertEquals(
				"The perFormSpecilalAction method should make the card in the given index in the given player's hand visible",
				true, (boolean) isVisibleMethod.invoke(hand.get(0)));

	}

	// ////// testing player class methods////

	@Test(timeout = 1000)
	public void testUpdateTotalMethodExisitInClassPlayer() {
		try {
			testMethodExistence(Class.forName(playerPath), "updateTotal",
					int.class, null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}

	}

	@Test(timeout = 1000)
	public void testUpdateTotalMethodInClassPlayerNumberCardsLogic()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Object player;
		ArrayList<Object> hand;
		Object card1;
		Object card2;
		Object card3;
		Object card4;

		// Use reflection to create a Card instance\
		int n1 = (int) (Math.random() * 9) + 1;
		int n2 = (int) (Math.random() * 9) + 1;
		int n3 = (int) (Math.random() * 9) + 1;
		int n4 = (int) (Math.random() * 9) + 1;
		Class<?> NumberCardClass = Class.forName(numberCardPath);

		Constructor<?> numberCardConstructor = NumberCardClass
				.getDeclaredConstructor(int.class);
		card1 = numberCardConstructor.newInstance(n1);
		card2 = numberCardConstructor.newInstance(n2);
		card3 = numberCardConstructor.newInstance(n3);
		card4 = numberCardConstructor.newInstance(n4);

		// Create the player's hand and add the card to it
		hand = new ArrayList<>();
		hand.add(card1);
		hand.add(card2);
		hand.add(card3);
		hand.add(card4);

		// Use reflection to create a Player instance
		Class<?> playerClass = Class.forName(playerPath);
		Constructor<?> playerConstructor = playerClass
				.getDeclaredConstructor(String.class);
		player = playerConstructor.newInstance("P1");

		// Use reflection to access the hand field of the player
		Field handField = playerClass.getDeclaredField("hand");
		handField.setAccessible(true);

		// Set the hand field to the ArrayList containing the card
		handField.set(player, hand);

		Method updateTotalMethod = playerClass.getMethod("updateTotal");

		int expectedTotal = n1 + n2 + n3 + n4;

		int actulaTotal = (int) updateTotalMethod.invoke(player);

		assertEquals(
				"the (updateTotal) method should return the sum of card values as follows : \n–numberCards: adds the value of the number in the card.\n– ActionCard : adds 10 points.\n– SkruCard : adds the value of the skru card.\n Wrong number card logic!! \n ",
				expectedTotal, actulaTotal);
	}

	@Test(timeout = 1000)
	public void testUpdateTotalMethodInClassPlayerActionCardLogic()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Object player;
		ArrayList<Object> hand;
		Object card1;
		Object card2;

		// Use reflection to create a Card instance\

		Class<?> ActionCardClass = Class.forName(ActionCardPath);

		Constructor<?> ActionCardConstructor = ActionCardClass
				.getDeclaredConstructor();

		card1 = ActionCardConstructor.newInstance();
		card2 = ActionCardConstructor.newInstance();

		// Create the player's hand and add the card to it
		hand = new ArrayList<>();
		hand.add(card1);
		hand.add(card2);

		// Use reflection to create a Player instance
		Class<?> playerClass = Class.forName(playerPath);
		Constructor<?> playerConstructor = playerClass
				.getDeclaredConstructor(String.class);
		player = playerConstructor.newInstance("P1");

		// Use reflection to access the hand field of the player
		Field handField = playerClass.getDeclaredField("hand");
		handField.setAccessible(true);

		// Set the hand field to the ArrayList containing the card
		handField.set(player, hand);

		Method updateTotalMethod = playerClass.getMethod("updateTotal");

		int expectedTotal = 20;

		int actulaTotal = (int) updateTotalMethod.invoke(player);

		assertEquals(
				"the (updateTotal) method should return the sum of card values as follows : \n–numberCards: adds the value of the number in the card.\n– ActionCard : adds 10 points.\n– SkruCard : adds the value of the skru card.\n Wrong Action card logic!! \n",
				expectedTotal, actulaTotal);
	}

	@Test(timeout = 1000)
	public void testUpdateTotalMethodInClassPlayerSkruCardLogic()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Object player;
		ArrayList<Object> hand;
		Object card1;
		Object card2;

		// Use reflection to create a Card instance\

		Class<?> SkruCardClass = Class.forName(skruCardPath);

		Constructor<?> ActionCardConstructor = SkruCardClass
				.getDeclaredConstructor(Class.forName(skruTypePath));

		@SuppressWarnings({ "unchecked", "rawtypes" })
		Object color1 = Enum.valueOf((Class<Enum>) Class.forName(skruTypePath),
				"RED");
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Object color2 = Enum.valueOf((Class<Enum>) Class.forName(skruTypePath),
				"GREEN");

		card1 = ActionCardConstructor.newInstance(color1);
		card2 = ActionCardConstructor.newInstance(color2);

		// Create the player's hand and add the card to it
		hand = new ArrayList<>();
		hand.add(card1);
		hand.add(card2);

		// Use reflection to create a Player instance
		Class<?> playerClass = Class.forName(playerPath);
		Constructor<?> playerConstructor = playerClass
				.getDeclaredConstructor(String.class);
		player = playerConstructor.newInstance("P1");

		// Use reflection to access the hand field of the player
		Field handField = playerClass.getDeclaredField("hand");
		handField.setAccessible(true);

		// Set the hand field to the ArrayList containing the card
		handField.set(player, hand);

		Method updateTotalMethod = playerClass.getMethod("updateTotal");

		int expectedTotal = 24;

		int actulaTotal = (int) updateTotalMethod.invoke(player);

		assertEquals(
				"the (updateTotal) method should return the sum of card values as follows : \n–numberCards: adds the value of the number in the card.\n– ActionCard : adds 10 points.\n– SkruCard : adds the value of the skru card.\n Wrong Skru card logic!! \n",
				expectedTotal, actulaTotal);
	}

	@Test(timeout = 1000)
	public void testDiscardCardMethodExisitInClassPlayer() {
		try {
			testMethodExistence(Class.forName(playerPath), "discardCard",
					void.class, new Class[] { Class.forName(cardPath),
							ArrayList.class });
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}

	}

	@Test(timeout = 1000)
	public void testDiscardCardMethodInClassPlayerLogicRemoveCardFromPlayerHand()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Object player;
		ArrayList<Object> deck;
		Object card1;
		Object card2;

		// Use reflection to create a Card instance\

		Class<?> SkruCardClass = Class.forName(skruCardPath);
		Class<?> ActiomCardClass = Class.forName(ActionCardPath);

		Constructor<?> SkruCardConstructor = SkruCardClass
				.getDeclaredConstructor(Class.forName(skruTypePath));

		Constructor<?> ActionCardConstructor = ActiomCardClass
				.getDeclaredConstructor();

		@SuppressWarnings({ "unchecked", "rawtypes" })
		Object color1 = Enum.valueOf((Class<Enum>) Class.forName(skruTypePath),
				"RED");
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Object color2 = Enum.valueOf((Class<Enum>) Class.forName(skruTypePath),
				"GREEN");

		card1 = SkruCardConstructor.newInstance(color1);
		card2 = SkruCardConstructor.newInstance(color2);
		Object card3 = ActionCardConstructor.newInstance();
		Object card4 = ActionCardConstructor.newInstance();

		Class<?> playerClass = Class.forName(playerPath);
		Constructor<?> playerConstructor = playerClass
				.getDeclaredConstructor(String.class);
		player = playerConstructor.newInstance("P1");
		// Use reflection to access the hand field of the player
		Field handField = playerClass.getDeclaredField("hand");
		handField.setAccessible(true);

		// Create the player's hand and add the card to it
		ArrayList<Object> hand = new ArrayList<>();
		hand.add(card3);
		hand.add(card4);

		// Set the hand field to the ArrayList containing the card
		handField.set(player, hand);

		// Create the player's hand and add the card to it

		deck = new ArrayList<>();

		deck.add(card1);
		deck.add(card2);

		Method discardCardMethod = player.getClass().getMethod("discardCard",
				Class.forName(cardPath), ArrayList.class);

		discardCardMethod.invoke(player, card3, deck);
		assertEquals(
				"The discardCard method should remove the card from the player's hand.",
				card4, (hand.get(0)));

	}

	@Test(timeout = 1000)
	public void testDiscardCardMethodInClassPlayerLogicAddsToDeck()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Object player;
		ArrayList<Object> deck;
		Object card1;
		Object card2;

		// Use reflection to create a Card instance\

		Class<?> SkruCardClass = Class.forName(skruCardPath);
		Class<?> ActiomCardClass = Class.forName(ActionCardPath);

		Constructor<?> SkruCardConstructor = SkruCardClass
				.getDeclaredConstructor(Class.forName(skruTypePath));

		Constructor<?> ActionCardConstructor = ActiomCardClass
				.getDeclaredConstructor();

		@SuppressWarnings({ "unchecked", "rawtypes" })
		Object color1 = Enum.valueOf((Class<Enum>) Class.forName(skruTypePath),
				"RED");
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Object color2 = Enum.valueOf((Class<Enum>) Class.forName(skruTypePath),
				"GREEN");

		card1 = SkruCardConstructor.newInstance(color1);
		card2 = SkruCardConstructor.newInstance(color2);
		Object card3 = ActionCardConstructor.newInstance();
		Object card4 = ActionCardConstructor.newInstance();

		Class<?> playerClass = Class.forName(playerPath);
		Constructor<?> playerConstructor = playerClass
				.getDeclaredConstructor(String.class);
		player = playerConstructor.newInstance("P1");
		// Use reflection to access the hand field of the player
		Field handField = playerClass.getDeclaredField("hand");
		handField.setAccessible(true);

		// Create the player's hand and add the card to it
		ArrayList<Object> hand = new ArrayList<>();
		hand.add(card3);
		hand.add(card4);

		// Set the hand field to the ArrayList containing the card
		handField.set(player, hand);

		// Create the player's hand and add the card to it

		deck = new ArrayList<>();

		deck.add(card1);
		deck.add(card2);

		Method discardCardMethod = player.getClass().getMethod("discardCard",
				Class.forName(cardPath), ArrayList.class);

		discardCardMethod.invoke(player, card3, deck);
		assertEquals(
				"The discardCard method should add the card at the top of the pile (last element), the card should be visible",
				card3, (deck.get(deck.size() - 1)));

	}

	@Test(timeout = 1000)
	public void testDiscardCardMethodInClassPlayerLogicAddsCardFaceUp()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Object player;
		ArrayList<Object> deck;
		Object card1;
		Object card2;

		// Use reflection to create a Card instance\

		Class<?> SkruCardClass = Class.forName(skruCardPath);
		Class<?> ActiomCardClass = Class.forName(ActionCardPath);

		Constructor<?> SkruCardConstructor = SkruCardClass
				.getDeclaredConstructor(Class.forName(skruTypePath));

		Constructor<?> ActionCardConstructor = ActiomCardClass
				.getDeclaredConstructor();

		@SuppressWarnings({ "unchecked", "rawtypes" })
		Object color1 = Enum.valueOf((Class<Enum>) Class.forName(skruTypePath),
				"RED");
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Object color2 = Enum.valueOf((Class<Enum>) Class.forName(skruTypePath),
				"GREEN");

		card1 = SkruCardConstructor.newInstance(color1);
		card2 = SkruCardConstructor.newInstance(color2);
		Object card3 = ActionCardConstructor.newInstance();
		Object card4 = ActionCardConstructor.newInstance();

		Class<?> playerClass = Class.forName(playerPath);
		Constructor<?> playerConstructor = playerClass
				.getDeclaredConstructor(String.class);
		player = playerConstructor.newInstance("P1");
		// Use reflection to access the hand field of the player
		Field handField = playerClass.getDeclaredField("hand");
		handField.setAccessible(true);

		// Create the player's hand and add the card to it
		ArrayList<Object> hand = new ArrayList<>();
		hand.add(card3);
		hand.add(card4);

		// Set the hand field to the ArrayList containing the card
		handField.set(player, hand);

		// Create the player's hand and add the card to it

		deck = new ArrayList<>();

		deck.add(card1);
		deck.add(card2);

		Method discardCardMethod = player.getClass().getMethod("discardCard",
				Class.forName(cardPath), ArrayList.class);

		Method isVisibleMethod = card2.getClass().getMethod("isVisible");
		discardCardMethod.invoke(player, card3, deck);
		assertEquals(
				"The discardCard method should add the card at the top of the pile (last element), the card should be visible",
				true,
				(boolean) isVisibleMethod.invoke(deck.get(deck.size() - 1)));

	}

	// //// game class methods////

	@Test(timeout = 1000)
	public void testMethodDrawCardFromDiscardDeckLogicAddCardToPlayerHand()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gamePlayersField = gameClass.getDeclaredField("players");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");

		Method drawCardFromDiscrdDeckMethod = gameClass
				.getMethod("DrawCardFromDiscardDeck");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		discardDeckField.setAccessible(true);

		ArrayList<Object> dd = (ArrayList<Object>) discardDeckField.get(game);

		Object c = dd.get(dd.size() - 1);
		drawCardFromDiscrdDeckMethod.invoke(game);
		Object c1 = ((ArrayList<Object>) handField.get(p1))
				.get(((ArrayList<Object>) handField.get(p1)).size() - 1);

		assertEquals(
				"DrawCardFromDiscardDeck should add the top most card in the discard deck (last element) to the current players hand",
				c, c1);

	}

	@Test(timeout = 1000)
	public void testMethodDrawCardFromDiscardDeckLogicRemoveCardFromDiscardDeck()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gamePlayersField = gameClass.getDeclaredField("players");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");

		Method drawCardFromDiscrdDeckMethod = gameClass
				.getMethod("DrawCardFromDiscardDeck");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		discardDeckField.setAccessible(true);

		ArrayList<Object> dd = (ArrayList<Object>) discardDeckField.get(game);

		Object c = dd.get(dd.size() - 1);
		drawCardFromDiscrdDeckMethod.invoke(game);

		if (dd.size() > 0)
			fail("The drawFromDiscardDeck should remove the top card (last element) of discard deck");

	}

	@Test(timeout = 1000)
	public void testMethodDrawCardFromDiscardDeckLogicSetHasDrawn()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gamePlayersField = gameClass.getDeclaredField("players");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");

		Method drawCardFromDiscrdDeckMethod = gameClass
				.getMethod("DrawCardFromDiscardDeck");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		discardDeckField.setAccessible(true);
		hasDrawnField.setAccessible(true);
		drawCardFromDiscrdDeckMethod.invoke(game);

		assertEquals(
				"the drawFromDiscardDeck method should set the hasDrawn attribute of the current player to true",
				true, hasDrawnField.get(p1));

	}

	@Test(timeout = 1000)
	public void testMethodDrawCardFromDiscardDeckLogicDrawOnlyOnce()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> cannotPlayException = Class.forName(cannotPlayExceptionPath);
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gamePlayersField = gameClass.getDeclaredField("players");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");

		Method drawCardFromDiscrdDeckMethod = gameClass
				.getMethod("DrawCardFromDiscardDeck");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		discardDeckField.setAccessible(true);
		hasDrawnField.setAccessible(true);
		hasDrawnField.set(p1, true);

		try {

			drawCardFromDiscrdDeckMethod.invoke(game);
			fail("Expected CannotPlayException not thrown player can only draw if he did not draw");
		} catch (InvocationTargetException e) {
			Throwable thrownException = e.getTargetException();
			assertNotNull("Expected exception was thrown", thrownException);
			assertTrue(
					"Expected CannotPlayException was not thrown when tryig to draw",
					cannotPlayException.isInstance(thrownException));
		}

	}

	@Test(timeout = 1000)
	public void testMethodDrawCardFromDeckLogicDrawOnlyOnce()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> cannotPlayException = Class.forName(cannotPlayExceptionPath);
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gamePlayersField = gameClass.getDeclaredField("players");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field DeckField = gameClass.getDeclaredField("deck");
		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");

		Method drawCardFromDeckMethod = gameClass.getMethod("DrawCardFromDeck");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		DeckField.setAccessible(true);
		hasDrawnField.setAccessible(true);
		hasDrawnField.set(p1, true);

		try {

			drawCardFromDeckMethod.invoke(game);
			fail("Expected CannotPlayException not thrown player can only draw if he did not draw");
		} catch (InvocationTargetException e) {
			Throwable thrownException = e.getTargetException();
			assertNotNull("Expected exception was thrown", thrownException);
			assertTrue(
					"Expected CannotPlayException was not thrown when tryig to draw ",
					cannotPlayException.isInstance(thrownException));
		}

	}

	@Test(timeout = 1000)
	public void testMethodDrawCardFromDeckLogicMakeCardVisible()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> cannotPlayException = Class.forName(cannotPlayExceptionPath);
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gamePlayersField = gameClass.getDeclaredField("players");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field DeckField = gameClass.getDeclaredField("deck");
		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");
		Field isVisbleField = Class.forName(cardPath).getDeclaredField(
				"visible");

		Method drawCardFromDeckMethod = gameClass.getMethod("DrawCardFromDeck");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		DeckField.setAccessible(true);
		hasDrawnField.setAccessible(true);
		isVisbleField.setAccessible(true);

		drawCardFromDeckMethod.invoke(game);
		int s = ((ArrayList<Object>) handField.get(p1)).size();
		Object c = ((ArrayList<Object>) handField.get(p1)).get(s - 1);
		assertEquals("the drawn card from deck shoud be visible", true,
				isVisbleField.get(c));

	}

	@Test(timeout = 1000)
	public void testMethodDrawCardFromDeckLogicAddCardToPlayerHand()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gamePlayersField = gameClass.getDeclaredField("players");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field DeckField = gameClass.getDeclaredField("deck");

		Method drawCardFromDeckMethod = gameClass.getMethod("DrawCardFromDeck");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		DeckField.setAccessible(true);

		ArrayList<Object> dd = (ArrayList<Object>) DeckField.get(game);

		Object c = dd.get(dd.size() - 1);
		drawCardFromDeckMethod.invoke(game);
		Object c1 = ((ArrayList<Object>) handField.get(p1))
				.get(((ArrayList<Object>) handField.get(p1)).size() - 1);

		assertEquals(
				"DrawCardFromDeck should add the top most card in the deck (last element) to the current players hand",
				c, c1);

	}

	@Test(timeout = 1000)
	public void testMethodDrawCardFromDeckLogicRemoveCardFromDiscardDeck()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gamePlayersField = gameClass.getDeclaredField("players");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field DeckField = gameClass.getDeclaredField("deck");

		Method drawCardFromDeckMethod = gameClass.getMethod("DrawCardFromDeck");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		DeckField.setAccessible(true);

		ArrayList<Object> dd = (ArrayList<Object>) DeckField.get(game);

		Object c = dd.get(dd.size() - 1);
		int s1 = dd.size();
		drawCardFromDeckMethod.invoke(game);
		int s2 = dd.size();
		Object c1 = dd.get(dd.size() - 1);

		if (c1.equals(c) && (s1 == s2))
			fail("The drawFromDeck should remove the top card (last element) of deck");

	}

	@Test(timeout = 1000)
	public void testMethodDrawCardFromDeckLogicSetHasDrawn()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gamePlayersField = gameClass.getDeclaredField("players");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("deck");
		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");

		Method drawCardFromDeckMethod = gameClass.getMethod("DrawCardFromDeck");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		discardDeckField.setAccessible(true);
		hasDrawnField.setAccessible(true);
		drawCardFromDeckMethod.invoke(game);

		assertEquals(
				"the drawFromDeck method should set the hasDrawn attribute of the current player to true",
				true, hasDrawnField.get(p1));

	}

	@Test(timeout = 1000)
	public void testMethodReplicateLogicCorrect()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");

		Method replicateMethod = gameClass.getMethod("replicate",
				Class.forName(cardPath));

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		discardDeckField.setAccessible(true);
		ArrayList<Object> hand = (ArrayList<Object>) (handField.get(p1));
		int s = ((ArrayList<Object>) discardDeckField.get(game)).size();
		Object c = ((ArrayList<Object>) discardDeckField.get(game)).get(s - 1);
		hand.add(c);
		int s2 = ((ArrayList<Object>) handField.get(p1)).size();

		replicateMethod.invoke(game, c);

		assertEquals(
				"After Replicate the card should be added to the discard deck",
				s + 1, ((ArrayList<Object>) discardDeckField.get(game)).size());
		assertEquals(
				"After Replicate the card should be removed from the current player hand",
				s2 - 1, ((ArrayList<Object>) handField.get(p1)).size());

	}

	@Test(timeout = 1000)
	public void testMethodReplicateLogicWrong() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");

		Method replicateMethod = gameClass.getMethod("replicate",
				Class.forName(cardPath));

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		discardDeckField.setAccessible(true);
		ArrayList<Object> hand = (ArrayList<Object>) (handField.get(p1));
		int s = ((ArrayList<Object>) handField.get(p1)).size();
		int s1 = ((ArrayList<Object>) discardDeckField.get(game)).size();

		Object c = ((ArrayList<Object>) handField.get(p1)).get(s - 1);

		replicateMethod.invoke(game, c);

		assertEquals(
				"If a player trys to replicate a card not the top of the discard deck , the top card should be added to his hand",
				s + 1, ((ArrayList<Object>) handField.get(p1)).size());
		assertEquals(
				"If a player trys to replicate a card not the top of the discard deck , the top card should be removed from the discard deck when added to the players hand ",
				s1 - 1, ((ArrayList<Object>) discardDeckField.get(game)).size());

	}

	@Test(timeout = 1000)
	public void testDrawCardFromDiscardDeckMethodExisitInClassGame() {
		try {
			testMethodExistence(Class.forName(gamePath),
					"DrawCardFromDiscardDeck", void.class, null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}

	}

	@Test(timeout = 1000)
	public void testDrawCardFromDeckMethodExisitInClassGame() {
		try {
			testMethodExistence(Class.forName(gamePath), "DrawCardFromDeck",
					void.class, null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}

	}

	@Test(timeout = 1000)
	public void testReplicateMethodExisitInClassGame() {
		try {
			testMethodExistence(Class.forName(gamePath), "replicate",
					void.class, new Class[] { Class.forName(cardPath) });
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}

	}

	// PlayCard
	@Test(timeout = 1000)
	public void testPlayCardMethodExisitInClassGame() {
		try {
			testMethodExistence(Class.forName(gamePath), "PlayCard",
					void.class, new Class[] { Class.forName(cardPath) });
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}

	}

	@Test(timeout = 1000)
	public void testPlayCardMethodInClassGameLogicAddCardToDiscard()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");

		Method playCardMethod = gameClass.getMethod("PlayCard",
				Class.forName(cardPath));

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		discardDeckField.setAccessible(true);
		ArrayList<Object> hand = (ArrayList<Object>) (handField.get(p1));
		int s = ((ArrayList<Object>) handField.get(p1)).size();

		Object c = ((ArrayList<Object>) handField.get(p1)).get(s - 1);
		playCardMethod.invoke(game, c);
		int s1 = ((ArrayList<Object>) discardDeckField.get(game)).size();
		Object c1 = ((ArrayList<Object>) discardDeckField.get(game))
				.get(s1 - 1);

		assertEquals(
				"The PlayCard Method should add the card c to the Discard cards pile",
				c1, c);

	}

	@Test(timeout = 1000)
	public void testPlayCardMethodInClassGameLogicSetHasPlayed()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");

		Method playCardMethod = gameClass.getMethod("PlayCard",
				Class.forName(cardPath));

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		discardDeckField.setAccessible(true);
		hasPlayedField.setAccessible(true);
		ArrayList<Object> hand = (ArrayList<Object>) (handField.get(p1));
		int s = ((ArrayList<Object>) handField.get(p1)).size();

		Object c = ((ArrayList<Object>) handField.get(p1)).get(s - 1);
		boolean h1 = (boolean) hasPlayedField.get(p1);
		playCardMethod.invoke(game, c);

		boolean h2 = (boolean) hasPlayedField.get(p1);

		assertEquals(
				"the PlayCard method should set the hasPlayed in the current player",
				true, h2);

	}

	@Test(timeout = 1000)
	public void testPlayCardMethodInClassGameLogicThrwosCannotPlay()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");

		Method playCardMethod = gameClass.getMethod("PlayCard",
				Class.forName(cardPath));

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		discardDeckField.setAccessible(true);
		hasPlayedField.setAccessible(true);
		ArrayList<Object> hand = (ArrayList<Object>) (handField.get(p1));
		int s = ((ArrayList<Object>) handField.get(p1)).size();
		Object c = ((ArrayList<Object>) handField.get(p1)).get(s - 1);
		hasPlayedField.set(p1, true);
		Class<?> cannotPlayException = Class.forName(cannotPlayExceptionPath);

		try {
			playCardMethod.invoke(game, c);
			fail("Expected CannotPlayException not thrown in PlayCard player can only play if he did not play before");
		} catch (InvocationTargetException e) {
			Throwable thrownException = e.getTargetException();
			assertNotNull("Expected exception was thrown", thrownException);
			assertTrue(
					"Expected CannotPlayException was not thrown when tryig to play twice",
					cannotPlayException.isInstance(thrownException));
		}

	}

	@Test(timeout = 1000)
	public void testPlayCardMethodInClassGameLogicThrwosCannotPlayWrongCard()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);
		Class<?> actionCardClass = Class.forName(ActionCardPath);
		Constructor<?> actionCardConstructor = actionCardClass.getConstructor();
		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");

		Method playCardMethod = gameClass.getMethod("PlayCard",
				Class.forName(cardPath));

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);
		discardDeckField.setAccessible(true);
		hasPlayedField.setAccessible(true);
		ArrayList<Object> hand = (ArrayList<Object>) (handField.get(p1));
		int s = ((ArrayList<Object>) handField.get(p1)).size();

		hasPlayedField.set(p1, true);
		Class<?> cannotPlayException = Class.forName(cannotPlayExceptionPath);

		Object c = ((ArrayList<Object>) handField.get(p1)).remove(0);
		try {
			playCardMethod.invoke(game, c);
			fail("Expected CannotPlayException not thrown in PlayCard player can only play a card he has");
		} catch (InvocationTargetException e) {
			Throwable thrownException = e.getTargetException();
			assertNotNull("Expected exception was thrown", thrownException);
			assertTrue(
					"Expected CannotPlayException was not thrown when tryig to play he does not have",
					cannotPlayException.isInstance(thrownException));
		}

	}

	@Test(timeout = 1000)
	public void testUseSpecialCard1MethodExisitInClassGame() {
		try {
			testMethodExistence(Class.forName(gamePath), "useSpecialCard",
					void.class, new Class[] { Class.forName(playerPath),
							int.class, int.class });
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}

	}

	@Test(timeout = 1000)
	public void testUseSpecialCard2MethodExisitInClassGame() {
		try {
			testMethodExistence(
					Class.forName(gamePath),
					"useSpecialCard",
					void.class,
					new Class[] { Class.forName(playerPath),
							Class.forName(cardPath) });
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}

	}

	@Test(timeout = 1000)
	public void testUseSpecialCard3MethodExisitInClassGame() {
		try {
			testMethodExistence(Class.forName(gamePath), "useSpecialCard",
					void.class, new Class[] { int.class, int.class, int.class,
							int.class });
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}
	}

	// declareSkru
	@Test(timeout = 1000)
	public void testDeclareSkruMethodExisitInClassGame() {
		try {
			testMethodExistence(Class.forName(gamePath), "declareSkru",
					void.class, null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}
	}

	// performActionCard
	@Test(timeout = 1000)
	public void testPerformActionCardMethodExisitInClassGame() {
		try {
			testMethodExistence(
					Class.forName(gamePath),
					"performActionCard",
					void.class,
					new Class[] { Class.forName(playerPath),
							Class.forName(cardPath), int.class });
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}
	}

	// endTurn
	@Test(timeout = 1000)
	public void testEndTurnMethodExisitInClassGame() {
		try {
			testMethodExistence(Class.forName(gamePath), "endTurn", void.class,
					null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}

	}

	// endRound
	@Test(timeout = 1000)
	public void testEndRoundMethodExisitInClassGame() {
		try {
			testMethodExistence(Class.forName(gamePath), "endRound",
					void.class, null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}

	}

	// gameOver
	@Test(timeout = 1000)
	public void testGameOverMethodExisitInClassGame() {
		try {
			testMethodExistence(Class.forName(gamePath), "gameOver",
					boolean.class, null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}

	}

	// getWinner
	@Test(timeout = 1000)
	public void testGetWinnerMethodExisitInClassGame() {
		try {
			testMethodExistence(Class.forName(gamePath), "getWinner",
					Class.forName(playerPath), null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Please check the console for the error, its an error from this catch statement."
					+ e.getClass() + " occurred");
		}

	}

	@Test(timeout = 1000)
	public void testUseSpecialCardSwap() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");

		Method useSpecialCardMethod = gameClass.getMethod("useSpecialCard",
				Class.forName(playerPath), int.class, int.class);

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);

		int i1 = (int) (Math.random() * 4);
		int i2 = (int) (Math.random() * 4);

		ArrayList<Object> hand = (ArrayList<Object>) (handField.get(p1));
		ArrayList<Object> hand2 = (ArrayList<Object>) (handField.get(p2));

		Object c1 = ((ArrayList<Object>) (handField.get(p1))).get(i1);
		Object c2 = ((ArrayList<Object>) (handField.get(p2))).get(i2);

		useSpecialCardMethod.invoke(game, p2, i1, i2);

		Object newc1 = ((ArrayList<Object>) (handField.get(p1))).get(i1);
		Object newc2 = ((ArrayList<Object>) (handField.get(p2))).get(i2);

		assertEquals(
				"the useSpecialCard method reprsenting SWAP should swap the card between current player and the given player ",
				newc1, c2);
		assertEquals(
				"the useSpecialCard method reprsenting SWAP should swap the card between current player and the given player ",
				newc2, c1);

	}

	@Test(timeout = 1000)
	public void testUseSpecialCardGive() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");

		Method useSpecialCardMethod = gameClass.getMethod("useSpecialCard",
				Class.forName(playerPath), Class.forName(cardPath));

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);

		int i1 = (int) (Math.random() * 4);

		Object c1 = ((ArrayList<Object>) (handField.get(p1))).get(i1);

		useSpecialCardMethod.invoke(game, p2, c1);
		ArrayList<Object> hand = (ArrayList<Object>) (handField.get(p1));
		ArrayList<Object> hand2 = (ArrayList<Object>) (handField.get(p2));

		Object newc1 = ((ArrayList<Object>) (handField.get(p2))).get(hand2
				.size() - 1);

		boolean f = hand.contains(c1);
		assertEquals(
				"the useSpecialCard method reprsenting GIVE should remove the card from the current player and give it to the given player ",
				newc1, c1);
		assertEquals(
				"the useSpecialCard method reprsenting GIVE should remove the card from the current player's hand",
				f, false);
	}

	@Test(timeout = 1000)
	public void testUseSpecialCardMasterEye() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {
		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");

		Method useSpecialCardMethod = gameClass.getMethod("useSpecialCard",
				int.class, int.class, int.class, int.class);

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);

		Field visibleField = Class.forName(cardPath)
				.getDeclaredField("visible");
		visibleField.setAccessible(true);

		int i1 = (int) (Math.random() * 4);
		int i2 = (int) (Math.random() * 4);
		int i3 = (int) (Math.random() * 4);
		int i4 = (int) (Math.random() * 4);

		ArrayList<Object> hand = (ArrayList<Object>) (handField.get(p1));
		ArrayList<Object> hand2 = (ArrayList<Object>) (handField.get(p2));

		Object c1 = ((ArrayList<Object>) (handField.get(p1))).get(i1);
		Object c2 = ((ArrayList<Object>) (handField.get(p2))).get(i2);
		Object c3 = ((ArrayList<Object>) (handField.get(p3))).get(i3);
		Object c4 = ((ArrayList<Object>) (handField.get(p4))).get(i4);

		visibleField.set(c1, false);
		visibleField.set(c2, false);
		visibleField.set(c3, false);
		visibleField.set(c4, false);

		useSpecialCardMethod.invoke(game, i1, i2, i3, i4);

		if (!(boolean) visibleField.get(c1))
			fail("useSpecialCard represnting MASTEREYE should reveal the card at  index i1 for the 1st player in the players Arraylist");

		if (!(boolean) visibleField.get(c2))
			fail("useSpecialCard represnting MASTEREYE should reveal the card at  index i2 for the 2nd player in the players Arraylist");

		if (!(boolean) visibleField.get(c3))
			fail("useSpecialCard represnting MASTEREYE should reveal the card at  index i3 for the 3rd player in the players Arraylist");

		if (!(boolean) visibleField.get(c4))
			fail("useSpecialCard represnting MASTEREYE should reveal the card at  index i4 for the 4th player in the players Arraylist");

	}

	@Test(timeout = 1000)
	public void testDeclareSkruLogic() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");

		Method declareSkruMethod = gameClass.getMethod("declareSkru");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);

		Field declaredSkruPlayer = gameClass.getDeclaredField("declaredSkru");
		declaredSkruPlayer.setAccessible(true);

		Field turnCount = playerClass.getDeclaredField("turnCount");
		turnCount.setAccessible(true);

		turnCount.set(p1, 5);

		declareSkruMethod.invoke(game);
		Object o = declaredSkruPlayer.get(game);

		assertEquals(
				"declareSkru method should set the declared skru attribute to the current player",
				o, p1);

	}

	@Test(timeout = 1000)
	public void testDeclareSkruThrowsCannotSkruException()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");

		Method declareSkruMethod = gameClass.getMethod("declareSkru");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);

		Field declaredSkruPlayer = gameClass.getDeclaredField("declaredSkru");
		declaredSkruPlayer.setAccessible(true);

		Field turnCount = playerClass.getDeclaredField("turnCount");
		turnCount.setAccessible(true);

		// turnCount.set(p1, 5);
		declaredSkruPlayer.set(game, p2);
		Class<?> cannotSkruException = Class.forName(cannotSkruExceptionPath);
		try {
			declareSkruMethod.invoke(game);
			fail("Expected cannotSkruException not thrown , a player can declare skru only if no one has alread did , and the player should atleast be at turn 3");
		} catch (InvocationTargetException e) {
			Throwable thrownException = e.getTargetException();
			assertNotNull("Expected exception was thrown", thrownException);
			assertTrue(
					"Expected cannotSkruException not thrown , a player can declare skru only if no one has alread did , and the player should atleast be at turn 3",
					cannotSkruException.isInstance(thrownException));
		}

	}

	@Test(timeout = 1000)
	public void testperformActionCardLogic() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");

		Method performActionCardMethod = gameClass.getMethod(
				"performActionCard", Class.forName(playerPath),
				Class.forName(cardPath), int.class);

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);

		Field visibleField = Class.forName(cardPath)
				.getDeclaredField("visible");
		visibleField.setAccessible(true);

		Class<?> seeSelfClass = Class.forName(selfRevealerCardPath);
		Constructor<?> seeSelfConstructor = seeSelfClass
				.getConstructor(int.class);
		Object c = seeSelfConstructor.newInstance(7);

		int i = (int) (Math.random() * 2) + 2;
		performActionCardMethod.invoke(game, p1, c, i);

		ArrayList<Object> hand = (ArrayList<Object>) handField.get(p1);

		Object c1 = hand.get(i);
		assertEquals(
				"the performActionCard Method should make the card in the given index visible in the given player's hand",
				true, visibleField.get(c1));

	}

	@Test(timeout = 1000)
	public void testperformActionCardSelfRevealThrowCardActionException()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");

		Method performActionCardMethod = gameClass.getMethod(
				"performActionCard", Class.forName(playerPath),
				Class.forName(cardPath), int.class);

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);

		Field visibleField = Class.forName(cardPath)
				.getDeclaredField("visible");
		visibleField.setAccessible(true);

		Class<?> seeSelfClass = Class.forName(selfRevealerCardPath);
		Constructor<?> seeSelfConstructor = seeSelfClass
				.getConstructor(int.class);
		Object c = seeSelfConstructor.newInstance(7);

		int i = (int) (Math.random() * 2) + 2;
		Class<?> cardActionException = Class.forName(cardActionExceptionPath);
		try {
			performActionCardMethod.invoke(game, p2, c, i);
			fail("Expected cardActionException not thrown , slef reveal card an only show a card for belonging to the current player");
		} catch (InvocationTargetException e) {
			Throwable thrownException = e.getTargetException();
			assertNotNull("Expected exception was thrown", thrownException);
			assertTrue(
					"Expected cardActionException not thrown , slef reveal card can only show a card for belonging to the current player",
					cardActionException.isInstance(thrownException));
		}

	}

	@Test(timeout = 1000)
	public void testperformActionCardOthersRevealThrowCardActionException()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");

		Method performActionCardMethod = gameClass.getMethod(
				"performActionCard", Class.forName(playerPath),
				Class.forName(cardPath), int.class);

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);

		Field visibleField = Class.forName(cardPath)
				.getDeclaredField("visible");
		visibleField.setAccessible(true);

		Class<?> seeOtherClass = Class.forName(othersRevealerCardPath);
		Constructor<?> seeOtherConstructor = seeOtherClass
				.getConstructor(int.class);
		Object c = seeOtherConstructor.newInstance(9);

		int i = (int) (Math.random() * 2) + 2;
		Class<?> cardActionException = Class.forName(cardActionExceptionPath);
		try {
			performActionCardMethod.invoke(game, p1, c, i);
			fail("Expected cardActionException not thrown , others reveal card  can not show a card for belonging to the current player");
		} catch (InvocationTargetException e) {
			Throwable thrownException = e.getTargetException();
			assertNotNull("Expected exception was thrown", thrownException);
			assertTrue(
					"Expected cardActionException not thrown , others reveal card can not show a card for belonging to the current player",
					cardActionException.isInstance(thrownException));
		}

	}

	@Test(timeout = 1000)
	public void testEndTurnLogicThrowCannotPlayException()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");

		Method endTurnMrthod = gameClass.getMethod("endTurn");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);

		Field visibleField = Class.forName(cardPath)
				.getDeclaredField("visible");
		visibleField.setAccessible(true);

		Class<?> seeOtherClass = Class.forName(othersRevealerCardPath);
		Constructor<?> seeOtherConstructor = seeOtherClass
				.getConstructor(int.class);
		Object c = seeOtherConstructor.newInstance(9);

		int i = (int) (Math.random() * 2) + 2;
		Class<?> cannotPlayException = Class.forName(cannotPlayExceptionPath);
		try {
			endTurnMrthod.invoke(game);
			fail("Expected cannotPlayException not thrown , player can end turn only if he played a card");
		} catch (InvocationTargetException e) {
			Throwable thrownException = e.getTargetException();
			assertNotNull("Expected exception was thrown", thrownException);
			assertTrue(
					"Expected cannotPlayException not thrown , player can end turn only if he played a card",
					cannotPlayException.isInstance(thrownException));
		}

	}

	@Test(timeout = 1000)
	public void testEndTurnLogicResetFlags() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");
		hasPlayedField.setAccessible(true);
		hasPlayedField.set(p1, true);

		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");
		hasDrawnField.setAccessible(true);
		hasDrawnField.set(p1, true);

		Method endTurnMrthod = gameClass.getMethod("endTurn");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);

		endTurnMrthod.invoke(game);
		assertEquals(
				"the end turn methof should reset the hasPlayed flag to false before updating the current player",
				false, hasPlayedField.get(p1));
		assertEquals(
				"the end turn methof should reset the hasDrawn flag to false before updating the current player",
				false, hasDrawnField.get(p1));

	}

	@Test(timeout = 1000)
	public void testEndTurnLogicUpdateCurrentTurnTotal()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");
		hasPlayedField.setAccessible(true);
		hasPlayedField.set(p1, true);

		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");
		hasDrawnField.setAccessible(true);
		hasDrawnField.set(p1, true);

		Field currenrTurnTotalField = playerClass
				.getDeclaredField("currenrTurnTotal");
		currenrTurnTotalField.setAccessible(true);

		Method updateTotalMethod = playerClass.getMethod("updateTotal");
		handField.setAccessible(true);
		int total = (int) updateTotalMethod.invoke(p1);

		Method endTurnMrthod = gameClass.getMethod("endTurn");

		handField.setAccessible(true);
		gameCurrentPlayerField.setAccessible(true);

		endTurnMrthod.invoke(game);
		assertEquals(
				"The endTurn method shoud set the currentTurnTotal of the current player using the updateTotal method before changing the currentPlayer",
				total, currenrTurnTotalField.get(p1));

	}

	@Test(timeout = 1000)
	public void testEndTurnLogicUpdateTurnCount()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field gameCurrentPlayerField = gameClass
				.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");
		hasPlayedField.setAccessible(true);
		hasPlayedField.set(p1, true);

		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");
		hasDrawnField.setAccessible(true);
		hasDrawnField.set(p1, true);

		Field turnCountField = playerClass.getDeclaredField("turnCount");
		turnCountField.setAccessible(true);

		int oldCount = (int) (Math.random() * 10);
		turnCountField.set(p1, oldCount);

		Method endTurnMrthod = gameClass.getMethod("endTurn");

		endTurnMrthod.invoke(game);
		assertEquals(
				"The endTurn method shoud increment the turnCount of the current player by 1 before changing the currentPlayer",
				oldCount + 1, turnCountField.get(p1));

	}

	@Test(timeout = 1000)
	public void testEndTurnLogicUpdateCurrentPlayer()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);
		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field currentPlayerField = gameClass.getDeclaredField("currentPlayer");
		currentPlayerField.setAccessible(true);

		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");
		hasPlayedField.setAccessible(true);
		hasPlayedField.set(p1, true);
		hasPlayedField.set(p2, true);
		hasPlayedField.set(p3, true);
		hasPlayedField.set(p4, true);

		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");
		hasDrawnField.setAccessible(true);
		hasDrawnField.set(p1, true);

		Field turnCountField = playerClass.getDeclaredField("turnCount");
		turnCountField.setAccessible(true);

		Method endTurnMrthod = gameClass.getMethod("endTurn");

		endTurnMrthod.invoke(game);
		assertEquals(
				"the endTurn should update the current player from player 1 to player 2 ",
				true, (currentPlayerField.get(game).equals(p2)));

		endTurnMrthod.invoke(game);
		assertEquals(
				"the endTurn should update the current player from player 2 to player 3 ",
				true, (currentPlayerField.get(game).equals(p3)));

		endTurnMrthod.invoke(game);
		assertEquals(
				"the endTurn should update the current player from player 3 to player 4 ",
				true, (currentPlayerField.get(game).equals(p4)));

		endTurnMrthod.invoke(game);
		assertEquals(
				"the endTurn should update the current player from player 4 back to player 1 ",
				true, (currentPlayerField.get(game).equals(p1)));

	}

	@Test(timeout = 1000)
	public void testendRoundLogicResetDeclaredSkru()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field CurrentPlayerField = gameClass.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");
		hasPlayedField.setAccessible(true);
		hasPlayedField.set(p1, true);

		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");
		hasDrawnField.setAccessible(true);
		hasDrawnField.set(p1, true);

		Field turnCountField = playerClass.getDeclaredField("turnCount");
		turnCountField.setAccessible(true);

		Field declaredSkruField = gameClass.getDeclaredField("declaredSkru");
		declaredSkruField.setAccessible(true);
		declaredSkruField.set(game, p2);

		Method endRoundMethod = gameClass.getMethod("endRound");

		endRoundMethod.invoke(game);
		assertEquals(
				"The endRound method should reset the declaredSkru attribute to null",
				null, declaredSkruField.get(game));

		// Field playersField = gameClass.getDeclaredField("players");
		// playersField.setAccessible(true);
		//
		// ArrayList<Object> ps = (ArrayList<Object>) playersField.get(game);
		// for (Object object : ps) {
		// //TODO
		// }
		//

	}

	@Test(timeout = 1000)
	public void testendRoundLogicUpdateCurrentRound()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field CurrentPlayerField = gameClass.getDeclaredField("currentPlayer");
		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");
		hasPlayedField.setAccessible(true);
		hasPlayedField.set(p1, true);

		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");
		hasDrawnField.setAccessible(true);
		hasDrawnField.set(p1, true);

		Field turnCountField = playerClass.getDeclaredField("turnCount");
		turnCountField.setAccessible(true);

		Field declaredSkruField = gameClass.getDeclaredField("declaredSkru");
		declaredSkruField.setAccessible(true);
		declaredSkruField.set(game, p2);

		Field currentRoundField = gameClass.getDeclaredField("currentRound");
		currentRoundField.setAccessible(true);
		int r = (int) (Math.random() * 4);
		currentRoundField.set(game, r);

		Method endRoundMethod = gameClass.getMethod("endRound");

		endRoundMethod.invoke(game);
		assertEquals(
				"The endRound method should increment the currentRound by 1",
				r + 1, currentRoundField.get(game));

		// Field playersField = gameClass.getDeclaredField("players");
		// playersField.setAccessible(true);
		//
		// ArrayList<Object> ps = (ArrayList<Object>) playersField.get(game);
		// for (Object object : ps) {
		// //TODO
		// }
		//

	}

	@Test(timeout = 1000)
	public void testendRoundLogicResetDeckAndDiscardDeck()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field CurrentPlayerField = gameClass.getDeclaredField("currentPlayer");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");
		hasPlayedField.setAccessible(true);
		hasPlayedField.set(p1, true);

		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");
		hasDrawnField.setAccessible(true);
		hasDrawnField.set(p1, true);

		Field turnCountField = playerClass.getDeclaredField("turnCount");
		turnCountField.setAccessible(true);

		Field declaredSkruField = gameClass.getDeclaredField("declaredSkru");
		declaredSkruField.setAccessible(true);
		declaredSkruField.set(game, p2);

		Field discardDeckField = gameClass.getDeclaredField("discardDeck");
		Field deckField = gameClass.getDeclaredField("deck");

		discardDeckField.setAccessible(true);
		deckField.setAccessible(true);

		ArrayList<Object> deck = (ArrayList<Object>) deckField.get(game);
		ArrayList<Object> discardDeck = (ArrayList<Object>) discardDeckField
				.get(game);

		Method endRoundMethod = gameClass.getMethod("endRound");

		endRoundMethod.invoke(game);

		ArrayList<Object> deck2 = (ArrayList<Object>) deckField.get(game);
		ArrayList<Object> discardDeck2 = (ArrayList<Object>) discardDeckField
				.get(game);

		assertEquals(
				"the endRound method sould rest the deck (generate a new one)",
				false, deck.equals(deck2));
		assertEquals("the endRound method sould rest the discardDeck ", false,
				discardDeck.equals(discardDeck2));

	}

	// test players hand reset
	@Test(timeout = 1000)
	public void testendRoundLogicResetHandofThePlayers()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		Field turnCountField = playerClass.getDeclaredField("turnCount");
		turnCountField.setAccessible(true);

		Field currenrTurnTotalField = playerClass
				.getDeclaredField("currenrTurnTotal");
		currenrTurnTotalField.setAccessible(true);



		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);
		
		

		
		
		
		Field CurrentPlayerField = gameClass.getDeclaredField("currentPlayer");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");
		hasPlayedField.setAccessible(true);
		hasPlayedField.set(p1, true);

		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");
		hasDrawnField.setAccessible(true);
		hasDrawnField.set(p1, true);

		Field declaredSkruField = gameClass.getDeclaredField("declaredSkru");
		declaredSkruField.setAccessible(true);
		declaredSkruField.set(game, p2);

		Field handField = playerClass.getDeclaredField("hand");
		handField.setAccessible(true);
		
		
		ArrayList<Object> h1 = new ArrayList<Object>((ArrayList<Object>) handField.get(p1));
		ArrayList<Object> h2 = new ArrayList<Object>((ArrayList<Object>) handField.get(p2));
		ArrayList<Object> h3 =new ArrayList<Object>((ArrayList<Object>) handField.get(p3));
		ArrayList<Object> h4 = new ArrayList<Object>((ArrayList<Object>) handField.get(p4));
		
		
		
		Method endRoundMethod = gameClass.getMethod("endRound");

		endRoundMethod.invoke(game);
		
		ArrayList<Object> h11 = (ArrayList<Object>) handField.get(p1);
	
		ArrayList<Object> h22 = (ArrayList<Object>) handField.get(p2);
		ArrayList<Object> h33 = (ArrayList<Object>) handField.get(p3);
		ArrayList<Object> h44 = (ArrayList<Object>) handField.get(p4);

		if(h1.equals(h11))
			fail("the endRound Method should reset the game this includes the player's hand (distribute new cards)");
		if(h2.equals(h22))
			fail("the endRound Method should reset the game this includes the player's hand (distribute new cards)");
		if(h3.equals(h33))
			fail("the endRound Method should reset the game this includes the player's hand (distribute new cards)");
		if(h4.equals(h44))
			fail("the endRound Method should reset the game this includes the player's hand (distribute new cards)");
		
	}

	// test players score reset
	@Test(timeout = 1000)
	public void testendRoundLogicResetCurrentTotalofThePlayers()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		Field turnCountField = playerClass.getDeclaredField("turnCount");
		turnCountField.setAccessible(true);

		Field currenrTurnTotalField = playerClass
				.getDeclaredField("currenrTurnTotal");
		currenrTurnTotalField.setAccessible(true);

		currenrTurnTotalField.set(p1, ((int) (Math.random() * 50) + 1));
		currenrTurnTotalField.set(p2, ((int) (Math.random() * 50) + 1));
		currenrTurnTotalField.set(p3, ((int) (Math.random() * 50) + 1));
		currenrTurnTotalField.set(p4, ((int) (Math.random() * 50) + 1));

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field CurrentPlayerField = gameClass.getDeclaredField("currentPlayer");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");
		hasPlayedField.setAccessible(true);
		hasPlayedField.set(p1, true);

		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");
		hasDrawnField.setAccessible(true);
		hasDrawnField.set(p1, true);

		Field declaredSkruField = gameClass.getDeclaredField("declaredSkru");
		declaredSkruField.setAccessible(true);
		declaredSkruField.set(game, p2);

		Method endRoundMethod = gameClass.getMethod("endRound");

		endRoundMethod.invoke(game);
		for (Object p : players) {
			if ((int) (currenrTurnTotalField.get(p)) != 0)
				fail("the endRound method should reset the currentTurnTotal of all players to 0");
		}

	}

	// test turn count
	@Test(timeout = 1000)
	public void testendRoundLogicResetTurnCountofThePlayers()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);

		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		Field turnCountField = playerClass.getDeclaredField("turnCount");
		turnCountField.setAccessible(true);

		

		turnCountField.set(p1, ((int) (Math.random() * 10) + 1));
		turnCountField.set(p2, ((int) (Math.random() * 10) + 1));
		turnCountField.set(p3, ((int) (Math.random() * 10) + 1));
		turnCountField.set(p4, ((int) (Math.random() * 10) + 1));

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field handField = playerClass.getDeclaredField("hand");
		Field CurrentPlayerField = gameClass.getDeclaredField("currentPlayer");
		Field hasPlayedField = playerClass.getDeclaredField("hasPlayed");
		hasPlayedField.setAccessible(true);
		hasPlayedField.set(p1, true);

		Field hasDrawnField = playerClass.getDeclaredField("hasDrawn");
		hasDrawnField.setAccessible(true);
		hasDrawnField.set(p1, true);

		Field declaredSkruField = gameClass.getDeclaredField("declaredSkru");
		declaredSkruField.setAccessible(true);
		declaredSkruField.set(game, p2);

		Method endRoundMethod = gameClass.getMethod("endRound");

		endRoundMethod.invoke(game);
		for (Object p : players) {
			if ((int) (turnCountField.get(p)) != 0)
				fail("the endRound method should reset the turnCount of all players to 0");
		}

	}

	@Test(timeout = 1000)
	public void testGameOverMethodCorrect() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);
		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field currentRoundField = gameClass.getDeclaredField("currentRound");
		currentRoundField.setAccessible(true);

		int r = (int) (Math.random() * 4) + 6;
		currentRoundField.set(game, r);

		Method gameOverMethod = gameClass.getDeclaredMethod("gameOver");
		boolean go = (boolean) gameOverMethod.invoke(game);
		assertEquals("The game is over after the 5th round ", true, go);
	}

	@Test(timeout = 1000)
	public void testGameOverMethodWrong() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);
		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);
		Field currentRoundField = gameClass.getDeclaredField("currentRound");
		currentRoundField.setAccessible(true);

		int r = (int) (Math.random() * 5);
		currentRoundField.set(game, r);

		Method gameOverMethod = gameClass.getDeclaredMethod("gameOver");
		boolean go = (boolean) gameOverMethod.invoke(game);
		assertEquals("The game is not over till the 5th round is over", false,
				go);

	}

	@Test(timeout = 1000)
	public void testGetWinner() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {

		Class<?> playerClass = Class.forName(playerPath);
		Class<?> gameClass = Class.forName(gamePath);

		Constructor<?> gameConstructor = Class.forName(gamePath)
				.getConstructor(ArrayList.class);
		Constructor<?> playerConstructor = Class.forName(playerPath)
				.getConstructor(String.class);

		Object p1 = playerConstructor.newInstance("p1");
		Object p2 = playerConstructor.newInstance("p2");
		Object p3 = playerConstructor.newInstance("p3");
		Object p4 = playerConstructor.newInstance("p4");

		ArrayList<Object> players = new ArrayList<Object>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		Object game = gameConstructor.newInstance(players);

		Field totalScoresField = gameClass.getDeclaredField("totalScores");

		totalScoresField.setAccessible(true);

		int[][] score1 = { { 0, 0, 0, 0, 0 }, { 5, 5, 5, 5, 5 },
				{ 5, 5, 5, 5, 5 }, { 5, 5, 5, 5, 5 }, };

		totalScoresField.set(game, score1);

		Method getWinnerMethod = gameClass.getMethod("getWinner");

		Object w1 = getWinnerMethod.invoke(game);
		assertEquals(
				"the getWinner method should retrun the player with the lowest over all score",
				0, players.indexOf(w1));

		int[][] score2 = { { 5, 5, 5, 5, 5 }, { 0, 0, 0, 0, 0 },

		{ 5, 5, 5, 5, 5 }, { 5, 5, 5, 5, 5 }, };

		totalScoresField.set(game, score2);

		Object w2 = getWinnerMethod.invoke(game);
		assertEquals(
				"the getWinner method should retrun the player with the lowest over all score",
				1, players.indexOf(w2));

	}

}
