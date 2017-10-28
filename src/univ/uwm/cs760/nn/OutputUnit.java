package univ.uwm.cs760.nn;

public class OutputUnit {
	
	private String actualClass;
	private String predictedClass;
	private Integer foldNum;
	private Double positiveConfidence;
	
	public OutputUnit(String actualClass, String predictedClass, Integer foldNum, Double positiveConfidence)
	{
		this.actualClass = actualClass;
		this.predictedClass = predictedClass;
		this.foldNum = foldNum;
		this.positiveConfidence = positiveConfidence;
	}
	
	public String getActualClass() {
		return actualClass;
	}
	public void setActualClass(String actualClass) {
		this.actualClass = actualClass;
	}
	public String getPredictedClass() {
		return predictedClass;
	}
	public void setPredictedClass(String predictedClass) {
		this.predictedClass = predictedClass;
	}
	public Integer getFoldNum() {
		return foldNum;
	}
	public void setFoldNum(Integer foldNum) {
		this.foldNum = foldNum;
	}
	public Double getPositiveConfidence() {
		return positiveConfidence;
	}
	public void setPositiveConfidence(Double positiveConfidence) {
		this.positiveConfidence = positiveConfidence;
	}
    public String toString()
    {
    	    return this.getFoldNum() + " " + this.getPredictedClass() + " " + this.getActualClass() + " " + String.format("%.16f", this.getPositiveConfidence()); 
    }
	
}
