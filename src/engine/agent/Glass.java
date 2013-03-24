package engine.agent;

public class Glass {
//Data
	//enum GlassState;
	public Recipe recipe;
	String name;
	//GlassState state;

	//Methods
	public Glass(Recipe r, String name) {
	this.recipe = r;
	this.name = name;
	//this.state = GlassState.start;
	}
	
	
	public String getName(){
		return name;
	}
}
