package univ.uwm.cs760.nn.graph;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import univ.uwm.cs760.nn.OutputUnit;
import univ.uwm.cs760.nn.util.MathFunctions;
import univ.uwm.cs760.nn.util.Pair;
import weka.core.Instance;
import weka.core.Instances;

public class Graph {
	
	private Map<Vertex, Pair<ArrayList<Vertex>, ArrayList<Vertex> > >  adjacencyList;
	private Map<String, Map<String, Edge> > edgeList;
	private Map<String,Vertex> vertexList;
	
	public Graph()
	{
		adjacencyList = new LinkedHashMap<Vertex, Pair<ArrayList<Vertex>, ArrayList<Vertex> > >();
		edgeList = new HashMap<String, Map<String, Edge> >();
		vertexList =  new HashMap<>();
	}
	
	public void addVertex(Vertex vertex)
	{
		ArrayList<Vertex> inVertices = new ArrayList<>();
		ArrayList<Vertex> outVertices = new ArrayList<>();
		adjacencyList.put(vertex, new Pair<ArrayList<Vertex>, ArrayList<Vertex> >(inVertices, outVertices));
		vertexList.put(vertex.getVertexID(), vertex);
	}
	
	public void addEdge(Edge edge)
	{
		Vertex startVertex = edge.getStartVertex();
		Vertex endVertex = edge.getEndVertex();
		ArrayList<Vertex> inAdjacentVertices = adjacencyList.get(endVertex).getFirst();
		ArrayList<Vertex> outAdjacentVertices = adjacencyList.get(startVertex).getSecond();
		
		outAdjacentVertices.add(endVertex);
		inAdjacentVertices.add(startVertex);
		if(edgeList.containsKey(startVertex.getVertexID()))
			edgeList.get(startVertex.getVertexID()).put(endVertex.getVertexID(), edge);
		else
		{
			Map<String, Edge> value = new HashMap<>();
			value.put(endVertex.getVertexID(), edge);
			edgeList.put(startVertex.getVertexID(), value );
		}
	}
	
	public void printGraph()
	{
		for(Vertex v : adjacencyList.keySet())
		{
			System.out.print(v.getVertexID() + " : {");
			Pair<ArrayList<Vertex>, ArrayList<Vertex> > adjacencies = adjacencyList.get(v);
			for(Vertex inV : adjacencies.getFirst())
				System.out.print(inV.getVertexID() + ",");
			System.out.print("} {");
			for(Vertex outV : adjacencies.getSecond())
				System.out.print(outV.getVertexID() + ",");
			System.out.println("}");
		}
	}
	
	public void printEdges()
	{
		for(String s : edgeList.keySet())
		{
			for(String e : edgeList.get(s).keySet())
			{
				System.out.println("start : " + s + " end : " + e + " weight : " + edgeList.get(s).get(e).getWeight());
			}
		}
	}
	
    public ArrayList<Vertex> getInAdjacentVertices(Vertex vertex)
    {
    	    return adjacencyList.get(vertex).getFirst();
    }
    
    public ArrayList<Vertex> getOutAdjacentVertices(Vertex vertex)
    {
    	    return adjacencyList.get(vertex).getSecond();
    }
    
    public Edge getEdge(String start, String end)
    {
    	    return edgeList.get(start).get(end);
    }
    
    public Vertex getVertex(String vertexID)
    {
    	    return vertexList.get(vertexID);
    }
    
    public int numVertices()
    {
    	    return vertexList.size();
    }
    
    public int numEdges()
    {
    	   return edgeList.size();
    }
    
    public void learn(Instances data, Integer numEpochs, double learningRate, Vector<Integer> testInstances)
    {
    	    int epoch = 0;
    	    while(epoch < numEpochs)
    	    {
    	    	    epoch++;
    	    	    for(int i = 0; i < data.numInstances() ; i++)
    	    	    {
    	    	    	    if(!(testInstances.contains(i)))
    	    	    	    {
    	    	    	    	    double[][] outMatrix = new double[data.numAttributes()][3];
	    	    	    	    for(int k = 0; k < (data.numAttributes() -1); k++)
	    	    	    	    {
	    	    	    	    	    outMatrix[k][0] = data.instance(i).value(k);
	    	    	    	    }
	    	    	    	    outMatrix[data.numAttributes() -1][0] = 1;
	    	    	    	    for(int k = 0; k < (data.numAttributes() -1); k++)
	    	    	    	    {
	    	    	    	    	    double outMatrixVal = 0;
	    	    	    	    	    String end = "attr_"+ Integer.toString(k) + "_1";
	    	    	    	    	    for(int x = 0; x < (data.numAttributes()); x++)
	    	    	    	    	    {
	    	    	    	    	    	    String start = "attr_" + Integer.toString(x) + "_0";
	    	    	    	    	    	    outMatrixVal += (outMatrix[x][0] * this.getEdge(start, end).getWeight()); 
	    	    	    	    	    }
	    	    	    	    	    outMatrix[k][1] = MathFunctions.calculateSigmoid(outMatrixVal);
	    	    	    	    }
	    	    	    	    outMatrix[data.numAttributes() -1][1] = 1;
	    	    	    	    double finalOutput = 0;
	    	    	    	    for(int x = 0; x < (data.numAttributes()); x++)
	    	    	    	    {
	    	    	    	    	    String start = "attr_" + Integer.toString(x) + "_1";
	    	    	    	    	    String end = "attr_0_2";
	    	    	    	    	    finalOutput += (outMatrix[x][1] * this.getEdge(start, end).getWeight()); 
	    	    	    	    }
	    	    	    	    outMatrix[0][2] = MathFunctions.calculateSigmoid(finalOutput);
	    	    	    	    
	    	    	    	    Enumeration classLabels =  data.classAttribute().enumerateValues();
	    	    	    	    String firstClassLabel = (String) classLabels.nextElement();
	    	    	    	    if(data.instance(i).stringValue(data.classAttribute()).equals(firstClassLabel))
	    	    	    	    {
	    	    	    	        updateWeights(outMatrix, data.instance(i), learningRate, (0 - outMatrix[0][2]));
	    	    	    	    }
	    	    	    	    else
	    	    	    	    {
	    	    	    	    	    updateWeights(outMatrix, data.instance(i), learningRate, (1 - outMatrix[0][2]));
	    	    	    	    }
    	    	    	    }
    	    	    }
    	    }
    }

	private void updateWeights(double[][] outMatrix, Instance instance, double learningRate, double d) {
		for(int x = 0; x < (instance.numAttributes()); x++)
		{
			String start = "attr_" + Integer.toString(x) + "_0";
			for(int y = 0; y < (instance.numAttributes()); y++)
			{
				String end = "attr_" + Integer.toString(y) + "_1";
				String endOut = "attr_0_2";
	    			Edge edgeOut = this.getEdge(end, endOut);
	    	        Edge edge = this.getEdge(start, end);
		    	    double changeInWeight = learningRate * (outMatrix[y][1]) * (1-outMatrix[y][1]) * (outMatrix[x][0]) * (edgeOut.getWeight()) * d;
		    	    double newWeight = edge.getWeight() + changeInWeight;
		    	    edge.setWeight(newWeight);
			}
		}
		for(int x = 0; x < (instance.numAttributes()); x++)
	    {
			String start = "attr_" + Integer.toString(x) + "_1";
			String end = "attr_0_2";
	    	    Edge edge = this.getEdge(start, end);
	    	    double changeInWeightOutput = learningRate * (outMatrix[x][1]) * d;
	    	    double newWeight = edge.getWeight() + changeInWeightOutput;
	    	    edge.setWeight(newWeight);
	    }
		
	}

	public Integer test(Instances data, Vector<Integer> testInstances, Map<Integer, OutputUnit> output, Map<Double, String> reverseOutputForROC, Integer foldNumber) {
		Enumeration classLabels =  data.classAttribute().enumerateValues();
        String classLabel_1 = classLabels.nextElement().toString();
        String classLabel_2 = classLabels.nextElement().toString();
        Integer correctPredictions  = 0;
        for(int i = 0; i < testInstances.size(); i++)	
        {
	        double[][]  outMatrix = new double[data.numAttributes()][3];
	    	    for(int k = 0; k < (data.numAttributes() -1); k++)
	    	    {
	    	    	    outMatrix[k][0] = data.instance(testInstances.get(i)).value(k);
	    	    }
	    	    outMatrix[data.numAttributes() -1][0] = 1;
	    	    for(int k = 0; k < (data.numAttributes() -1); k++)
	    	    {
	    	    	    double outMatrixVal = 0;
	    	    	    for(int x = 0; x < (data.numAttributes()); x++)
	    	    	    {
	    	    	    	    String start = "attr_" + Integer.toString(x) + "_0";
	    	    	    	    String end = "attr_"+ Integer.toString(k) + "_1";
	    	    	    	    outMatrixVal += outMatrix[x][0] * this.getEdge(start, end).getWeight(); 
	    	    	    }
	    	    	    outMatrix[k][1] = MathFunctions.calculateSigmoid(outMatrixVal);
	    	    }
	    	    outMatrix[data.numAttributes() -1][1] = 1;
	    	    double finalOutput = 0;
	    	    for(int x = 0; x < (data.numAttributes()); x++)
	    	    {
	    	    	    String start = "attr_" + Integer.toString(x) + "_1";
	    	    	    String end = "attr_0_2";
	    	    	    finalOutput += outMatrix[x][1] * this.getEdge(start, end).getWeight(); 
	    	    }
	    	    outMatrix[0][2] = MathFunctions.calculateSigmoid(finalOutput);
	    	    double prediction = outMatrix[0][2];
	    	    if(prediction < 0.5)
	    	    {    
	    	    	    String actualLabel = data.instance(testInstances.get(i)).stringValue(data.classAttribute());
	    	    	    if(actualLabel.equals(classLabel_1)) correctPredictions++;
	    	    	    OutputUnit value = new OutputUnit(actualLabel, classLabel_1, foldNumber + 1, prediction);
	    	    	    output.put(testInstances.get(i), value);
	    	    	    reverseOutputForROC.put(prediction, actualLabel);
	    	    }
	    	    else 
	    	    {
	    	    	    String actualLabel = data.instance(testInstances.get(i)).stringValue(data.classAttribute());
	    	    	    if(actualLabel.equals(classLabel_2)) correctPredictions++;
	    	    	    OutputUnit value = new OutputUnit(actualLabel, classLabel_2, foldNumber+ 1, prediction);
	    	    	    output.put(testInstances.get(i), value);
	    	    	    reverseOutputForROC.put(prediction, actualLabel);
	    	    }
        }
        return correctPredictions;
	}
}
