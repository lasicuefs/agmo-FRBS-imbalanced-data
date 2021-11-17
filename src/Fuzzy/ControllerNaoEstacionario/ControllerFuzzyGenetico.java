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
public class ControllerFuzzyGenetico {

    // classificador = null
    public static void main(String[] args) throws IOException, RecognitionException, JMException, SecurityException, ClassNotFoundException, Exception {
        boolean desbalanceado = false;
        DiretorioCC dir = new DiretorioCC();
        Iterator exemplos = dir.problemas().iterator();
        double acuraciaSAG;
        double InterpSAG;
        
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
           
            
            //indiceJanela < dataset.janelas.size()
            for (int indiceJanela = 0; indiceJanela < dataset.janelas.size(); indiceJanela++) {
                List janela = (List<Instancia>) dataset.janelas.get(indiceJanela);
                wm = new WangMendel(formate.jLogicFuzzy, janela, dataset.nomesCaracteristicas);
                wm.criarRegras();

                Classico treinar = new Classico(wm.fis, wm.regras, janela, dataset.nomesCaracteristicas);
                acuraciaSAG = treinar.classificar(desbalanceado);
                InterpSAG = treinar.interpretabilidade();
                
                NSGAII_mainCC nsga = new NSGAII_mainCC();
                List resultado = nsga.executarAG(wm, dataset, indiceJanela, desbalanceado);
                acuraciaCAG = (double) resultado.get(0);
                InterpCAG = (double) resultado.get(1);
                geracao = (int) resultado.get(2);
                melhoresRegras =  (List<Regra>)resultado.get(3);
                System.out.println("Acuracia AG: " + acuraciaCAG + " Inter AG: " + InterpCAG + " Gerações: " + geracao);
                excel.criarCelulas(acuraciaSAG, acuraciaCAG , InterpSAG, InterpCAG, geracao, "Sem Desvio");
                
                //Falta criar conjunto de teste em subjanelas
            }
        }
    } 
}

