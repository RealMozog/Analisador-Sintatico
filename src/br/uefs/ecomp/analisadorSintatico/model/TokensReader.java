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
    }
    
    private void setErro(int line, String expected, String found){
        Error erro = new Error(line, expected, found); 
        this.erroList.addErro(erro);
    }
    
    private void next(Token token){
        if(this.arq.hasNext()){
            token = this.arq.next();
        }
        
        stateZero(token);
    }
    
    public ErrorList stateZero(Token token) {
        if(this.arq.hasNext()){
            global_values(token);
            
            if(token.getLexema().equals("}")){
                next(token);
            } else {
                setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
                while(!token.getLexema().equals("function") || !token.getLexema().equals("procedure")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next(token);
                }
            }
            
            if(token.getCodigo().equals("PRE")){
                switch (token.getLexema()) {
                    case "function":
                        next(token);
                        if(scan.isType(token.getLexema()) || token.getCodigo().equals("IDE")){
                            next(token);
                        } else {
                            setErro(token.getLine(), "Type or Identifier", token.getLexema());
                            while(!token.getCodigo().equals("IDE")){
                                next(token);
                                if(!arq.hasNext()){
                                    break;
                                }
                            }
                        }
                        
                        if(token.getCodigo().equals("IDE")){
                            next(token);
                        } else {
                            setErro(token.getLine(), "Identifier", token.getLexema());
                            panicState(token, "(");
                        }
                        
                        function_procedure(token);
                        _return(token);
                        
                        if(token.getLexema().equals("}")){
                           next(token);
                        } else {
                            setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
                            while(arq.hasNext()){
                                next(token);
                            }
                        }
                        break;
                    case "procedure":
                        next(token);
                        if(token.getCodigo().equals("IDE") || token.getLexema().equals("start")){
                            next(token);
                        } else {
                            setErro(token.getLine(), "Identifier or reserverd word: \"start", token.getLexema());
                            panicState(token, "(");
                        }
                        
                        function_procedure(token);
                        
                        if(token.getLexema().equals("}")){
                           next(token);
                        } else {
                            setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
                            while(arq.hasNext()){
                                next(token);
                            }
                        }
                        break;
                    default:
                        break;
                }  
            }
        }
        
        return this.erroList;
    }
    
    private void global_values(Token token){
        
        if(token.getLexema().equals("var")){
            next(token);
            if(token.getLexema().equals("{")){
                next(token);
            } else {
                setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
                next(token);
            }
            
            var_values_declaration(token);
            
            if(token.getLexema().equals("}")){
                next(token);
            } else {
                setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
                panicState(token, "const");
            }
            
            if(token.getLexema().equals("const")){
                next(token);
            } else {
                setErro(token.getLine(), "reserved word: \"const", token.getLexema());
                panicState(token, "{");
            }
            
            if(token.getLexema().equals("{")){
                next(token);
            } else {
                setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
                next(token);
            }

            const_values_declaration(token);
        }
        
        if(token.getLexema().equals("const")){
            next(token);
            if(token.getLexema().equals("{")){
                next(token);
            } else {
                setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
                next(token);
            }
            
            const_values_declaration(token);
            
            if(token.getLexema().equals("}")){
                next(token);
            } else {
                setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
                panicState(token, "var");
            }
            
            if(token.getLexema().equals("var")){
                next(token);
            } else {
                setErro(token.getLine(), "reserved word: \"var", token.getLexema());
                panicState(token, "{");
            }
            
            if(token.getLexema().equals("{")){
                next(token);
            } else {
                setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
                next(token);
            }

            var_values_declaration(token);
            
            if(token.getLexema().equals("}")){
                next(token);
            } else {
                setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
                next(token);
            }
        }
    }
    
    private void var_values_declaration(Token token){
        
        if(scan.isType(token.getLexema())){
            next(token);
            var_values_atribuition(token);
            var_more_atribuition(token);
            if(token.getLexema().equals(";")){
                next(token);
            } else {
                setErro(token.getLine(), "Delimitator:\";", token.getLexema());
                next(token);
                var_values_declaration(token);
            }
        }
        
        if(token.getLexema().equals("typedef")){
            next(token);
            if(token.getLexema().equals("struct")){
                next(token);
            } else {
                setErro(token.getLine(), "Reserved word: \"struct", token.getLexema());
                while(!token.getCodigo().equals("IDE")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next(token);
                }
            }
            
            IDE_struct(token);
            
            var_values_declaration(token);
        }
        
        if(token.getLexema().equals("struct")){
            next(token);
            
            IDE_struct(token);
            
            var_values_declaration(token);
        }
    }
    
    private void IDE_struct(Token token){
        
        if(token.getCodigo().equals("IDE")){
            next(token);
        } else {
            setErro(token.getLine(), "Identifier", token.getLexema());
            panicState(token, "{");
        }
        
        IDE_struct_2(token);
    }
    
    private void IDE_struct_2(Token token){
        
        if(token.getLexema().equals("extends")){
            next(token);
            if(token.getCodigo().equals("IDE")){
                next(token);
            } else {
                setErro(token.getLine(), "Identifier", token.getLexema());
                panicState(token, "{");
            }
        }
        
        if(token.getLexema().equals("{")){
            next(token);
        } else {
            setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
            panicState(token, "var");
        }
        
        if(token.getLexema().equals("var")){
            next(token);
        } else {
            setErro(token.getLine(), "Reserved word: \"var", token.getLexema());
            panicState(token, "{");
        }
        
        if(token.getLexema().equals("{")){
            next(token);
        } else {
            setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
            next(token);
        }
        
        var_values_declaration(token);
        
        if(token.getLexema().equals("}")){
            next(token);
        } else {
            setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
            panicState(token, "}");
        }

        if(token.getLexema().equals("}")){
            next(token);
        } else {
            setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
            next(token);
        }
        
        var_values_declaration(token);
    }
    
    private void var_values_atribuition(Token token){
        
        if(token.getCodigo().equals("IDE")){
            next(token);
        } else {
            setErro(token.getLine(), "Identifier", token.getLexema());
            next(token);
        }
        
        array_verification(token);
    }
    
    private void array_verification(Token token){
        
        if(token.getLexema().equals("[")){
            next(token);
            if(token.getCodigo().equals("NRO")){
                next(token);
            } else {
                setErro(token.getLine(), "Number", token.getLexema());
                panicState(token, "]");
            }
            
            array_verification(token);
        }
    }
    
    private void var_more_atribuition(Token token){
        
        if(token.getLexema().equals(",")){
            next(token);
            var_values_atribuition(token);
            var_more_atribuition(token);
        }
    }
    
    private void const_values_declaration(Token token){
        
        if(scan.isType(token.getLexema())){
            next(token);
            const_values_atribuition(token);
            const_more_atribuition(token);
            if(token.getLexema().equals(";")){
                next(token);
            } else {
                setErro(token.getLine(), "Delimitator: \";", token.getLexema());
                next(token);
            }
            
            const_values_declaration(token);
        } 
    }
    
    private void const_values_atribuition(Token token){
        
        if(token.getCodigo().equals("IDE")){
            next(token);
        } else {
            setErro(token.getLine(), "Identifier", token.getLexema());
            panicState(token, "=");
        }
        
        if(token.getLexema().equals("=")){
            next(token);
        } else {
            setErro(token.getLine(), "Operator: \"=", token.getLexema());
            while(!token.getCodigo().equals("NRO") || !token.getCodigo().equals("CDC")
                    || scan.isBooleans(token.getLexema())){
                if(!this.arq.hasNext()){
                    break;
                }
                next(token);
            }
        }
        
        value_const(token);
    }
    
    private void value_const(Token token){
        
        if(token.getCodigo().equals("NRO") || token.getCodigo().equals("CDC")
                || scan.isBooleans(token.getLexema())){
            next(token);
        } else {
            setErro(token.getLine(), "Number, string, boolean", token.getLexema());
            next(token);
            const_more_atribuition(token);
        }
    }
    
    private void const_more_atribuition(Token token){
        
        if(token.getLexema().equals(",")){
            next(token);
            const_values_atribuition(token);
            const_more_atribuition(token);
        }
    }
    
    private void function_procedure (Token token){
        
        if(token.getLexema().equals("(")){
            next(token);
        } else {
            setErro(token.getLine(), "Delimitator:\"(", token.getLexema());
            while(!scan.isType(token.getLexema()) || !token.getCodigo().equals("IDE")){
                if(!arq.hasNext()){
                    break;
                }
                next(token);                
            }
        }
        
        params_list(token);
        
        if(token.getLexema().equals(")")){
            next(token);
        } else {
            setErro(token.getLine(), "Delimitator:\")", token.getLexema());
            panicState(token, "{");
        }
        
        if(token.getLexema().equals("{")){
            next(token);
        } else {
            setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
            while(!token.getLexema().equals("var")){
                if(!arq.hasNext()){
                    break;
                }
                next(token);
            }
        }
        
        if(token.getLexema().equals("var")) {
            next(token);
        } else {
            setErro(token.getLine(), "Reserved word: \"var", token.getLexema());
            panicState(token, "{");
        }
        
        var_fuctions_procedures(token);
        
        if(scan.isCommands(token.getLexema()) || scan.isModifiers(token.getLexema())
                || token.getCodigo().equals("IDE")){
            commands(token);
        }
    }
    
    private void params_list (Token token) {
        
    }
    
    private void var_fuctions_procedures (Token token){
        
    }
    
    private void commands (Token token) {
        
        if(token.getLexema().equals("if")){
            next(token);
            ifStatement(token);
        }
        
        if(token.getLexema().equals("while")){
            next(token);
            whileStatement(token);
        }
        
        if(token.getLexema().equals("read")){
            next(token);
            readStatement(token);
        }
        
        if(token.getLexema().equals("print")){
            next(token);
            printStatement(token);
        }
        
        if(scan.isModifiers(token.getLexema())) {
            next(token);
            call_variable(token);
        }
        
        if(token.getCodigo().equals("IDE")){
            next(token);
            if(token.getLexema().equals(".") || token.getLexema().equals("[")){
                paths(token);
            }
            
            if(token.getLexema().equals("=")){
                next(token);
                assing(token);
            } else {
                call_procedure_function(token);
            }
        }
    }
    
    private void assing(Token token){
        
        if(token.getCodigo().equals("IDE")){
            next(token);
            if(token.getLexema().equals("(")){
                call_procedure_function(token);
            } else {
                expression(token);
            }
        }
        
        if(token.getCodigo().equals("CDC")){
            next(token);
        }
        
        if(token.getLexema().equals("-")){
            next(token);
            if(token.getCodigo().equals("NRO")){
                next(token);
                expression(token);
            } else {
                setErro(token.getLine(), "Number", token.getLexema());
                while(!token.getCodigo().equals("NRO")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next(token);
                }
            }
        }
        
        if(token.getLexema().equals("!")){
            next(token);
            unary_operation(token);
        }
        
        if(scan.isBooleans(token.getLexema())){
            next(token);
            expression(token);
        } else {
            setErro(token.getLine(), "Number, string, boolean, varible, function, procedure", token.getLexema());
            panicState(token, ";");
        }
        
        if(token.getLexema().equals(";")){
            next(token);
        } else {
            setErro(token.getLine(), "Delimitator: \";", token.getLexema());
            next(token);
        }
        
        commands(token);
    }
    
    private void unary_operation(Token token){
        
    }
    
    private void expression(Token token) {
        
    }
    
    private void call_procedure_function(Token token){
        
        if(token.getLexema().equals("(")){
            next(token);
        } else {
            setErro(token.getLine(), "Delimitator: \"(", token.getLexema());
            while(!token.getCodigo().equals("NRO") || !token.getCodigo().equals("CDC")
                    || !scan.isBooleans(token.getLexema()) || !scan.isModifiers(token.getLexema())
                    || !token.getLexema().equals("-")){
                if(!this.arq.hasNext()){
                    break;
                }
                next(token);
            }
        }
        
        realParams(token);
        
        if(token.getLexema().equals(")")){
            next(token);
        } else {
            setErro(token.getLine(), "Delimitator: \")", token.getLexema());
            panicState(token, ";");
        } 
        
        if(token.getLexema().equals(";")){
            next(token);
        } else {
            setErro(token.getLine(), "Delimitator: \";", token.getLexema());
            next(token);
        }
        
        commands(token);
    }
    
    private void realParams(Token token){
        realParam(token);
    }
    
    private void realParam(Token token){
        
        if(token.getCodigo().equals("NRO") || token.getCodigo().equals("CDC") 
                || scan.isBooleans(token.getLexema()) || scan.isModifiers(token.getLexema())
                || token.getLexema().equals("-") || token.getCodigo().equals("IDE")){
            if(scan.isModifiers(token.getLexema())){
                next(token);
                call_variable(token);
                if(token.getLexema().equals("IDE")){
                    next(token);
                } else {
                    setErro(token.getLine(), "Identifier", token.getLexema());
                    panicState(token, ",");
                }
            } else 
                if(token.getLexema().equals("-")){
                    next(token);
                    if(token.getCodigo().equals("NRO")){
                        next(token);
                    } else {
                        setErro(token.getLine(), "Number", token.getLexema());
                        panicState(token, ",");
                    }
                }
            else {
                next(token);
            }
            
            more_real_params(token);
        } 
    }
    
    private void more_real_params(Token token){
        
        if(token.getLexema().equals(",")){
            next(token);
            if(token.getCodigo().equals("NRO") || token.getCodigo().equals("CDC") 
                || scan.isBooleans(token.getLexema()) || scan.isModifiers(token.getLexema())
                || token.getLexema().equals("-") || token.getCodigo().equals("IDE")){
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
        
        if(token.getLexema().equals(".")){
            next(token);
        } else {
            setErro(token.getLine(), "Delimitator: \".", token.getLexema());
            while(!token.getCodigo().equals("IDE")){
                if(!arq.hasNext()){
                    break;
                }
                next(token);
            }
        }
    }
    
    private void paths (Token token){
        
        if(token.getLexema().equals(".")){
            next(token);
            struct(token);
        }
        
        if(token.getLexema().equals("[")){
            next(token);
            matriz(token);
        }
    }
    
    private void struct(Token token){
        
        if(token.getCodigo().equals("IDE")){
            next(token);
            if(token.getLexema().equals(".") || token.getLexema().equals("[")){
                paths(token);
            }
        } else {
            setErro(token.getLine(), "Identifier", token.getLexema());
            panicState(token, "=");
        }
    }
    
    private void matriz(Token token){
        
        if(token.getCodigo().equals("NRO") || token.getCodigo().equals("IDE")){
            next(token);
        } else {
            setErro(token.getLine(), "Number, indetifier", token.getLexema());
            panicState(token, "]");
        }
        
        if(token.getLexema().equals("]")){
            next(token);
        } else {
            setErro(token.getLine(), "Delimitator: \"]", token.getLexema());
            panicState(token, "=");
        }
    }
    
    private void panicState (Token token, String sinalizer) {
        while(!token.getLexema().equals(sinalizer)){
            if(!arq.hasNext()){
                break;
            }
            next(token);
        }
    }
}
