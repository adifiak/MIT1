package hu.bme.mit.yakindu.analysis.workhere;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.base.types.Event;
import org.yakindu.base.types.Property;
import org.yakindu.sct.model.sgraph.Scope;
import org.yakindu.sct.model.sgraph.Statechart;

import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		EList<Scope> scopes = s.getScopes();
		EList<Event> events = null;
		EList<Property> variables = null;
		
		//Extract every event and variable
		for(Scope scope : scopes) {
			if(events == null) {
				events = scope.getEvents();
			} else {
				events.addAll(scope.getEvents());
			}
			
			if(variables == null) {
				variables = scope.getVariables();
			} else {
				variables.addAll(scope.getVariables());
			}
		}
		
		//Top of file
		System.out.println(
				"package hu.bme.mit.yakindu.analysis.workhere;\r\n" + 
				"\r\n" + 
				"import java.io.IOException;\r\n" + 
				"import java.util.Scanner;\r\n" + 
				"\r\n" + 
				"import hu.bme.mit.yakindu.analysis.RuntimeService;\r\n" + 
				"import hu.bme.mit.yakindu.analysis.TimerService;\r\n" + 
				"import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;\r\n" + 
				"import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;\r\n" + 
				"\r\n" + 
				"public class CodeGenerator {\r\n" + 
				"	\r\n" + 
				"	public static void main(String[] args) throws IOException {\r\n" + 
				"		ExampleStatemachine s = new ExampleStatemachine();\r\n" + 
				"		s.setTimer(new TimerService());\r\n" + 
				"		RuntimeService.getInstance().registerStatemachine(s, 200);		\r\n\r\n" + 
				"		s.init();\r\n" + 
				"		s.enter();\r\n" + 
				"		s.runCycle();\r\n" + 
				"		print(s);\r\n" + 
				"		\r\n" + 
				"		Scanner sc = new Scanner(System.in);\r\n" + 
				"		while(true) {\r\n" + 
				"			System.out.println(\"Enter event:\");\r\n" + 
				"			\r\n" + 
				"			String event = sc.nextLine();\r\n" + 
				"			\r\n" + 
				"			switch(event) {");
		//Input processing
		for(Event event : events) {
			String originalName = event.getName();
			String nameToUse = capitalizeFirstChar(originalName);
			System.out.println(
				"				case \"" + originalName + "\":\r\n" + 
				"					s.raise" + nameToUse + "();\r\n" + 
				"					s.runCycle();\r\n" + 
				"					break;");	
		}
		//End of main loop
		System.out.println(
				"				case \"exit\":\r\n" + 
				"					System.exit(0);\r\n" + 
				"					break;\r\n" +
				"			}\r\n" + 
				"			print(s);\r\n" + 
				"		}\r\n" + 
				"	}\r\n");
		//Print function header
		System.out.println(
				"	public static void print(IExampleStatemachine s) {");
		//Print function body
		for(Property variable : variables) {
			String symbolOfVariable = variable.getName().substring(0, 1).toUpperCase();
			String nameToUse = capitalizeFirstChar(variable.getName());
			System.out.println(
					"		System.out.println(\"" + symbolOfVariable + " = \" + s.getSCInterface().get" + nameToUse + "());");
		}
		//Ending of open parts
		System.out.println(
				"	}\r\n" +
				"}");
	}
	
	private static boolean checkName(String nameToTest) {
		ModelManager manager = new ModelManager();
		
		EObject root = manager.loadModel("model_input/example.sct");
		
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				if(state.getName().equals(nameToTest)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private static String capitalizeFirstChar(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	
	private static void hardCodedCodeGenerator() {
		//Header
		System.out.println("public static void print(IExampleStatemachine s) {");
		
		//Variables
		System.out.println("System.out.println(\"W = \" + s.getSCInterface().getWhiteTime())");
		System.out.println("System.out.println(\"B = \" + s.getSCInterface().getBlackTime())");
		
		//Events
		System.out.println("System.out.println(\"start\")");
		System.out.println("System.out.println(\"white\")");
		System.out.println("System.out.println(\"black\")");
		
		//Ending bracket
		System.out.println("}");
	}
}
