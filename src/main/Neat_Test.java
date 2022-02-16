package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Random;
import java.util.TreeMap;

import neat.*;

public class Neat_Test {

	public static void main(String[] args) {
	/*	
		ArrayList<Gene> genes = new ArrayList<Gene>();
		genes.add(new Gene(0, 1, new int [] {0,2}, true));
		genes.add(new Gene(1, 1, new int [] {2,1}, true));
		genes.add(new Gene(2, 1, new int [] {2,3}, true));
		genes.add(new Gene(3, 1, new int [] {3,1}, true));
		ArrayList<Neuron> neurons = new ArrayList<Neuron>();
		neurons.add(new StandardNeuron(0, 0, NeuronType.Input));
		neurons.add(new StandardNeuron(1, 0, NeuronType.Output));
		neurons.add(new StandardNeuron(2, 0, NeuronType.Internal));
		neurons.add(new StandardNeuron(3, 0, NeuronType.Internal));
		
		Genome g = new Genome(genes, neurons);
		float[] a = g.feed(new float[] {1});
		System.out.println(a[0] +" " + a[1]);
		if(true)return;
		*/
		TestSimulation sim = new TestSimulation(100, .1f, 1, 3);
		sim.runGeneration();
		
		Genome test = sim.bestGenome;
		
		System.out.println(sim.bestGenome);
		System.out.println("Think " + test.feed(new float[] {.1f})[0] +" with " + test.feed(new float[] {.1f})[1] + " confidence");
		System.out.println("Think " + test.feed(new float[] {.3f})[0] +" with " + test.feed(new float[] {.3f})[1] + " confidence");
		System.out.println("Think " + test.feed(new float[] {.5f})[0] +" with " + test.feed(new float[] {.5f})[1] + " confidence");
		System.out.println("Think " + test.feed(new float[] {.7f})[0] +" with " + test.feed(new float[] {.7f})[1] + " confidence");
		System.out.println("Think " + test.feed(new float[] {.9f})[0] +" with " + test.feed(new float[] {.9f})[1] + " confidence");
		/*
		Map<Float, Integer> mp = new TreeMap<Float, Integer>();
		mp.put(0f,1);
		mp.put(1f,3);
		mp.put(2f,4);
		mp.put(3f,-1);
		mp.put(4f,2);
		mp.put(5f,99);
		
		NavigableSet<Float> descending = ((TreeMap)mp).descendingKeySet();
		
		for(float key : descending)
			System.out.println(key);
		/*
		ArrayList<Gene> genes = new ArrayList<Gene>();

		ArrayList<Neuron> neurons = new ArrayList<Neuron>();
		neurons.add(new TestNeuron(0, 0, NeuronType.Input));
		neurons.add(new TestNeuron(1, 0, NeuronType.Input));
		neurons.add(new TestNeuron(2, 0, NeuronType.Input));
		neurons.add(new TestNeuron(3, 0, NeuronType.Internal));
		neurons.add(new TestNeuron(4, 0, NeuronType.Output));
		neurons.add(new TestNeuron(5, 0, NeuronType.Internal));
		neurons.add(new TestNeuron(6, 0, NeuronType.Internal));
		neurons.add(new TestNeuron(7, 0, NeuronType.Output));
		Genome genome = new Genome(genes,neurons);
		
		float[] arr = genome.feed(new float[] {0,1,0});
		System.out.println("Neuron: " + arr[0] +" with val: " + arr[1]);
		
	*/
	/*	
		ArrayList<Gene> genes1 = new ArrayList<Gene>();
		genes1.add(new Gene(0, 1, new int [] {0,2}, true));
		genes1.add(new Gene(1, 1, new int [] {0,3}, true));
		genes1.add(new Gene(2, 1, new int [] {3,5}, true));
		ArrayList<Neuron> neurons1 = new ArrayList<Neuron>();
		neurons1.add(new TestNeuron(0, 0, NeuronType.Input));
		neurons1.add(new TestNeuron(5, 0, NeuronType.Output));
		neurons1.add(new TestNeuron(2, 0, NeuronType.Internal));
		neurons1.add(new TestNeuron(3, 0, NeuronType.Internal));
		
		Genome g1 = new Genome(genes1,neurons1);
		
		System.out.println(g1.feed(new float[]{1f})[0] + " " + g1.feed(new float[]{1f})[1]);
		ArrayList<Gene> genes2 = new ArrayList<Gene>();
		genes2.add(new Gene(0, 1, new int [] {0,2}, true));
		genes2.add(new Gene(3, 1, new int [] {2,5}, true));
		ArrayList<Neuron> neurons2 = new ArrayList<Neuron>();
		neurons2.add(new TestNeuron(0, 0, NeuronType.Input));
		neurons2.add(new TestNeuron(5, 0, NeuronType.Output));
		neurons2.add(new TestNeuron(2, 0, NeuronType.Internal));
		
		Genome g2 = new Genome(genes2,neurons2);
		Queue<Integer> q = g2.orderedEvaluation();
		while(!q.isEmpty())
		{
			System.out.println("a " +q.remove());
		}
		System.out.println(g2.feed(new float[]{1f})[0] + " " + g2.feed(new float[]{1f})[1]);
		
		Simulation sim = new TestSimulation();
		Genome child = sim.reproduce(Simulation.EQUAL_FITNESS, g1, g2);
		
		for(Gene g : child.getGenes())
		{
			System.out.println(g.getConnections()[0]+" "+g.getConnections()[1]);
		}
		
		System.out.println(child.feed(new float[]{1f})[0] + " " + child.feed(new float[]{1f})[1]);
		*/
	}
	private static float remap(float val, float initialLow, float initialHigh, float newLow, float newHigh)
	{
		return (val - initialLow) / (initialHigh - initialLow) * (newHigh - newLow) + newLow;
	}
}
