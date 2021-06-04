/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Fuzzy.BaseConhecimento.WangMendel;
import ArquivoFLC.Formate;
import Fuzzy.Raciocínio.Classico;
import java.io.File;
import Util.Diretorio;
import Util.Excel;
import Util.Keel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import jmetal.metaheuristics.nsgaII.NSGAII_main;
import jmetal.util.JMException;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author Allen Hichard
 */
public class controller {
    
    public static void main(String[] args) throws IOException, RecognitionException, JMException, SecurityException, ClassNotFoundException{
        boolean desbalanceado = true;
        Diretorio dir = new Diretorio();
        Iterator treinamento = dir.problemas("tra").iterator();
        Iterator teste = dir.problemas("tst").iterator();
        ArrayList<Resultado> listaResultados = new ArrayList<>();
        while (treinamento.hasNext()){
            Resultado resultado = new Resultado();
            String train = (String) treinamento.next();
            Keel keelTreinamento = new Keel();
            Formate formate = new Formate();
            keelTreinamento.extrair(train);
            formate.gerarFLC(keelTreinamento);
            System.out.println(formate.jLogicFuzzy);
            WangMendel wm  = new WangMendel(formate.jLogicFuzzy, keelTreinamento.instancias, keelTreinamento.nomesCaracteristicas);
            wm.criarRegras();
            
            Classico treinar = new Classico(wm.fis, wm.regras, keelTreinamento.instancias, keelTreinamento.nomesCaracteristicas);
            resultado.acuraciaTrainSAG = treinar.classificar(desbalanceado);
            //resultado.InterpSAG = 1 - (1.0*wm.regras.size()/keelTreinamento.instancias.size());
            resultado.InterpSAG = treinar.interpretabilidade();
            String test = (String) teste.next();
            Keel keelTeste = new Keel();
            keelTeste.extrair(test);
            Classico testar = new Classico(wm.fis, wm.regras, keelTeste.instancias, keelTeste.nomesCaracteristicas);
            resultado.acuraciaTestSAG = testar.classificar(desbalanceado);
            resultado.nome = keelTreinamento.nomeArquivo;
            NSGAII_main nsga = new NSGAII_main(resultado, wm, formate, keelTreinamento,keelTeste, desbalanceado);
            listaResultados.add(resultado);
            System.out.println(resultado.toString());
            //boolean deletouTreino = new File(train).delete();
            //boolean deletouTeste = new File(test).delete();
            //if (deletouTreino && deletouTeste) System.out.println("Datasets treinados, testados e deletados\n " + train + " ****** "+ test);
        }
        
        System.out.println(listaResultados.size());
        Excel exc = new Excel();
        exc.criarTabela(listaResultados.get(0).nome, listaResultados);
    }
}
