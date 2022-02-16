package neat;

/**
 * <p>A Gene describes the weight and connection pointers between two neurons.</p>
 * <p>An innovation number is assigned outside the Gene and is based on the connection pointers.</p>
 * <p>For example, any gene with a connection from 1 > 2 will be assigned an innovation number of 1.</p>
 * @author Gai'Zar
 *
 */
public class Gene {

	/**
	 * Innovation Number
	 */
	private int id;
	
	/**
	 * Connection weight <br>
	 * Magnitude of First weight represents how much this neuron is influenced<br>
	 * Positivity means positive correlation and vice-versa.<br>
	 */
	private float weight;
	
	/**
	 * An array holding how two neurons are connected.
	 */
	private int[] connections;
	
	/**
	 * Whether or not the gene is enabled.
	 */
	private boolean enabled;
	
	/**
	 * Constructor for a gene
	 * @param id - innovation number
	 * @param weight - connection weight
	 * @param connection1 - first neuron
	 * @param connection2 - second neuron
	 * @param enabled
	 */
	public Gene(int id, float weight, int[] connections, boolean enabled)
	{
		this.id = id;
		this.weight = weight;
		this.connections = connections;
		this.enabled = enabled;
	}
	
	//	Abstract Methods
	
	
	//	Object Methods
	
	/**
	 * Returns true if both genes start from the same neuron and end at the same neuron.
	 * @param gene
	 * @return
	 */
	public boolean shareStructure(Gene gene)
	{
		return connections[0] == gene.getConnections()[0] &&
			connections[1] == gene.getConnections()[1];
	}
	
	/**
	 * Returns an exact copy of this gene
	 * @return
	 */
	public Gene getCopy()
	{
		Gene temp = new Gene(id,weight, connections, enabled);
		return temp;
	}
	//	Getters and Setters
	
	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public int[] getConnections() {
		return connections;
	}

	public void setConnections(int[] connections) {
		this.connections = connections;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
