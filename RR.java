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
        int TQ=50; // Time-Quantum; Tempo limite para cada processo; 20 milisegundos
        int PReq;
        int BTLim = 1200; //Limite de Burst Time de 1.2 segundos

        /* Sessão para input do usuário */
        Scanner PReqInput = new Scanner(System.in); // Abre scanner

        System.out.print(String.format("Quantos processos gostaria de criar? (Max: %d)\n#> ",PLim));
        PReq = PReqInput.nextInt(); // Pega input

        if(PReq > PLim || PReq < 1){ //Checa se PReq é maior que limite de processos ou PReq é negativo ou 0
            System.out.println(String.format("Impossivel criar %d com limite de processos %d",PReq,PLim));
            System.exit(0);
        }
        PReqInput.close(); //Fecha scanner


        String InOut[] = {"Disco","Fita magnética","Impressora"};
        System.out.println(String.format("Tempo Limite: %d", TQ));
        int TempoInit = 0;
        int GuardaInOut = new int[PReq];
        int[] GuardaBT = new int[PReq];
        for(int i=0;i<PReq;i++){

            /*
            * Geração aleatória de BT com limite de BTLim
            * 1) Milisegundos do tempo atual com limite de valor 32,767
            * 2) Milisegundos x 1103515245 + 12345; & para que valor sempre seja positivo
            * 3) Se resultado de BTRVal for maior que o limite de BTLim, % corta valor de BTRVal para que ele seja menor ou igual à BTRVal
            * 4) pega o short BT e guarda o burst time do processo
            */ 
            short Init = (short) System.nanoTime();
            int BTRVal = (Init * 1103515245 + 12345) & Integer.MAX_VALUE; 
            short BT = (short) (BTRVal % (BTLim + 1)); 
            GuardaBT[i] = BT;
            //----------------------------------------------

            long X = System.nanoTime();
            long m = ((BTRVal * 20) * 2003515245 + 401245991) & Integer.MAX_VALUE; //-----
            long c = ((m*5) * 510351524 + 901245991) & Integer.MAX_VALUE;          //    |---Repetição de BTRVal para criação das variáveis de LCG
            long a = ((m*9) * 903515245 + 6956825) & Integer.MAX_VALUE;            //-----
             /*              
             * X -> Números pseudo-randômicos (inicializador)
             * m -> Módulo
             * a -> Multiplicador
             * c -> Incremento           
             */

            int posicaoTipos = (int) ((a * X + c) % m) % 3;
            int posTotal = (posicaoTipos<0)?-posicaoTipos:posicaoTipos; //Substituição para Math.abs()
            int MediaInOut[] = {30, 80, 120};
            GuardaInOut[i] = MediaInOut[posTotal];
            
            /* 
             * LCG - Linear congruential generator
             * X[n+1] = (a * X[n] + c) % m
             */ 
            System.out.print(String.format("Processo %d (%s): BT=%d ms; InOut=%d ms\n", i+1,titulos[posTotal],GuardaBT[i], GuardaInOut[i]));
        
        }
        
        /* ---------------------------- */
    }
}

