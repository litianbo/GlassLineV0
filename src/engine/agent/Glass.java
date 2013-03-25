package engine.agent;

public class Glass {
	// Data
	// public enum GlassState{NULL,ARRIVED_TO_POPUP};
	public Recipe recipe;
	String name;

	// GlassState state;

	// Methods
	public Glass(Recipe r, String name) {
		this.recipe = r;
		this.name = name;
		// this.state = GlassState.NULL;
	}

	public String getName() {
		return name;
	}
}
