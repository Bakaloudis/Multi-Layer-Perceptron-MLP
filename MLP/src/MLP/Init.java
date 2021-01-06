package MLP;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Init {
	
	private  Data[] educationSet;
	private  Data[] testSet;
	
	public static final int K = 4; 		// number of categories
	public static final int d = 2; 		// number of inputs (in H1)
	public static final int H1 = 10; 	// number of neurons in H1
	public static final int H2 = 6; 	// number of neurons in H2
	public static final int B = 300;  	// size of batch
	
	public static final double EDU_RATE = 0.001;
	public static final String typeOfFunctionInH2 = "tanh";
	
	private HiddenLayer1[] neuronsH1;
	private HiddenLayer2[] neuronsH2;
	private OutputLayer[] neuronsOut;
	
	// sum of errors of each education example in one epoch
	private double squaredErrorOfEpoch; 
	private double lastEpochError;
	
	//for testing the network
	private int correctExamples = 0;
	private int wrongExamples = 0;
	
	private ArrayList<Data> correctList = new ArrayList<Data>(); 
	private ArrayList<Data> wrongList = new ArrayList<Data>();
	
	private int numOfExamplesC1 = 0;
	private int numOfExamplesC2 = 0;
	private int numOfExamplesC3 = 0;
	private int numOfExamplesC4 = 0;

	public void loadTrainData(String filename) throws FileNotFoundException {
		
		Scanner inputReader = null;
		inputReader = new Scanner(new FileInputStream(filename));
		
		int i = 0;
		while (inputReader.hasNextLine( )) {
			String line = inputReader.nextLine( );
			String[] lineData = line.trim().split("\\s+");	
			educationSet[i] = new Data(Double.parseDouble(lineData[0]), Double.parseDouble(lineData[1]), lineData[2]);
			i++;
		}
		
		inputReader.close();
	}
	
	public void loadTestData(String filename) throws FileNotFoundException {
		
		Scanner inputReader = null;
		inputReader = new Scanner(new FileInputStream(filename));
		
		int i = 0;
		while (inputReader.hasNextLine( )) {
			String line = inputReader.nextLine( );
			String[] lineData = line.trim().split("\\s+");	
			Data data = new Data(Double.parseDouble(lineData[0]), Double.parseDouble(lineData[1]), lineData[2]);
			testSet[i] = data;
			i++;
		}
		
		inputReader.close();
	}
	
	private void realCategories() {
		
		int numOfActualC1 = 0;
		int numOfActualC2 = 0;
		int numOfActualC3 = 0;
		int numOfActualC4 = 0;
		
		for (int i=0; i<testSet.length ; i++) {
			switch(testSet[i].getC()) { 
	        case "1": 
	        	numOfActualC1 ++;
	            break; 
	        case "2": 
	        	numOfActualC2 ++;
	            break; 
	        case "3": 
	        	numOfActualC3 ++;
	            break; 
			case "4":
				numOfActualC4 ++;
	            break; 
			}
				
		}
		
		System.out.println("In C1: " + numOfActualC1);
		System.out.println("In C2: " + numOfActualC2);
		System.out.println("In C3: " + numOfActualC3);
		System.out.println("In C4: " + numOfActualC4);
		
	}
	
	private void printTestStatistics() {
		
		System.out.println("\nTest Set size: "+ testSet.length);
		System.out.println("Train Set size: "+ educationSet.length);
		System.out.println("\nCorrectly Categorized: " + this.correctExamples);
		System.out.println("Wrongly Categorized: " + this.wrongExamples);
		
		double percentage = (this.correctExamples / (double)testSet.length) * 100;
		System.out.println("Percentage: " + percentage + "% correct" );
		
		System.out.println("\n---Actual---");
		realCategories();
		
		System.out.println("\n---Predicted---");
		System.out.println("In C1: " + this.numOfExamplesC1);
		System.out.println("In C2: " + this.numOfExamplesC2);
		System.out.println("In C3: " + this.numOfExamplesC3);
		System.out.println("In C4: " + this.numOfExamplesC4);
		
		System.out.println("\n* = correct\n+ = error ");
	}
	
	
	private void compareOutandCorrect(double[] netOut, Data example) {
		
		/* Find maximum of the networks output */
		double max = -100000000.000; //this is the smallest value
		int maxIndex = 0;
		for(int i=0; i < K; i++) {
			if(netOut[i] > max) {
				max = netOut[i];
				maxIndex = i;
				//System.out.println("Found Max!"+ maxIndex);
			}
		}
		String category = example.getC();
		/* Add it to category & Compare if its actually correct */
		
		switch(maxIndex) {
		
		case 0:
			
			this.numOfExamplesC1 ++;
			
			if(category.equals("1")) {
				this.correctExamples ++;
				this.correctList.add(example);
			}else {
				this.wrongExamples ++;
				this.wrongList.add(example);
			}
			break;
			
		case 1: 
			
			this.numOfExamplesC2 ++;
			
			if(category.equals("2")) {
				this.correctExamples ++;
				this.correctList.add(example);
			}else {
				this.wrongExamples ++;
				this.wrongList.add(example);
			}
			break;
			
		case 2: 
			
			this.numOfExamplesC3 ++;
			
			if(category.equals("3")) {
				this.correctExamples ++;
				this.correctList.add(example);
			}else {
				this.wrongExamples ++;
				this.wrongList.add(example);
			}
			break;
		
		case 3: 
			
			this.numOfExamplesC4 ++;
			
			if(category.equals("4")) {
				this.correctExamples ++;
				this.correctList.add(example);
			}else {
				this.wrongExamples ++;
				this.wrongList.add(example);
			}
			break;
		
		}
		
	}
	
	public void testNetwork() {
	
		for (int i=0; i< testSet.length ; i++) {
			double[] input = testSet[i].toVectorNoBias();
			double[] netOut = forwardPass(input);
			compareOutandCorrect(netOut,testSet[i]);
		}

		Plot p = new Plot(correctList,wrongList);
		p.setVisible(true);
		printTestStatistics();
		
	}
	
	public double[] calculateDiffWithExpected(double[] networkOutput, String exampleCategory) {
		
			double[] expectedOut = new double[K];
			switch(exampleCategory) { 
	        case "1": 
	        	expectedOut = new double[] {1,0,0,0};
	            break; 
	        case "2": 
	        	expectedOut = new double[] {0,1,0,0};
	            break; 
	        case "3": 
	        	expectedOut = new double[] {0,0,1,0};
	            break; 
	        case "4":
	        	expectedOut = new double[] {0,0,0,1};
	            break; 
			}
			
			double[] diff = new double[K];
			for(int i=0; i < K; i++) {
				diff[i] =  networkOutput[i] - expectedOut[i];
			}
			
			return diff;
		}
		
	public double calculateSquareError(double[] networkOutput, String exampleCategory) {  // Calculate the error from ONLY one example, call it after forward pass, 																							 
		double[] expectedOut = new double[K];											  // so that we don't pass the network one extra time.
		switch(exampleCategory) { 
		case "1": 
        	expectedOut = new double[] {1,0,0,0};
            break; 
        case "2": 
        	expectedOut = new double[] {0,1,0,0};
            break; 
        case "3": 
        	expectedOut = new double[] {0,0,1,0};
            break; 
        case "4": 
        	expectedOut = new double[] {0,0,0,1};
            break; 
		}
		
		double sum = 0;
		for(int i=0; i < K; i++) {
			sum +=  Math.pow(Math.abs((networkOutput[i] - expectedOut[i])), 2);
		}
		
		double error = 0.5 * sum;
		
		return error;
			
	}
	
	public double[] forwardPass(double[] input) {
		
		
		double[] outputH1 = new double[H1];
		double[] outputH2 = new double[H2];
		double[] networkOutput = new double[K];
		
		for (int i = 0 ; i < H1; i++) {
			neuronsH1[i].setInput(input);
			outputH1[i] = neuronsH1[i].getOutput();
		}
		
		for (int i = 0 ; i < H2; i++) {
			neuronsH2[i].setInput(outputH1);
			outputH2[i] = neuronsH2[i].getOutput();
		}
		
		for (int i = 0 ; i < K; i++) {
			neuronsOut[i].setInput(outputH2);
			networkOutput[i] = neuronsOut[i].getOutput();
		}
		
		return networkOutput;
	}
	
	public void backPropagation(Data example) {
		
		double[] input  = example.toVectorNoBias();
		double[] networkOut = forwardPass(input);
		double[] difference = calculateDiffWithExpected(networkOut, example.getC());
		
		this.squaredErrorOfEpoch += calculateSquareError(networkOut, example.getC());
		
		
		for(int i = 0; i < K; i++){
			neuronsOut[i].setError( difference[i] * neuronsOut[i].derivative() );
			neuronsOut[i].calcDerivativeOfWeights();
		}
		
		for (int i = 0; i < H2; i++){
			double dotProdWeightsError = 0;
			for (int j = 0; j < K; j++) {
				double weightji = neuronsOut[j].getWeight(i);
				double errorj = neuronsOut[j].getError();
				dotProdWeightsError += weightji * errorj;
			}
			
			neuronsH2[i].setError( dotProdWeightsError * neuronsH2[i].derivative() ); 
			neuronsH2[i].calcDerivativeOfWeights();
		}
		
		for (int i = 0; i < H1; i++){
			double dotProdWeightsError = 0;
			for (int j = 0; j < H2; j++) {
				double weightji = neuronsH2[j].getWeight(i);
				double errorj = neuronsH2[j].getError();
				dotProdWeightsError += weightji * errorj;
			}
			
			neuronsH1[i].setError( dotProdWeightsError * neuronsH1[i].derivative() );
			neuronsH1[i].calcDerivativeOfWeights();
			
		}
		
		
	}
	
	public void educate() {
		
		int epoch = 0;
		
		do {
			System.out.println("Epoch : " + epoch );
			
			clearAllDerivatives();
			clearErrorOfEpoch();
			
			int counterOfBatches = 0;
			for(int i=0; i < educationSet.length; i+=B) {
				counterOfBatches ++;
				for (int j = i; j < counterOfBatches*B; j++) {
					backPropagation(educationSet[j]);
				}
				updateAllWeights();
				clearAllDerivatives();
			}
			
			printErrorOfEpoch();
			epoch ++;
			
			
		}while((epoch < 500 || Math.abs(calcErrorBetween2Epochs()) > 0.01) && epoch < 10000 );

		
	}
	
	private void initHiddenLayer1(int numOfH1Neurons, int numOfInputs) {
		
		neuronsH1 = new HiddenLayer1[numOfH1Neurons];
		
		for (int i = 0 ; i < numOfH1Neurons ; i++) {
			neuronsH1[i] = new HiddenLayer1(numOfInputs);
			
		}
		System.out.println("Hidden Layer H1: " + numOfH1Neurons + " neurons, " + numOfInputs + " inputs.");
		
	}
	
	private void initHiddenLayer2(int numOfH2Neurons, int numOfInputs) {
		
		neuronsH2 = new HiddenLayer2[numOfH2Neurons];
		
		for (int i = 0 ; i< numOfH2Neurons ; i++) {
			neuronsH2[i] = new HiddenLayer2(numOfInputs,typeOfFunctionInH2);
		}
		System.out.println("Hidden Layer H2: " + numOfH2Neurons + " neurons, " + numOfInputs + " inputs.");
	
	}
	
	private void initOutputLayer(int numOfNeurons, int numOfInputs) {
		
		neuronsOut = new OutputLayer[numOfNeurons];
		
		for (int i = 0 ; i < numOfNeurons ; i++) {
			neuronsOut[i] = new OutputLayer(numOfInputs);
		}
		System.out.println("Final Layer: " + numOfNeurons + " neurons, " + numOfInputs + " inputs.");
	
	}
	
	public void setUpNetwork() {
		
		System.out.println("---Initializing MLP Network---");
		
		initHiddenLayer1(H1,d);
		initHiddenLayer2(H2,H1);
		initOutputLayer(K,H2);
		
	}
	
	public Init() {
		
		this.squaredErrorOfEpoch = 0;
		this.lastEpochError = 0;
		
		educationSet = new Data[3000];
		testSet = new Data[3000];
		
		setUpNetwork();
		
	}
	
	private void clearErrorOfEpoch() {
		this.squaredErrorOfEpoch = 0;
		
	}
	
	public void printErrorOfEpoch() {
		
		System.out.println("Squared Error: " + this.squaredErrorOfEpoch);
	}
	
	public double calcErrorBetween2Epochs() {
		
		double epochDiff = this.lastEpochError - this.squaredErrorOfEpoch;
		this.lastEpochError = this.squaredErrorOfEpoch;
		return epochDiff;
	}
		
	public void updateAllWeights() {
		for(int i = 0; i < H1; i++) {
			neuronsH1[i].updateWeights(EDU_RATE);
		}
		for(int i = 0; i < H2; i++) {
			neuronsH2[i].updateWeights(EDU_RATE);
		}
		for(int i = 0; i < K; i++) {
			neuronsOut[i].updateWeights(EDU_RATE);
		}
	}
	
	public void clearAllDerivatives() {
		for(int i = 0; i < H1; i++) {
			neuronsH1[i].clearVectorOfDerivatives();
		}
		for(int i = 0; i < H2; i++) {
			neuronsH2[i].clearVectorOfDerivatives();
		}
		for(int i = 0; i < K; i++) {
			neuronsOut[i].clearVectorOfDerivatives();
		}
	}
	
	public void printWeights() {
		String str = "";
		for(int i = 0; i < H1; i++) {
			str += neuronsH1[i].toString();
		}
		for(int i = 0; i < H2; i++) {
			str += neuronsH2[i].toString();
		}
		for(int i = 0; i < K; i++) {
			str += neuronsOut[i].toString();
		}
		System.out.println(str);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
	
		Init network = new Init();
		
		network.loadTrainData("S1_training.txt");
		network.loadTestData("S1_testing.txt");
		
		network.educate();
		
		network.testNetwork();

		
	}

}
