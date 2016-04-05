package tools.icfg;

import neo4j.batchInserter.ImportedNodeListener;
import neo4j.batchInserter.ImportedNodeWalker;

import org.apache.commons.cli.ParseException;

import tools.GraphDbWalker;

public class ICFGMain extends GraphDbWalker
{

	static ImportedNodeWalker walker = new ImportedNodeWalker();
	static ICFGListener listener = new ICFGListener();

	private static ICFGCommandLineInterface cmd = new ICFGCommandLineInterface();

	public static void main(String[] args)
	{
		parseCommandLine(args);
		//System.out.println(cmd.getDatabaseDir());
		setDatabaseDirectory(cmd.getDatabaseDir());
		//System.out.println("all was ok1");
		initializeDatabase();
		System.out.println("all was ok2");
		walker.setType("CallExpression");
		walker.addListener(listener);
		walker.walk();
		//System.out.println("all was ok");
		//listener.visitNode(Long.valueOf(22));
		 //ICFGListener.
		shutdownDatabase();
	}

	private static void parseCommandLine(String[] args)
	{
		try
		{
			cmd.parseCommandLine(args);
		}
		catch (RuntimeException | ParseException ex)
		{
			System.out.println("has some exception");
			printHelpAndTerminate(ex);
		}
	}

	private static void printHelpAndTerminate(Exception ex)
	{
		System.err.println(ex.getMessage());
		cmd.printHelp();
		System.exit(1);
	}

}
