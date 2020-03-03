package br.uefs.ecomp.analisadorSintatico.view;

import br.uefs.ecomp.AnalisadorLexico.model.Token;
import br.uefs.ecomp.analisadorSintatico.model.Error;
import br.uefs.ecomp.AnalisadorLexico.view.AnalisadorLexico;
import br.uefs.ecomp.analisadorSintatico.controller.AnalisadorSintaticoController;
import br.uefs.ecomp.AnalisadorLexico.model.TokensList;
import br.uefs.ecomp.analisadorSintatico.model.AnaliseLexica;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Alessandro Costa
 */
public class AnalisadorSintático {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        AnaliseLexica al = new AnaliseLexica();        
        AnalisadorSintaticoController controller = new AnalisadorSintaticoController();
        Iterator<Token> it; 
        Iterator<Error> e;
        String output = "";
        int count = 1;
        
        for (List<Token> arq: al.getArqs()){
            it = arq.iterator();
            controller.analiseArq(it);
            e = controller.iteratorErrors();

            if(!e.hasNext()){
                output = "Nenhum erro sintático encontrado no arquivo de entrada" + count;
                System.out.print(output + "\n");
            } else {
                //System.out.print(e.hasNext());
                while(e.hasNext()){
                    output += e.next().toString() + "\n";
                }
            }
            
            write_output(output, count);
            output = "";
            count++;
        }
        
       /* al.getArqs().stream().forEach(i -> {
            System.out.print( "\n");
            
            
            while(it.hasNext()){
                
                // System.out.print(it.next().toString() + "\n");
            }
            count ++;
        });*/
    }
    
    public static void write_output(String erros, int count) throws IOException{
        
        Path path = Paths.get("output\\saida" + count + ".txt");
        
        byte[] strToBytes = erros.getBytes();
        
        Files.write(path, strToBytes);
    }
    
    public static Iterator<Token> readFile (int count, int size, Iterator<String> iterator) {
        TokensList tk;
        Iterator<Token> it;
        Token token;
        
        tk = new TokensList(size);

        while (iterator.hasNext()){
            String s = iterator.next();
            String[] t = s.substring(1, s.length() - 1).split(" ");
            t[0] = t[0].replace(":", "");
            t[1] = t[1].replace(",", "");
            token = new Token(t[2], Integer.parseInt(t[0]));
            token.setCodigo(t[1]);
            tk.addToken(token);
        }

        it = tk.pegarTodos().iterator();
        return it;
    }
    
}
