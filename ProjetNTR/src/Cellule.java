import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

public class Cellule {
	private ArrayList<Users> utilisateurs;
	private ArrayList<Users> utilisateursBesoin;
	private UR[][] bandePassante;
	private HashMap<Long, Integer> delai;
	private int moyenne;

	public Cellule(){
		this.utilisateurs = new ArrayList<Users>();
		this.utilisateursBesoin = new ArrayList<Users>();
		this.bandePassante = new UR[128][5];
		this.delai = new HashMap<Long, Integer>();
	}

	public Users maxSNR(ArrayList<Users> list) {
		Users tmp = list.get(0);
		for(Users i : list){
			if(i.getDebit()>tmp.getDebit()){
				tmp=i;
			}
		}
		return tmp;
	}

	public void addUser(Users login){
		this.utilisateursBesoin.add(login);
	}


	public static void main(String[] args) {
		Users user1 = new Users();
		Users user2 = new Users();

		user1.setDebitMoyen(6);
		user2.setDebitMoyen(8);

		Cellule cell = new Cellule();
		long i = 500000000;
		long compte=0;
		//TODO 
		//Initialisation
		for(int a =0;a<128;a++){
			for(int b=0; b<5;b++){
				cell.bandePassante[a][b]= new UR();
			}
		}
		int somme;
		while(i!=0) {
			cell.utilisateurs.clear();
			cell.utilisateursBesoin.clear();
			cell.addUser(user1);
			cell.addUser(user2);
			cell.maxSNR(cell.utilisateursBesoin);
			user1.setDebit();
			user2.setDebit();

			System.out.println("Débit user0 : " + user1.getDebit());
			System.out.println("Débit user1 : " + user2.getDebit());
			i--;
			for(Users u : cell.utilisateursBesoin){
				Random r = new Random();
				int nbp = r.nextInt(29);
				System.out.println("nbp = "+nbp);
				u.addDemand(nbp);
			}
			somme = 0;
			for(int a =0;a<128;a++){
				for(int b=0; b<5;b++){
					user1.setDebit();
					user2.setDebit();

					Users proprio = cell.maxSNR(cell.utilisateursBesoin);
					while(proprio.assez()){
						cell.utilisateurs.add(proprio);
						System.out.println("chui bloqué sa mere");
						cell.utilisateursBesoin.remove(proprio);
						System.out.println("Besoin = " +cell.utilisateursBesoin.size());
						System.out.println(cell.utilisateurs.size());
						// Tous les utilisateur sont satisfait plus besoin d'attribuer la trame.
						if(cell.utilisateursBesoin.isEmpty()){
							break;
						}
						proprio = cell.maxSNR(cell.utilisateursBesoin);
					}
					// On sort avec une succesion de break.
					if (cell.utilisateursBesoin.isEmpty()){
						break;
					}
					cell.bandePassante[a][b].setProprietaire(proprio);
					proprio.ajoutUR(proprio.getDebit());
					somme += proprio.getDebit();
					System.out.print("Popriétaire: " + cell.bandePassante[a][b].getProprietaire().getID());
					System.out.println("avec le debit = " + cell.bandePassante[a][b].getProprietaire().getDebit());
				}
				// Le dernier est ici.
				if (cell.utilisateursBesoin.isEmpty()){
					break;
				}
			}
			System.out.println("débit de la trame " + somme/128*5);
			int delai = 0;
			int conso = 0;
			for (Users u : cell.utilisateurs){
				System.out.println("tamer");
				while(!u.getBuffer().isEmpty() && u.getSommeUR() != 0) {
					if (u.getBuffer().size()!=0){
						conso = u.getBuffer().size();
						delai = u.soulager();
						conso-=u.getBuffer().size();
						cell.moyenne = delai/conso;
						cell.delai.put(compte, cell.moyenne);
					}
				}
			}
			for (Users u : cell.utilisateursBesoin){
				while(!u.getBuffer().isEmpty() && u.getSommeUR() != 0) {
					if (u.getBuffer().size()!=0){
						conso = u.getBuffer().size();
						delai = u.soulager();
						conso-=u.getBuffer().size();
						cell.moyenne = delai/conso;
						cell.delai.put(compte, cell.moyenne);
						System.out.println("tamer2");
					}
				}
			}

			compte ++;
		}
		System.out.println(i);
		
	}

}
