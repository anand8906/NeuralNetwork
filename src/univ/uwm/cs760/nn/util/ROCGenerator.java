package univ.uwm.cs760.nn.util;

import java.util.ArrayList;

public class ROCGenerator {

	public ArrayList<Pair<Double, Double>> generateROC(ArrayList<Pair<String, Double>> reversedList)
	{   
		ArrayList<Pair<String, Double>> instances = new ArrayList<>();
		for(int i = reversedList.size()-1; i >= 0; i--)
		{
			instances.add(reversedList.get(i));
		}
		
		ArrayList<Pair<Double, Double>> coordinates = new ArrayList<>();
		ArrayList<Double> tprs = new ArrayList<>();
		ArrayList<Double> fprs = new ArrayList<>();
		Double num_pos = 0.0;
		Double num_neg = 0.0;
		for(Pair<String, Double> p : instances)
		{
			if(p.getFirst().equals("Rock")) num_neg++;
			else num_pos++;
		}
		Double TP = 0.0;
		Double FP = 0.0;
		Double last_TP = 0.0;
		
		for(int i = 0; i < instances.size();i++)
		{
			Pair<String, Double> curr = instances.get(i);
			if ((i > 1) && ( curr.getSecond() != instances.get(i-1).getSecond()) && ( curr.getFirst().equals("Rock") ) && ( TP > last_TP ))
			{
			  double FPR = FP / num_neg;
			  double TPR = TP / num_pos;
			  //System.out.println("FP : " + FP + " num_neg : " + num_neg + " TP : " + TP +  " num_pos : " + num_pos + " FPR : " + FPR + " TPR : " + TPR);
			  fprs.add(FPR);
			  tprs.add(TPR);
			  coordinates.add(new Pair<Double, Double>(FPR, TPR));
			  last_TP = TP ;
			}
			if(curr.getFirst().equals("Mine"))
			    ++TP;
			else
			    ++FP;
		}
		Double FPR = FP/num_neg;
		Double TPR = TP/num_pos;
		fprs.add(FPR);
		tprs.add(TPR);
		//System.out.println("FP : " + FP + " num_neg : " + num_neg + " TP : " + TP +  " num_pos : " + num_pos + " FPR : " + FPR + " TPR : " + TPR);
		coordinates.add(new Pair<Double, Double>(FPR, TPR));
		
		for(Double d: fprs)
			System.out.println(d);
		System.out.println("==================");
		for(Double d: tprs)
			System.out.println(d);
		
		
		return coordinates;
	}
}
