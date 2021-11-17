/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Concept.Controller;

/* Code used for Lift-per-drift: an evaluation metric for classification frameworks with concept drift detection
 * 
 * This code is made available for research reproducability. For any other purposes, please contact the author first at rand079 at aucklanduni dot ac dot nz
 */

import moa.classifiers.core.driftdetection.AbstractChangeDetector;
import moa.classifiers.core.driftdetection.HDDM_A_Test;
import moa.classifiers.core.driftdetection.HDDM_W_Test;

import java.util.Random;


public class DriftController {

	public static AbstractChangeDetector[] detectors = new AbstractChangeDetector[14];
	public static String[] detectorNames = new String[14];
        public static int detector_atual = -1;
	
	public static void createDetectors() throws Exception{
		//detectors
		detectors = new  AbstractChangeDetector[6];
		detectorNames = new String[6];
		
		HDDM_W_Test ddHDDMwStrict = new HDDM_W_Test();
		ddHDDMwStrict.lambdaOption.setValue(0.05);
		ddHDDMwStrict.driftConfidenceOption.setValue(0.0005);
		detectors[0] = ddHDDMwStrict;
		detectorNames[0] = "ddHDDMwStrict";
		
		HDDM_W_Test ddHDDMwMed = new HDDM_W_Test();
		ddHDDMwMed.lambdaOption.setValue(0.05);
		ddHDDMwMed.driftConfidenceOption.setValue(0.01);
		detectors[1] = ddHDDMwMed;
		detectorNames[1] = "ddHDDMwMed";
		
		HDDM_W_Test ddHDDMwLoose = new HDDM_W_Test();
		ddHDDMwLoose.lambdaOption.setValue(0.05);
		ddHDDMwLoose.driftConfidenceOption.setValue(0.2);
		detectors[2] = ddHDDMwLoose;
		detectorNames[2] = "ddHDDMwLoose";
                
                ///////////////////////////////////////////////////////
                
                HDDM_A_Test ddHDDMaStrict = new HDDM_A_Test();
		//ddHDDMaStrict.lambdaOption.setValue(0.05);
		ddHDDMaStrict.driftConfidenceOption.setValue(0.0005);
		detectors[3] = ddHDDMaStrict;
		detectorNames[3] = "ddHDDMaStrict";
		
		HDDM_A_Test ddHDDMaMed = new HDDM_A_Test();
		//ddHDDMaMed.lambdaOption.setValue(0.05);
		ddHDDMaMed.driftConfidenceOption.setValue(0.01);
		detectors[4] = ddHDDMaMed;
		detectorNames[4] = "ddHDDMaMed";
		
		HDDM_A_Test ddHDDMaLoose = new HDDM_A_Test();
		//ddHDDMaLoose.lambdaOption.setValue(0.05);
		ddHDDMaLoose.driftConfidenceOption.setValue(0.2);
		detectors[5] = ddHDDMaLoose;
		detectorNames[5] = "ddHDDMaLoose";
                
		
	}
        
        public boolean detectar(double acuracia){
            double erro = 100 - 100*acuracia;
            detectors[detector_atual].input(erro);
            return detectors[detector_atual].getChange();
        }
        
        
        public void resetarDeteccao(){
             detectors[detector_atual].resetLearning();
        }
        
        
        public static void comunicacao(AbstractChangeDetector[] detectors, int c, float acuracia){
            double erro = 100 - acuracia;
            detectors[c].input(erro);
            if (detectors[c].getChange()){
                System.out.println("Acurácia: " + acuracia + " Erro: "+erro+" - " + detectors[c].getChange());
                detectors[c].resetLearning();
            } else{
                System.out.println("Acurácia: " + acuracia + " Erro: "+erro);
            }
        }
        
	public static void main(String[] args) throws Exception {
		
        //.getChange()
                createDetectors();
                int detect = 3;
                detectors[detect].prepareForUse();
                System.out.println("using detector " + detectorNames[0]);
                Random gerador = new Random();
                /*for (int i = 100; i > 0; i--){
                    float acuracia = 100*gerador.nextFloat();
                    comunicacao(detectors, detect, i);

                }*/
                comunicacao(detectors, detect, 97);
                comunicacao(detectors, detect, 32);
                comunicacao(detectors, detect, 30);
                comunicacao(detectors, detect, 30);
                comunicacao(detectors, detect, 30);
                comunicacao(detectors, detect, 30);
                comunicacao(detectors, detect, 30);
        }
                                   

        

                          
	
}