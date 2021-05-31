//  ZDT3.java
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

package jmetal.problems.ZDT;

import ArquivoFLC.Formate;
import ArquivoFLC.SistemaFuzzyAG;
import Controller.Processamento;
import Controller.ReducaoRegras;
import Fuzzy.BaseConhecimento.WangMendel;
import Fuzzy.Raciocínio.Classico;
import Model.Instancia;
import Model.Regra;
import Util.Keel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.ArrayReal;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;
import net.sourceforge.jFuzzyLogic.FIS;
import org.antlr.runtime.RecognitionException;


public class ZDT3 extends Problem {
    
  public static Processamento p;
  public SistemaFuzzyAG sistemaFuzzy;
  public WangMendel wm;
  public Formate formate;
  public Keel keelTreinamento;
  public int index = 0;
  public ReducaoRegras reducao;
  public boolean desbalanceado; 
  
  
  
  public ZDT3(WangMendel wm, Formate formate, Keel keelTreinamento, boolean desbalanceado) {
    p = new Processamento();
    p.preProcessamento(wm.regras, keelTreinamento.nomesClasses.size(), formate);
    numberOfVariables_  = p.cromossomo.size();
    numberOfObjectives_ =  2;
    numberOfConstraints_=  0;
    problemName_        = "ZDT3";
        
    upperLimit_ = p.limitesSuperiores;
    lowerLimit_ = p.limitesInferiores;
    /*for (double valor: upperLimit_){
        System.out.print(valor + " ");
    }
    System.out.println("");
    for (double valor: p.cromossomo){
        System.out.print(valor + " ");
    }
    System.out.println("");
    for (double valor: lowerLimit_){
        System.out.print(valor + " ");
    }
    System.out.println("");*/
   
       
    this.wm = wm;
    this.formate = formate;
    this.keelTreinamento = keelTreinamento;
    this.desbalanceado = desbalanceado;
    //solutionType_ = new ArrayRealSolutionType(this, wm, formate, keelTreinamento);

    solutionType_ = new ArrayRealSolutionType(this, p.cromossomo);
  } 
      
  
 
  public void evaluate(Solution solution) throws JMException {
    ArrayReal cromossomo = (ArrayReal)solution.getDecisionVariables()[0];
    p.gerandoRegras(cromossomo);
    sistemaFuzzy = new SistemaFuzzyAG(p.novosPontosCentrais);
    sistemaFuzzy.gerarFLC(keelTreinamento);
    //reducao = new ReducaoRegras(sistemaFuzzy.fis, this.keelTreinamento.instancias, this.keelTreinamento.nomesCaracteristicas, p.regras);
    reducao = new ReducaoRegras(p.regras);

    List<Regra> regras = reducao.reduzirRegras();
    Classico treinar = new Classico(sistemaFuzzy.fis, regras, this.keelTreinamento.instancias, this.keelTreinamento.nomesCaracteristicas);
    double acc = treinar.classificar(this.desbalanceado);
    //System.out.println("Acurácia = " + acc + " Index = " + ++index);
    solution.setObjective(0,-acc);
    //solution.setObjective(1, 1.0*regras.size()/this.keelTreinamento.instancias.size()-1);
    solution.setObjective(1, -treinar.interpretabilidade());
  } 
    
 
   
  
} 
