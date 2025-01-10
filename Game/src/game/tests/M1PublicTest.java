package game.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import game.engine.Player;
import game.enums.SkruType;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class M1PublicTest {

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

	private void testMethodAbsence(Class aClass, String methodName) {
		Method[] methods = aClass.getDeclaredMethods();
		assertFalse(aClass.getSimpleName() + " class should not override "
				+ methodName + " method",
				containsMethodName(methods, methodName));
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
	String playerPath = "game.engine.Player";
	String gamePath = "game.engine.Game";

	// ///////////////test interfaces exisit ////
	@Test(timeout = 1000)
	public void testSpecialCardInterfaceExisit() throws ClassNotFoundException {
		testIsInterface(Class.forName(specialCarInterfacePath));
	}

	// /////////test enums//////////////////////////
	@Test(timeout = 1000)
	public void testSkruTypeIsEnum() throws ClassNotFoundException {
		testIsEnum(Class.forName(skruTypePath));
	}

	@Test(timeout = 1000)
	public void testSkruTypeEnumValues() {
		testEnumValues(skruTypePath, "SkruType",
				new String[] { "GREEN", "RED" });
	}

	// ///////////test card class ///////////////

	@Test(timeout = 1000)
	public void testCardExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(cardPath));
	}

	@Test(timeout = 1000)
	public void testIsCardAbstractClass() throws Exception {
		testClassIsAbstract(Class.forName(cardPath));
	}

	@Test(timeout = 1000)
	public void testCardInstanceVariables() throws NoSuchFieldException,
			SecurityException, ClassNotFoundException {

		testInstanceVariablesArePresent(Class.forName(cardPath), "visible",
				true);
	}

	@Test(timeout = 1000)
	public void testCardInstanceVariableVisibleIsPrivate()
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		testInstanceVariableIsPrivate(Class.forName(cardPath), "visible");
	}

	@Test(timeout = 1000)
	public void testInstanceVariableVisibleInCardSetter() throws Exception {
		testSetterMethodExistsInClass(Class.forName(cardPath), "setVisible",
				boolean.class, true);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableVisibleInCardGetter() throws Exception {
		testGetterMethodExistsInClass(Class.forName(cardPath), "isVisible",
				boolean.class);
	}

	// ///////test Skru card class //////////////////
	@Test(timeout = 1000)
	public void testSkruCardExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(skruCardPath));
	}

	@Test(timeout = 1000)
	public void testSkruCardIsSubclassOfCardClass()
			throws ClassNotFoundException {
		testClassIsSubClass(Class.forName(skruCardPath),
				Class.forName(cardPath));
	}

	@Test(timeout = 1000)
	public void testSkruCardInstanceVariables() throws NoSuchFieldException,
			SecurityException, ClassNotFoundException {

		testInstanceVariablesArePresent(Class.forName(skruCardPath), "type",
				true);
	}

	@Test(timeout = 1000)
	public void testSkruCardInstanceVariableTypeIsPrivate()
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		testInstanceVariableIsPrivate(Class.forName(skruCardPath), "type");
	}

	@Test(timeout = 1000)
	public void testSkruCardInstanceVariableTypeIsFinal()
			throws ClassNotFoundException {
		testInstanceVariableIsFinal(Class.forName(skruCardPath), "type");
	}

	@Test(timeout = 1000)
	public void testConstructorSkruCardWithType() throws ClassNotFoundException {
		Class[] inputs = { Class.forName(skruTypePath) };
		testConstructorExists(Class.forName(skruCardPath), inputs);
	}

	@Test(timeout = 1000)
	public void testGetValueMethodExistsInSkruCard()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException {
		Class<?> clazz = Class.forName(skruCardPath);
		Method method = clazz.getDeclaredMethod("getValue");
		assertNotNull("The method getValue() should exist in class skruCard",
				method);
		assertEquals(
				"The return type of getValue() in skruCard class should be int",
				int.class, method.getReturnType());

	}

	@Test(timeout = 1000)
	public void tesGetValueMethodInSkruCardLogic()
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Class aClass = Class.forName(skruCardPath);

		Object color1 = Enum.valueOf((Class<Enum>) Class.forName(skruTypePath),
				"RED");
		Object color2 = Enum.valueOf((Class<Enum>) Class.forName(skruTypePath),
				"GREEN");

		Constructor<?> c = Class.forName(skruCardPath).getConstructor(
				Class.forName(skruTypePath));
		Object scr = c.newInstance(color1);
		Object scg = c.newInstance(color2);

		Method m1 = scr.getClass().getDeclaredMethod("getValue");
		Method m2 = scg.getClass().getDeclaredMethod("getValue");

		assertEquals("red skru cards has should return 25 as value", 25,
				m1.invoke(scr));
		assertEquals("green skru cards has should return -1 as value", -1,
				m2.invoke(scg));

	}

	// ///////////////////////////test action card
	// class/////////////////////////////
	@Test(timeout = 1000)
	public void testActionCardExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(ActionCardPath));
	}

	@Test(timeout = 1000)
	public void testActionCardIsSubclassOfCardClass()
			throws ClassNotFoundException {
		testClassIsSubClass(Class.forName(ActionCardPath),
				Class.forName(cardPath));
	}

	// ///////////////////////////test give card////////////////////////////////
	@Test(timeout = 1000)
	public void testGiveCardExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(giveCardPath));
	}

	@Test(timeout = 1000)
	public void testGiveCardIsSubclassOfActionCardClass()
			throws ClassNotFoundException {
		testClassIsSubClass(Class.forName(giveCardPath),
				Class.forName(ActionCardPath));
	}

	// ////////////////////////////////Replica Card////////////////////////
	@Test(timeout = 1000)
	public void testReplicaCardExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(replicaCardPath));
	}

	@Test(timeout = 1000)
	public void testReplicaCardIsSubclassOfActionCardClass()
			throws ClassNotFoundException {
		testClassIsSubClass(Class.forName(replicaCardPath),
				Class.forName(ActionCardPath));
	}

	// ////////////////////////////MasterEyeCard/////////////////
	@Test(timeout = 1000)
	public void testMasterEyeCardExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(masterEyeCardPath));
	}

	@Test(timeout = 1000)
	public void testMasterEyeCardIsSubclassOfActionCardClass()
			throws ClassNotFoundException {
		testClassIsSubClass(Class.forName(masterEyeCardPath),
				Class.forName(ActionCardPath));
	}

	// ///////////////////////////SwapCard////////////////////////////////
	@Test(timeout = 1000)
	public void testSwapCardExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(swapCardPath));
	}

	@Test(timeout = 1000)
	public void testSwapCardIsSubclassOfActionCardClass()
			throws ClassNotFoundException {
		testClassIsSubClass(Class.forName(swapCardPath),
				Class.forName(ActionCardPath));
	}

	// ////////////////////// number card///////////////////////////////

	@Test(timeout = 1000)
	public void testNumberCardExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(numberCardPath));
	}

	@Test(timeout = 1000)
	public void testNumberCardIsSubclassOfCardClass()
			throws ClassNotFoundException {
		testClassIsSubClass(Class.forName(numberCardPath),
				Class.forName(cardPath));
	}

	@Test(timeout = 1000)
	public void testNumberCardInstanceVariables() throws NoSuchFieldException,
			SecurityException, ClassNotFoundException {

		testInstanceVariablesArePresent(Class.forName(numberCardPath),
				"number", true);
	}

	@Test(timeout = 1000)
	public void testNumberCardInstanceVariableTypeIsPrivate()
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		testInstanceVariableIsPrivate(Class.forName(numberCardPath), "number");
	}

	@Test(timeout = 1000)
	public void testNumberCardInstanceVariableTypeIsFinal()
			throws ClassNotFoundException {
		testInstanceVariableIsFinal(Class.forName(numberCardPath), "number");
	}

	@Test(timeout = 1000)
	public void testConstructorNumberCardWithInt()
			throws ClassNotFoundException {
		Class[] inputs = { int.class };
		testConstructorExists(Class.forName(numberCardPath), inputs);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableNumberInNumberCardSetter() throws Exception {
		testSetterMethodExistsInClass(Class.forName(numberCardPath),
				"setNumber", int.class, false);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableNumberInNumberCardGetter() throws Exception {
		testGetterMethodExistsInClass(Class.forName(numberCardPath),
				"getNumber", int.class);
	}

	// ///////////////////////////self revealer class////////////////////////
	@Test(timeout = 1000)
	public void testSelfRevealerCardExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(selfRevealerCardPath));
	}

	@Test(timeout = 1000)
	public void testSelfRevealerCardIsSubclassOfActionCardClass()
			throws ClassNotFoundException {
		testClassIsSubClass(Class.forName(selfRevealerCardPath),
				Class.forName(numberCardPath));
	}

	@Test(timeout = 1000)
	public void testSelfRevealerCardClassImplementsSpecialCardInterface() {
		try {
			testClassImplementsInterface(Class.forName(selfRevealerCardPath),
					Class.forName(specialCarInterfacePath));
		} catch (ClassNotFoundException e) {
			assertTrue(e.getClass().getName() + " occurred: " + e.getMessage(),
					false);
		}
	}

	// ///////////////////////////others revealer class////////////////////////
	@Test(timeout = 1000)
	public void testOthersRevealerCardClassImplementsSpecialCardInterface() {
		try {
			testClassImplementsInterface(Class.forName(othersRevealerCardPath),
					Class.forName(specialCarInterfacePath));
		} catch (ClassNotFoundException e) {
			assertTrue(e.getClass().getName() + " occurred: " + e.getMessage(),
					false);
		}
	}

	@Test(timeout = 1000)
	public void testOthersRevealerCardExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(othersRevealerCardPath));
	}

	@Test(timeout = 1000)
	public void testOthersRevealerCardIsSubclassOfActionCardClass()
			throws ClassNotFoundException {
		testClassIsSubClass(Class.forName(othersRevealerCardPath),
				Class.forName(numberCardPath));
	}

	// ////////////////// CannotRevealException ///////////////
	@Test(timeout = 1000)
	public void testCannotRevealExceptionExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(cannotRevealExceptionPath));
	}

	@Test(timeout = 1000)
	public void testCannotRevealCardIsSubclassOfExceptionClass()
			throws ClassNotFoundException {
		testClassIsSubClass(Class.forName(cannotRevealExceptionPath),
				Class.forName("java.lang.Exception"));
	}

	@Test(timeout = 1000)
	public void testConstructorCannotRevealExceptionEmpty()
			throws ClassNotFoundException {
		Class[] inputs = {};
		testConstructorExists(Class.forName(cannotRevealExceptionPath), inputs);
	}

	@Test(timeout = 1000)
	public void testConstructorCannotRevealExceptionWithString()
			throws ClassNotFoundException {
		Class[] inputs = { String.class };
		testConstructorExists(Class.forName(cannotRevealExceptionPath), inputs);
	}

	// ////////////////// CannotSkruException ///////////////
	@Test(timeout = 1000)
	public void testCannotSkruExceptionExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(cannotSkruExceptionPath));
	}

	@Test(timeout = 1000)
	public void testCannotSkruCardIsSubclassOfExceptionClass()
			throws ClassNotFoundException {
		testClassIsSubClass(Class.forName(cannotSkruExceptionPath),
				Class.forName("java.lang.Exception"));
	}

	@Test(timeout = 1000)
	public void testConstructorCannotSkruExceptionEmpty()
			throws ClassNotFoundException {
		Class[] inputs = {};
		testConstructorExists(Class.forName(cannotSkruExceptionPath), inputs);
	}

	@Test(timeout = 1000)
	public void testConstructorCannotSkruExceptionWithString()
			throws ClassNotFoundException {
		Class[] inputs = { String.class };
		testConstructorExists(Class.forName(cannotSkruExceptionPath), inputs);
	}

	// ////////////////// CardActionException ///////////////
	@Test(timeout = 1000)
	public void testCardActionExceptionExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(cardActionExceptionPath));
	}

	@Test(timeout = 1000)
	public void testCardActionExceptionIsSubclassOfExceptionClass()
			throws ClassNotFoundException {
		testClassIsSubClass(Class.forName(cardActionExceptionPath),
				Class.forName("java.lang.Exception"));
	}

	@Test(timeout = 1000)
	public void testConstructorCardActionExceptionEmpty()
			throws ClassNotFoundException {
		Class[] inputs = {};
		testConstructorExists(Class.forName(cardActionExceptionPath), inputs);
	}

	@Test(timeout = 1000)
	public void testConstructorCardActionExceptionWithString()
			throws ClassNotFoundException {
		Class[] inputs = { String.class };
		testConstructorExists(Class.forName(cardActionExceptionPath), inputs);
	}

	// /////////////////player class //////////////////////////////
	@Test(timeout = 1000)
	public void testPlayerExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(playerPath));
	}

	@Test(timeout = 1000)
	public void testPlayerInstanceVariables() throws NoSuchFieldException,
			SecurityException, ClassNotFoundException {

		testInstanceVariablesArePresent(Class.forName(playerPath), "name", true);
		testInstanceVariablesArePresent(Class.forName(playerPath), "hand", true);
		testInstanceVariablesArePresent(Class.forName(playerPath),
				"currenrTurnTotal", true);
	}

	@Test(timeout = 1000)
	public void testConstructorPlayerWithString() throws ClassNotFoundException {
		Class[] inputs = { String.class };
		testConstructorExists(Class.forName(playerPath), inputs);
	}

	@SuppressWarnings("unchecked")
	@Test(timeout = 1000)
	public void tesPlayerConstrucotLogic() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {
		Class aClass = Class.forName(playerPath);

		Constructor<?> c = Class.forName(playerPath).getConstructor(
				String.class);

		Object p = c.newInstance("p1");

		Field hand = Class.forName(playerPath).getDeclaredField("hand");
		Field name = Class.forName(playerPath).getDeclaredField("name");
		name.setAccessible(true);
		hand.setAccessible(true);

		if (name.get(p) == null) {
			fail("The player constructor should intialize the name attribute to be the iput name not null");
		}

		if (((ArrayList<Object>) hand.get(p)) == null) {
			fail("The Player constructor should intialize the hand attribute to be an empty arraylist an not null");
		}

	}

	@Test(timeout = 1000)
	public void testInstanceVariableNameInPlayerGetter() throws Exception {
		testGetterMethodExistsInClass(Class.forName(playerPath), "getName",
				String.class);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableNameInPlayerSetter() throws Exception {
		testSetterMethodExistsInClass(Class.forName(playerPath), "setName",
				boolean.class, false);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableCurrenrTurnTotalInPlayerGetter()
			throws Exception {
		testGetterMethodExistsInClass(Class.forName(playerPath),
				"getCurrenrTurnTotal", int.class);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableCurrenrTurnTotalInPlayerSetter()
			throws Exception {
		testSetterMethodExistsInClass(Class.forName(playerPath),
				"setCurrenrTurnTotal", int.class, true);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableHandInPlayerGetter() throws Exception {
		testGetterMethodExistsInClass(Class.forName(playerPath), "getHand",
				ArrayList.class);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableHandInPlayerSetter() throws Exception {
		testSetterMethodExistsInClass(Class.forName(playerPath), "setHand",
				boolean.class, false);
	}

	// ///////////////Game class ///////////////////////////////
	@Test(timeout = 1000)
	public void testGameExisit() throws ClassNotFoundException {
		assertNotNull(Class.forName(gamePath));
	}

	@Test(timeout = 1000)
	public void testGameInstanceVariables() throws NoSuchFieldException,
			SecurityException, ClassNotFoundException {

		testInstanceVariablesArePresent(Class.forName(gamePath),
				"currentRound", true);
		testInstanceVariablesArePresent(Class.forName(gamePath),
				"currentPlayer", true);
		testInstanceVariablesArePresent(Class.forName(gamePath), "totalScores",
				true);
		testInstanceVariablesArePresent(Class.forName(gamePath), "players",
				true);
		testInstanceVariablesArePresent(Class.forName(gamePath), "deck", true);
		testInstanceVariablesArePresent(Class.forName(gamePath), "discardDeck",
				true);
		testInstanceVariablesArePresent(Class.forName(gamePath),
				"MAX_NUMBER_OF_ROUNDS", true);
		testInstanceVariablesArePresent(Class.forName(gamePath),
				"MAX_NUMBER_OF_PLAYERS", true);
	}

	@Test(timeout = 1000)
	public void testGameMAX_NUMBER_OF_ROUNDSisStatic()
			throws ClassNotFoundException {
		testInstanceVariableIsStatic(Class.forName(gamePath),
				"MAX_NUMBER_OF_ROUNDS");
	}

	@Test(timeout = 1000)
	public void testGameMAX_NUMBER_OF_PLAYERSSisStatic()
			throws ClassNotFoundException {
		testInstanceVariableIsStatic(Class.forName(gamePath),
				"MAX_NUMBER_OF_PLAYERS");
	}

	@Test(timeout = 1000)
	public void testInstanceVariableMAX_NUMBER_OF_ROUNDSInGameIsFinal()
			throws Exception {
		assertTrue(
				"the MAX_NUMBER_OF_ROUNDS attribute is Final",
				Modifier.isFinal(Class.forName(gamePath)
						.getDeclaredField("MAX_NUMBER_OF_ROUNDS")
						.getModifiers()));
	}

	@Test(timeout = 1000)
	public void testInstanceVariableMAX_NUMBER_OF_PLAYERSInGameIsFinal()
			throws Exception {
		assertTrue(
				"the MAX_NUMBER_OF_PLAYERS attribute is Final",
				Modifier.isFinal(Class.forName(gamePath)
						.getDeclaredField("MAX_NUMBER_OF_PLAYERS")
						.getModifiers()));
	}

	@Test(timeout = 1000)
	public void testGameInstanceVariableCurrentRoundIsPrivate()
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		testInstanceVariableIsPrivate(Class.forName(gamePath), "currentRound");
	}

	@Test(timeout = 1000)
	public void testGameInstanceVariableCurrentPlayerIsPrivate()
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		testInstanceVariableIsPrivate(Class.forName(gamePath), "currentPlayer");
	}

	@Test(timeout = 1000)
	public void testGameInstanceVariableTotalScoresIsPrivate()
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		testInstanceVariableIsPrivate(Class.forName(gamePath), "totalScores");
	}

	@Test(timeout = 1000)
	public void testGameInstanceVariablePlayersIsPrivate()
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		testInstanceVariableIsPrivate(Class.forName(gamePath), "players");
	}

	@Test(timeout = 1000)
	public void testGameInstanceVariableDeckIsPrivate()
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		testInstanceVariableIsPrivate(Class.forName(gamePath), "deck");
	}

	@Test(timeout = 1000)
	public void testGameInstanceVariableDiscardDeckIsPrivate()
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		testInstanceVariableIsPrivate(Class.forName(gamePath), "discardDeck");
	}

	@Test(timeout = 1000)
	public void testInstanceVariableCurrentRoundInGameGetter() throws Exception {
		testGetterMethodExistsInClass(Class.forName(gamePath),
				"getCurrentRound", int.class);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableCurrentRoundInGameSetter() throws Exception {
		testSetterMethodExistsInClass(Class.forName(gamePath),
				"setCurrentRound", int.class, true);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableCurrentPlayerInGameGetter()
			throws Exception {
		testGetterMethodExistsInClass(Class.forName(gamePath),
				"getCurrentPlayer", Class.forName(playerPath));
	}

	@Test(timeout = 1000)
	public void testInstanceVariableCurrentPlayerInGameSetter()
			throws Exception {
		testSetterMethodExistsInClass(Class.forName(gamePath),
				"setCurrentPlayer", Class.forName(playerPath), true);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableTotalScoresInGameGetter() throws Exception {
		testGetterMethodExistsInClass(Class.forName(gamePath),
				"getTotalScores", int[][].class);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableTotalScoresInGameSetter() throws Exception {
		testSetterMethodExistsInClass(Class.forName(gamePath),
				"setTotalScores", int[][].class, false);
	}

	@Test(timeout = 1000)
	public void testInstanceVariablePlayersInGameGetter() throws Exception {
		testGetterMethodExistsInClass(Class.forName(gamePath), "getPlayers",
				ArrayList.class);
	}

	@Test(timeout = 1000)
	public void testInstanceVariablePlayersInGameSetter() throws Exception {
		testSetterMethodExistsInClass(Class.forName(gamePath), "setPlayers",
				ArrayList.class, false);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableDeckInGameGetter() throws Exception {
		testGetterMethodExistsInClass(Class.forName(gamePath), "getDeck",
				ArrayList.class);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableDeckInGameSetter() throws Exception {
		testSetterMethodExistsInClass(Class.forName(gamePath), "setDeck",
				ArrayList.class, true);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableDiscardDeckInGameGetter() throws Exception {
		testGetterMethodExistsInClass(Class.forName(gamePath),
				"getDiscardDeck", ArrayList.class);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableDiscardDeckInGameSetter() throws Exception {
		testSetterMethodExistsInClass(Class.forName(gamePath),
				"setDiscardDeck", ArrayList.class, true);
	}

	// ////////generate method/////

	@Test(timeout = 1000)
	public void testGenerateDeckMethodExisit() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException {
		Class<?> clazz = Class.forName(gamePath);
		Method method = clazz.getDeclaredMethod("generateDeck");
		assertNotNull("The method generateDeck() should exist in class Game",
				method);
		assertEquals(
				"The return type of generateDeck() in Game class should be ArryList<Card>",
				ArrayList.class, method.getReturnType());

	}

	@Test(timeout = 1000)
	public void testGenerateDeckMethodTotalNumberCards()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);
		Class<?> nc = Class.forName(numberCardPath);
		nc.getTypeName();
		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Method m1 = scr.getClass().getDeclaredMethod("generateDeck");
		m1.setAccessible(true);
		ArrayList<Object> o2 = (ArrayList<Object>) m1.invoke(o);
		int count = 0;
		for (Object object : o2) {
			if (object.getClass().getTypeName() == nc.getTypeName())
				count++;
		}

		assertEquals(
				"The deck should contain 32 Number cards as follows : \n 8 x \"0\" cards\n"
						+ "4 x \"1\" cards\n" + "4 x \"2\" cards\n"
						+ "4 x \"3\" cards\n" + "4 x \"4\" cards\n"
						+ "4 x \"5\" cards\n" + "4 x \"6\" cards\n ", 32, count);
	}

	@Test(timeout = 1000)
	public void testGenerateDeckMethodTotalGiveCards()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);
		Class<?> nc = Class.forName(giveCardPath);
		nc.getTypeName();
		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Method m1 = scr.getClass().getDeclaredMethod("generateDeck");
		m1.setAccessible(true);
		ArrayList<Object> o2 = (ArrayList<Object>) m1.invoke(o);
		int count = 0;
		for (Object object : o2) {
			if (object.getClass().getTypeName() == nc.getTypeName())
				count++;
		}

		assertEquals("The deck should contain 4 Give Cards", 4, count);

	}

	@Test(timeout = 1000)
	public void testGenerateDeckMethodTotalMasterEyeCards()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);
		Class<?> nc = Class.forName(masterEyeCardPath);
		nc.getTypeName();
		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Method m1 = scr.getClass().getDeclaredMethod("generateDeck");
		m1.setAccessible(true);
		ArrayList<Object> o2 = (ArrayList<Object>) m1.invoke(o);
		int count = 0;
		for (Object object : o2) {
			if (object.getClass().getTypeName() == nc.getTypeName())
				count++;
		}

		assertEquals("The deck should contain 4 MasterEye Cards", 4, count);
	}

	@Test(timeout = 1000)
	public void testGenerateDeckMethodTotalReplicaCards()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);
		Class<?> nc = Class.forName(replicaCardPath);
		nc.getTypeName();
		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Method m1 = scr.getClass().getDeclaredMethod("generateDeck");
		m1.setAccessible(true);
		ArrayList<Object> o2 = (ArrayList<Object>) m1.invoke(o);
		int count = 0;
		for (Object object : o2) {
			if (object.getClass().getTypeName() == nc.getTypeName())
				count++;
		}

		assertEquals("The deck should contain 8 replica Cards", 8, count);
	}

	@Test(timeout = 1000)
	public void testGenerateDeckMethodTotalCards()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);
		Class<?> nc = Class.forName(replicaCardPath);
		nc.getTypeName();
		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Method m1 = scr.getClass().getDeclaredMethod("generateDeck");
		m1.setAccessible(true);
		ArrayList<Object> o2 = (ArrayList<Object>) m1.invoke(o);
		assertEquals("The deck should contain 70 cards in total ", 70,
				o2.size());
	}

	@Test(timeout = 1000)
	public void testGenerateDeckMethodTotalSelfRevealerCards()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);
		Class<?> nc = Class.forName(selfRevealerCardPath);
		nc.getTypeName();
		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Method m1 = scr.getClass().getDeclaredMethod("generateDeck");
		m1.setAccessible(true);
		ArrayList<Object> o2 = (ArrayList<Object>) m1.invoke(o);
		int count = 0;
		for (Object object : o2) {
			if (object.getClass().getTypeName() == nc.getTypeName())
				count++;
		}

		assertEquals(
				"The deck should contain 6 self revealer Cards 3x '7' and 3x '8' ",
				6, count);
	}

	@Test(timeout = 1000)
	public void testGenerateDeckMethodTotalOthersRevealerCards()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);
		Class<?> nc = Class.forName(othersRevealerCardPath);
		nc.getTypeName();
		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Method m1 = scr.getClass().getDeclaredMethod("generateDeck");
		m1.setAccessible(true);
		ArrayList<Object> o2 = (ArrayList<Object>) m1.invoke(o);
		int count = 0;
		for (Object object : o2) {
			if (object.getClass().getTypeName() == nc.getTypeName())
				count++;
		}

		assertEquals(
				"The deck should contain 4 self revealer Cards 2x '9' and 2x '10' ",
				4, count);

	}

	@Test(timeout = 1000)
	public void testGenerateDeckMethodTotalGreenSkruCards()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);
		Class<?> nc = Class.forName(skruCardPath);
		nc.getTypeName();
		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Method m1 = scr.getClass().getDeclaredMethod("generateDeck");
		m1.setAccessible(true);
		ArrayList<Object> o2 = (ArrayList<Object>) m1.invoke(o);
		int count = 0;
		Object color2 = Enum.valueOf((Class<Enum>) Class.forName(skruTypePath),
				"GREEN");
		for (Object object : o2) {
			if (object.getClass().getTypeName() == nc.getTypeName())
				count++;
		}

		assertEquals(
				"The deck should contain 8 skru cards 4 GREEN SkruCards and 4 RED skruCard ",
				8, count);

	}

	@Test(timeout = 1000)
	public void testGenerateDeckMethodShuffleCards()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);
		Class<?> nc = Class.forName(skruCardPath);
		nc.getTypeName();
		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Method m1 = scr.getClass().getDeclaredMethod("generateDeck");
		m1.setAccessible(true);
		ArrayList<Object> o2 = (ArrayList<Object>) m1.invoke(o);
		ArrayList<Object> o3 = (ArrayList<Object>) m1.invoke(o);
		ArrayList<Object> o4 = (ArrayList<Object>) m1.invoke(o);

		int count = 0;

		for (int i = 0; i < 20; i++) {
			if (o2.get(i).getClass().getTypeName() == o3.get(i).getClass()
					.getTypeName()
					&& (o4.get(i).getClass().getTypeName() == o3.get(i)
							.getClass().getTypeName())) {
				count++;
			}

		}
		if (count >= 10)
			fail("the generateDeck method should shuffle the deck after creation \n you can use the Collections.shuffle() method check it out :) ");

	}

	// ///// game constructor /////
	@Test(timeout = 1000)
	public void testConstructorGameClass() throws ClassNotFoundException {
		Class[] inputs = { ArrayList.class };
		testConstructorExists(Class.forName(gamePath), inputs);
	}

	@Test(timeout = 1000)
	public void testConstructorGameClassLogicPlayersAdded()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {

		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Field pf = Class.forName(gamePath).getDeclaredField("players");
		pf.setAccessible(true);
		ArrayList<Object> players = (ArrayList<Object>) pf.get(scr);

		if (players == null || players.size() == 0)
			fail("the Game constructor should add the input players to the players arraylist ");

		assertEquals(
				"The Game Constructor should add the given players to the players Arraylist ",
				4, players.size());

	}

	@Test(timeout = 1000)
	public void testConstructorGameClassLogicEachPlayerHas4Cards()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {

		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Field pf = Class.forName(gamePath).getDeclaredField("players");
		Field df = Class.forName(gamePath).getDeclaredField("deck");
		pf.setAccessible(true);
		df.setAccessible(true);

		Field hf = Class.forName(playerPath).getDeclaredField("hand");
		hf.setAccessible(true);

		ArrayList<Object> players = (ArrayList<Object>) pf.get(scr);
		ArrayList<Object> deck = (ArrayList<Object>) df.get(scr);
		if (deck == null || deck.size() == 0)
			fail("the Game constructor should call the generateDeck method to initalize the deck ");

		if (players == null || players.size() == 0)
			fail("the Game constructor should add the input players to the players arraylist ");

		for (Object object : players) {
			ArrayList<Object> hand = (ArrayList<Object>) hf.get(object);
			assertEquals(
					"The Game Constructor should distrbute 4 cards to each player",
					4, hand.size());
		}

	}

	@Test(timeout = 1000)
	public void testConstructorGameClassLogicDeckGenerated()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {

		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Field pf = Class.forName(gamePath).getDeclaredField("players");

		Field df = Class.forName(gamePath).getDeclaredField("deck");
		df.setAccessible(true);
		ArrayList<Object> deck = (ArrayList<Object>) df.get(scr);

		if (deck == null || deck.size() == 0)
			fail("the Game constructor should call the generateDeck method to initalize the deck ");

	}

	@Test(timeout = 1000)
	public void testGameConstructorIntializeCurrentRound()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {

		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Field df = Class.forName(gamePath).getDeclaredField("currentRound");
		df.setAccessible(true);

		int currentRound = (int) df.get(scr);
		assertEquals("the Game constructor should set the current round to 1 ", 1 , currentRound);

	}

	@Test(timeout = 1000)
	public void testGameConstructorIntializeCurrentPlayer()
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {

		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Field df = Class.forName(gamePath).getDeclaredField("currentPlayer");
		Field nf = Class.forName(playerPath).getDeclaredField("name");
		nf.setAccessible(true);
		df.setAccessible(true);

		Object currentPlayer =  df.get(scr);
		if(currentPlayer==null)
			fail("The game constructor should intialize the current player to be the 1st player in the players arraylist");
		else{
			String name = (String) nf.get(currentPlayer);
			assertEquals("The game constructor should intialize the current player to be the 1st player in the players arraylist", "p1" , name);

		}

	}

	@Test(timeout = 1000)
	public void testGameConstructorIntializeDiscardDeck() 
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {

		Constructor<?> pc = Class.forName(playerPath).getConstructor(
				String.class);
		Constructor<?> c = Class.forName(gamePath).getConstructor(
				ArrayList.class);

		Object p1 = pc.newInstance("p1");
		Object p2 = pc.newInstance("p2");
		Object p3 = pc.newInstance("p3");
		Object p4 = pc.newInstance("p4");

		ArrayList<Object> o = new ArrayList<Object>();
		o.add(p1);
		o.add(p2);
		o.add(p3);
		o.add(p4);

		Object scr = c.newInstance(o);
		Field df = Class.forName(gamePath).getDeclaredField("discardDeck");
		Field nf = Class.forName(playerPath).getDeclaredField("name");
		nf.setAccessible(true);
		df.setAccessible(true);

		ArrayList<Object> discardDeck =  (ArrayList<Object>) df.get(scr);
		if(discardDeck==null)
			fail("The game constructor should intialize thediscardDeck to have 1 card visible but the discardDeck was NULL");
		else{
			
			assertEquals("The game constructor should intialize thediscardDeck to have 1 card visible", 1 , discardDeck.size());
			
		}

	}

	// /////////////////////////////////////////////////////////

}
