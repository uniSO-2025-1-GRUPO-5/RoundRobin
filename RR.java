import java.time.Instant;
import java.util.Scanner;

public class RR{ 
    /*
     * Regras:
     * Sem Orientação a Objetos
     */
    public static void main(String args[]){
        /*
        Limite máximo de processos criados; 
        &
        Definição da fatia de tempo dada aos processos em execução; 
        */

        int PLim = 10; // Limite de criação de processos;
        int TQ=20; // Time-Quantum; Tempo limite para cada processo; 20 milisegundos
        int PReq;
        int BTLim = 1200; //Limite de Burst Time de 1.2 segundos

        // Geração aleatória de BT com limite de BTLim
        short Init =(short)Instant.now().toEpochMilli(); //Milisegundos do tempo atual com limite de valor 32,767
        int BTRVal = (Init * 1103515245 + 12345) & Integer.MAX_VALUE; //Milisegundos x 1103515245 + 12345; & para que valor sempre seja positivo
        short BT = (short) (BTRVal % (BTLim + 1)); // Se resultado de BTRVal for maior que o limite de BTLim, % corta valor de BTRVal para que ele seja menor ou igual à BTRVal

        /* Sessão para input do usuário */
        Scanner PReqInput = new Scanner(System.in); // Abre scanner

        System.out.print(String.format("Quantos processos gostaria de criar? (Max: %d)\n#> ",PLim));
        PReq = PReqInput.nextInt(); // Pega input

        if(PReq > PLim || PReq < 1){ //Checa se PReq é maior que limite de processos ou PReq é negativo ou 0
            System.out.println(String.format("Impossivel criar %d com limite de processos %d",PReq,PLim));
            System.exit(0);
        }
        PReqInput.close(); //Fecha scanner
        
        /* ---------------------------- */
    }
}