/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Arquivo.Utill;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Allen Hichard
 */
public class DiretorioSC {
    
    public String diretorioRaiz;
    
    public DiretorioSC(){
        this.diretorioRaiz = System.getProperty("user.dir")+"\\Repositorio\\SemConcept";
    }
    
    public List<String> problemas(String train_test){
        List listDatasets = new ArrayList();
        File pastas = new File(this.diretorioRaiz);
        String problemas[] = pastas.list();
        for (String problema : problemas){
            File dats = new File(this.diretorioRaiz + "\\" + problema);
            String datasets[] = dats.list();
            for (String dataset : datasets){
                if (dataset.contains(train_test)) 
                    listDatasets.add(this.diretorioRaiz + "\\" +problema + "\\"+dataset);
            }
        }
        return listDatasets;
    }
    
    public static void main(String[] args){
        DiretorioSC dir = new DiretorioSC();
        System.out.println(dir.diretorioRaiz);
        System.out.println(dir.problemas("tra").size());
        System.out.println(dir.problemas("tst").size());
    }
                  
}

    



/*



        for dataset in os.listdir(caminhoDatasets+"/"+pastaDataset):
            if dataset.__contains__(treino_teste+".dat"):
                datasetsList.append(caminhoDatasets+"/"+pastaDataset+"/"+dataset)
    return datasetsList

"""print(datasets("tra"))
print(datasets("tst"))"""
*/