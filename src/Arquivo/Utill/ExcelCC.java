/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Arquivo.Utill;

import Fuzzy.Model.Resultado;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author User
 */
public class ExcelCC {

    private static String fileName;
    public Row row;
    public HSSFSheet folha;
    public HSSFWorkbook workbook;

    public ExcelCC(String nome, int qtdElemJanela, String sistema) {
        fileName = "Resultados//" + nome + " - " + qtdElemJanela + " - " + sistema + ".xls";

        try{ //edita
            FileInputStream file = new FileInputStream(new File(fileName));
            this.workbook = new HSSFWorkbook(file);
            this.folha = workbook.getSheetAt(0);
            this.row = folha.getRow(folha.getPhysicalNumberOfRows());
           
        } catch(IOException e){ // se não encontrar o arquivo, cria o arquivo
            this.workbook = new HSSFWorkbook();
            this.folha = workbook.createSheet("Resultados " + nome);
        }

    }

    public void salvar(){
        try {
            FileOutputStream out = new FileOutputStream(new File(ExcelCC.fileName));
            this.workbook.write(out);
            out.close();
            //System.out.println("Arquivo Excel criado/editado com sucesso!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Arquivo não encontrado!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro na edição do arquivo!");
        }
    
    }
    public void criarCabecalho() {
        Row row = folha.createRow(0);
        Cell cabecalho0 = row.createCell(0);
        cabecalho0.setCellValue("Acuracia Fuzzy");
        Cell cabecalho1 = row.createCell(1);
        cabecalho1.setCellValue("Acuracia AG");
        Cell cabecalho2 = row.createCell(2);
        cabecalho2.setCellValue("Interpretabilidade Fuzzy");
        Cell cabecalho3 = row.createCell(3);
        cabecalho3.setCellValue("Interpretabilidade AG");
        Cell cabecalho4 = row.createCell(4);
        cabecalho4.setCellValue("Gerações");
        Cell cabecalho5 = row.createCell(5);
        cabecalho5.setCellValue("Desvio");
        
        salvar();
    }

    

    public void criarCelulas(double acuraciaFuzzy, double acuraciaAG, double interpFuzzy,
                                    double interpAG, int geracoes, String desvio) {
        int rownum = folha.getPhysicalNumberOfRows();
        row = folha.createRow(rownum++);
        
        Cell colunaA = row.createCell(0);
        colunaA.setCellValue(acuraciaFuzzy);

        Cell colunaB = row.createCell(1);
        colunaB.setCellValue(acuraciaAG);

        Cell colunaC = row.createCell(2);
        colunaC.setCellValue(interpFuzzy);

        Cell colunaD = row.createCell(3);
        colunaD.setCellValue(interpAG);

        Cell colunaE = row.createCell(4);
        colunaE.setCellValue(geracoes);

        Cell colunaF = row.createCell(5);
        colunaF.setCellValue(desvio);

        salvar();
        
    }

}
