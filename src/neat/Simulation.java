package neat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * Template class for creating a simulation
 * @author Gai'Zar
 *
 */
public abstract class Simulation {
	
	/**
	 * Identifier for parent 1 having greater fitness
	 */
	public static final int P1_G_FITNESS = 1;
	
	/**
	 * Identifier for parent 2 having greater fitness
	 */
	public static final int P2_G_FITNESS = -1;
	
	//	Simulation Variables
	public static Random random = new Random();
	/**
	 * 	Current population of genomes.
	 */
	
	public List<Genome> population = new ArrayList<Genome>();
	
	/**
	 * 	Stores the list of unique connections
	 */
	protected List<Gene> innovationDatabase = new ArrayList<Gene>(); 
	
	protected boolean shouldContinue = true;
	protected int generationNumber = 0;
	protected int generation_size;
	protected float mutation_rate;
	
	protected int numInput;
	protected int numOutput;
	
	protected float generationFitness = 0;
	protected float highestFitness = -(Float.MAX_VALUE);
	protected float generationHighest = -(Float.MAX_VALUE);
	public Genome bestGenome = null;
	public Simulation(int generation_size, float mutation_rate, int numInput, int numOutput)
	{
		this.generation_size = generation_size;
		this.mutation_rate = mutation_rate;
		this.numInput = numInput;
		this.numOutput = numOutput;
	}
	
	/**
	 * Simulates a generation
	 */
	public void runGeneration()
	{
		System.out.print("Generation " + generationNumber +"\t");
		if(population.size() == 0)	//	first generation
		{
			//	initialize with empty genomes
			for(int i = 0; i < generation_size;i++)
			{
				List<Gene> genes = new ArrayList<Gene>();
				genes.add(new Gene(0,1,new int[] {0, numInput}, true));
				List<Neuron> neurons = new ArrayList<Neuron>();
				for(int j = 0; j< numInput;j++)
				{
					neurons.add(new StandardNeuron(j,0,NeuronType.Input));
				}
				for(int j = 0; j< numOutput;j++)
				{
					neurons.add(new StandardNeuron(j + numInput,0,NeuronType.Output));
				}
				population.add(new Genome(genes, neurons));
			}
		}
		// run through each genome in the population to assign innovation numbers
		for(int i = 0; i < population.size();i++)
		{
			for(int j = 0; j < population.get(i).getGenes().size();j++)
			{
				boolean isUnique = true;
				for(int k = 0; k < innovationDatabase.size(); k++)
				{
					//	If the gene is in the database 
					if(population.get(i).getGenes().get(j).shareStructure(innovationDatabase.get(k)))
					{
						population.get(i).getGenes().get(j).setID(k);
						isUnique = false;
						break;
					}
				}
				if(isUnique)	// if gene is not in the database
				{
					population.get(i).getGenes().get(j).setID(innovationDatabase.size());
					innovationDatabase.add(population.get(i).getGenes().get(j).getCopy());
				}
			}
		}

		Map<Float, List<Genome>> fitnessMap = new TreeMap<Float, List<Genome>>();
		//	Simulate the populace
		for(int i = 0; i< population.size();i++)
		{
			GeneStats gs = simulate(population.get(i));
			float fitness = evaluateGenome(gs);
			fitnessStats(fitness, population.get(i));
			if(!fitnessMap.containsKey(fitness))
			{
				fitnessMap.put(fitness, new ArrayList<Genome>());
			}
			fitnessMap.get(fitness).add(population.get(i));
		}
		//	33% of the population will be kept to become the parents of the next generation.
		//	a genome's chance of survival is logistic
		
		List<Genome> survivingPopulation = new ArrayList<Genome>();
		int index = 0;
		
		//	To navigate backwards to access fittest individual first
		NavigableSet<Float> descending = ((TreeMap)fitnessMap).descendingKeySet();
		Queue<Map<Integer,Genome>> failedGenomes = new LinkedList<Map<Integer,Genome>>();	
		// in case not enough genomes are stored, the highest fittest ones are stored also stores what index it would be inserted in
		for(float key : descending)
		{
			for(Genome genome : fitnessMap.get(key))
			{
				//	 a genome's relative position is mapped to a new range of -5 to 5
				float newValue = remap(index,0,population.size(),-5,5);
				
				if(random.nextFloat() <
					(1/ (1+4.6*Math.pow(Math.E,-newValue))))
				{
					survivingPopulation.add(genome);
				}
				else if(survivingPopulation.size() + failedGenomes.size() < .33 * generation_size)
				{
					Map<Integer, Genome> temp = new TreeMap<Integer, Genome>();
					temp.put(index, genome);
					failedGenomes.add(temp);
				}
				index++;
			}
			if((survivingPopulation.size() >= .33 * generation_size))
			{
				break;
			}
		}
		while(survivingPopulation.size() < .33 * generation_size)
		{
			Map<Integer, Genome> g = failedGenomes.remove();
			Integer key = 1;
			for(int k : g.keySet())
				key = k;
			survivingPopulation.add(key, g.get(key));
		}
		//	Resetting variables
		population.clear();
		innovationDatabase.clear();
		generationNumber++;
		
		highestFitness= -1 *Float.MAX_VALUE;
		System.out.println(" Average fitness: " + (float)generationFitness/generation_size);
		generationFitness = 0;

		//	Reproducing and mutating
		int i = 0;
		int j = survivingPopulation.size()-1;
		while(population.size() < generation_size)
		{
			int comparison;
			if(i < j)
				comparison = P1_G_FITNESS;
			else
			{
				comparison = P2_G_FITNESS;
			}
			Genome g = reproduce(comparison,survivingPopulation.get(i),survivingPopulation.get(j));
			g = mutate(g);
			population.add(g);
			i++;
			if(i >= survivingPopulation.size())
			{
				i = 0;
			
			}
			boolean checker = true;
			while(checker)
			{
				j = (int)(random.nextFloat()*survivingPopulation.size());
				checker = j == i;
			}
		}
		if(shouldContinue)
			runGeneration();
	}
	
	//	Abstract methods
	
	/**
	 * Template method for defining what fitness value a genome should get.
	 * Use 'simulate(genome)' with this method.
	 * @param genome
	 * @return
	 */
	public abstract float evaluateGenome(GeneStats stats);
	
	/**
	 * Performs the simulation-specific evaluation of a genome. Results are written into a GeneStats variable.
	 * @param genome
	 * @return
	 */
	public abstract GeneStats simulate(Genome genome);
	
	/**
	 * Creates a new Genome based off a mutation from the original genome.
	 * @param genome
	 * @return
	 */
	public Genome mutate(Genome genome)
	{
		int flag;	// 0 if changing only weights, 1 if adding connections, 2 if adding node
		float rand = random.nextFloat();
		if(rand < .80)
			flag = 0;
		else if (rand < .9)
			flag = 1;
		else
			flag = 2;
		List<Gene> geneList = new ArrayList<Gene>();
		
		for(int i = 0; i< genome.getGenes().size();i++)
		{
			Gene temp = genome.getGenes().get(i).getCopy();
			if((random.nextFloat()) < .02f)
			{
				temp.setEnabled(!temp.isEnabled());
			}
			temp.setWeight(temp.getWeight() + (random.nextFloat()-.5f)* 10 );
			geneList.add(temp);
		}
		
		List<Neuron> neuronList = new ArrayList<Neuron>();
		
		for(int i = 0; i< genome.getNeurons().size(); i++)
		{
			Neuron temp = genome.getNeurons().get(i).getCopy();
			temp.setBias(temp.getBias() + (random.nextFloat()-.5f)* 10 );
			neuronList.add(temp);
		}
		
		Genome g = new Genome(geneList, neuronList);
		if(flag == 1)	//	a connection can only be added by checking a gene's orderedEvaluation and picking a connection earlier as the parent
		{
		//	Gene gene = geneList.get((int)(random.nextFloat() * genome.getGenes().size()-1));
			
			//	If there are 5 neurons,  0,4,2,3,1 
			//	then only the first four neurons can form a connection and the neurons after can be connected
			List<Integer> neuronOrder = (List<Integer>) g.orderedEvaluation();

				int firstConnection = (int) (random.nextFloat()*(neuronOrder.size()-1));
				int secondConnection = (int) ((firstConnection+1) + (random.nextFloat()*(neuronOrder.size()-1-firstConnection)) );
				
			Gene gene = new Gene(0,random.nextFloat()*10,
				new int[] {neuronOrder.get(firstConnection), neuronOrder.get(secondConnection)}
				, true);
			boolean isUnique = true;
			for(int i = 0; i < g.getGenes().size();i++)
			{
				if(g.getGenes().get(i).getConnections()[0] == gene.getConnections()[0] &&
						g.getGenes().get(i).getConnections()[1] == gene.getConnections()[1])
				{
					isUnique = false;
					break;
				}
			}
			if(isUnique)
				g.getGenes().add(gene);
			
		}
		else if (flag == 2)
		{
			// if no genes exist
			if(g.getGenes().size() == 0 )
			{
				return g;
			}
			Gene gene = g.getGenes().get((int)(random.nextFloat()*g.getGenes().size()));
			
			//	Grabbing a random neuron to modify
			Neuron neuron = g.getNeurons().get(0).getCopy();
			
			// finding an unused id
			
			for(int i = 1; i < g.getNeurons().size();i++)
				if(g.getNeurons().get(i).getId() > neuron.getId())
					neuron.setId(g.getNeurons().get(i).getId());
			
			neuron.setId(neuron.getId()+1);
			
			neuron.setBias(neuron.getBias() + random.nextFloat()*1-.5f);
			neuron.setType(NeuronType.Internal);
			
			Gene copy1 = gene.getCopy();
			copy1.setWeight(copy1.getWeight()*(random.nextFloat()*2-1));
			Gene copy2 = gene.getCopy();
			copy2.setWeight(copy2.getWeight()*(random.nextFloat()*2-1));
			
			copy1.setConnections(new int[] {gene.getConnections()[0], neuron.getId()});
			copy2.setConnections(new int[] {neuron.getId(), gene.getConnections()[1]});

			if(!gene.isEnabled())
			{
				copy1.setEnabled(false);
				copy2.setEnabled(false);
			}
			
			gene.setEnabled(false);
			
			g.getGenes().add(copy1);
			g.getGenes().add(copy2);
			
			g.getNeurons().add(neuron);
			
		}
		return g;
	}
	
	/**
	 * Creates a new Genome as a child of two parent genomes.
	 * @param identifier - 1 if genome1 is higher fitness, 0 if equal, and -1 if genome2 is higher
	 * @param genome1
	 * @param genome2
	 * @return
	 */
	public Genome reproduce(int identifier, Genome genome1, Genome genome2)
	{
		//	Stores a copy of each innovation number from each parent to determine inheritance
		Map<Integer, Gene> genes = new HashMap<Integer, Gene>();
		
		if(identifier == P1_G_FITNESS)	//	If parent 1's fitness if greater
		{
			for(Gene gene : genome1.getGenes())
			{
				genes.put(gene.getID(),gene.getCopy());
			}
			for(Gene gene : genome2.getGenes())
			{
				if(genes.keySet().contains(gene.getID()) && (random.nextFloat()) > .5)
				{
					Gene temp = gene.getCopy();
					temp.setEnabled(genes.get(gene.getID()).isEnabled());
					genes.put(gene.getID(), temp);
				}
			}
		}
		else if(identifier == P2_G_FITNESS)	//	If parent 1's fitness if greater
		{
			for(Gene gene : genome2.getGenes())
			{
				genes.put(gene.getID(),gene.getCopy());
			}
			for(Gene gene : genome1.getGenes())
			{
				if(genes.keySet().contains(gene.getID()) && (random.nextFloat()) > .5)
				{
					Gene temp = gene.getCopy();
					temp.setEnabled(genes.get(gene.getID()).isEnabled());
					genes.put(gene.getID(), temp);
				}
			}
		}
		ArrayList<Neuron> neurons = new ArrayList<Neuron>();
		
		if(identifier == P1_G_FITNESS)	//	If parent 1's fitness if greater
		{
			//	Inherit topological structure from parent 1
			for(Neuron neuron : genome1.getNeurons())
				neurons.add(neuron.getCopy());
		}
		else if (identifier == P2_G_FITNESS)	//	If parent 2's fitness is greater
		{
			for(Neuron neuron : genome2.getNeurons())
				neurons.add(neuron.getCopy());
		}
		// Return new genome
		List<Gene> geneList = new ArrayList<Gene>();
		for(Integer key : genes.keySet())
			geneList.add(genes.get(key).getCopy());
		
		Genome genome = new Genome(geneList,neurons);
		return genome;
	}
	
	public void fitnessStats(float fitness, Genome g)
	{
		if(fitness > highestFitness)
		{
//			System.out.println("New highest fitness: " + fitness);
			highestFitness = fitness;
			bestGenome = g;
		}
		generationFitness += fitness;
	}
	//	Helper Methods
	//	https://forum.unity.com/threads/re-map-a-number-from-one-range-to-another.119437/
	private static float remap(float val, float initialLow, float initialHigh, float newLow, float newHigh)
	{
		return (val - initialLow) / (initialHigh - initialLow) * (newHigh - newLow) + newLow;
	}
	
	//	Getters and Setters
	
	public List<Genome> getPopulation() {
		return population;
	}
	public void setPopulation(List<Genome> population) {
		this.population = population;
	}
}
