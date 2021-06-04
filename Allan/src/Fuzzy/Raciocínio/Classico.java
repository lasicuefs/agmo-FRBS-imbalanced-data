/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fuzzy.Raciocínio;

import Model.Instancia;
import Model.Regra;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.html.HTMLDocument;
import net.sourceforge.jFuzzyLogic.FIS;

/**
 *
 * @author Allen Hichard
 */
public class Classico {
    
    public FIS fis;
    public List<Instancia> instancias;
    public List<Regra> regras;
    public List<String> variaveis;
    public List<Integer> gabarito;
    public List<Integer> resultadoClassificacao;
    
    public Classico(FIS fis, List<Regra> regras, List<Instancia> instancias, List<String> variaveis){
        this.fis = fis;
        this.instancias = instancias;
        this.variaveis = variaveis;
        this.regras = regras;
        this.gabarito = new ArrayList<>();
        this.resultadoClassificacao = new ArrayList<>();
    }
    
    public double classificar(boolean desbalanceado){
        String terms[] = {"BAIXA", "MEDIA", "ALTA"};
        for (Instancia instancia : this.instancias){
            List<Double> atributos = instancia.caracteristicas;
            this.gabarito.add(instancia.classe);
            int classificacao = -1;
            double maiorTnorma = -1;
            for (Regra regra : this.regras){
                double tnorma = 1;
                Iterator var = this.variaveis.iterator();
                Iterator caracteristicas = instancia.caracteristicas.iterator();
                Iterator antecedentes = regra.antecedentes.iterator();
                while (var.hasNext()){
                    String variavel = (String) var.next(); //atributos 
                    double valor = (double) caracteristicas.next(); //valores das instancias
                    int indiceTermo = (int) antecedentes.next(); //id do conjunto
                    fis.setVariable(variavel, valor);
                    if (indiceTermo != -1)
                        tnorma *= fis.getVariable(variavel).getMembership(terms[indiceTermo]);
                }
                if (tnorma > maiorTnorma){
                    maiorTnorma = tnorma;
                    classificacao = regra.consequente;
                }
            }
            this.resultadoClassificacao.add(classificacao);
        }
        if (desbalanceado) return this.AUC();
        return this.acc();
    }
    
    private double acc(){
        Iterator gabarito = this.gabarito.iterator();
        Iterator resultado = this.resultadoClassificacao.iterator();

        //System.out.println(this.gabarito.size());
        //System.out.println(this.resultadoClassificacao.size());
        double total = this.resultadoClassificacao.size();
        int count = 0;
        
        while (gabarito.hasNext()) {
            int gab = (int) gabarito.next();
            int res = (int) resultado.next(); 
            if (gab == res) count++;
        }
        
        return count/total;
    }
    
    public double interpretabilidade (){
        double numerador = 0;
        for (Regra regra: this.regras){
            Iterator antecedentes = regra.antecedentes.iterator();
            while(antecedentes.hasNext()){
                int antecedente = (int) antecedentes.next();
                if (antecedente != -1) numerador++;        
            }
            numerador++;
        }
        return 1 - (numerador/(this.instancias.size()* this.regras.get(0).tamanhoInstancia()));
    }
    
    private double AUC(){
        Iterator gabarito = this.gabarito.iterator();
        Iterator resultado = this.resultadoClassificacao.iterator();

        //System.out.println(this.gabarito.size());
        //System.out.println(this.resultadoClassificacao.size());
        double total = this.resultadoClassificacao.size();
        int count = 0;
        double VP = 0;
        double VN = 0;
        double FP = 0;
        double FN = 0;
        //System.out.println("total = " + total);
        while (gabarito.hasNext()) {
            int gab = (int) gabarito.next();
            int res = (int) resultado.next();
            //System.out.println(gab + " -------------- " + res);
            if (gab == 0 && res == 0) VP++;
            else if (gab == 1 && res == 1) VN++;
            else if (gab == 1 && res == 0) FP++;
            else if (gab == 0 && res == 1) FN++;
        }
        
        //System.out.println(VP+"--"+VN+"--"+FP+"--"+FN);
        
        double taxa_VP, taxa_VN, taxa_FP, taxa_FN;
        
        if((VP + FN)!=0) taxa_VP = VP / (VP + FN);
        else taxa_VP = 0;
        
        if((VN + FP)!=0) taxa_VN = VN / (VN + FP);
        else taxa_VN = 0;
        
        if((FP + VN)!=0) taxa_FP = FP / (FP + VN);
        else taxa_FP = 0;
        
        if((FN + VP)!=0) taxa_FN = FN / (FN + VP);
        else taxa_FN = 0;
        
        double AUC = (1 + taxa_VP - taxa_FP) / 2;
        return AUC;
    }
    
}




    
    
        
       
       
       



