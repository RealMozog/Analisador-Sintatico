/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uefs.ecomp.analisadorSintatico.controller;

import br.uefs.ecomp.AnalisadorLexico.model.Token;
import br.uefs.ecomp.analisadorSintatico.model.TokensReader;
import java.util.Iterator;

/**
 *
 * @author Alessandro Costa
 */
public class AnalisadorSintaticoController {
    TokensReader tr;
    
    public void analiseArq (Iterator<Token> arq){
        
        tr = new TokensReader(arq);
    }
}
