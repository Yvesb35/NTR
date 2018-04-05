import java.util.LinkedHashMap;

public class UR {
	
	private Users proprietaire;
	private int nbTrame;
		public UR(Users user1){
			this.proprietaire = user1;
			this.nbTrame = 0;
	}
		public UR(){
		}
		public Users getProprietaire() {
			return proprietaire;
		}
		public void setProprietaire(Users proprietaire) {
			this.proprietaire = proprietaire;
		}
		public int getNbTrame() {
			return this.nbTrame;
		}
}
