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


import Fuzzy.ControllerNaoEstacionario.ProcessamentoCC;
import Fuzzy.ControllerEstacionario.ReducaoRegras;
import Fuzzy.BaseConhecimento.WangMendel;
import Fuzzy.Raciocínio.Classico;
import Fuzzy.Model.Instancia;
import Fuzzy.Model.Regra;
import Arquivo.Utill.KeelCC;
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


public class ZDT3CC extends Problem {
    
  public static ProcessamentoCC p;
  public WangMendel wm;
  public KeelCC keelTreinamento;
  public int index = 0;
  public ReducaoRegras reducao;
  public int indiceJanela;
  public boolean desbalanceado; 
  
  
  
  public ZDT3CC(WangMendel wm, KeelCC keelTreinamento, int indiceJanela, boolean desbalanceado) {
    p = new ProcessamentoCC();
    p.preProcessamento(wm.regras, keelTreinamento.nomesClasses.size());
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
    this.keelTreinamento = keelTreinamento;
    this.desbalanceado = desbalanceado;
    this.indiceJanela = indiceJanela;
    //solutionType_ = new ArrayRealSolutionType(this, wm, formate, keelTreinamento);

    solutionType_ = new ArrayRealSolutionType(this, p.cromossomo);
  } 
      
  
 
  public void evaluate(Solution solution) throws JMException {
    ArrayReal cromossomo = (ArrayReal)solution.getDecisionVariables()[0];
    p.gerandoRegras(cromossomo);
    //reducao = new ReducaoRegras(sistemaFuzzy.fis, this.keelTreinamento.instancias, this.keelTreinamento.nomesCaracteristicas, p.regras);
    reducao = new ReducaoRegras(p.regras);

    List<Regra> regras = reducao.reduzirRegras();
    Classico treinar = new Classico(this.wm.fis, regras, (List<Instancia>) keelTreinamento.janelas.get(this.indiceJanela), this.keelTreinamento.nomesCaracteristicas);
    double acc = treinar.classificar(this.desbalanceado);
    //System.out.println("Acurácia = " + acc + " Index = " + ++index);
    solution.setObjective(0,-acc);
    //solution.setObjective(1, 1.0*regras.size()/this.keelTreinamento.instancias.size()-1);
    solution.setObjective(1, -treinar.interpretabilidade());
  } 
    
 
   
  
} 
