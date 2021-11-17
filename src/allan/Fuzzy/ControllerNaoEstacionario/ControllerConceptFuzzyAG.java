/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fuzzy.ControllerNaoEstacionario;

import Fuzzy.Model.Resultado;
import Fuzzy.BaseConhecimento.WangMendel;
import Fuzzy.ArquivoFLC.FormateCC;
import Fuzzy.Raciocínio.Classico;
import java.io.File;
import Arquivo.Utill.DiretorioCC;
import Arquivo.Utill.ExcelCC;
import Arquivo.Utill.KeelCC;
import Concept.Controller.DriftController;
import Fuzzy.Model.Instancia;
import Fuzzy.Model.Regra;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jmetal.metaheuristics.nsgaII.NSGAII_mainCC;
import jmetal.util.JMException;
import net.sourceforge.jFuzzyLogic.FIS;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author Allen Hichard
 */
public class ControllerConceptFuzzyAG {

    // classificador = null
    public static void main(String[] args) throws IOException, RecognitionException, JMException, SecurityException, ClassNotFoundException, Exception {
        boolean desbalanceado = false;
        DiretorioCC dir = new DiretorioCC();
        Iterator exemplos = dir.problemas().iterator();
        double acuraciaSAG;
        double InterpSAG;
        int qtdDesvio = 0;
        
        Classico teste;
        
        double acuraciaCAG;
        double InterpCAG;
        int geracao;

        double acuraciaSAG_pos_treino;
        double interpSAG_pos_treino;
        
        double acuraciaCAG_pos_treino;
        double interpCAG_pos_treino;
        
        boolean teveDesvio;
        
        
        while (exemplos.hasNext()) {

            List<Regra> melhoresRegras;
            WangMendel wm;
            WangMendel wmDesvio;

            // Inicialização do Concept
            DriftController drift = new DriftController();
            drift.createDetectors();
            DriftController.detector_atual = 3;
            drift.detectors[DriftController.detector_atual].prepareForUse();
            System.out.println("using detector " + drift.detectorNames[DriftController.detector_atual]);

            // Criação do FLC
            String caminho = (String) exemplos.next();
            KeelCC dataset = new KeelCC();
            
           
            
            FormateCC formate = new FormateCC();
            dataset.extrair(caminho);
            formate.gerarFLC(dataset);
            ExcelCC excel = new ExcelCC(dataset.nomeArquivo, dataset.qtdElemJanelas, "CAG");
            excel.criarCabecalho();
            //System.out.println(formate.jLogicFuzzy);

            //Primeira Classificação
            System.out.println("Construindo Primeiro Sistema Fuzzy");
            wm = new WangMendel(formate.jLogicFuzzy, (List<Instancia>) dataset.janelas.get(0), dataset.nomesCaracteristicas);
            wm.criarRegras();
            
            NSGAII_mainCC nsga = new NSGAII_mainCC();
            List resultado = nsga.executarAG(wm, dataset, 0, desbalanceado);
            geracao = (int) resultado.get(2);
            melhoresRegras =  (List<Regra>)resultado.get(3);
            
            //indiceJanela < dataset.janelas.size()
            for (int indiceJanela = 1; indiceJanela < dataset.janelas.size(); indiceJanela++) {
                teste = new Classico(wm.fis, melhoresRegras, (List<Instancia>) dataset.janelas.get(indiceJanela), dataset.nomesCaracteristicas);
                acuraciaSAG = teste.classificar(desbalanceado);
                InterpSAG = teste.interpretabilidade();
                excel.criarCelulas(acuraciaSAG, 0, InterpSAG, 0, 0, "----");
                teveDesvio = drift.detectar(acuraciaSAG);
                System.out.println("Bloco: " + indiceJanela + ", Acuracia: " + acuraciaSAG);

                
                if (teveDesvio) {
                    qtdDesvio+=1;
                    System.out.println("Reconstruindo Sistema Fuzzy");
                    wmDesvio = new WangMendel(formate.jLogicFuzzy, (List<Instancia>) dataset.janelas.get(indiceJanela), dataset.nomesCaracteristicas);
                    wmDesvio.criarRegras();
                    nsga = new NSGAII_mainCC();
                    resultado = nsga.executarAG(wmDesvio, dataset, indiceJanela, desbalanceado);
                    geracao = (int) resultado.get(2);
                    wm = wmDesvio;
                    melhoresRegras =  (List<Regra>)resultado.get(3);
                    //drift.detectar(acuracia);
                    
                   
                }
            }
        }
    } 
}

