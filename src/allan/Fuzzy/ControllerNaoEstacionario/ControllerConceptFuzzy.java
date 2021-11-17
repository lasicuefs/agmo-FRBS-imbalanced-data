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
public class ControllerConceptFuzzy {

    // classificador = null
    public static void main(String[] args) throws IOException, RecognitionException, JMException, SecurityException, ClassNotFoundException, Exception {
        boolean desbalanceado = false;
        DiretorioCC dir = new DiretorioCC();
        Iterator exemplos = dir.problemas().iterator();
        double acuracia;
        double InterpSAG;

        Classico teste;

        double acuracia_pos_treino;
        double interp_pos_treino;
        boolean teveDesvio;
        int qtdDesvio = 0;

        while (exemplos.hasNext()) {

            List<Regra> melhoresRegras;

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
            ExcelCC excel = new ExcelCC(dataset.nomeArquivo, dataset.qtdElemJanelas, "SAG");
            excel.criarCabecalho();
            //System.out.println(formate.jLogicFuzzy);

            //Primeira Classificação
            System.out.println("Construindo Primeiro Sistema Fuzzy");
            WangMendel wm = new WangMendel(formate.jLogicFuzzy, (List<Instancia>) dataset.janelas.get(0), dataset.nomesCaracteristicas);
            wm.criarRegras();
            melhoresRegras = wm.regras;

            //indiceJanela < dataset.janelas.size()
            for (int indiceJanela = 1; indiceJanela < dataset.janelas.size(); indiceJanela++) {
                teste = new Classico(wm.fis, melhoresRegras, (List<Instancia>) dataset.janelas.get(indiceJanela), dataset.nomesCaracteristicas);
                acuracia = teste.classificar(desbalanceado);
                InterpSAG = teste.interpretabilidade();
                excel.criarCelulas(acuracia, 0, InterpSAG, 0, 0, "----");
                System.out.println("Bloco: " + indiceJanela + ", Acuracia: " + acuracia);
                teveDesvio = drift.detectar(acuracia);

                if (teveDesvio) {
                    System.out.println("Aconteceu Desvio");
                    qtdDesvio += 1;
                    wm = new WangMendel(formate.jLogicFuzzy, (List<Instancia>) dataset.janelas.get(indiceJanela), dataset.nomesCaracteristicas);
                    wm.criarRegras();
                    melhoresRegras = wm.regras;

                }
            }
        }
    }

}

// Treinamento Do AG
/*NSGAII_mainCC nsga = new NSGAII_mainCC();
            List resultado = nsga.executarAG(wm, formate, dataset, indiceJanela, desbalanceado);
            System.out.println("Acuracia AG: " + resultado.get(0) + " Inter AG: " + resultado.get(1) + " Gerações: " + resultado.get(2));
            melhorjLogicFuzzy = (String) resultado.get(3);
            melhorFis = (FIS) resultado.get(4);
            melhoresRegras =  (List<Regra>)resultado.get(5);
 */
//treinar = new Classico(melhorFis, melhoresRegras, (List<Instancia>) dataset.janelas.get(indiceJanela), dataset.nomesCaracteristicas);
//acuracia = treinar.classificar(desbalanceado);
//InterpSAG = treinar.interpretabilidade();
//System.out.println("Acuracia Teste: " + acuracia + " Inter Teste: " + InterpSAG);

