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
    ErrorList erroList;
    ScanLexema scan;
    
    public TokensReader(Iterator<Token> arq){
        this.arq = arq;
        scan = new ScanLexema();
        this.erroList = new ErrorList();
        
        stateZero(arq.next());
    }
    
    private void setErro(int line, String expected, String found){
        Error erro = new Error(line, expected, found); 
        this.erroList.addErro(erro);
    }
    
    private void stateZero(Token token) {
        if(arq.hasNext()){
            if(token.getCodigo().equals("PRE")){
                switch (token.getLexema()) {
                    case "function":
                        token = arq.next();
                        if(scan.isType(token.getLexema()) || token.getCodigo().equals("IDE")){
                            token = arq.next();
                        } else {
                            setErro(token.getLine(), "Type or Identifier", token.getLexema());
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
                            setErro(token.getLine(), "Identifier", token.getLexema());
                            panicState(token, "(");
                        }
                        
                        function_procedure(token);
                        _return(token);
                        
                        if(token.getLexema().equals("}")){
                           token = arq.next();
                        } else {
                            setErro(token.getLine(), "Delimitator:\"}\"", token.getLexema());
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
                            setErro(token.getLine(), "Identifier or reserverd word: \"start\"", token.getLexema());
                            panicState(token, "(");
                        }
                        
                        function_procedure(token);
                        
                        if(token.getLexema().equals("}")){
                           token = arq.next();
                        } else {
                            setErro(token.getLine(), "Delimitator:\"}\"", token.getLexema());
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
            setErro(token.getLine(), "Delimitator:\"(\"", token.getLexema());
            while(!scan.isType(token.getLexema()) || !token.getCodigo().equals("IDE")){
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
            setErro(token.getLine(), "Delimitator:\")\"", token.getLexema());
            panicState(token, "{");
        }
        
        if(token.getLexema().equals("{")){
            token = arq.next();
        } else {
            setErro(token.getLine(), "Delimitator:\"{\"", token.getLexema());
            while(!token.getLexema().equals("var")){
                token = arq.next();
                if(!arq.hasNext()){
                    break;
                }
            }
        }
        
        if(token.getLexema().equals("var")) {
            token = arq.next();
        } else {
            setErro(token.getLine(), "Reserved word: \"var\"", token.getLexema());
            panicState(token, "{");
        }
        
        var_fuctions_procedures(token);
        
        if(scan.isCommands(token.getLexema()) || scan.isModifiers(token.getLexema())
                || token.getCodigo().equals("IDE")){
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
        
        if(token.getLexema().equals("if")){
            token = this.arq.next();
            ifStatement(token);
        }
        
        if(token.getLexema().equals("while")){
            token = this.arq.next();
            whileStatement(token);
        }
        
        if(token.getLexema().equals("read")){
            token = this.arq.next();
            readStatement(token);
        }
        
        if(token.getLexema().equals("print")){
            token = this.arq.next();
            printStatement(token);
        }
        
        if(scan.isModifiers(token.getLexema())) {
            token = this.arq.next();
            if(token.getLexema().equals(".")){
                call_variable(token);
            } else {
                setErro(token.getLine(), "Delimitator: \".\"", token.getLexema());
                while(!token.getCodigo().equals("IDE")){
                    token = arq.next();
                    if(!arq.hasNext()){
                        break;
                    }
                }
            }
        }
        
        if(token.getCodigo().equals("IDE")){
            token = this.arq.next();
            call_procedure_function(token);
        }
    }
    
    private void call_procedure_function(Token token){
        
        if(token.getLexema().equals("(")){
            token = this.arq.next();
        } else {
            setErro(token.getLine(), "Delimitator: \"(\"", token.getLexema());
            while(!token.getCodigo().equals("NRO") || !token.getCodigo().equals("CDC")
                    || !scan.isBooleans(token.getLexema()) || !scan.isModifiers(token.getLexema())
                    || !token.getLexema().equals("-")){
                token = this.arq.next();
                if(!this.arq.hasNext()){
                    break;
                }
            }
        }
        
        realParams(token);
        
        if(token.getLexema().equals(")")){
            token = this.arq.next();
        } else {
            setErro(token.getLine(), "Delimitator: \")\"", token.getLexema());
            panicState(token, ";");
        } 
        
        if(token.getLexema().equals(";")){
            token = this.arq.next();
        } else {
            setErro(token.getLine(), "Delimitator: \";\"", token.getLexema());
            token = this.arq.next();
        }
        
        commands(token);
    }
    
    private void realParams(Token token){
        realParam(token);
    }
    
    private void realParam(Token token){
        
        if(token.getCodigo().equals("NRO") || token.getCodigo().equals("CDC") 
                || scan.isBooleans(token.getLexema()) || scan.isModifiers(token.getLexema())
                || token.getLexema().equals("-")){
            if(scan.isModifiers(token.getLexema())){
                token = this.arq.next();
                call_variable(token);
            } else 
                if(token.getLexema().equals("-")){
                    token = this.arq.next();
                    if(token.getCodigo().equals("NRO")){
                        token = this.arq.next();
                    } else {
                        setErro(token.getLine(), "Number", token.getLexema());
                        panicState(token, ",");
                    }
                }
            else {
                token = this.arq.next();
            }
            
            more_real_params(token);
        } 
    }
    
    private void more_real_params(Token token){
        
        if(token.getLexema().equals(",")){
            token = this.arq.next();
            if(token.getCodigo().equals("NRO") || token.getCodigo().equals("CDC") 
                || scan.isBooleans(token.getLexema()) || scan.isModifiers(token.getLexema())
                || token.getLexema().equals("-")){
                realParam(token);
            } else {
                setErro(token.getLine(), "Number, string, boolean, variable", token.getLexema());
                panicState(token, ",");
                more_real_params(token);
            }
        }
    }
    
    private void printStatement(Token token){
        
    }
    
    private void readStatement(Token token){
        
    }
    
    private void whileStatement(Token token){
        
    }
    
    private void ifStatement(Token token){
        
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
