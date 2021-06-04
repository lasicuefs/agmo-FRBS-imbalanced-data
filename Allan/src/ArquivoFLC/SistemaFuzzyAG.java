/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArquivoFLC;

import Util.Keel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author Allen Hichard
 */
public class SistemaFuzzyAG {
    
    public FIS fis;
    public String jLogicFuzzy;
    
    public List<Double> pontosCentrais;
    
    public SistemaFuzzyAG(List<Double> pontosCentrais){
        this.jLogicFuzzy = "";
        this.pontosCentrais = pontosCentrais;
    }
    
    public void gerarFLC(Keel keel){
        //System.out.println("aqqqqqqqqqqqq"+keel.nomeArquivo);
        
        this.jLogicFuzzy += "FUNCTION_BLOCK " + keel.nomeArquivo + "\r\n\r\n";
        
        varInput(keel);
        //varOutput(keel);
        fuzzify(keel);
        this.jLogicFuzzy += "END_FUNCTION_BLOCK";
        try {
            this.fis = FIS.createFromString(jLogicFuzzy, true);
            //JFuzzyChart.get().chart(fis.getFunctionBlock(null));
        } catch (RecognitionException ex) {
            Logger.getLogger(SistemaFuzzyAG.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void varInput(Keel keel) {
        this.jLogicFuzzy += "\tVAR_INPUT\r\n";
        Iterator caracteristicas = keel.nomesCaracteristicas.iterator();
        Iterator tipos = keel.tipos.iterator();
        while (caracteristicas.hasNext()) {
            String input = (String) caracteristicas.next();
            String tipo = (String) tipos.next();
            this.jLogicFuzzy += "\t\t";
            this.jLogicFuzzy += input + ": " + tipo + ";";
            this.jLogicFuzzy += "\r\n";
        }
        this.jLogicFuzzy += "\tEND_VAR\r\n\r\n";
    }
    
    private void varOutput(Keel keel) {
        this.jLogicFuzzy += "\tVAR_OUTPUT\r\n";
        this.jLogicFuzzy += "\t\t";
        this.jLogicFuzzy += "CLASS: real;";
        this.jLogicFuzzy += "\r\n";
        this.jLogicFuzzy += "\tEND_VAR\r\n";
        this.jLogicFuzzy += "\r\n";
    }
    
    private void fuzzify(Keel keel) {
        
        Iterator li = keel.limitesInferiores.iterator();
        Iterator ls = keel.limitesSuperiores.iterator();
        Iterator caracteristicas  = keel.nomesCaracteristicas.iterator();
        Iterator pontosCentrais = this.pontosCentrais.iterator();
        while (li.hasNext()) {
            double limiteInferior = Double.parseDouble((String) li.next());
            double limiteSuperior = Double.parseDouble((String) ls.next());
            double largura_base_superior = (limiteSuperior - limiteInferior) / (2 * 3 - 1);
            double largura_entre_pontos_inferiores = (limiteSuperior - limiteInferior) / (3 - 1);
            String caracteristica = (String) caracteristicas.next();
            this.jLogicFuzzy += "\t";
            this.jLogicFuzzy += "FUZZIFY " + caracteristica;
            this.jLogicFuzzy += "\r\n";
            definirIntervalos(limiteInferior, limiteSuperior, largura_base_superior, largura_entre_pontos_inferiores, (double)pontosCentrais.next());
            this.jLogicFuzzy += "\tEND_FUZZIFY\r\n";
            this.jLogicFuzzy += "\r\n";   
        }
    }
    
    
    private void trapezioInicial(double limInferior, double largura_base_superior, 
            double ponto_referencial, double largura_entre_pontos_inferiores){
        double p2 = limInferior + largura_base_superior/2;
        double pontoMedio = ponto_referencial + largura_entre_pontos_inferiores;
        String trapezioInicial = "(" + limInferior + ", 1) (" + p2 + ", 1) (" + pontoMedio + ", 0)";
        this.jLogicFuzzy += "\t\t";
        this.jLogicFuzzy += "TERM BAIXA := " + trapezioInicial + " ;";
        this.jLogicFuzzy += "\r\n";
    }
    
    private double triangulo (double pontoCentral, double ponto_referencial, double largura_base_superior, double largura_entre_pontos_inferiores){
        double pontoInicial = ponto_referencial;
        double pontoFinal = ponto_referencial + largura_entre_pontos_inferiores * 2;
        String triangulo = "(" + pontoInicial + ", 0) (" + pontoCentral + ", 1) (" + pontoFinal + ", 0)";
        this.jLogicFuzzy += "\t\t";
        this.jLogicFuzzy += "TERM MEDIA := " + triangulo + " ;";
        this.jLogicFuzzy += "\r\n";
        return ponto_referencial += largura_entre_pontos_inferiores;
    }
    
    private void trapezioFinal(double limSuperior, double largura_base_superior, double ponto_referencial){
        double pontoInicial = ponto_referencial;
        double p1 = limSuperior - largura_base_superior/2;
        String trapezioFinal = "(" + pontoInicial + ", 0) (" + p1 + ", 1) (" + limSuperior + ", 1)";
        this.jLogicFuzzy += "\t\t";
        this.jLogicFuzzy += "TERM ALTA := " + trapezioFinal + " ;";
        this.jLogicFuzzy += "\r\n";
    }
    
    private void definirIntervalos(double limInferior, double limSuperior, double largura_base_superior, double largura_entre_pontos_inferiores, double pontoCentral) {
        double pontoReferencial = limInferior;
        trapezioInicial(limInferior, largura_base_superior, pontoReferencial, largura_entre_pontos_inferiores);
        pontoReferencial = triangulo(pontoCentral, pontoReferencial, largura_base_superior, largura_entre_pontos_inferiores);
        trapezioFinal(limSuperior, largura_base_superior, pontoReferencial);
        
    }
    
    
}
