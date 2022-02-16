package neat;

/**
 * <p>A neuron can either be an input, internal, or hidden.</p>
 * <p>Neurons also contain an int representing their identifier</p>
 * @author Gai'Zar
 *
 */
public abstract class Neuron {

	/**
	 * The unique number associated with a neuron.
	 */
	private int id;
	
	/**
	 * How much a neuron requires from its inputs
	 */
	private float bias;
	/**
	 * The type of neuron.
	 */
	private NeuronType type;
	
	/**
	 * Constructor for a neuron
	 * @param id - unique identifier
	 * @param type - input, internal, or output
	 */
	public Neuron(int id, float bias, NeuronType type)
	{
		this.id = id;
		this.bias = bias;
		this.type = type;
	}
	
	/**
	 * Given an input, apply the biases and the activation function form the output
	 * @param input
	 * @return
	 */
	public float value(float input)
	{
		//	Weighted sum - 
		return activationFunction(input - bias);
	}
	
	public abstract Neuron getCopy();
	
	/**
	 * Activation function for neurons to add nonlinearity.
	 * @param num
	 * @return
	 */
	public abstract float activationFunction(float num);
	
	//	Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public float getBias() {
		return bias;
	}

	public void setBias(float bias) {
		this.bias = bias;
	}

	public NeuronType getType() {
		return type;
	}

	public void setType(NeuronType type) {
		this.type = type;
	}
	
	/*
	 * 	Example Activation Functions
	 */
	
	public static float exact(float num)
	{
		return num;
	}
}
