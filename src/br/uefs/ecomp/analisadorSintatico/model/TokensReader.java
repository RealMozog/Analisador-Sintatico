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
    Token token;
    
    public TokensReader(Iterator<Token> arq){
        this.arq = arq;
        scan = new ScanLexema();
        this.erroList = new ErrorList();
        token = this.arq.next();
    }
    
    private void setErro(int line, String expected, String found){
        Error erro = new Error(line, expected, found); 
        this.erroList.addErro(erro);
    }
    
    private void next(){
        if(this.arq.hasNext()){
            this.token = this.arq.next();
        } else {
            stateZero();
        }
    }
    
    public ErrorList stateZero() {
        if(this.arq.hasNext()){
            global_values();
            
            if(this.token.getCodigo().equals("PRE")){
                switch (this.token.getLexema()) {
                    case "function":
                        next();
                        if(scan.isType(token.getLexema()) || token.getCodigo().equals("IDE")){
                            next();
                        } else {
                            setErro(token.getLine(), "Type or Identifier", token.getLexema());
                            while(!token.getCodigo().equals("IDE")){
                                next();
                                if(!this.arq.hasNext()){
                                    break;
                                }
                            }
                        }
                        
                        if(token.getCodigo().equals("IDE")){
                            next();
                        } else {
                            setErro(token.getLine(), "Identifier", token.getLexema());
                            panicState("(");
                        }
                        
                        function_procedure();
                        _return();
                        
                        if(token.getLexema().equals("}")){
                           next();
                        } else {
                            setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
                            while(arq.hasNext()){
                                next();
                            }
                        }
                        break;
                    case "procedure":
                        next();
                        if(token.getCodigo().equals("IDE") || token.getLexema().equals("start")){
                            next();
                        } else {
                            setErro(token.getLine(), "Identifier or reserverd word: \"start", token.getLexema());
                            panicState("(");
                        }
                        
                        function_procedure();
                        
                        if(token.getLexema().equals("}")){
                           next();
                        } else {
                            setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
                            while(arq.hasNext()){
                                next();
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
    
    private void global_values(){
        
        if(token.getLexema().equals("var")){
            next();
            if(token.getLexema().equals("{")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
                while(!scan.isType(token.getLexema()) || !token.getCodigo().equals("IDE")
                        || !token.getLexema().equals("typedef") || !token.getLexema().equals("struct")
                        || !token.getLexema().equals("}")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            var_values_declaration();
            
            if(token.getLexema().equals("}")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
                panicState("const");
            }
            
            if(token.getLexema().equals("const")){
                next();
            } else {
                setErro(token.getLine(), "reserved word: \"const", token.getLexema());
                panicState("{");
            }
            
            if(token.getLexema().equals("{")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
                while(!scan.isType(token.getLexema()) || !token.getCodigo().equals("IDE")
                        || !token.getLexema().equals("}")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }

            const_values_declaration();
            
            if(token.getLexema().equals("}")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
                while(!token.getLexema().equals("function") || !token.getLexema().equals("procedure")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
        }
        
        if(token.getLexema().equals("const")){
            next();
            if(token.getLexema().equals("{")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
                while(!scan.isType(token.getLexema()) || !token.getCodigo().equals("IDE")
                        || !token.getLexema().equals("}")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            const_values_declaration();
            
            if(token.getLexema().equals("}")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
                panicState("var");
            }
            
            if(token.getLexema().equals("var")){
                next();
            } else {
                setErro(token.getLine(), "reserved word: \"var", token.getLexema());
                panicState("{");
            }
            
            if(token.getLexema().equals("{")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
                while(!scan.isType(token.getLexema()) || !token.getCodigo().equals("IDE")
                        || !token.getLexema().equals("typedef") || !token.getLexema().equals("struct")
                        || !token.getLexema().equals("}")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }

            var_values_declaration();
            
            if(token.getLexema().equals("}")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
                while(!token.getLexema().equals("function") || !token.getLexema().equals("procedure")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
        }
    }
    
    private void var_values_declaration(){
        
        if(scan.isType(token.getLexema())){
            next();
            var_values_atribuition();
            var_more_atribuition();
            if(token.getLexema().equals(";")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator:\";", token.getLexema());
                while(!scan.isType(token.getLexema()) || !token.getCodigo().equals("IDE")
                        || !token.getLexema().equals("typedef") || !token.getLexema().equals("struct")
                        || !token.getLexema().equals("}")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            var_values_declaration();
        }
        
        if(token.getLexema().equals("typedef")){
            next();
            if(token.getLexema().equals("struct")){
                next();
            } else {
                setErro(token.getLine(), "Reserved word: \"struct", token.getLexema());
                while(!token.getCodigo().equals("IDE")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            IDE_struct();
            
            var_values_declaration();
        }
        
        if(token.getLexema().equals("struct")){
            next();
            
            IDE_struct();
            
            var_values_declaration();
        }
    }
    
    private void IDE_struct(){
        
        if(token.getCodigo().equals("IDE")){
            next();
        } else {
            setErro(token.getLine(), "Identifier", token.getLexema());
            panicState("{");
        }
        
        IDE_struct_2();
    }
    
    private void IDE_struct_2(){
        
        if(token.getLexema().equals("extends")){
            next();
            if(token.getCodigo().equals("IDE")){
                next();
            } else {
                setErro(token.getLine(), "Identifier", token.getLexema());
                panicState("{");
            }
        }
        
        if(token.getLexema().equals("{")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
            panicState("var");
        }
        
        if(token.getLexema().equals("var")){
            next();
        } else {
            setErro(token.getLine(), "Reserved word: \"var", token.getLexema());
            panicState("{");
        }
        
        if(token.getLexema().equals("{")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
            while(!scan.isType(token.getLexema()) || !token.getCodigo().equals("IDE")
                    || !token.getLexema().equals("typedef") || !token.getLexema().equals("struct")
                    || !token.getLexema().equals("}")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        var_values_declaration();
        
        if(token.getLexema().equals("}")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
            panicState("}");
        }

        if(token.getLexema().equals("}")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator:\"}", token.getLexema());
            while(!scan.isType(token.getLexema()) || !token.getCodigo().equals("IDE")
                    || !token.getLexema().equals("typedef") || !token.getLexema().equals("struct")
                    || !token.getLexema().equals("}")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        var_values_declaration();
    }
    
    private void var_values_atribuition(){
        
        if(token.getCodigo().equals("IDE")){
            next();
        } else {
            setErro(token.getLine(), "Identifier", token.getLexema());
            while(!scan.isType(token.getLexema()) || !token.getCodigo().equals("IDE")
                    || !token.getLexema().equals("typedef") || !token.getLexema().equals("struct")
                    || !token.getLexema().equals("}") || !token.getLexema().equals("[")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        array_verification();
    }
    
    private void array_verification(){
        
        if(token.getLexema().equals("[")){
            next();
            if(token.getCodigo().equals("NRO")){
                next();
            } else {
                setErro(token.getLine(), "Number", token.getLexema());
                panicState("]");
            }
            
            array_verification();
        }
    }
    
    private void var_more_atribuition(){
        
        if(token.getLexema().equals(",")){
            next();
            var_values_atribuition();
            var_more_atribuition();
        }
    }
    
    private void const_values_declaration(){
        
        if(scan.isType(token.getLexema())){
            next();
            const_values_atribuition();
            const_more_atribuition();
            if(token.getLexema().equals(";")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator: \";", token.getLexema());
                while(!scan.isType(token.getLexema()) || !token.getCodigo().equals("IDE")
                        || !token.getLexema().equals("}")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            const_values_declaration();
        } 
    }
    
    private void const_values_atribuition(){
        
        if(token.getCodigo().equals("IDE")){
            next();
        } else {
            setErro(token.getLine(), "Identifier", token.getLexema());
            panicState("=");
        }
        
        if(token.getLexema().equals("=")){
            next();
        } else {
            setErro(token.getLine(), "Operator: \"=", token.getLexema());
            while(!token.getCodigo().equals("NRO") || !token.getCodigo().equals("CDC")
                    || scan.isBooleans(token.getLexema())){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        value_const();
    }
    
    private void value_const(){
        
        if(token.getCodigo().equals("NRO") || token.getCodigo().equals("CDC")
                || scan.isBooleans(token.getLexema())){
            next();
        } else {
            setErro(token.getLine(), "Number, string, boolean", token.getLexema());
            panicState(";");
        }
    }
    
    private void const_more_atribuition(){
        
        if(token.getLexema().equals(",")){
            next();
            const_values_atribuition();
            const_more_atribuition();
        }
    }
    
    private void function_procedure (){
        
        if(token.getLexema().equals("(")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator:\"(", token.getLexema());
            while(!scan.isType(token.getLexema()) || !token.getCodigo().equals("IDE")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();                
            }
        }
        
        params_list();
        
        if(token.getLexema().equals(")")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator:\")", token.getLexema());
            panicState("{");
        }
        
        if(token.getLexema().equals("{")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator:\"{", token.getLexema());
            while(!token.getLexema().equals("var")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        if(token.getLexema().equals("var")) {
            next();
        } else {
            setErro(token.getLine(), "Reserved word: \"var", token.getLexema());
            panicState("{");
        }
        
        var_fuctions_procedures();
        
        if(scan.isCommands(token.getLexema()) || scan.isModifiers(token.getLexema())
                || token.getCodigo().equals("IDE")){
            commands();
        }
    }
    
    private void params_list () {
        param();
    }
    
    private void param(){
        
        if(scan.isType(token.getLexema()) || token.getCodigo().equals("IDE")){
            next();
            if(token.getCodigo().equals("IDE")){
                next();
            } else {
                setErro(token.getLine(), "Identifier", token.getLexema());
                while(!token.getLexema().equals(",") || !token.getLexema().equals(")")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }

            more_params();
        }
    }
    
    private void more_params(){
        
        if(token.getLexema().equals(",")){
            next();
            param();
        } 
    }
            
    
    private void var_fuctions_procedures (){
        
        if(token.getLexema().equals("{")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator: \"{", token.getLexema());
            while(!scan.isType(token.getLexema()) || !token.getLexema().equals("struct")
                    || !token.getLexema().equals("typedef") || !token.getLexema().equals("}")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        var_values_declaration();
        
        if(token.getLexema().equals("}")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator: \"}", token.getLexema());
            while(!scan.isCommands(token.getLexema()) || !token.getCodigo().equals("IDE")
                    || !token.getLexema().equals("return") || !token.getLexema().equals("}")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
    }
    
    private void commands () {
        
        if(token.getLexema().equals("if")){
            next();
            ifStatement();
        }
        
        if(token.getLexema().equals("while")){
            next();
            whileStatement();
        }
        
        if(token.getLexema().equals("read")){
            next();
            readStatement();
        }
        
        if(token.getLexema().equals("print")){
            next();
            printStatement();
        }
        
        call_variable();
        
        if(token.getCodigo().equals("IDE")){
            next();
            if(token.getLexema().equals("(")){
                call_procedure_function();
            } else {
                paths();
                if(token.getLexema().equals("=")){
                    next();
                    assing();
                } else {
                    setErro(token.getLine(), "Attribuition Operator: \"=", token.getLexema());
                    while(!token.getCodigo().equals("NRO")|| !scan.isModifiers(token.getLexema())
                            || token.getCodigo().equals("IDE") || !token.getLexema().equals("-")
                            || token.getCodigo().equals("CDC")){
                        if(!this.arq.hasNext()){
                            break;
                        }
                        next();
                    }
                }
            }
        }
    }
    
    private void assing(){
        
        if(scan.isModifiers(token.getLexema()) || token.getLexema().equals("-") 
                || token.getCodigo().equals("NRO") || scan.isBooleans(token.getLexema())){
            expression();
        } else if(token.getCodigo().equals("IDE")){
                expression();
                if(token.getLexema().equals("(")){
                    call_procedure_function();
                }
        } else if(token.getCodigo().equals("CDC")){
                next();
        } else {
            setErro(token.getLine(), "Number, string, boolean, variable, function, procedure", token.getLexema());
            panicState(";");
        }
        
        if(token.getLexema().equals(";")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator: \";", token.getLexema());
            while(!scan.isCommands(token.getLexema()) || !token.getCodigo().equals("IDE")
                    || !token.getLexema().equals("return") || !token.getLexema().equals("}")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        commands();
    }
    
    private void unary_operation(){
        
        if(scan.isUnaryOp(token.getLexema())){
            next();
            final_value();
        } else 
            if(token.getLexema().equals("!")){
                next();
                final_value();
            } 
            else {
                final_value();
                if(scan.isUnaryOp(token.getLexema())){
                    next();
                } else {
                    setErro(token.getLine(), "Unary operators", token.getLexema());
                    while(!scan.isPlusMinus(token.getLexema()) || !scan.isTimesDiv(token.getLexema())
                            || !token.getLexema().equals(";") || !scan.isRelationalOpStronger(token.getLexema())
                            || !scan.isRelationalOpWeaker(token.getLexema())){
                        if(!this.arq.hasNext()){
                            break;
                        }
                        next();
                    }
                }
            }
        
    }
    
    private void final_value(){
        
        if(scan.isModifiers(token.getLexema())){
            call_variable();
        }

        if(token.getCodigo().equals("IDE")){
            next();
            paths();
        } else {
            setErro(token.getLine(), "Identifier", token.getLexema());
            while(!scan.isOperators(token.getLexema()) || !token.getLexema().equals(";")
                    || !token.getLexema().equals(")") || !token.getLexema().equals(".")
                    || !token.getLexema().equals("[") || !scan.isUnaryOp(token.getLexema())){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        if(token.getLexema().equals("-")){
            next();
            if(token.getCodigo().equals("NRO")){
                next();
            } else {
                setErro(token.getLine(), "Number", token.getLexema());
                while(!scan.isOperators(token.getLexema()) || !token.getLexema().equals(";")
                        || !token.getLexema().equals(")") || !scan.isUnaryOp(token.getLexema())){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
        }
    }
    
    private void commands_exp(){
        
        logical_exp();
    }
    
    private void expression() {
        
        aritmetic_exp();
        if(scan.isRelationalOpStronger(token.getLexema()) || scan.isRelationalOpWeaker(token.getLexema())){
            logical_exp();
        }
    }
    
    private void logical_exp(){
        
        relational_exp();
        opt_logical_exp();
    }
    
    private void relational_exp(){
        
        if(token.getLexema().equals("(")){
            next();
            logical_exp();
            if(token.getLexema().equals(")")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator: \")", token.getLexema());
                while(!scan.isLogicalOp(token.getLexema()) || !token.getLexema().equals(")")
                        || !token.getLexema().equals(";")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
        } else {
            aritmetic_exp();
            opt_relational_exp();
        }
    }
    
    private void aritmetic_exp(){
        
        if(token.getLexema().equals("(")){
            next();
            relational_exp();
            if(token.getLexema().equals(")")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator: \")", token.getLexema());
                while(!scan.isRelationalOpStronger(token.getLexema()) || !scan.isRelationalOpWeaker(token.getLexema())){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
        } else {
            operation();
            op_sum();
        }
    }
    
    private void operation(){
        
        op_unary();
        op_times_div();
    }
    
    private void op_unary(){
        
        if(token.getLexema().equals("(")){
            next();
            aritmetic_exp();
            if(token.getLexema().equals(")")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator: \")", token.getLexema());
                while(!scan.isRelationalOpStronger(token.getLexema()) || !scan.isRelationalOpWeaker(token.getLexema())
                        || !scan.isPlusMinus(token.getLexema()) || !scan.isTimesDiv(token.getLexema())){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
        } else {
            unary_operation();
            final_value();
        }
        
    }
    
    private void op_times_div(){
        
        if(scan.isTimesDiv(token.getLexema())){
            next();
            op_unary();
            op_times_div();
        }
    }
    
    private void op_sum(){
        
        if(scan.isPlusMinus(token.getLexema())){
            next();
            operation();
            op_sum();
        }
    }
    
    private void inequal_exp(){
        
        if(scan.isRelationalOpStronger(token.getLexema())){
            next();
            aritmetic_exp();
            equal_exp();
        }
    }
    
    private void equal_exp(){
        
        if(scan.isRelationalOpWeaker(token.getLexema())){
            next();
            aritmetic_exp();
            inequal_exp();
            equal_exp();
        }
    }
    
    private void opt_relational_exp(){
        
        if(scan.isRelationalOpStronger(token.getLexema())){
            next();
            aritmetic_exp();
            inequal_exp();
            equal_exp();
        } else if(scan.isRelationalOpWeaker(token.getLexema())){
            next();
            aritmetic_exp();
            inequal_exp();
        } else {
            setErro(token.getLine(), "Relational Operators: \"(", token.getLexema());
            while(!token.getLexema().equals("(") || !scan.isUnaryOp(token.getLexema())
                    || !scan.isModifiers(token.getLexema()) || !token.getCodigo().equals("IDE")
                    || !token.getLexema().equals("-") || !scan.isBooleans(token.getLexema())
                    || !token.getCodigo().equals("NRO")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
    }
    
    private void opt_logical_exp(){
        
        if(scan.isLogicalOp(token.getLexema())){
            next();
            logical_exp();
        }
    }
    
    private void call_procedure_function(){
        
        if(token.getLexema().equals("(")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator: \"(", token.getLexema());
            while(!token.getCodigo().equals("NRO") || !token.getCodigo().equals("CDC")
                    || !scan.isBooleans(token.getLexema()) || !scan.isModifiers(token.getLexema())
                    || !token.getLexema().equals("-")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        realParams();
        
        if(token.getLexema().equals(")")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator: \")", token.getLexema());
            panicState(";");
        } 
        
        if(token.getLexema().equals(";")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator: \";", token.getLexema());
            while(!scan.isCommands(token.getLexema()) || !token.getCodigo().equals("IDE")
                    || !token.getLexema().equals("return") || !token.getLexema().equals("}")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        commands();
    }
    
    private void realParams(){
        realParam();
    }
    
    private void realParam(){
        
        if(token.getCodigo().equals("NRO") || token.getCodigo().equals("CDC") 
                || scan.isBooleans(token.getLexema()) || scan.isModifiers(token.getLexema())
                || token.getLexema().equals("-") || token.getCodigo().equals("IDE")){
            if(scan.isModifiers(token.getLexema())){
                call_variable();
                if(token.getLexema().equals("IDE")){
                    next();
                } else {
                    setErro(token.getLine(), "Identifier", token.getLexema());
                    panicState(",");
                }
            } else 
                if(token.getLexema().equals("-")){
                    next();
                    if(token.getCodigo().equals("NRO")){
                        next();
                    } else {
                        setErro(token.getLine(), "Number", token.getLexema());
                        panicState(",");
                    }
                }
            else {
                next();
            }
            
            more_real_params();
        } 
    }
    
    private void more_real_params(){
        
        if(token.getLexema().equals(",")){
            next();
            if(token.getCodigo().equals("NRO") || token.getCodigo().equals("CDC") 
                || scan.isBooleans(token.getLexema()) || scan.isModifiers(token.getLexema())
                || token.getLexema().equals("-") || token.getCodigo().equals("IDE")){
                realParam();
            } else {
                setErro(token.getLine(), "Number, string, boolean, variable", token.getLexema());
                panicState(",");
            }
            more_real_params();
        }
    }
    
    private void printStatement(){
        
        if(token.getLexema().equals("(")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator: \"(", token.getLexema());
            while(!scan.isModifiers(token.getLexema()) || !token.getCodigo().equals("IDE")
                    || !token.getCodigo().equals("CDC")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        print_params();
        
        if(token.getLexema().equals(")")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator: \")", token.getLexema());
            panicState(";");
        }
        
        if(token.getLexema().equals(";")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator: \";", token.getLexema());
            while(!scan.isCommands(token.getLexema()) || !token.getCodigo().equals("IDE")
                    || !token.getLexema().equals("return") || !token.getLexema().equals("}")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
    }
    
    private void print_params(){
        
        print_param();
        more_print_param();
    }
    
    private void print_param(){
        
        if(token.getCodigo().equals("CDC")){
            next();
        } else 
            if(scan.isModifiers(token.getLexema())){
                call_variable();
        } else 
            if(token.getCodigo().equals("IDE")){
                next();
                paths();
        } else {
            setErro(token.getLine(), "Varible, string", token.getLexema());
            while(!token.getLexema().equals(")")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
    }
    
    private void more_print_param(){
        
        if(token.getLexema().equals(",")){
            next();
            print_params();
        }
    }
    
    private void readStatement(){
        
    }
    
    private void whileStatement(){
        
    }
    
    private void ifStatement(){
        
    }
    
    private void _return () {
        
    }
    
    private void call_variable (){
        
        if(scan.isModifiers(token.getLexema())){
            next();
            if(token.getLexema().equals(".")){
                next();
            } else {
                setErro(token.getLine(), "Delimitator: \".", token.getLexema());
                while(!token.getCodigo().equals("IDE")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            if(token.getCodigo().equals("IDE")){
                next();
            } else {
               setErro(token.getLine(), "Identifier", token.getLexema());
               while(!scan.isOperators(token.getLexema()) || !token.getLexema().equals(";")
                        || !token.getLexema().equals(")") || !token.getLexema().equals(".")
                        || !token.getLexema().equals("[") || !token.getLexema().equals("]")
                        || !token.getLexema().equals(")") || !scan.isUnaryOp(token.getLexema())){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            paths();
        }
    }
    
    private void paths (){
        
        if(token.getLexema().equals(".")){
            next();
            struct();
        }
        
        if(token.getLexema().equals("[")){
            next();
            matriz();
        }
    }
    
    private void struct(){
        
        if(token.getCodigo().equals("IDE")){
            next();
            if(token.getLexema().equals(".") || token.getLexema().equals("[")){
                paths();
            }
        } else {
            setErro(token.getLine(), "Identifier", token.getLexema());
            panicState("=");
        }
    }
    
    private void matriz(){
        
        if(token.getCodigo().equals("NRO") || token.getCodigo().equals("IDE")){
            next();
        } else {
            setErro(token.getLine(), "Number, indetifier", token.getLexema());
            panicState("]");
        }
        
        if(token.getLexema().equals("]")){
            next();
        } else {
            setErro(token.getLine(), "Delimitator: \"]", token.getLexema());
            panicState("=");
        }
    }
    
    private void panicState (String sinalizer) {
        while(!token.getLexema().equals(sinalizer)){
            if(!this.arq.hasNext()){
                break;
            }
            next();
        }
    }
}
