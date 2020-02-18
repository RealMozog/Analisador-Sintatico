/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uefs.ecomp.analisadorSintatico.model;

import br.uefs.ecomp.AnalisadorLexico.model.Token;
import java.util.Iterator;

/**
 *
 * @author sandr
 */
public class TokensReader {
    Iterator<Token> arq;
    ScanLexema scan;
    
    public TokensReader(Iterator<Token> arq){
        this.arq = arq;
        scan = new ScanLexema();
        
        stateZero(arq.next());
    }
    
    private void stateZero(Token token) {
        
        if(arq.hasNext()){
            if(token.getCodigo().equals("PRE")){
                switch (token.getLexema()) {
                    case "function":
                        token = arq.next();
                        if(scan.isType(token.getLexema())){
                            token = arq.next();
                        } else {
                            while(!token.getCodigo().equals("IDE")){
                                token = arq.next();
                                if(!arq.hasNext()){
                                    break;
                                }
                            }
                        }
                        
                        if(token.getCodigo().equals("IDE")){
                            token = arq.next();
                        } else {
                            panicState(token, "(");
                        }
                        
                        function_procedure(token);
                        _return(token);
                        
                        if(token.getLexema().equals("}")){
                           token = arq.next();
                        } else {
                            while(arq.hasNext()){
                                token = arq.next();
                            }
                        }
                        break;
                    case "procedure":
                        token = arq.next();
                        if(token.getCodigo().equals("IDE") || token.getLexema().equals("start")){
                            token = arq.next();
                        } else {
                            panicState(token, "(");
                        }
                        
                        function_procedure(token);
                        
                        if(token.getLexema().equals("}")){
                           token = arq.next();
                        } else {
                            while(arq.hasNext()){
                                token = arq.next();
                            }
                        }
                        break;
                    default:
                        break;
                }  
            }
        }
    }
    
    public void function_procedure (Token token){
        
        if(token.getLexema().equals("(")){
            token = arq.next();
        } else {
            while(!scan.isType(token.getLexema())){
                token = arq.next();
                if(!arq.hasNext()){
                    break;
                }
            }
        }
        
        params_list(arq.next());
        
        if(token.getLexema().equals(")")){
            token = arq.next();
        } else {
            panicState(token, "{");
        }
        
        if(token.getLexema().equals("{")){
            token = arq.next();
        } else {
            while(!token.getLexema().equals("var") || !scan.isCommands(token.getLexema())
                    || !scan.isModifiers(token.getLexema()) || !token.getCodigo().equals("IDE")){
                token = arq.next();
                if(!arq.hasNext()){
                    break;
                }
            }
        }
        
        if(token.getLexema().equals("var")) {
            token = arq.next();
            var_fuctions_procedures(token);
        }
        
        if(scan.isCommands(token.getLexema())){
            commands(token);
        }
        
        if(scan.isModifiers(token.getLexema())) {
            token = arq.next();
            if(token.getLexema().equals(".")){
                call_variable(token);
            } else {
                while(!token.getCodigo().equals("IDE")){
                    token = arq.next();
                    if(!arq.hasNext()){
                        break;
                    }
                }
            }
        }
        
        if(token.getCodigo().equals("IDE")) {
            token = arq.next();
            paths(token);
        }
    }
    
    private void params_list (Token token) {
        
    }
    
    private void var_fuctions_procedures (Token token){
        
    }
    
    private void commands (Token token) {
        
    }
    
    private void _return (Token token) {
        
    }
    
    private void call_variable (Token token){
        
    }
    
    private void paths (Token token){
        
    }
    
    private void panicState (Token token, String sinalizer) {
        while(!token.getLexema().equals(sinalizer)){
            token = arq.next();
            if(!arq.hasNext()){
                break;
            }
        }
    }
}
