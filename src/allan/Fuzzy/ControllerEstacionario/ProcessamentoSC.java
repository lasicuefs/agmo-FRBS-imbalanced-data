/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fuzzy.ControllerEstacionario;

import Fuzzy.ArquivoFLC.FormateSC;
import Fuzzy.Model.Regra;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.ArrayReal;
import jmetal.util.JMException;

/**
 *
 * @author Allen Hichard
 */
public class ProcessamentoSC {
    
    public int qtdAntecedente;
    public int qtdConsequente;
    public int qtdPontosCentrais;
    public double[] limitesInferiores;
    public double[] limitesSuperiores;
    public List <Double> cromossomo;
    public List <Regra> regras;
    public List <Double> novosPontosCentrais;
    
 
    
    public void preProcessamento(List<Regra> regras, int quantClasse, FormateSC formate){
        int index = 0;
        this.cromossomo = new ArrayList<>();
        List <Integer> antecedentes = new ArrayList<>();
        List <Integer> consequentes = new ArrayList<>();
        for (Regra regra: regras){
            antecedentes.addAll(regra.antecedentes);
            consequentes.add(regra.consequente);
        }
        this.qtdAntecedente = antecedentes.size();
        this.qtdConsequente = consequentes.size();
        this.qtdPontosCentrais = formate.pontosCentrais.size();
        for (int valor: antecedentes){
            this.cromossomo.add(valor*1.0);
        }
        for (int valor: consequentes){
            this.cromossomo.add(valor*1.0);
        }
        for (double valor: formate.pontosCentrais){
            this.cromossomo.add(valor);
        }
        
        int size = this.cromossomo.size();
        this.limitesInferiores = new double[size];
        this.limitesSuperiores = new double[size];
        
        for (Integer i: antecedentes){
            this.limitesInferiores[index] = -1.0;
            this.limitesSuperiores[index] = 2.0;
            index++;
        }
        for (Integer i: consequentes){
            this.limitesInferiores[index] = 0.0;
            this.limitesSuperiores[index] = quantClasse - 1.0;
            index++;
        }
        Iterator inf = formate.LimitesInferiores.iterator();
        Iterator sup = formate.LimitesSuperior.iterator();
        while (inf.hasNext()){
            this.limitesInferiores[index] = (double) inf.next();
            this.limitesSuperiores[index] = (double) sup.next();
            index++;
        }
        

    }
    
   

    public List<Regra> gerandoRegras(ArrayInt solucao1, ArrayInt solucao2) throws JMException {
        this.regras = new ArrayList<>();
        this.novosPontosCentrais = new ArrayList<>();
        int proporcao = solucao1.getLength() / solucao2.getLength();
        
        List<Integer> antecedentes = new ArrayList<>();
        int indexClasse = 0;
        List <Regra> regras = new ArrayList<>();
        for (int valor: solucao1.array_){
            antecedentes.add(valor);
        }
        for (int i = 0; i < antecedentes.size(); i += proporcao){
            Regra regra = new Regra();
            regra.antecedentes = antecedentes.subList(i, i+proporcao);
            regra.consequente = solucao2.getValue(indexClasse++);
            regras.add(regra);
            
        }
        /*for (Regra r: regras){
            System.out.println(r.toString());
        }*/
        return regras;
        //System.out.println(antecedente.subList(0, proporcao).toString());
       
    }


    public void gerandoRegras(ArrayReal cromossomo) {
        this.regras = new ArrayList<>();
        this.novosPontosCentrais = new ArrayList<>();
        int quantidadeRegras = this.qtdAntecedente / this.qtdConsequente;
        List<Double> valores = new ArrayList<>();
        int indexClasse = this.qtdAntecedente;
        
        for (double valor: cromossomo.array_){
            valores.add(valor);
        }
        for (int i = 0; i < this.qtdAntecedente; i += quantidadeRegras){
            Regra regra = new Regra();
            List<Double> antecedente = valores.subList(i, i+quantidadeRegras);
            for (double valor: antecedente){
               regra.addAntecedente((int) Math.round(valor));
            }
            regra.addConsequente((int) Math.round(valores.get(indexClasse)));
            indexClasse += 1;
            this.regras.add(regra);
        }
        
        int indexPonto = this.qtdAntecedente + this.qtdConsequente;
        for (int i = indexPonto; i < valores.size(); i += 1){ 
            this.novosPontosCentrais.add(valores.get(i));
        }
    }
}
