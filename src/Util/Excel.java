/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import Controller.Resultado;
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
public class Excel {

    private static String fileName;

    public void criarTabela(String nome, ArrayList<Resultado> resultados) {
        fileName = nome + ".xls";
        HSSFSheet folha;
        HSSFWorkbook workbook;
        try{ //edita
            FileInputStream file = new FileInputStream(new File(fileName));
            workbook = new HSSFWorkbook(file);
            folha = workbook.getSheetAt(0);
            Row row = folha.getRow(folha.getPhysicalNumberOfRows());
            this.criarCelulas(row, folha, resultados);
              
       
        } catch(IOException e){ // se não encontrar o arquivo, cria o arquivo
            workbook = new HSSFWorkbook();
            folha = workbook.createSheet("Resultados " + nome);
            this.datasetDesbalanceado(folha, resultados);
        }
        
        try {
            FileOutputStream out = new FileOutputStream(new File(Excel.fileName));
            workbook.write(out);
            out.close();
            System.out.println("Arquivo Excel criado/editado com sucesso!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Arquivo não encontrado!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro na edição do arquivo!");
        }
    }

    private void datasetBalanceado(HSSFSheet folha, ArrayList<Resultado> resultados) {
        Row row = folha.createRow(0);
        Cell cabecalho0 = row.createCell(0);
        cabecalho0.setCellValue("ID");
        Cell cabecalho1 = row.createCell(1);
        cabecalho1.setCellValue("Acuracia Treino Sem AG");
        Cell cabecalho2 = row.createCell(2);
        cabecalho2.setCellValue("Acuracia Teste Sem AG");
        Cell cabecalho3 = row.createCell(3);
        cabecalho3.setCellValue("Acuracia Treino Com AG");
        Cell cabecalho4 = row.createCell(4);
        cabecalho4.setCellValue("Acuracia Teste Com AG");
        Cell cabecalho5 = row.createCell(5);
        cabecalho5.setCellValue("Interpretabilidade Sem AG");
        Cell cabecalho6 = row.createCell(6);
        cabecalho6.setCellValue("Interpretabilidade Com AG");
        Cell cabecalho7 = row.createCell(7);
        cabecalho7.setCellValue("Quantidade de Gerações");
        this.criarCelulas(row, folha, resultados);
    }

    public void datasetDesbalanceado(HSSFSheet folha, ArrayList<Resultado> resultados) {
        Row row = folha.createRow(0);
        Cell cabecalho0 = row.createCell(0);
        cabecalho0.setCellValue("ID");
        Cell cabecalho1 = row.createCell(1);
        cabecalho1.setCellValue("AUC Treino Sem AG");
        Cell cabecalho2 = row.createCell(2);
        cabecalho2.setCellValue("AUC Teste Sem AG");
        Cell cabecalho3 = row.createCell(3);
        cabecalho3.setCellValue("AUC Treino Com AG");
        Cell cabecalho4 = row.createCell(4);
        cabecalho4.setCellValue("AUC Teste Com AG");
        Cell cabecalho5 = row.createCell(5);
        cabecalho5.setCellValue("Interpretabilidade Sem AG");
        Cell cabecalho6 = row.createCell(6);
        cabecalho6.setCellValue("Interpretabilidade Com AG");
        Cell cabecalho7 = row.createCell(7);
        cabecalho7.setCellValue("Quantidade de Gerações");
        this.criarCelulas(row, folha, resultados);
    }

    private void criarCelulas(Row row, HSSFSheet folha, ArrayList<Resultado> resultados) {
        int rownum = folha.getPhysicalNumberOfRows();
        for (Resultado resultado : resultados) {
            System.out.println("daaaaaaaale");
            row = folha.createRow(rownum++);
            int cellnum = 0;
            Cell cellID = row.createCell(cellnum++);
            cellID.setCellValue(rownum - 1);

            Cell cellAcuraciaTraSAG = row.createCell(cellnum++);
            cellAcuraciaTraSAG.setCellValue(resultado.acuraciaTrainSAG);

            Cell cellAcuraciaTstSAG = row.createCell(cellnum++);
            cellAcuraciaTstSAG.setCellValue(resultado.acuraciaTestSAG);

            Cell cellAcuraciaTraCAG = row.createCell(cellnum++);
            cellAcuraciaTraCAG.setCellValue(resultado.acuraciaTrainCAG);

            Cell cellAcuraciaTstCAG = row.createCell(cellnum++);
            cellAcuraciaTstCAG.setCellValue(resultado.acuraciaTestCAG);

            Cell cellInterpretabilidadeSAG = row.createCell(cellnum++);
            cellInterpretabilidadeSAG.setCellValue(resultado.InterpSAG);

            Cell cellInterpretabilidadeCAG = row.createCell(cellnum++);
            cellInterpretabilidadeCAG.setCellValue(resultado.InterpCAG);
            
            Cell cellQtdGeracoes = row.createCell(cellnum++);
            cellQtdGeracoes.setCellValue(resultado.qtd_geracoes);
        }
    }

    

}
