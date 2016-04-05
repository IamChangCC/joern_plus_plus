package tools.fcg;


import java.util.List;
import java.util.ArrayList;
//import org.neo4j.graphdb.Node;
import neo4j.readWriteDB.Neo4JDBInterface;
import neo4j.traversals.readWriteDB.Traversals;
import neo4j.batchInserter.Neo4JBatchInserter;
//import org.neo4j.graphdb.Direction;
//import org.neo4j.graphdb.DynamicRelationshipType;
//import org.neo4j.graphdb.Relationship;
//import org.neo4j.graphdb.RelationshipType;

//import databaseNodes.EdgeTypes;
//import ddg.DDGCreator;
//import ddg.DataDependenceGraph.DDG;
//import ddg.DataDependenceGraph.DDGDifference;
//import ddg.DataDependenceGraph.DefUseRelation;
//import ddg.DefUseCFG.DefUseCFG;
import neo4j.traversals.batchInserter.Function;
import org.neo4j.graphdb.index.IndexHits;
// Determine functions to patch and hand over
// individual functions to FunctionPatcher

public class AddFCG
{
	static String[] function_name_arry;
	public List<Long> funtion_id_arry=new  ArrayList<Long>();
	//cc added at 20160303
	Function function = new Function();
	//private static ReadWriteDBProvider readwritedbprovider=new ReadWriteDBProvider();
	public static  Traversals traversals = new Traversals();
	public void initialize(String databaseDir)
	{
		System.out.println(databaseDir);
		Neo4JDBInterface.setDatabaseDir(databaseDir);
		Neo4JDBInterface.openDatabase();
		System.out.println("openDatabase successfully!");
		//for(Long i:)
		//getNodeById
	}	
	

	//get the name according to function id
	public String getFuntionNamebyId(Long func_id)
	{
		String function_name;
		try{
			function_name = Traversals.getFunctionNamefromId(func_id);
		}
		catch(Exception ex){
			System.out.println(func_id.toString()+":  This function has not found the decleration!");
			return null;
			}
		return function_name;
		}
		
	

	
	

	public void shutdown()
	{
		Neo4JDBInterface.closeDatabase();
	}
	
	//get all function Ids
	public void get_FunctionIdbyName(String fun_name) {
		// TODO Auto-generated method stub
		System.out.println("In the function of get_FunctionIdbyName:"+fun_name);
		 //hits ;
		//try{
		System.out.println("In the try");
		IndexHits<Long> hits = Function.getFunctionsByName(fun_name);
		System.out.println(hits.size());
		//IndexHits<Long> hits = Function.getFunctionsByName(fun_name);
		/*}
		catch(Exception ex){
			System.err.println(ex.fillInStackTrace());
			return;
		}		
				
		if(hits.size()==0)
		{
			System.err.println("There is no functions! ");
		}*/
		for (Long FunctionId : hits){				
			funtion_id_arry.add(FunctionId);
			//String function_name=Traversals.getFunctionNamefromId(FunctionId); 
			System.out.println(FunctionId.toString());
		}
	}

}
