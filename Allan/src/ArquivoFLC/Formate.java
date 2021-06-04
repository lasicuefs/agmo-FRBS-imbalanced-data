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

/**
 *
 * @author Allen Hichard
 */
public class Formate {
    
    public String jLogicFuzzy;
    
    public List<Double> LimitesInferiores;
    public List<Double> LimitesSuperior;
    public List<Double> pontosCentrais;
    
    public Formate(){
        this.jLogicFuzzy = "";
        this.LimitesSuperior = new ArrayList<>();
        this.LimitesInferiores = new ArrayList<>();
        this.pontosCentrais = new ArrayList<>();
    }
    
    public void gerarFLC(Keel keel){
        if(keel.nomeArquivo.contains("-")){
            String nomeConcatenado = "";
            String[] nomeArq = keel.nomeArquivo.split("-");
            for (String nomeArq1 : nomeArq) {
                //System.out.println(nomeArq1);
                nomeConcatenado += nomeArq1;
            }
            keel.nomeArquivo = nomeConcatenado;  
        }
        this.jLogicFuzzy += "FUNCTION_BLOCK " + keel.nomeArquivo + "\r\n\r\n";
        varInput(keel);
        //varOutput(keel);
        fuzzify(keel);
        this.jLogicFuzzy += "END_FUNCTION_BLOCK";
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
        while (li.hasNext()) {
            double limiteInferior = Double.parseDouble((String) li.next());
            double limiteSuperior = Double.parseDouble((String) ls.next());
            double largura_base_superior = (limiteSuperior - limiteInferior) / (2 * 3 - 1);
            double largura_entre_pontos_inferiores = (limiteSuperior - limiteInferior) / (3 - 1);
            //System.out.println(keel.nomesCaracteristicas);
            String caracteristica = (String) caracteristicas.next();
            this.jLogicFuzzy += "\t";
            this.jLogicFuzzy += "FUZZIFY " + caracteristica;
            this.jLogicFuzzy += "\r\n";
            definirIntervalos(limiteInferior, limiteSuperior, largura_base_superior, largura_entre_pontos_inferiores);
            this.jLogicFuzzy += "\tEND_FUZZIFY\r\n";
            this.jLogicFuzzy += "\r\n";   
        }
    }
    
    
    private void trapezioInicial(double limInferior, double largura_base_superior, 
            double ponto_referencial, double largura_entre_pontos_inferiores){
        double p2 = limInferior + largura_base_superior/2;
        double pontoMedio = ponto_referencial + largura_entre_pontos_inferiores;
        this.LimitesInferiores.add(p2);
        String trapezioInicial = "(" + limInferior + ", 1) (" + p2 + ", 1) (" + pontoMedio + ", 0)";
        this.jLogicFuzzy += "\t\t";
        this.jLogicFuzzy += "TERM BAIXA := " + trapezioInicial + " ;";
        this.jLogicFuzzy += "\r\n";
    }
    
    private double triangulo (double ponto_referencial, double largura_base_superior, double largura_entre_pontos_inferiores){
        double pontoInicial = ponto_referencial;
        double pontoMedio = ponto_referencial + largura_entre_pontos_inferiores;
        double pontoFinal = ponto_referencial + largura_entre_pontos_inferiores * 2;
        this.pontosCentrais.add(pontoMedio);
        String triangulo = "(" + pontoInicial + ", 0) (" + pontoMedio + ", 1) (" + pontoFinal + ", 0)";
        this.jLogicFuzzy += "\t\t";
        this.jLogicFuzzy += "TERM MEDIA := " + triangulo + " ;";
        this.jLogicFuzzy += "\r\n";
        return ponto_referencial += largura_entre_pontos_inferiores;
    }
    
    private void trapezioFinal(double limSuperior, double largura_base_superior, double ponto_referencial){
        double pontoInicial = ponto_referencial;
        double p1 = limSuperior - largura_base_superior/2;
        this.LimitesSuperior.add(p1);
        String trapezioFinal = "(" + pontoInicial + ", 0) (" + p1 + ", 1) (" + limSuperior + ", 1)";
        this.jLogicFuzzy += "\t\t";
        this.jLogicFuzzy += "TERM ALTA := " + trapezioFinal + " ;";
        this.jLogicFuzzy += "\r\n";
    }
    
    private void definirIntervalos(double limInferior, double limSuperior, double largura_base_superior, double largura_entre_pontos_inferiores) {
        
        double pontoReferencial = limInferior;
        trapezioInicial(limInferior, largura_base_superior, pontoReferencial, largura_entre_pontos_inferiores);
        pontoReferencial = triangulo(pontoReferencial, largura_base_superior, largura_entre_pontos_inferiores);
        trapezioFinal(limSuperior, largura_base_superior, pontoReferencial);
        
    }
    
    
}
