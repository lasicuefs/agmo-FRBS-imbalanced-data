/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fuzzy.ControllerEstacionario;

import Fuzzy.Model.Resultado;
import Fuzzy.BaseConhecimento.WangMendel;
import Fuzzy.ArquivoFLC.FormateSC;
import Fuzzy.Raciocínio.Classico;
import java.io.File;
import Arquivo.Utill.DiretorioSC;
import Arquivo.Utill.ExcelSC;
import Arquivo.Utill.KeelSC;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import jmetal.metaheuristics.nsgaII.NSGAII_mainSC;
import jmetal.util.JMException;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author Allen Hichard
 */
public class ControllerSemConcept {
    // classificador = null
    public static void main(String[] args) throws IOException, RecognitionException, JMException, SecurityException, ClassNotFoundException{
        boolean desbalanceado = true;
        DiretorioSC dir = new DiretorioSC();
        Iterator treinamento = dir.problemas("tra").iterator();
        Iterator teste = dir.problemas("tst").iterator();
        ArrayList<Resultado> listaResultados = new ArrayList<>();
        while (treinamento.hasNext()){
            Resultado resultado = new Resultado();
            String train = (String) treinamento.next();
            KeelSC keelTreinamento = new KeelSC();
            FormateSC formate = new FormateSC();
            keelTreinamento.extrair(train);
            formate.gerarFLC(keelTreinamento);
            System.out.println(formate.jLogicFuzzy);
            //Sinalização  = Mandar para o detector
            //if False: pega a proximo bloco
            //else: algoritmo genético
            
            WangMendel wm  = new WangMendel(formate.jLogicFuzzy, keelTreinamento.instancias, keelTreinamento.nomesCaracteristicas);
            wm.criarRegras();
            
            
            Classico treinar = new Classico(wm.fis, wm.regras, keelTreinamento.instancias, keelTreinamento.nomesCaracteristicas);
            resultado.acuraciaTrainSAG = treinar.classificar(desbalanceado);
            //resultado.InterpSAG = 1 - (1.0*wm.regras.size()/keelTreinamento.instancias.size());
            resultado.InterpSAG = treinar.interpretabilidade();
            String test = (String) teste.next();
            KeelSC keelTeste = new KeelSC();
            keelTeste.extrair(test);
            Classico testar = new Classico(wm.fis, wm.regras, keelTeste.instancias, keelTeste.nomesCaracteristicas);
            resultado.acuraciaTestSAG = testar.classificar(desbalanceado);
            
            
            resultado.nome = keelTreinamento.nomeArquivo;
            NSGAII_mainSC nsga = new NSGAII_mainSC(resultado, wm, formate, keelTreinamento,keelTeste, desbalanceado);
            listaResultados.add(resultado);
            System.out.println(resultado.toString());
            //boolean deletouTreino = new File(train).delete();
            //boolean deletouTeste = new File(test).delete();
            //if (deletouTreino && deletouTeste) System.out.println("Datasets treinados, testados e deletados\n " + train + " ****** "+ test);
        }
        
        System.out.println(listaResultados.size());
        ExcelSC exc = new ExcelSC();
        exc.criarTabela(listaResultados.get(0).nome, listaResultados);
    }
}
