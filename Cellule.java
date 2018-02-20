import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Cellule {
	private ArrayList<Users> utilisateurs;
	private ArrayList<Users> roundrobin;
	private ArrayList<Users> utilisateursBesoin;
	private UR[][] bandePassante;
	private HashMap<Users, ArrayList<Integer>> delaiUtilisateurs;
	private int moyenne;

	public Cellule() {
		this.utilisateurs = new ArrayList<Users>();
		this.utilisateursBesoin = new ArrayList<Users>();
		this.bandePassante = new UR[128][5];
		this.delaiUtilisateurs = new HashMap<Users, ArrayList<Integer>>();
	}
	
	public Users roundRobin() {
		Users user = this.roundrobin.remove(0);
		this.roundrobin.add(user);
		return user;
	}
	public Users maxSNR(ArrayList<Users> list) {
		Users tmp = list.get(0);
		for (Users i : list) {
			if (i.getDebit() > tmp.getDebit()) {
				tmp = i;
			}
		}
		return tmp;
	}

	public int moyenndelai(ArrayList<Integer> al) {
		int res = 0;
		for (Integer i : al) {
			res += i;
		}
		res = res / al.size();
		return res;
	}

	public void initdelaiuser(ArrayList<Users> us) {
		for (Users use : us) {
			delaiUtilisateurs.put(use, new ArrayList<Integer>());
		}
	}

	public void addUser(Users login) {
		this.utilisateursBesoin.add(login);
	}

	public static void main(String[] args) {

		int moyenneGlobal = 0;
		double delaiGlobal = 0;
		double consoGlobal = 0;
		int consoUserTrame = 0;
		int delaiUserTrame = 0;
		Users user1 = new Users();
		Users user2 = new Users();
		Users user3 = new Users();
		Users user4 = new Users();
		Users user5 = new Users();
		Users user6 = new Users();
		Users user7 = new Users();
		Users user8 = new Users();
		Users user9 = new Users();
		Users user10 = new Users();
		
		Cellule cell = new Cellule();
		long i = 10000;
		long compte = 0;
		// TODO
		// Initialisation
		for (int a = 0; a < 128; a++) {
			for (int b = 0; b < 5; b++) {
				cell.bandePassante[a][b] = new UR();
			}
		}
		//int somme;
		long debut = System.currentTimeMillis();
		while (i != 0) {
			cell.utilisateurs.clear();
			cell.utilisateursBesoin.clear();
			cell.addUser(user1);
			cell.addUser(user2);
			cell.addUser(user3);
			cell.addUser(user4);
			cell.addUser(user5);
			cell.addUser(user6);
			cell.addUser(user7);
			cell.addUser(user8);
			cell.addUser(user9);
			cell.addUser(user10);
			cell.maxSNR(cell.utilisateursBesoin);
			cell.initdelaiuser(cell.utilisateursBesoin);
			user1.setDebit();
			user2.setDebit();
			user3.setDebit();
			user4.setDebit();
			user5.setDebit();
			user6.setDebit();
			user7.setDebit();
			user8.setDebit();
			user9.setDebit();
			user10.setDebit();

			System.out.println("Débit user0 : " + user1.getDebit());
			System.out.println("Débit user1 : " + user2.getDebit());
			System.out.println("Débit user0 : " + user3.getDebit());
			System.out.println("Débit user1 : " + user4.getDebit());
			System.out.println("Débit user0 : " + user5.getDebit());
			System.out.println("Débit user1 : " + user6.getDebit());
			System.out.println("Débit user0 : " + user7.getDebit());
			System.out.println("Débit user1 : " + user8.getDebit());
			System.out.println("Débit user0 : " + user9.getDebit());
			System.out.println("Débit user1 : " + user10.getDebit());
			i--;
			// On initialise a chaque trame le nombre de paquet a recevoir des utilisateurs
			for (Users u : cell.utilisateursBesoin) {
				Random r = new Random();
				int nbp = r.nextInt(100);
				System.out.println("nbp = " + nbp);
				System.out.println(cell.delaiUtilisateurs.get(u));
				u.addDemand(nbp);
			}
			//somme = 0;
			for (int a = 0; a < 128; a++) {
				for (int b = 0; b < 5; b++) {
					user1.setDebit();
					user2.setDebit();
					user3.setDebit();
					user4.setDebit();
					user5.setDebit();
					user6.setDebit();
					user7.setDebit();
					user8.setDebit();
					user9.setDebit();
					user10.setDebit();

					Users proprio = cell.maxSNR(cell.utilisateursBesoin);
					while (proprio.assez()) {
						cell.utilisateurs.add(proprio);
						cell.utilisateursBesoin.remove(proprio);
						System.out.println("Nb d'utilisateurs qui ont besoin = " + cell.utilisateursBesoin.size());
						System.out.println("Nb d'utilisateurs satisfaits = " + cell.utilisateurs.size());
						// Tous les utilisateur sont satisfait plus besoin
						// d'attribuer la trame.
						if (cell.utilisateursBesoin.isEmpty()) {
							break;
						}
						proprio = cell.maxSNR(cell.utilisateursBesoin);
					}
					// On sort avec une succesion de break.
					if (cell.utilisateursBesoin.isEmpty()) {
						break;
					}
					cell.bandePassante[a][b].setProprietaire(proprio);
					proprio.ajoutUR(proprio.getDebit());
					//somme += proprio.getDebit();
					System.out.print("Popriétaire: " + cell.bandePassante[a][b].getProprietaire().getID());
					System.out.println(" avec le debit = " + cell.bandePassante[a][b].getProprietaire().getDebit());
				}
				// Le dernier est ici.
				if (cell.utilisateursBesoin.isEmpty()) {
					break;
				}
			}
			// On a fini la trame 
			int delai = 0;
			int conso = 0;
			for (Users u : cell.utilisateurs) {
				consoUserTrame = 0;
				delaiUserTrame = 0;
				conso = u.getBuffer().size();
				while (!u.getBuffer().isEmpty() && u.getSommeUR() != 0) {
					if (u.getBuffer().size() != 0) {
						delai = u.soulager();
						if (delai > -1) {
							delaiUserTrame += delai;
							delaiGlobal += delai;
						}
					}
				}
				conso -= u.getBuffer().size();
				if (conso > 0) {
					consoGlobal += conso;
					consoUserTrame += conso;
				}
				if(consoUserTrame !=0){
				System.out.println(u.getID());
				cell.moyenne = delaiUserTrame / consoUserTrame;
				cell.delaiUtilisateurs.get(u).add(cell.moyenne);// Porbleme ici
																// et y'a une
																// probabilité
																// de division
																// par 0
				}
				u.plusTimes();
			}
			for (Users u : cell.utilisateursBesoin) {
				consoUserTrame = 0;
				delaiUserTrame = 0;
				conso = u.getBuffer().size();
				while (!u.getBuffer().isEmpty() && u.getSommeUR() != 0) {
					if (u.getBuffer().size() != 0) {
						delai = u.soulager();
						if (delai > -1) {
							delaiUserTrame += delai;
							delaiGlobal += delai;
						}

					}
				}
				conso -= u.getBuffer().size();
				if (conso > 0) {
					consoGlobal += conso;
					consoUserTrame += conso;
				}
				System.out.println(u.getID());
				if(consoUserTrame !=0){
				cell.moyenne = delaiUserTrame / consoUserTrame;
				cell.delaiUtilisateurs.get(u).add(cell.moyenne);
			}
				u.plusTimes();
			}

			compte++;
		}
		System.out.println(cell.delaiUtilisateurs.size());
		for (Users u : cell.delaiUtilisateurs.keySet()) {
			System.out.println(cell.moyenndelai(cell.delaiUtilisateurs.get(u)));
		}
		moyenneGlobal = (int) (delaiGlobal / consoGlobal);
		System.out.println("Moyenne global = " + moyenneGlobal);
		System.out.println("Conso global = " + consoGlobal);
		System.out.println("delai global = " + delaiGlobal);
		long fin = System.currentTimeMillis();
		System.out.println("Temps d'execution = " + (fin-debut)/1000 + "secondes");
	}
}
