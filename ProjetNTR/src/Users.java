import java.util.ArrayList;
import java.util.Random;

public class Users {
	private int debit;
	private int debitMoyen;
	private static int nbTotalID = 0;
	private int SommeUR;
	private ArrayList<Packet> bufferToReceive;

	private int ID;

	public Users() {
		int tmp = (int)Math.round(Math.random());
		if(tmp == 0) {
			this.debitMoyen = 8;
		}
		else {
			this.debitMoyen = 6;
		}
		this.debit = this.debitMoyen;
		this.ID=nbTotalID;
		nbTotalID++;
		this.SommeUR=0;
		this.bufferToReceive = new ArrayList<Packet>();
	}

	public int getSommeUR() {
		return this.SommeUR;
	}

	public int getDebit(){
		return this.debit;
	}
	public void setDebit(){
		Random r = new Random();
		this.debit = r.nextInt(this.debitMoyen*2+1 );
	}
	public void setDebit(int deb){
		this.debit = deb;
	}
	public void setDebitMoyen(int debit){
		this.debitMoyen = debit;
	}
	public ArrayList<Packet> getBuffer() {
		return bufferToReceive;
	}
	public void setBuffer(ArrayList<Packet> buffer) {
		this.bufferToReceive = buffer;
	}
	public void ajoutUR(int s){
		this.SommeUR = this.SommeUR + s;
	}
	public boolean assez(){
		return this.SommeUR>=bufferToReceive.size() * 50;
	}
	public boolean vide(){
		return bufferToReceive.isEmpty() ;
	}
	public void addDemand(int nbp){
		for(int i =0;i<nbp;i++){
			Packet pick = new Packet();
			this.bufferToReceive.add(pick);
		}
	}
	public int getID(){
		return this.ID;
	}
	//mettre le cas si taille = 8 et somme ur = 9 (pour pas faire de nb négatif)
	public int soulager(){
		int res =0;
		//Cas où sommeUR est >= 10
		if(this.SommeUR >= 50) {
			//Cas où on a qu'un seul paquet  dans le buffer
			if(this.bufferToReceive.size() == 1) {
				this.SommeUR -= this.bufferToReceive.get(0).getTaille();
				int tmp = this.bufferToReceive.get(0).getTime();
				this.bufferToReceive.remove(0);
				return tmp;
			}
			//Cas où on a plus d'un paquet dans le buffer
			else {
				this.SommeUR -= this.bufferToReceive.get(0).getTaille();
				int tmp = this.bufferToReceive.get(0).getTime();
				this.bufferToReceive.remove(0);
				return tmp;
			}
		}
		//Cas où sommeUR est inférieur à 10
		else {
			if(this.bufferToReceive.get(0).getTaille() <= this.SommeUR) {
				this.SommeUR -= this.bufferToReceive.get(0).getTaille();
				int tmp = this.bufferToReceive.get(0).getTime();
				this.bufferToReceive.remove(0);
				return tmp;
			}
			else {
				this.bufferToReceive.get(0).setTaille(this.bufferToReceive.get(0).getTaille()-this.SommeUR);
				this.SommeUR =0; 
				System.out.println("je suis passer par la");
				res = 0;
			}
		}
		this.SommeUR = 0;
		return res;
	}
	
	public void plusTimes(){
		for( Packet p : bufferToReceive){
			p.setTime(p.getTime()+1);
		}
	}
}
