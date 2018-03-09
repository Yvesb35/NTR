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

public class Cellule {
	private ArrayList<Users> utilisateurs;
	private ArrayList<Users> roundrobin;
	private ArrayList<Users> utilisateursBesoin;
	private UR[][] bandePassante;
	private HashMap<Users, ArrayList<Double>> delaiUtilisateurs;
	private double moyenne;
	private double[] resultatfinalglobal;
	private double[] resultatfinalproche;
	private double[] resultatfinalloin;
	private double[] rempliTramePourcentage;
	private ArrayList<Double> remplitramePour1000;
	private double[] sommepaquetCreer;

	public Cellule() {
		this.utilisateurs = new ArrayList<Users>();
		this.utilisateursBesoin = new ArrayList<Users>();
		this.bandePassante = new UR[128][5];
		this.delaiUtilisateurs = new HashMap<Users, ArrayList<Double>>();
		this.resultatfinalglobal = new double[10];
		this.resultatfinalloin = new double[10];
		this.resultatfinalproche = new double[10];
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

	public double moyenndelai(ArrayList<Double> al) {
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
	
	public void initdelaiuser(ArrayList<Users> us) {
		for (Users use : us) {
			delaiUtilisateurs.put(use, new ArrayList<Double>());
		}
	}
	//public void
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

	public static void main(String[] args) {
		long nbTrame = 10000;

		double moyenneGlobal = 0;
		double delaiGlobal = 0;
		double consoGlobal = 0;

		Cellule cell = new Cellule();
		// On creer tout les utilisateurs
		Users [] tabUser = new Users[20];
		for(int ind = 0; ind <20; ind++){
			tabUser[ind] = new Users();
			if(ind % 2 == 0){
				tabUser[ind].setDebitMoyen(6);
			}else {
				tabUser[ind].setDebitMoyen(8);
			}
			cell.delaiUtilisateurs.put(tabUser[ind], new ArrayList<Double>());
		}

		long i = nbTrame;
		long compte = 0;
		double remplissageTrame = 0;
		// TODO
		// Initialisation de la Bande Passante.
		for (int a = 0; a < 128; a++) {
			for (int b = 0; b < 5; b++) {
				cell.bandePassante[a][b] = new UR();
			}
		}
		long debut = System.currentTimeMillis();
		for (int mache = 1; mache<11; mache++){
			System.out.println("C'est parti pour "+ mache *2 + " utilisateurs");
			System.out.println("*********************************************");
			System.out.println("*********************************************");

			//int somme;
			XYSeries serie = new XYSeries("Delai ");
			i=nbTrame;
			cell.remplitramePour1000.clear();
			while (i != 0) {
				cell.utilisateurs.clear();
				cell.utilisateursBesoin.clear();

				for (int rokette = 0; rokette<mache*2; rokette++){
					cell.addUser(tabUser[rokette]);
				}
				
				cell.varierDebit();
				cell.maxSNR(cell.utilisateursBesoin);
				cell.initdelaiuser(cell.utilisateursBesoin);
	
				i--;

				// On initialise a chaque trame le nombre de paquet a recevoir des utilisateurs
				for (Users u : cell.utilisateursBesoin) {
					Random r = new Random();
					cell.checkTheRandom(tabUser, mache);
					int moypaquet = u.getMoyNBpaq();
					//System.out.println("moypaquet = "+ moypaquet);
					int nbp = r.nextInt(2*moypaquet+1);
					
					//System.out.println("nbp = " + nbp);
					cell.sommepaquetCreer[mache-1] += (double)nbp;
					//System.out.println(cell.delaiUtilisateurs.get(u));
					
					u.addDemand(nbp);
				}
				//somme = 0;
				int a;
				int b=0;
				double URalloue =0;
				for ( a = 0; a < 128; a++) {
					for ( b = 0; b < 5; b++) {
						cell.varierDebit();

						Users proprio = cell.maxSNR(cell.utilisateursBesoin);
						while (proprio.assez()) {
							cell.utilisateurs.add(proprio);
							cell.utilisateursBesoin.remove(proprio);
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
						URalloue++;
						proprio.ajoutUR(proprio.getDebit());
						//somme += proprio.getDebit();
						//System.out.print("Popri�taire: " + cell.bandePassante[a][b].getProprietaire().getID());
						//System.out.println(" avec le debit = " + cell.bandePassante[a][b].getProprietaire().getDebit());
					}
					// Le dernier est ici.
					if (cell.utilisateursBesoin.isEmpty()) {
						break;
					}
				}
				
				remplissageTrame = (URalloue/(128*5))*100;
				cell.remplitramePour1000.add(remplissageTrame);
				// On a fini la trame 
				double delai = 0;
				double conso = 0;
				for (Users u : cell.utilisateurs) {
					conso = u.getBuffer().size();
					while (!u.getBuffer().isEmpty() && u.getSommeUR() != 0) {
						if (u.getBuffer().size() != 0) {
							delai = u.soulager();
							if (delai > -1) {
								u.setDelai(u.getDelai() + delai);
								delaiGlobal += delai;
							}
						}
					}

					conso -= u.getBuffer().size();
					if (conso > 0) {
						consoGlobal += conso;
						u.setConso(u.getConso()+ conso);
					}
					if (u.getConso() >15){
						cell.moyenne = u.getDelai()/ u.getConso();
						cell.delaiUtilisateurs.get(u).add(cell.moyenne);
						if(u.getID()==0 ){
							serie.add(i, cell.moyenne);
						}
						u.setDelai(0);
						u.setConso(u.getConso()-15);
						
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

						}
					}
					conso -= u.getBuffer().size();
					if (conso > 0) {
						consoGlobal += conso;
						u.setConso(u.getConso()+ conso);
					}
					if (u.getConso() >15){
						cell.moyenne = u.getDelai()/ u.getConso();
						cell.delaiUtilisateurs.get(u).add(cell.moyenne);
						if(u.getID()==0 ){
							serie.add(i, cell.moyenne);
						}
						u.setDelai(0);
						u.setConso(u.getConso()-15);
						
		
					}
					u.plusTimes();
				}
				cell.checkTheRandom(tabUser,mache);
				compte++;
			}
			
			// Sorti du while (i)
			// On fait les moyennes pour chaque users.
			System.out.println("Moyenne remplissageTrame = " + cell.moyenndelai(cell.remplitramePour1000));
			cell.rempliTramePourcentage[mache-1] = cell.moyenndelai(cell.remplitramePour1000);
			double moyenne = 0 ; 
			double moyenneloin = 0;
			double moyenneproche = 0;
			for (Users u : cell.delaiUtilisateurs.keySet()){
				moyenne +=  cell.moyenndelai(cell.delaiUtilisateurs.get(u));
				//System.out.println(moyenne);
				if (u.getDelaiMoyen()==6){
					moyenneloin += cell.moyenndelai(cell.delaiUtilisateurs.get(u));
				}else{
					moyenneproche += cell.moyenndelai(cell.delaiUtilisateurs.get(u));
				}
				//size += cell.delaiUtilisateurs.get(u).size();
				//On doit clear tout les infos des users utilisé
				//TODO
				/*for(int k =0;k<mache*2;k++){
					tabUser[k].clear();
					cell.delaiUtilisateurs.get(tabUser[k]).clear();
				}*/
			}
			//Fin du mache
			// On fait la moyenne final 
			double moyennefinal = moyenne / mache*2;
			double moyennefinalproche = moyenneproche/ mache*2;
			double moyennefinalloin = moyenneloin / mache *2;
			// On la stocke dans le tableau qui va servir a faire le graphe après.
			cell.resultatfinalglobal[mache-1] = moyennefinal;
			cell.resultatfinalloin[mache - 1] = moyennefinalloin;
			cell.resultatfinalproche[mache -1] = moyennefinalproche;
			XYDataset xyDatasettest = new XYSeriesCollection(serie);
			JFreeChart charttest = ChartFactory.createXYLineChart
				      ("NBP paquet User1", "nb trames", "nbp", xyDatasettest, PlotOrientation.VERTICAL, true, true, false);
			ChartFrame frametest=new ChartFrame("Courbe %nbp/nb trame",charttest);
			frametest.setVisible(true);
			frametest.setSize(300,300);
		}
		//on sort du mache
		System.out.println(cell.toString(cell.resultatfinalglobal));
		//Global
		XYSeries serie1 = new XYSeries("Courbe Délai");
		//Ceux qui sont proche
		XYSeries serie2 = new XYSeries("% UR");
		// Ceux qui sont loin
		XYSeries serie3 = new XYSeries("Nb Paquets");

		for(int j = 0 ; j<cell.resultatfinalglobal.length;j++){
			serie1.add((j+1)*2, cell.resultatfinalglobal[j]);
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
	}
}
