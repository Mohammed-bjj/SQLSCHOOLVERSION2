package apprendreSQL.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import apprendreSQL.Model.analysisTypeMetier.syntax.general.ParseException;
import apprendreSQL.Model.analysisTypeMetier.syntax.general.ParserSQL;
import apprendreSQL.Model.data.BankQuestion;
import apprendreSQL.Model.data.Factory;
import apprendreSQL.Model.data.Observers;
import apprendreSQL.Model.data.Question;
import apprendreSQL.View.Gui;
/**
 * 
 * @author equipe SQLSCOOL
 * @version 2.0
 * cette class est une unité intermidiare entre la vue et le model  
 * qui comme responsabilité de gérer le traitement logique
 *
 */
public class Controller {

	/*
	 *  représente la vue de l'application
	 */
	private Gui view;
	private Integer index;
	/**
	 *  represente le parseur de l'application
	 */
	private ParserSQL parser1;
	private ParserSQL parSql2;
	
	/**
	 * banque de question où il y a tout les questions faite par l'enseignant
	 */
	private BankQuestion questions;
	
	
	
	/**
	 *   représente la partie semantique de l'application
	 * @throws org.json.simple.parser.ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
//	private Semantique semantique;
	
	public Controller() 
	{
		index = 0;
		try {
			init();
			view.sendMessage("*************************!!!!!!!   Hello !!!!! ***********************");
		} catch (IOException | org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * cette methode initialise toutes les attributs 
	 * @throws org.json.simple.parser.ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * 
	 * methode initialise tout les attributs de la class 
	 */
	private void init() throws FileNotFoundException, IOException, org.json.simple.parser.ParseException  {
		
		questions   = Factory.makeListQuestion();
		view        = Factory.makeView(this);
		
		this.parser1 = Factory.makeParserSQL("general");
		this.parSql2  = Factory.makeParserSQL("particulier");
		this.parSql2.setcontroller(this);
		
		this.parser1.registerObserver((Observers) parSql2);
		this.parser1.setDestination("eleve");
		
		
		DisplayView();
		eventManager();
	}
	
	
	
	/**
	 *   methode qui gère les évènements lors du clic sur les trois boutons (next,precedent, exécuter)
	 */
	private void eventManager() {
		/**
		 *   gestion d'evenement pour le bouton next 
		 */
		view.getButtonNext().addActionListener( e ->  nextQuestion());
		
		/**
		 *  gestion d'evenement pour le bouton précédent
		 */
		view.getButtonPrecedent().addActionListener(e ->  precedentQuestion() );
		
		/**
		 *  gestion d'evenement pour le bouton executer
		 */
		view.getButtonExu().addActionListener( e -> 
					{
						if(view.getText().length() != 0)
							start_analysis();
					}
			);
	}
	
	
	/**
	 *  methode lance l'analyse lexical, syntaxique et semantique repectivement l'ordre 
	 */
	private void start_analysis() {
    	  try {
        	  parSql2.reset();
    		  launch();
    	  } catch (ParseException e1) {
    		  view.sendMessage(" Error : "+ e1.getMessage());
    	  }

	}
	/**
	 * methode applique le scénario suivant en ordre :
	 * 1 ] charge une question selon le choi de l'utilisateur
	 * 2 ] construire les régles de décision pour le parseur 2
	 * 3 ] demarer l'analyse générale pour vérifier l'appartenance des mots à l'ensemble des mots de SQL
	 *                                              l'appartenance d'une requête au Langage généré par une grammaire
	 * 4 ] verifier l'appartenance de la requête au sous langage généré par l'etape 2
	 * 5 ] verifier la sémantique
	 *                                               
	 * @throws ParseException
	 */
	private void launch() throws ParseException {
		Question questionSelectionne = questions.findQuestionByID(index);
    	parSql2.updateReponses(questionSelectionne.getReponses());
		parser1.ReInit(Factory.translateToStream(view.getText()));
		parser1.sqlStmtList();
		parSql2.sqlStmtList();
		parSql2.startAnalyseSemantic(questionSelectionne.isOrdre());
		// senman(parse2)
	    view.sendMessage("OK. Correcte syntaxique oui oui \n ");
	}
	/*
	 * Methode mis à jour l'attribut Question dans la class Gui lors du clic sur boutton Precedent
	 */
	private void precedentQuestion() {
		if(index >= 1){
			 index -= 1;
			 updateQuestion(index);
		 }
	}
	private void updateQuestion(int index) {
		 parSql2.reset();
		 view.setQuestion(questions.findQuestionByID(index));
		 parSql2.updateReponses(view.getQuestion().getReponses());
	}
	/**
	 * Methode mis à jour l'attribut Question dans la class Gui lors du clic sur boutton Precedent
	 */
	private void nextQuestion() {
		 if(index < questions.size() - 1){
			 index += 1;
			 updateQuestion(index);
		 }
	}
	
	
	
	
	/**
	 *   Cette méthode appelle 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void DisplayView() {	
		view.display();
	}


	/**
	 * cette methode renvoie l'attribut view 
	 * @return renvoie l'attribut view 
	 */
	public Gui getView() {
		return view;
	}
	
	
	
	
	//******************************* developement ces fonctionnalité dans les prochaines version  ///////////////
   
	
	public void sendIsKeyWord(String word) {
		
	}





	/**
	 * 
	 * @return question indéxé par id 
	 */
	public Question getQuestions() {
		return questions.findQuestionByID(0);
	}

	
}
