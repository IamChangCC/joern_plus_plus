package tools.fcg;

import org.apache.commons.cli.ParseException;
import org.neo4j.graphdb.Node;

import neo4j.batchInserter.Neo4JBatchInserter;
import neo4j.readWriteDB.Neo4JDBInterface;
import neo4j.traversals.readWriteDB.Traversals;
import outputModules.neo4j.Neo4JIndexer;
import tools.argumentTainter.CommandLineInterface;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.IndexHits;

import databaseNodes.EdgeTypes;
import ddg.DDGCreator;
import ddg.DataDependenceGraph.DDG;
import ddg.DataDependenceGraph.DDGDifference;
import ddg.DataDependenceGraph.DefUseRelation;
import ddg.DefUseCFG.DefUseCFG;
import org.junit.AfterClass;
import org.junit.BeforeClass;
// Determine functions to patch and hand over
// individual functions to FunctionPatcher

public class AddFCGMain
{
	static CommandLineInterface cmd = new CommandLineInterface();
	//private static Indexer indexer = new Neo4JIndexer();
	static String databaseDir;
	public static void main(String[] args)
	{
		databaseDir= cmd.getDatabaseDir();
		//Neo4JBatchInserter.setIndexDirectoryName(databaseDir);
		Neo4JBatchInserter.openDatabase();
		String current_fun_name;
		AddFCG addfcg = new AddFCG();
		System.out.println(databaseDir);
		addfcg.initialize(databaseDir);
		String function_name = "*";
		addfcg.get_FunctionIdbyName(function_name);
		for (Long fun_id :addfcg.funtion_id_arry){
			current_fun_name = addfcg.getFuntionNamebyId(fun_id);
			
		}
		Neo4JBatchInserter.closeDatabase();
		addfcg.shutdown();
	}
	
	/*public static IndexHits<Node> get_calleebyName(String function_name){
		String query = "type:Callee AND code:" + function_name;
		IndexHits<Node> hits = Neo4JDBInterface.queryIndex(query);
	}*/
	public static void add_FunctionCall_edge(Long fun_id,String fun_name ){
		String query = "type:Callee AND code:" + fun_name;
		IndexHits<Node> hits = Neo4JDBInterface.queryIndex(query);
		for (Node CalleeNode : hits){
			linkFunctionToCalleeNode(fun_id,	CalleeNode.getId());
			}	
		}
	
	public static void linkFunctionToCalleeNode(long functionId,long CalleeNodeId)
	{
		RelationshipType rel = DynamicRelationshipType
				.withName(EdgeTypes.FUNCTION_CALL);
		Neo4JBatchInserter.addRelationship( functionId,CalleeNodeId, rel, null);			
			/*
			List<Long> arguments = getArgumentsByCallId(CalleeNodeId);
			if (arguments == null)
				return;

			IndexHits<Long> dstIds = resolver.resolveByCallId(callNodeId);
			if (dstIds == null)
				return;

			connectArgumentsToDestinations(callNodeId, arguments, dstIds);*/
		}
}
