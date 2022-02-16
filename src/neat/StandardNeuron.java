package neat;

public class StandardNeuron extends Neuron{

	public StandardNeuron(int id, float bias, NeuronType type) {
		super(id, bias, type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public float activationFunction(float num) {
		// TODO Auto-generated method stub
		return (float) (1/(1+Math.pow(Math.E, -1 * num)));
	//	return num;
	}

	@Override
	public Neuron getCopy() {
		return new StandardNeuron(getId(), getBias(), getType());
	}

}
