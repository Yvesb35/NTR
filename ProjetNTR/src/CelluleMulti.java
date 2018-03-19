import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CelluleMulti {
	private ArrayList<Users> utilisateursServis;
	private ArrayList<Users> roundrobin;
	private ArrayList<Users> utilisateursBesoin;
	private UR[][] bandePassante;
	private HashMap<Users, ArrayList<Double>> delaiUtilisateurs;
	private double moyenne;
	private double[] resultatFinal;
	private double[] rempliTramePourcentage;
	private ArrayList<Double> remplitramePour1000;
	private double[] sommepaquetCreer;
	private HashMap<Users, ArrayList<Double>> energieUtilisateurs;

	public CelluleMulti() {
		this.utilisateursServis = new ArrayList<Users>();
		this.utilisateursBesoin = new ArrayList<Users>();
		this.bandePassante = new UR[128][5];
		this.delaiUtilisateurs = new HashMap<Users, ArrayList<Double>>();
		this.resultatFinal = new double[10];
		this.rempliTramePourcentage = new double[10];
		this.roundrobin= new ArrayList<Users>();
		this.remplitramePour1000 = new ArrayList<Double>(); 
		this.sommepaquetCreer = new double[10];
	}

	public Users roundRobin(ArrayList<Users> list) {
		Users user = list.remove(0);
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

	public double moyenneListe(ArrayList<Double> al) {
		double res = 0;
		if(!al.isEmpty()){
			for (Double i : al) {
				res += i;
			}
			res = res / al.size();
		}
		return res;
	}
	
	public int moyenndelaiINT(ArrayList<Integer> al) {
		int res = 0;
		if(!al.isEmpty()){
			for (Integer i : al) {
				res += i;
			}
			res = res / al.size();
		}

		return res;
	}
	
	public void initDelaiUser(ArrayList<Users> us) {
		for (Users use : us) {
			delaiUtilisateurs.put(use, new ArrayList<Double>());
		}
	}
	
	public void clearTimeSlot() {
		for (Users u : this.utilisateursBesoin) {
			u.setDejaEu(false);
		}
	}
	public String toString(double[] tab){
		String res = "[";
		for (int i =0 ; i<tab.length;i++){
			res += tab[i]+";";
		}
		res += "]";
		return res;
	}

	public void addUser(Users login) {
		this.utilisateursBesoin.add(login);
	}
	public void checkTheRandom( Users[] tab, int nb){
		for (int i =0; i<nb*2;i++){
			tab[i].setRandomNBTrame(tab[i].getRandomNBTrame()-1);
			if (tab[i].getRandomNBTrame() <= 0){
				Random r = new Random();
				tab[i].setRandomNBTrame(10);
				tab[i].setMoyNBpaq(r.nextInt(101)+1);
			}
		}
	}
	public void varierDebit(){
		for( Users u : this.utilisateursBesoin){
			u.setDebit();
		}
	}
	
	public void menage() {
		// on vide la map utilisateur / delai
		this.delaiUtilisateurs.clear();
		// On vide la liste des remplissage de la trame
		this.remplitramePour1000.clear();
		//On remet la moyenne a 0
		this.moyenne = 0;
	}
	
	public static void main(String[] args) {
		// On creer notre Cellule
		CelluleMulti cell = new CelluleMulti();
		// On creer tout les utilisateurs
		Users [] tabUser = new Users[20];
		
		
		long nbTrame = 10000;
		double moyenneGlobal = 0;
		double delaiGlobal = 0;
		double consoGlobal = 0;
		long i = nbTrame;
		long compte = 0;
		double remplissageTrame = 0;
		int DivUser = 1;
		ArrayList<Users> ListeUsersMax = new ArrayList<Users>();
		// Initialisation de la Bande Passante.
		for (int a = 0; a < 128; a++) {
			for (int b = 0; b < 5; b++) {
				cell.bandePassante[a][b] = new UR();
			}
		}
		
		
		
		// DEBUT DE LA SIMULATION
		long debut = System.currentTimeMillis();
		
		for (int nbUser = 1; nbUser<11; nbUser++){
			System.out.println("C'est parti pour "+ nbUser *2 + " utilisateurs");
			System.out.println("*********************************************");
			System.out.println("*********************************************");
			
			//On creer des utilisateur a chaque fois qu'on refait 
			for(int ind = 0; ind <nbUser*2; ind++){
				tabUser[ind] = new Users();
				if(ind % 2 == 0){
					tabUser[ind].setDebitMoyen(6);
				}else {
					tabUser[ind].setDebitMoyen(8);
				}
				cell.delaiUtilisateurs.put(tabUser[ind], new ArrayList<Double>());
			}
			
			//int somme;
			//XYSeries serie = new XYSeries("Delai ");
			// On remet a chaque fois le nombre de trame que l'on doit envoyer
			i=nbTrame;
			long x = i;
			while (i != 0) {
				cell.utilisateursServis.clear();
				cell.utilisateursBesoin.clear();
				// On ajoute dans la liste des utilisateurs dans le besoin le nombre qu'il faut
				for (int indicetabUser = 0; indicetabUser<nbUser*2; indicetabUser++){
					cell.addUser(tabUser[indicetabUser]);
				}
				
				cell.varierDebit();
				cell.maxSNR(cell.utilisateursBesoin);
				// Je sais pas ce que �a fou la ca ?
				//cell.initDelaiUser(cell.utilisateursBesoin);
	
				i--;
				x++;
				// On initialise a chaque trame le nombre de paquet a recevoir des utilisateurs
				cell.checkTheRandom(tabUser, nbUser);
				for (Users u : cell.utilisateursBesoin) {
					//TODO APPLIQUER CETTE FONCTION (LOI EXPONENTIELLE)
			
					int moypaquet = u.getMoyNBpaq();
					//System.out.println("moypaquet = "+ moypaquet);
					//ExponentialDistribution exp = new ExponentialDistribution((double) moypaquet);
					Random generator;
					generator = new Random();
					double num = generator.nextGaussian();
					
					double nbp = (num * 15) + moypaquet;
					while(nbp < 0) {
						generator = new Random();
						num = generator.nextGaussian();
						nbp = (num * 15 ) + moypaquet;
					}
					if(x%500 == 0) {
						nbp = 3500;
					}
					//System.out.println(nbp);
					/*Random rand = new Random();
				    double tmp = rand.nextDouble();
				    System.out.println(tmp);
					double nbp = -(1 / moypaquet) * Math.log(1 - tmp);*///r.nextInt(2*moypaquet+1);
					//System.out.println(nbp);
					//System.out.println("nbp = " + nbp);
					cell.sommepaquetCreer[nbUser-1] += (double)nbp;
					//System.out.println(cell.delaiUtilisateurs.get(u));
					u.addDemand((int)nbp);
				}
				//somme = 0;
				double URalloue =0;
				for (int timeSlot = 0;timeSlot < 5;timeSlot++){
					cell.clearTimeSlot();
					for (int k =0 ; k<DivUser; k ++){
						Users chanceux = cell.maxSNR(cell.utilisateursBesoin);
						ListeUsersMax.add(chanceux);
						cell.utilisateursBesoin.remove(chanceux);
					}
					for (int Porteuse = 0; Porteuse < 128;Porteuse++){					
						cell.varierDebit();
						Users proprio =  cell.maxSNR(ListeUsersMax);
						while (proprio.assez()) {
							cell.utilisateursServis.add(proprio);
							ListeUsersMax.remove(proprio);// cell.utilisateurBesoin
							// Tous les utilisateur sont satisfait plus besoin
							// d'attribuer la trame.
							if (ListeUsersMax.isEmpty()) {
								if(cell.utilisateursBesoin.isEmpty()) {
									break;
								}else {
									for (int k =0 ; k<DivUser && cell.utilisateursBesoin.isEmpty(); k ++){
										Users chanceux = cell.maxSNR(cell.utilisateursBesoin);
										ListeUsersMax.add(chanceux);
										cell.utilisateursBesoin.remove(chanceux);
									}
								}
								
							}
							proprio = cell.maxSNR(ListeUsersMax);
						}
						// On sort avec une succesion de break.
						if (cell.utilisateursBesoin.isEmpty()&& ListeUsersMax.isEmpty()) {
							break;
						}
						cell.bandePassante[Porteuse][timeSlot].setProprietaire(proprio);
						proprio.addEnergie();
						URalloue++;
						proprio.ajoutUR(proprio.getDebit());
					}
					// Derniere porteurse.
					if (cell.utilisateursBesoin.isEmpty() && ListeUsersMax.isEmpty()) {
						break;
					}
					cell.utilisateursBesoin.addAll(ListeUsersMax);// remet dans la liste des besoin les users qui n'ont pas �t� servis assez.
				}
				// On a fini la trame 
				remplissageTrame = (URalloue/(128*5))*100;
				cell.remplitramePour1000.add(remplissageTrame);// On met le taux de remplissage dans la liste
				
				double delai = 0;
				double conso = 0;
				for (Users u : cell.utilisateursServis) {
					conso = u.getBuffer().size();
					while (!u.getBuffer().isEmpty() && u.getSommeUR() != 0) {
						if (u.getBuffer().size() != 0) {
							delai = u.soulager();
							if (delai > -1) {
								u.setDelai(u.getDelai() + delai);
								delaiGlobal += delai;
							}
							if (u.getConso()==500){
								cell.moyenne = u.getDelai()/ u.getConso();
								cell.delaiUtilisateurs.get(u).add(cell.moyenne);
								u.setDelai(0);
								u.setConso(u.getConso()-500);
								
							}
						}
					}

					conso -= u.getBuffer().size();
					if (conso > 0) {
						consoGlobal += conso;
					}
					
					
					u.plusTimes();
				}
				for (Users u : cell.utilisateursBesoin) {
					conso = u.getBuffer().size();
					while (!u.getBuffer().isEmpty() && u.getSommeUR() != 0) {
						if (u.getBuffer().size() != 0) {
							delai = u.soulager();
							if (delai > -1) {
								u.setDelai(u.getDelai() + delai);
								delaiGlobal += delai;
							}
							if (u.getConso()==500){
								cell.moyenne = u.getDelai()/ u.getConso();
								cell.delaiUtilisateurs.get(u).add(cell.moyenne);
								u.setDelai(0);
								u.setConso(u.getConso()-500);
								
							}
						}
					}

					conso -= u.getBuffer().size();
					if (conso > 0) {
						consoGlobal += conso;
					}	
					u.plusTimes();
				}
				cell.checkTheRandom(tabUser,nbUser);
				compte++;
			}
			// On a fini toutes les trames.
			// On fait les moyennes pour chaque users.
			System.out.println("Moyenne remplissageTrame = " + cell.moyenneListe(cell.remplitramePour1000));
			cell.rempliTramePourcentage[nbUser-1] = cell.moyenneListe(cell.remplitramePour1000);
			double moyenne = 0 ; 
			for (Users u : cell.delaiUtilisateurs.keySet()){
				moyenne +=  cell.moyenneListe(cell.delaiUtilisateurs.get(u));
				/*if (u.getDelaiMoyen()==6){
					moyenneloin += cell.moyenneListe(cell.delaiUtilisateurs.get(u));
				}else{
					moyenneproche += cell.moyenneListe(cell.delaiUtilisateurs.get(u));
				}*/
				//size += cell.delaiUtilisateurs.get(u).size();
				//On doit clear tout les infos des users utilisé
				/*for(int k =0;k<nbUser*2;k++){
					tabUser[k].clear();
					cell.delaiUtilisateurs.get(tabUser[k]).clear();
				}*/
			}
			// On fait la moyenne final 
			double moyennefinal = moyenne / (nbUser*2);
			//double moyennefinalproche = moyenneproche/ (nbUser*2);
			//double moyennefinalloin = moyenneloin / (nbUser *2);
			// On la stocke dans le tableau qui va servir a faire le graphe après.
			cell.resultatFinal[nbUser-1] = moyennefinal;
			//cell.resultatfinalloin[nbUser - 1] = moyennefinalloin;
			//cell.resultatfinalproche[nbUser -1] = moyennefinalproche;
			/*XYDataset xyDatasettest = new XYSeriesCollection(serie);
			JFreeChart charttest = ChartFactory.createXYLineChart
				      ("NBP paquet User1", "nb trames", "nbp", xyDatasettest, PlotOrientation.VERTICAL, true, true, false);
			ChartFrame frametest=new ChartFrame("Courbe %nbp/nb trame",charttest);
			frametest.setVisible(true);
			frametest.setSize(300,300);*/
			// On fait le m�nage.
			cell.menage();
		}
		//on sort du nbUser
		System.out.println(cell.toString(cell.resultatFinal));
		//Global
		XYSeries serie1 = new XYSeries("Courbe Délai");
		//Ceux qui sont proche
		XYSeries serie2 = new XYSeries("% UR");
		// Ceux qui sont loin
		XYSeries serie3 = new XYSeries("Nb Paquets");
		
		/*XYSeries serie4 = new XYSeries("Délai à 10 utilisateurs");
		serie4.add(10, cell.resultatFinal[4]);*/

		for(int j = 0 ; j<cell.resultatFinal.length;j++){
			serie1.add((j+1)*2, cell.resultatFinal[j]);
		}
		for(int j = 0 ; j<cell.rempliTramePourcentage.length;j++){
			serie2.add((j+1)*2, cell.rempliTramePourcentage[j]);
		}
		for(int j = 0 ; j<cell.sommepaquetCreer.length;j++){
			serie3.add((j+1)*2, cell.sommepaquetCreer[j]);
		}
		moyenneGlobal = delaiGlobal / consoGlobal;
		System.out.println("Moyenne global = " + moyenneGlobal);
		long fin = System.currentTimeMillis();
		System.out.println("Temps d'execution = " + (fin-debut)/1000 + "secondes");
		
		/*
		 * Affichage des Courbes
		 * 
		 * 
		 */
		XYDataset xyDataset = new XYSeriesCollection(serie1);
		JFreeChart chart = ChartFactory.createXYLineChart
			      ("Délai en fonction du Trafic Load", "TL", "Delai", xyDataset, PlotOrientation.VERTICAL, true, true, false);
		ChartFrame frame1=new ChartFrame("Courbe D/TL",chart);
		frame1.setVisible(true);
		frame1.setSize(300,300);
		
		XYDataset xyDatasetRemplissage = new XYSeriesCollection(serie2);
		JFreeChart chartRemplissage = ChartFactory.createXYLineChart
			      ("% RU prises en fonction du Trafic Load", "TL", "% RU", xyDatasetRemplissage, PlotOrientation.VERTICAL, true, true, false);
		ChartFrame frameRemplissage=new ChartFrame("Courbe %RU/TL",chartRemplissage);
		frameRemplissage.setVisible(true);
		frameRemplissage.setSize(300,300);
		
		XYDataset xyDatasetNBPaquets = new XYSeriesCollection(serie3);
		JFreeChart chartNBPaquets = ChartFactory.createXYLineChart
			      ("NB PAQUETS en fonction du Trafic Load", "TL", "NBPAQUETS", xyDatasetNBPaquets, PlotOrientation.VERTICAL, true, true, false);
		ChartFrame frameNBPaquets=new ChartFrame("Courbe NbPaquets/TL",chartNBPaquets);
		frameNBPaquets.setVisible(true);
		frameNBPaquets.setSize(300,300);
		
		/*XYDataset xyDatasetRemplissage10Users = new XYSeriesCollection(serie4);
		JFreeChart chartRemplissage10Users = ChartFactory.createXYLineChart
			      ("Délai pour 10 utilisateurs", "TL", "Delai", xyDatasetRemplissage10Users, PlotOrientation.VERTICAL, true, true, false);
		ChartFrame frameRemplissage10Users=new ChartFrame("Courbe D/TL",chartRemplissage10Users);
		frameRemplissage10Users.setVisible(true);
		frameRemplissage10Users.setSize(300,300);*/
	}
}