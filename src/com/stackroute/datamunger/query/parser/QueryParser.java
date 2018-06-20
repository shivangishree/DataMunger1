package com.stackroute.datamunger.query.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*There are total 4 DataMungerTest file:
 * 
 * 1)DataMungerTestTask1.java file is for testing following 4 methods
 * a)getBaseQuery()  b)getFileName()  c)getOrderByClause()  d)getGroupByFields()
 * 
 * Once you implement the above 4 methods,run DataMungerTestTask1.java
 * 
 * 2)DataMungerTestTask2.java file is for testing following 2 methods
 * a)getFields() b) getAggregateFunctions()
 * 
 * Once you implement the above 2 methods,run DataMungerTestTask2.java
 * 
 * 3)DataMungerTestTask3.java file is for testing following 2 methods
 * a)getRestrictions()  b)getLogicalOperators()
 * 
 * Once you implement the above 2 methods,run DataMungerTestTask3.java
 * 
 * Once you implement all the methods run DataMungerTest.java.This test case consist of all
 * the test cases together.
 */

public class QueryParser {

	private QueryParameter queryParameter = new QueryParameter();

	/*
	 * This method will parse the queryString and will return the object of
	 * QueryParameter class
	 */
	public QueryParameter parseQuery(String queryString) {
        queryParameter.setFileName(getFileName(queryString)); 
        queryParameter.setBaseQuery(getBaseQuery(queryString));
        queryParameter.setRestrictions(getRestriction(queryString));
        queryParameter.setFields(getFields(queryString));
        queryParameter.setLogicalOperators(getLogicalOperators(queryString));
        queryParameter.setAggregateFunctions(getAggregateFunctions(queryString));
        queryParameter.setOrderByField(getOrderByFields(queryString));
        queryParameter.setGroupByField(getGroupByFields(queryString));
		return queryParameter;
	}

	/*
	 * Extract the name of the file from the query. File name can be found after the
	 * "from" clause.
	 */
	public String getFileName(String queryString) {
		String fileName;
        int positionOfFrom = queryString.indexOf(" from ");
        int positionOfSpace = queryString.indexOf(" ",positionOfFrom+6);
        if(positionOfSpace==-1) {
        fileName = 	queryString.substring(positionOfFrom+6);
        }
        else {
        	fileName = queryString.substring(positionOfFrom+6, positionOfSpace);
        }        
		return fileName;
	}
	/*
	 * 
	 * Extract the baseQuery from the query.This method is used to extract the
	 * baseQuery from the query string. BaseQuery contains from the beginning of the
	 * query till the where clause
	 */
	public String getBaseQuery(String queryString) {
		queryString =queryString.toLowerCase();
        String baseQuery = "";
		int indexOfWhere = queryString.indexOf("where");
		int indexOfGroupBy = queryString.indexOf("group by");
		int indexOfOrderBy = queryString.indexOf("order by");
		if(indexOfGroupBy == -1 && indexOfOrderBy == -1 && indexOfWhere == -1) {
		baseQuery = queryString.substring(0).trim();
		}
		else if(indexOfWhere != -1) {
		baseQuery = queryString.substring(0,indexOfWhere-1).trim();
		}
		else if(indexOfGroupBy != -1) {
		baseQuery = queryString.substring(0,indexOfGroupBy).trim();
		}
		else if(indexOfOrderBy != -1) {
		baseQuery = queryString.substring(0,indexOfOrderBy).trim();
		}
		return baseQuery;
	}

	/*
	 * extract the order by fields from the query string. Please note that we will
	 * need to extract the field(s) after "order by" clause in the query, if at all
	 * the order by clause exists. For eg: select city,winner,team1,team2 from
	 * data/ipl.csv order by city from the query mentioned above, we need to extract
	 * "city". Please note that we can have more than one order by fields.
	 */
	public List<String> getOrderByFields(String queryString) {
		queryString = queryString.toLowerCase();				
        int indexOfOrderby = queryString.indexOf(" order ");
		if(indexOfOrderby == -1) {
			return null;	
		}
		else {
		int indexOfAsc = queryString.indexOf(" asc");
		int indexOfDesc = queryString.indexOf(" desc");
		String orderByFields;
		if(indexOfAsc != -1) {
			 orderByFields = queryString.substring(indexOfOrderby+10,indexOfAsc);
			
		}
		else if(indexOfDesc != -1) {
			orderByFields = queryString.substring(indexOfOrderby+10,indexOfDesc);
		}
		else {
			orderByFields = queryString.substring(indexOfOrderby+10);
		}		
		String [] fields = orderByFields.split(",");
		List<String> fieldsList = new ArrayList<String>();
		for(String field:fields) {
			fieldsList.add(field);
		}
		return fieldsList;
		}
		
	}

	/*
	 * Extract the group by fields from the query string. Please note that we will
	 * need to extract the field(s) after "group by" clause in the query, if at all
	 * the group by clause exists. For eg: select city,max(win_by_runs) from
	 * data/ipl.csv group by city from the query mentioned above, we need to extract
	 * "city". Please note that we can have more than one group by fields.
	 */
	public List<String> getGroupByFields(String queryString) {
	    queryString = queryString.toLowerCase();
		int indexOfGroupby = queryString.indexOf("group by");
		String groupbyField;
		String[] groupbyFields;
		List<String> fieldsList;
		if(indexOfGroupby == -1) {
			return null;
		}
        int indexOfHaving = queryString.indexOf("having");
        int indexOfOrderby = queryString.indexOf("order by");
        if(indexOfHaving!= -1) {        	
        	groupbyField = queryString.substring(indexOfGroupby+9,indexOfHaving+ 7).trim();
        	groupbyFields = groupbyField.split(",");
        	fieldsList = new ArrayList<String>(Arrays.asList(groupbyFields));
        	return fieldsList;
        }
        if (indexOfOrderby != -1) {
        	groupbyField = queryString.substring(indexOfGroupby+9,indexOfOrderby).trim();
        	groupbyFields = groupbyField.split(",");
        	fieldsList = new ArrayList<String>(Arrays.asList(groupbyFields));
        	return fieldsList;
        }
        groupbyField = queryString.substring(indexOfGroupby+9).trim();
        groupbyFields = groupbyField.split(",");
        fieldsList = new ArrayList<String>(Arrays.asList(groupbyFields));
		return fieldsList;
	}
	/*
	 * Extract the selected fields from the query string. Please note that we will
	 * need to extract the field(s) after "select" clause followed by a space from
	 * the query string. For eg: select city,win_by_runs from data/ipl.csv from the
	 * query mentioned above, we need to extract "city" and "win_by_runs". Please
	 * note that we might have a field containing name "from_date" or "from_hrs".
	 * Hence, consider this while parsing.
	 */
	public List<String> getFields(String queryString) {
		queryString = queryString.toLowerCase();
        String [] fields = queryString.split("from")[0].split(" ")[1].split(",");
        List<String> fieldsList = new ArrayList<String>(Arrays.asList(fields)); 
        return fieldsList;
	}

	/*
	 * Extract the conditions from the query string(if exists). for each condition,
	 * we need to capture the following: 1. Name of field 2. condition 3. value
	 * 
	 * For eg: select city,winner,team1,team2,player_of_match from data/ipl.csv
	 * where season >= 2008 or toss_decision != bat
	 * 
	 * here, for the first condition, "season>=2008" we need to capture: 1. Name of
	 * field: season 2. condition: >= 3. value: 2008
	 * 
	 * the query might contain multiple conditions separated by OR/AND operators.
	 * Please consider this while parsing the conditions.
	 * 
	 */
	public List<Restriction> getRestriction(String queryString) {
		String [] conditions;
	    List<Restriction> restrictions = new ArrayList<>();//creating list of restriction
	    Restriction restriction;//reference variable of class restriction	    
	    int indexOfWhere = queryString.indexOf("where");
	    //returning null when there is no where
	    if(indexOfWhere == -1) {
	    	return null;
	    }
	    else {
	    conditions = queryString.split("where | group by | order by ")[1].trim().split(" and | or ");
	    for(String condition:conditions) {
//	    String name = condition.split(" ")[0].trim();
//	    String value = condition.split(" ")[2].trim();
//	    String restrictionCondition = condition.split(" ")[1].trim();
	    restriction = new Restriction();
	    String [] conditionSplitted = condition.split(" |'");
	    restriction.setName(conditionSplitted[0].trim());
	    restriction.setCondition(conditionSplitted[1].trim());
	    restriction.setValue(conditionSplitted[2].trim());
	    restrictions.add(restriction);
	    }    
	    return restrictions;
	    }  
		}

	/*
	 * Extract the logical operators(AND/OR) from the query, if at all it is
	 * present. For eg: select city,winner,team1,team2,player_of_match from
	 * data/ipl.csv where season >= 2008 or toss_decision != bat and city =
	 * bangalore
	 * 
	 * The query mentioned above in the example should return a List of Strings
	 * containing [or,and]
	 */
	public List<String> getLogicalOperators(String queryString) {
        queryString = queryString.toLowerCase();
        int indexOfWhere = queryString.indexOf("where");
        if(indexOfWhere == -1) {
		return null;
        }
        else {
        	String[] s = queryString.split(" ");
        	ArrayList<String> operatorArrayList = new ArrayList<String>();
        	for(String op:s) {
        		if(op.equals("and") || op.equals("or")) {
        			operatorArrayList .add(op);
        		}
        	}
           	return operatorArrayList;
        	}
	}
	/*
	 * Extract the aggregate functions from the query. The presence of the aggregate
	 * functions can determined if we have either "min" or "max" or "sum" or "count"
	 * or "avg" followed by opening braces"(" after "select" clause in the query
	 * string. in case it is present, then we will have to extract the same. For
	 * each aggregate functions, we need to know the following: 1. type of aggregate
	 * function(min/max/count/sum/avg) 2. field on which the aggregate function is
	 * being applied.
	 * 
	 * Please note that more than one aggregate function can be present in a query.
	 * 
	 * 
	 */
	public List<AggregateFunction> getAggregateFunctions(String queryString) {
		/* Logic -- Our aggregate function will be between select and from keyword so 
		 * first split on the basis of from and take first string. Split the first half 
		 * on select keyword. Hence our aggregate function may lie on aggregateAssume string
		 * now split it on comma and for each string array check if it start with "sum("...
		 * If it does add it to arrayList. If arrayList is empty means no aggregate function
		 * return null else copy from arrayList to string array and return the array */
			String[] splitOnWhere=queryString.split(" from ");
			String[] splitOnSelect=splitOnWhere[0].trim().split("select ");
			String aggregateAssume=splitOnSelect[1];
			String[] aggregateAssumeCommaSeparate=aggregateAssume.trim().split(",");
			ArrayList<String> listOfFunctions = new ArrayList<String>();
			List<AggregateFunction> listOfAggregateFunctions = new ArrayList<AggregateFunction>();
			AggregateFunction aggregateFunctions;
			for(int i=0;i<aggregateAssumeCommaSeparate.length;i++) {
				if(aggregateAssumeCommaSeparate[i].startsWith("sum(") || aggregateAssumeCommaSeparate[i].startsWith("count(")
						|| aggregateAssumeCommaSeparate[i].startsWith("min(") || aggregateAssumeCommaSeparate[i].startsWith("max(")
						|| aggregateAssumeCommaSeparate[i].contains("avg("))
				{
					listOfFunctions.add(aggregateAssumeCommaSeparate[i].trim());
				}
			}
			if(listOfFunctions.size()==0) {
				return null;
			}
			else {
				for(String function:listOfFunctions) {
//					String fieldAggregate= function.split("\\(|\\)")[1];
//					String functionAggregate = function.split("\\(|\\)")[0] ;	
					aggregateFunctions = new AggregateFunction();
					String[] functionSplited = function.split("\\(|\\)");
					aggregateFunctions.setField(functionSplited[1]);
					aggregateFunctions.setFunction(functionSplited[0]);
					listOfAggregateFunctions.add(aggregateFunctions);
				}
				return listOfAggregateFunctions;
			}

		}

}