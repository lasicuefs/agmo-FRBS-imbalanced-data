//  NSGAII_main.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.metaheuristics.nsgaII;

import ArquivoFLC.Formate;
import ArquivoFLC.SistemaFuzzyAG;
import Controller.Processamento;
import Controller.ReducaoRegras;
import Controller.Resultado;
import Fuzzy.BaseConhecimento.WangMendel;
import Fuzzy.Raciocínio.Classico;
import Model.Instancia;
import Model.Regra;
import Util.Keel;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ZDT.ZDT3;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import jmetal.core.Solution;
import jmetal.encodings.variable.ArrayReal;
import net.sourceforge.jFuzzyLogic.FIS;

/** 
 * Class to configure and execute the NSGA-II algorithm.  
 *     
 * Besides the classic NSGA-II, a steady-state version (ssNSGAII) is also
 * included (See: J.J. Durillo, A.J. Nebro, F. Luna and E. Alba 
 *                  "On the Effect of the Steady-State Selection Scheme in 
 *                  Multi-Objective Genetic Algorithms"
 *                  5th International Conference, EMO 2009, pp: 183-197. 
 *                  April 2009)
 */ 

public class NSGAII_main {
  public static Logger      logger_ ;      // Logger object
  public static FileHandler fileHandler_ ; // FileHandler object

  /**
   * @param args Command line arguments.
   * @throws JMException 
   * @throws IOException 
   * @throws SecurityException 
   * Usage: three options
   *      - jmetal.metaheuristics.nsgaII.NSGAII_main
   *      - jmetal.metaheuristics.nsgaII.NSGAII_main problemName
   *      - jmetal.metaheuristics.nsgaII.NSGAII_main problemName paretoFrontFile
   */
  public NSGAII_main(Resultado resultado, WangMendel wm, Formate formate, Keel keelTreinamento, Keel keelTeste, boolean desbalanceado) 
          throws JMException, SecurityException, IOException, ClassNotFoundException {
    Problem   problem   ; // The problem to solve
    Algorithm algorithm ; // The algorithm to use
    Operator  crossover ; // Crossover operator
    Operator  mutation  ; // Mutation operator
    Operator  selection ; // Selection operator
    
    HashMap  parameters ; // Operator parameters
    
    QualityIndicator indicators ; // Object to get quality indicators

    // Logger object and file to store log messages
    logger_      = Configuration.logger_ ;
    fileHandler_ = new FileHandler("NSGAII_main.log"); 
    logger_.addHandler(fileHandler_) ;
        
    indicators = null ;
    
    problem = new ZDT3(wm, formate, keelTreinamento, desbalanceado);
    
    
    
    algorithm = new NSGAII(problem);
    //algorithm = new ssNSGAII(problem);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize",100);
    algorithm.setInputParameter("maxEvaluations", Integer.MAX_VALUE);

    // Mutation and Crossover for Real codification 
    parameters = new HashMap() ;
    parameters.put("probability", 0.9) ;
    parameters.put("distributionIndex", 20.0) ;
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
    parameters.put("distributionIndex", 20.0) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    

    // Selection Operator 
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;                           

    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);

    // Add the indicator object to the algorithm
    algorithm.setInputParameter("indicators", indicators) ;
    
    // Execute the Algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    
    // Result messages 
    logger_.info("Total execution time: "+estimatedTime + "ms");
    logger_.info("Variables values have been writen to file VAR");
    population.printVariablesToFile("VAR");    
    logger_.info("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("FUN");
    
    
    Solution best = population.best(new Comparator() {
  
        @Override
        public int compare(Object t, Object t1) {
            Solution melhor = (Solution) t; 
            Solution candidata = (Solution) t1;
            if (melhor.getObjective(0) < candidata.getObjective(0))
               return 0;
            if (melhor.getObjective(0) == candidata.getObjective(0) &&
                melhor.getObjective(1) < candidata.getObjective(1))
                return 0;
            return 1;
        }
    });
    
    
    resultado.acuraciaTrainCAG = -1*best.getObjective(0);
    resultado.InterpCAG = -1*best.getObjective(1);
    resultado.qtd_geracoes = NSGAII.geracaoAtual;
    
    ArrayReal cromossomo = (ArrayReal)best.getDecisionVariables()[0];
    Processamento p = ZDT3.p;
    p.gerandoRegras(cromossomo);
    SistemaFuzzyAG sistemaFuzzy = new SistemaFuzzyAG(p.novosPontosCentrais);
    sistemaFuzzy.gerarFLC(keelTreinamento);
    //ReducaoRegras reducao = new ReducaoRegras(sistemaFuzzy.fis, keelTreinamento.instancias, keelTreinamento.nomesCaracteristicas, p.regras);
    ReducaoRegras reducao = new ReducaoRegras(p.regras);

    List<Regra> regras = reducao.reduzirRegras();
    for (Regra regra: regras){
        System.out.println(regra.toString());
    }
    Classico teste = new Classico(sistemaFuzzy.fis, regras, keelTeste.instancias, keelTeste.nomesCaracteristicas);
    resultado.acuraciaTestCAG = teste.classificar(desbalanceado);
    
    //System.out.println("*************************************");
    //System.out.println("ACC " + acc + " BEST " + resultado.acuraciaTrainCAG);
    //System.out.println("Inter " + (1.0*regras.size()/keelTreinamento.instancias.size()-1) + " BEST " + resultado.InterpCAG );

    
    
    
    //System.out.println("Melhor Solução");
    //System.out.println(best.getObjective(0)+ " / "+ best.getObjective(1));
    //System.out.println(best.getDecisionVariables()[0]);
    
    if (indicators != null) {
      logger_.info("Quality indicators") ;
      logger_.info("Hypervolume: " + indicators.getHypervolume(population)) ;
      logger_.info("GD         : " + indicators.getGD(population)) ;
      logger_.info("IGD        : " + indicators.getIGD(population)) ;
      logger_.info("Spread     : " + indicators.getSpread(population)) ;
      logger_.info("Epsilon    : " + indicators.getEpsilon(population)) ;  
     
      int evaluations = ((Integer)algorithm.getOutputParameter("evaluations")).intValue();
      logger_.info("Speed      : " + evaluations + " evaluations") ;      
    } // if
    
    
  } //main
} // NSGAII_main
