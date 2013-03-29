package engine.agent;

public class Recipe {
	Boolean needBreakout;
	Boolean needCrossseam;
	Boolean needCutting;
	Boolean needDrilling;
	Boolean needGrinding;
	Boolean needBaking;
	Boolean needPainting;
	Boolean needUV;
	Boolean needWashing;

	public Recipe(Boolean needBreakout,  Boolean needCrossseam,  Boolean needCutting,  Boolean needDrilling,  
			Boolean needGrinding,  Boolean needBaking,  Boolean needPainting,  Boolean needUV, Boolean needWashing){
		this.needBreakout = needBreakout;
		this.needCrossseam = needCrossseam;
		this.needCutting = needCutting;
		this.needDrilling = needDrilling;
		this.needGrinding = needGrinding;
		this.needBaking = needBaking;
		this.needPainting = needPainting;
		this.needUV = needUV;
		this.needWashing = needWashing;
	}
	
	public Recipe(){
		this.needBreakout = true;
		this.needCrossseam = true;
		this.needCutting = true;
		this.needDrilling = true;
		this.needGrinding = true;
		this.needBaking = true;
		this.needPainting = true;
		this.needUV = true;
		this.needWashing = true;
	}
	
	public Boolean getNeedBreakout(){
		return needBreakout;
	}
	
	public Boolean getNeedCrossseam(){
		return needCrossseam;
	}
	
	public Boolean getNeedCutting(){
		return needCutting;
	}
	
	public Boolean getNeedDrilling(){
		return needDrilling;
	}
	
	public Boolean getNeedGrinding(){
		return needGrinding;
	}
	
	public Boolean getNeedBaking(){
		return needBaking;
	}
	
	public Boolean getNeedPainting(){
		return needPainting;
	}
	
	public Boolean getNeedUV(){
		return needUV;
	}
	
	public Boolean getNeedWashing(){
		return needWashing;
	}
}
