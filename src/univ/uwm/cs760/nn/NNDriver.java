package univ.uwm.cs760.nn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import univ.uwm.cs760.nn.graph.Edge;
import univ.uwm.cs760.nn.graph.Graph;
import univ.uwm.cs760.nn.graph.Vertex;
import univ.uwm.cs760.nn.util.Pair;
import univ.uwm.cs760.nn.util.ROCGenerator;
import weka.core.Instances;

public class NNDriver {
	
	public static void main(String args[])
	{
		String trainfile = args[0];
		Integer num_folds = Integer.parseInt(args[1]);
		Double learning_rate = Double.parseDouble(args[2]);
		Integer num_epochs = Integer.parseInt(args[3]);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(trainfile));
			Instances data = new Instances(reader);
			Integer numOfAttr = data.numAttributes();
			data.setClassIndex(numOfAttr - 1);
			reader.close();
			Enumeration classLabels =  data.classAttribute().enumerateValues();
    	        String classLabel_1 = classLabels.nextElement().toString();
			Vector<Integer> class1Indices = new Vector<>();
			Vector<Integer> class2Indices = new Vector<>();
			for(int i = 0; i < data.numInstances(); i++)
			{
				if(data.instance(i).stringValue(data.classAttribute()).equals(classLabel_1))
					class1Indices.add(i);
				else 
					class2Indices.add(i);
			}
			
			Graph neuralNetwork =  new Graph();
			Random random = new Random();
			Double rangeMin = -1.0;
			Double rangeMax =  1.0;
			for(int i = 0; i < (numOfAttr); i++)
			{
				Vertex v1 = new Vertex("attr_" + Integer.toString(i) + "_0");
				Vertex v2 = new Vertex("attr_" + Integer.toString(i) + "_1");
				neuralNetwork.addVertex(v1);
				neuralNetwork.addVertex(v2);
			}
			
			Vertex outVertex = new Vertex("attr_0_2");
			neuralNetwork.addVertex(outVertex);
			
			for(int i = 0; i < (numOfAttr); i++)
			{
				Vertex v1 = neuralNetwork.getVertex("attr_" + Integer.toString(i) + "_0");
				for(int j = 0; j < (numOfAttr); j++)
				{
				    Vertex v2 = neuralNetwork.getVertex("attr_" + Integer.toString(j) + "_1");
				    Edge e = new Edge(v1,v2, (rangeMin + ((rangeMax-rangeMin)*random.nextDouble())));
				    neuralNetwork.addEdge(e);
				}
				Vertex v3 = neuralNetwork.getVertex("attr_" + Integer.toString(i) + "_1");
				Vertex v4 = neuralNetwork.getVertex("attr_0_2");
				Edge e = new Edge(v3,v4, (rangeMin + ((rangeMax-rangeMin)*random.nextDouble())));
				neuralNetwork.addEdge(e);
			}
			
			Vector<Integer> testInstances = new Vector<>();
			double class1Block = Math.ceil(((double)class1Indices.size()) / ((double)num_folds));
			double class2Block = Math.ceil(((double)class2Indices.size()) / ((double)num_folds));
			int class1CurrIndex = 0;
			int class2CurrIndex = 0;
			
			Map<Integer, OutputUnit> output = new TreeMap<>();
			Map<Double, String> reverseOutputForROC = new TreeMap<>();
			ArrayList<Pair<String, Double>> outputForROC = new ArrayList<Pair<String, Double>>();
			Integer correctPredictions = 0;
			for(int i = 0; i < num_folds; i++)
			{
				testInstances.clear();
				for(int j = 0; ((j < class1Block) && ((class1CurrIndex + j) < class1Indices.size())); j++)
			        testInstances.add(class1Indices.get(class1CurrIndex + j));
			    class1CurrIndex += class1Block;
				for(int j = 0; (j < class2Block && ((class2CurrIndex + j) < class2Indices.size())); j++)
			        testInstances.add(class2Indices.get(class2CurrIndex + j));
			    class2CurrIndex += class2Block;
			    neuralNetwork.learn(data, num_epochs, learning_rate, testInstances);
			    correctPredictions += neuralNetwork.test(data,testInstances, output, reverseOutputForROC, i);
			}
			for(Integer key : output.keySet())
				System.out.println(output.get(key).toString());
			for(Double d : reverseOutputForROC.keySet()) 
			{
			    Double confidence = d;
			    	String label = reverseOutputForROC.get(d);
			    	outputForROC.add(new Pair<String, Double>(label, confidence));
			}
			//ROCGenerator rocGenerator = new ROCGenerator();
			//rocGenerator.generateROC(outputForROC);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
