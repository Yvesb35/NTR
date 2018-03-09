
public class Packet {
	private int taille;
	private int time;
	
	public Packet() {
		this.taille = 20;
		this.time = 0;
	}
	
	public int getTaille() {
		return this.taille;
	}
	
	public void setTaille(int taille) {
		this.taille = taille;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public int getTime() {
		return this.time;
	}
}
