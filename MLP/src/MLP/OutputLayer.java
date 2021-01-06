package MLP;

import java.util.Random;

public class OutputLayer {
	
	private double[] weights;
	private double bias;
	private double[] input;
	private int numberOfInputs;
	private Random randomGenerator;
	private double error;
	private double[] derivativeOfWeights;
	private double derivativeOfBias;
	
	
	
	public OutputLayer(int numberOfInputs){
		
		this.numberOfInputs = numberOfInputs;
		this.weights = new double[numberOfInputs];
		this.bias = 0; // will be randomized later
		this.input = new double[numberOfInputs];
		
		this.derivativeOfWeights = new double[numberOfInputs];
		this.derivativeOfBias = 0;
		
		this.error = 0;
		
		this.randomGenerator = new Random();
		// randomize here
		randomizeWeights();
		
	}
	
	public String getderivativeOfWeights() {
		
		String str = "";
		for(int i =0 ;i< derivativeOfWeights.length; i++) {
			str += derivativeOfWeights[i] + " ";
		}
		str += "\n";
		return str;
	}

	
	// we randomize in range [-1,1]
	private void randomizeWeights(){
		for (int i = 0; i < numberOfInputs ; i ++){
			weights[i] = 2 * randomGenerator.nextDouble() - 1;
		}
		bias = 2 * randomGenerator.nextDouble() - 1;
	}
	
	public void updateWeights(double eduRate) {
		for ( int i = 0; i < numberOfInputs; i++) {
			
			weights[i] = weights[i] - eduRate * this.derivativeOfWeights[i];
		}
		this.bias = this.bias - eduRate * this.derivativeOfBias;
	}
	
	public void setInput(double[] input){
		
		this.input = input;
	}
	
	private double logisticFunction(double x){
		double toret = 1 / ( 1 + Math.exp(-x) ); 
		
		return toret;
	}
	
	public double derivative() {
		
		double toret = logisticFunction(dotProduct()) * (1 - logisticFunction(dotProduct()));
		
		return toret;
	}
	
	private double dotProduct(){
		double sum = 0;
		for(int i=0; i< numberOfInputs; i++){
			sum += weights[i] * input[i];
		}
		sum += bias;
	
		return sum;
	}
	
	public double getOutput(){
		
		return logisticFunction(dotProduct());
	}
	
	public double getWeight(int i) {
		
		return weights[i];
	}
	
	public double getError() {
		return this.error;
	}
	
	public void setError(double error) {
		this.error = error;
		//System.out.println("OutL error: " + error);
	}
	
	public void calcDerivativeOfWeights() {
		
		for(int j=0; j< numberOfInputs; j++){
			this.derivativeOfWeights[j] += error * input[j];
		}
		calcDerivativeOfBias();
	}
	
	private void calcDerivativeOfBias() {
		
		this.derivativeOfBias += this.error;
	}
	
	public void clearVectorOfDerivatives() {
		for ( int i = 0; i < numberOfInputs; i++) {
			this.derivativeOfWeights[i] = 0;
		}
		this.derivativeOfBias = 0;
	}
	
	public String toString() {
		String returnstr = "";
		returnstr += "Neuron of Final Layer: \n";
		for(int i = 0; i < numberOfInputs; i++) {
			returnstr += weights[i]+" ";
		}
		returnstr += "bias: " + this.bias;
		returnstr += "\n";
		return returnstr;
	}

}
