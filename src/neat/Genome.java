package neat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * A genome contains a list of genes and neurons.
 * @author Gai'Zar
 *
 */
public class Genome {

	/**
	 * List of genes
	 */
	private List<Gene> genes;
	
	/**
	 * List of neurons
	 */
	private List<Neuron> neurons;
	
	public Genome(List<Gene> genes, List<Neuron> neurons)
	{
		this.genes = genes;
		this.neurons = neurons;
	}
	
	//	Object Methods
	
	/**
	 * WARNING: This method (WILL) have problems if the genome does not have any genes <br><br>
	 * Used during a simulation. Inputs are fed to the genome and the neural structure returns 
	 * the output neuron with the highest value and the value it returned
	 * @param inputs - assumed to be formatted where index 0 corresponds to the first input neuron. <br>
	 * assumed that inputs.size = number of input neurons
	 * @return - floats where the first float is the neuron and the second is the confidence
	 */
	public float[] feed(float[] inputs)
	{
		//	Weights represents the current output value of a neuron
		HashMap<Integer,Float> weights = new HashMap<Integer, Float>();
		Queue<Integer> neuronsToEvaluate = orderedEvaluation();
		
		//	Feeding the inputs to the initial layer
		
		for(int input = 0; input < inputs.length; input++)
		{
			for(int i = 0; i< neurons.size();i++)
			{
				if(neurons.get(i).getId() == input)
				{
					weights.put(input, inputs[input]);
					continue;
				}
			}
		}
		
		List<Gene> geneCopy = new ArrayList<Gene>();
		for(Gene g: genes)
			geneCopy.add(g);
		
		//	Evaluate the neurons
		
		while(!neuronsToEvaluate.isEmpty())
		{
			int currentNeuron = neuronsToEvaluate.remove();
			
			//	At this point, this neuron is guaranteed to have it's parents finish their calculations
			//	If the neuron is not an internal neuron, their value becomes their activation function(weighted sum - bias)
			
			Neuron neuron = null;
			for(int i = 0; i< neurons.size();i++)	//	Makes sure to get the right neuron regardless of order
				if(neurons.get(i).getId() == currentNeuron)
				{
					neuron = neurons.get(i);
					break;
				}
			
			if(neuron.getType() != NeuronType.Input && 
					weights.containsKey(currentNeuron))
				weights.put(currentNeuron, neuron.value(weights.get(currentNeuron)));
		
			//	Neuron checks the genome for all the genes to determine connections.
			//	For each connecting gene, their weights are updated 
			
			for(int i = 0; i< geneCopy.size(); i++)
			{
				Gene gene = geneCopy.get(i);
				
				//	If gene is disabled, it doesn't contribute to network
				if(!gene.isEnabled())
				{
					geneCopy.remove(i);
					i--;
					continue;
				}
				//	If there is a connection
				if(gene.getConnections()[0] == currentNeuron)
				{
					int connectingNeuron = gene.getConnections()[1];
					
					//	Prevent duplicate additions, due to ordering, there won't be any errors
					if(!neuronsToEvaluate.contains(connectingNeuron))
					{
						neuronsToEvaluate.add(connectingNeuron);
					}
					
					//	If weights does not have the current neuron, a connection must have been severed/disabled
					if(weights.containsKey(currentNeuron))
					{	
						//	Mapping doesn't exist
						if(!weights.containsKey(connectingNeuron))
						{
							//	Initialized with connecting neuron's value and weighted by genome's connection strength
							weights.put(connectingNeuron, 
								gene.getWeight() * weights.get(currentNeuron));
						}
						
						//	Mapping exists
						else
						{
							weights.put(connectingNeuron, 
								weights.get(connectingNeuron) + 
								gene.getWeight() * weights.get(currentNeuron));
						}
					}
					geneCopy.remove(i);
					i--;
				}
			}
		}
		
		int neuron_num = 0;
		float max_val = -Float.MAX_VALUE;
		//	Find greatest value from outputs
		for(int i = 0; i < neurons.size(); i++)
		{
			if(weights.containsKey(neurons.get(i).getId()) && 	//	contains neuron weight
				neurons.get(i).getType() == NeuronType.Output &&	//	is output neuron
				weights.get(neurons.get(i).getId()) > max_val )	//	is greater than previous max
			{
				max_val = weights.get(neurons.get(i).getId());
				neuron_num = neurons.get(i).getId();
			}
		}
		return new float[] {neuron_num, max_val};
	}
	
	//	Helper Methods
	
	/**
	 * Looks through the genome and returns an ordered queue where each element
	 * is guaranteed to have all the values it needs to perform its calculation
	 * @return
	 */
	public Queue<Integer> orderedEvaluation()
	{
		int firstTime = 0;
		/*	Connections can be assigned a score starting from 0.
		 * 	0 represents an input node.
		*/
		Queue<Integer> queue = new LinkedList<Integer>();
		
		/*	Representation of a tree
		*	Key - neuron number
		*	Points to own level;
		*/
		HashMap<Integer,Integer> tree = new HashMap<Integer,Integer>();
		
		//	Initialize input neurons
		for(Neuron n : neurons)
			if(n.getType() == NeuronType.Input)
				tree.put(n.getId(), 0);
		
		//	Genes must be  added to a queue since if a child is processed before its parent, it will not correctly add its level
		Queue<Gene> g = new LinkedList<Gene>();
		for(Gene gene: genes)
			g.add(gene);
		
		//	Go through genome and add to tree
		while(!g.isEmpty())
		{
			Gene gene = g.remove();
			int parent = gene.getConnections()[0];
			int child = gene.getConnections()[1];

			firstTime++;
			//	If the parent was not processed yet
			if(!tree.containsKey(parent))
			{
				g.add(gene);
				continue;
			}
			//	if the mapping doesn't exist or if this new connection places the child on a higher level
			if (!tree.containsKey(child) || tree.get(child) < tree.get(parent) + 1)
				tree.put(child, tree.get(parent) + 1 );
		}
		//	converting tree into array for quicksort
		int [][] quicksortArr = new int[2][tree.keySet().size()];
		int i = 0;
		for(Integer key : tree.keySet())
		{
			quicksortArr[0][i] = tree.get(key);
			quicksortArr[1][i] = key;
			i++;
		}
		quicksort(quicksortArr,0,tree.keySet().size()-1);
		for(int j = 0; j < tree.keySet().size();j++)
		{
			queue.add(quicksortArr[1][j]);
		}
		return queue;
	}

	//	Quicksort methods
	private void quicksort(int[][] arr, int low, int high)
	{
		if(low < high)
		{
			int pi = partition(arr, low, high);
			quicksort(arr,low,pi-1);
			quicksort(arr,pi+1,high);
		}
	}
	private int partition(int[][]arr, int low, int high)
	{
		int pivot = arr[0][high];
		
		int i = (low-1);
		
		for(int j = low; j <= high - 1;j++)
		{
			if(arr[0][j] < pivot)
			{
				i++;
				swap(arr,i,j);
			}
		}
		swap(arr, i + 1, high);
		return (i+1);
	}
	private void swap(int [][] arr, int i, int j)
	{
		int temp1 = arr[0][i];
		int temp2 = arr[1][i];
		arr[0][i] = arr[0][j];
		arr[1][i] = arr[1][j];
		arr[0][j] = temp1;
		arr[1][j] = temp2;
	}

	@Override
	public String toString()
	{
		String str = "";
		for(int i = 0; i< genes.size();i++)
		{
			System.out.println("Gene no. " + genes.get(i).getID() +" : " +" from "+ genes.get(i).getConnections()[0]+
				" to "+ genes.get(i).getConnections()[1] +" is " + genes.get(i).isEnabled() + " with weight " + genes.get(i).getWeight());
		}
		for(int i = 0; i< neurons.size();i++)
		{
			System.out.println("Neurons no." + i +" : "+ neurons.get(i).getId()+
					" with bias of "+  neurons.get(i).getBias() 
					+" is a " + neurons.get(i).getType());
		}
		return str;
	}
	
	//	Getters and Setters
	
	public List<Gene> getGenes() {
		return genes;
	}
	public void setGenes(List<Gene> genes) {
		this.genes = genes;
	}
	public List<Neuron> getNeurons() {
		return neurons;
	}
	public void setNeurons(List<Neuron> neurons) {
		this.neurons = neurons;
	}
	
}
