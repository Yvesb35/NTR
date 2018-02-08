import java.util.LinkedHashMap;

public class UR {
	
	private Users proprietaire;
		public UR(Users user1){
		this.proprietaire = user1;
	}
		public UR(){
		}
		public Users getProprietaire() {
			return proprietaire;
		}
		public void setProprietaire(Users proprietaire) {
			this.proprietaire = proprietaire;
		}
}
