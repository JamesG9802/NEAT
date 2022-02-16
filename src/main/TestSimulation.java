package main;

import java.util.Random;

import neat.GeneStats;
import neat.Genome;
import neat.Simulation;

public class TestSimulation extends Simulation{

	public TestSimulation(int generation_size, float mutation_rate, int numInput, int numOutput) {
		super(generation_size, mutation_rate, numInput, numOutput);
		// TODO Auto-generated constructor stub
	}
	
	//	In this test, the genome will try to approximate x^2.
	//	The input will be a random number from -10 to 10.
	//	The first element in GeneStats will be the input, second will be output
	@Override
	public GeneStats simulate(Genome genome) {
		float input = Simulation.random.nextFloat();
		float[] output = genome.feed(new float[] {input});
		GeneStats gs = new GeneStats(){};
		gs.data.add(input);
		gs.data.add(output);
		gs.data.add(genome);
		return gs;
	}
	@Override
	public float evaluateGenome(GeneStats stats) {
		float expected = ((float)stats.data.get(0));
		if(expected < .33)
		{
			expected = 1;
		}
		else if(expected < .66)
		{
			expected = 2;
		}
		else
		{
			expected = 3;
		}
		
		
		float output[] = (float[])stats.data.get(1);
		float fitness = 0;	//(float)(-1 *Math.pow(expected - output,2)+1);
		if(output[0] == expected)
		{
			fitness = output[1] - .02f * ((Genome)stats.data.get(2)).getGenes().size();
		}
		else
		{
			fitness = -output[1] - .02f * ((Genome)stats.data.get(2)).getGenes().size();
		}
		if(generationNumber >= 2)
		{
			shouldContinue = false;
		}
		return fitness;
	}

}
