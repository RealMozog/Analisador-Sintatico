/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uefs.ecomp.analisadorSintatico.model;

import br.uefs.ecomp.AnalisadorLexico.controller.AnalisadorLexicoController;
import br.uefs.ecomp.AnalisadorLexico.model.Token;
import br.uefs.ecomp.AnalisadorLexico.model.TokenError;
import static br.uefs.ecomp.AnalisadorLexico.view.AnalisadorLexico.tokensInValidos;
import static br.uefs.ecomp.AnalisadorLexico.view.AnalisadorLexico.tokensValidos;
import static br.uefs.ecomp.AnalisadorLexico.view.AnalisadorLexico.writeOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sandr
 */
public class AnaliseLexica {
    private final ArrayList<List<Token>> arqs;
    
    
    public AnaliseLexica(){
        this.arqs = new ArrayList<>();
        
        readArqs();
    }
    
    private void readArqs(){
        AnalisadorLexicoController controller = new AnalisadorLexicoController();
        Iterator<Token> iterator;
        Iterator<TokenError> iteratorErr;
        int count = 1;
        String w = "";
        
        try{
            String path = "input\\entrada" + count + ".txt";
            String arq = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
 
            while (!arq.isEmpty()){
                
                controller.analiseArq(arq);
                
                iterator = controller.listTokens().iterator();
                w += tokensValidos(iterator);
                
                iteratorErr = controller.iteratorErros();
                
                if(!iteratorErr.hasNext()){
                    System.out.printf("Nenhum erro léxico encontrado no arquivo de entrada" + count + "\n");
                    this.addArq(controller.listTokens());
                } else {
                    w += tokensInValidos(iteratorErr);
                }
                
                // System.out.print(w + "\n");
                
                writeOutput (w, count);
                
                arq = null;
                w = "";
                count++;
                path = "input\\entrada" + count + ".txt";
                arq = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            }
            
        } catch (IOException e) {
             System.err.printf("Erro na abertura do arquivo: %s.\n",e.getMessage());
        }
    }
    
    private void addArq(List<Token> arq){
        this.arqs.add(arq);
    }

    public ArrayList<List<Token>> getArqs(){
        return this.arqs;
    }
}
