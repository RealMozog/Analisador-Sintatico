package br.uefs.ecomp.analisadorSintatico.view;

import br.uefs.ecomp.AnalisadorLexico.model.Token;
import br.uefs.ecomp.analisadorSintatico.model.Error;
import br.uefs.ecomp.AnalisadorLexico.view.AnalisadorLexico;
import br.uefs.ecomp.analisadorSintatico.controller.AnalisadorSintaticoController;
import br.uefs.ecomp.AnalisadorLexico.model.TokensList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        AnalisadorLexico al = new AnalisadorLexico();
        AnalisadorSintaticoController controller = new AnalisadorSintaticoController();
        int count = 1;
        
        
        try {
            String path = "input\\entrada" + count + ".txt";
            List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            Iterator<String> i;
            Iterator<Error> e;
            
            while(!lines.isEmpty()) {
                i = lines.iterator();
                controller.analiseArq(readFile(count, lines.size(), i));

                e = controller.iteratorErrors();
                while(e.hasNext()){
                    System.out.print(e.next().toString());
                }
                
                count++;
                path = "input\\entrada" + count + ".txt";
                lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            }
            
          
        } catch (IOException e) {
             System.err.printf("Erro na abertura do arquivo: %s.\n",e.getMessage());
        }
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
