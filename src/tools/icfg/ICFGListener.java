package tools.icfg;

import java.util.LinkedList;
import java.util.List;

import neo4j.batchInserter.ImportedNodeListener;
import neo4j.batchInserter.Neo4JBatchInserter;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.unsafe.batchinsert.BatchRelationship;

import databaseNodes.EdgeTypes;

public class ICFGListener extends ImportedNodeListener
{

	CallResolver resolver = new CallResolver();

	@Override
	public void visitNode(Long callNodeId)
	{
		System.out.println("In the function of visitNode! the callNodeId is"+callNodeId.toString());
		List<Long> arguments = getArgumentsByCallId(callNodeId);
		for(Long arguments_id :arguments){
			System.out.println("The arguments_id is "+arguments_id.toString());
		}
			
		if (arguments == null)
			return;

		IndexHits<Long> dstIds = resolver.resolveByCallId(callNodeId);
		//for(Long dstIds_id :dstIds){
		//	System.out.println("The dstIds_id is "+dstIds_id.toString());
		//}
			
		if (dstIds == null)
			return;

		connectArgumentsToDestinations(callNodeId, arguments, dstIds);

	}

	private List<Long> getArgumentsByCallId(Long callNodeId)
	{
		System.out.println("In the function of getArgumentsByCallId!");
		long argumentListId = getArgumentListByCallId(callNodeId);
		if (argumentListId == -1)
			return null;

		return getArgsFromArgList(argumentListId);
	}
	// cc added at 0405
	private void connectCalleeToDestinations(Long callNodeId, Long dstIds)	{
		RelationshipType rel = DynamicRelationshipType.withName(EdgeTypes.FUNCTION_CALL);
		Neo4JBatchInserter.addRelationship(callNodeId,dstIds, rel, null);
		}
	private void connectArgumentsToDestinations(Long callNodeId,
			List<Long> arguments, IndexHits<Long> dstIds)
	{
		System.out.println("In  connectArgumentsToDestinations! callNodeId is "+callNodeId.toString());
		//+"callNodeId is "+callNodeId.toString()+callNodeId is "+callNodeId.toString());
		for (Long dst : dstIds)
		{		
			connectCalleeToDestinations(callNodeId, dst);
			List<Long> parameters = getParametersByFunctionId(dst);
			for (Long para_id : parameters)			{
				System.out.println("The parameters has "+para_id.toString());
			}
			if (parameters == null)
				continue;
			addArgParamEdges(callNodeId, arguments, parameters);
			
		}
	}

	private List<Long> getParametersByFunctionId(Long dst)
	{
		System.out.println("In the function of getParametersByFunctionId!");
		String query = "type:ParameterList AND functionId:" + dst;
		IndexHits<Long> hits = Neo4JBatchInserter.queryIndex(query);

		if (hits == null)
			return null;

		if (hits.size() != 1)
			throw (new RuntimeException(
					"Warning: Parameterlist not found or more than one."));

		Long parameterListId = hits.next();
		List<Long> params = getParametersFromList(parameterListId);
		return getIdentifiersFromParams(params);
	}

	private void addArgParamEdges(Long callNodeId, List<Long> arguments,
			List<Long> parameters)
	{
		System.out.println("In the function of addArgParamEdges!");
		if (parameters.size() != arguments.size())
			return;

		RelationshipType rel = DynamicRelationshipType
				.withName(EdgeTypes.IS_ARG);
		for (int i = 0; i < arguments.size(); i++)
			Neo4JBatchInserter.addRelationship(arguments.get(i),
					parameters.get(i), rel, null);

	}

	private List<Long> getArgsFromArgList(long argumentListId)
	{
		System.out.println("In the function of getArgsFromArgList!");
		return getChildrenByNodeId(argumentListId);
	}

	private long getArgumentListByCallId(Long callNodeId)
	{
		System.out.println("In the function of getArgumentListByCallId! and the callNodeId is"+ callNodeId.toString());
		Iterable<BatchRelationship> rels = Neo4JBatchInserter
				.getRelationships(callNodeId);
		//System.out.println("successfully instanced the rels");
		for (BatchRelationship rel : rels)
		{
			long childId = rel.getEndNode();

			if (childId == callNodeId)
				continue;

			String childType = (String) Neo4JBatchInserter.getNodeProperties(
					childId).get("type");

			if (childType.equals("ArgumentList"))
				return childId;
		}

		return -1;
	}

	private List<Long> getIdentifiersFromParams(List<Long> params)
	{
		System.out.println("In the function of getIdentifiersFromParams!");
		List<Long> retval = new LinkedList<Long>();

		for (Long paramId : params)
		{
			Iterable<BatchRelationship> rels = Neo4JBatchInserter
					.getRelationships(paramId);
			for (BatchRelationship rel : rels)
			{
				if (rel.getEndNode() == paramId)
					continue;

				long identifierNode = rel.getEndNode();
				Object type = Neo4JBatchInserter.getNodeProperties(
						identifierNode).get("type");
				if (type.equals("Identifier"))
				{
					retval.add(identifierNode);
					break;
				}
			}
		}

		return retval;
	}

	private List<Long> getParametersFromList(Long parameterListId)
	{
		System.out.println("In the function of getParametersFromList!");
		return getChildrenByNodeId(parameterListId);
	}

	private List<Long> getChildrenByNodeId(Long nodeId)
	{
		System.out.println("In the function of getChildrenByNodeId!");
		List<Long> retval = new LinkedList<Long>();
		Iterable<BatchRelationship> rels = Neo4JBatchInserter
				.getRelationships(nodeId);

		for (BatchRelationship rel : rels)
		{
			if (rel.getEndNode() == nodeId)
				continue;
			long childId = rel.getEndNode();
			retval.add(childId);
		}
		return retval;
	}

}
