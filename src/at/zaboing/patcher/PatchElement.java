package at.zaboing.patcher;

public abstract class PatchElement {
	public String toString() {
		return getName() + " - " + getHash();
	}
	
	public abstract String getName();
	public abstract String getHash();
}
