import java.util.Scanner;

public class RR{ 
    /*
     * Regras:
     * Sem Orientação a Objetos
     */
    public static void main(String args[]){
        /*
        * Limite máximo de processos criados; 
        * Definição da fatia de tempo dada aos processos em execução; 
        */
        int PLim = 10; // Limite de criação de processos;
        int TQ=50; // Time-Quantum; Tempo limite para cada processo; 20 milisegundos
        int PReq;
        int BTLim = 1200; //Limite de Burst Time de 1.2 segundos
        /*-----------------------------------------------------------------*/
        
        /*GERAÇÃO DE PID*/
        short PIDInit = (short) System.nanoTime();
        int PIDRand = (PIDInit * 1103515245 + 12345) & Integer.MAX_VALUE; 
        short PIDGen = (short) (PIDRand % (BTLim + 1)*2); 
        /*-----------------------------------------------------------------*/

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
        int[] GuardaInOut = new int[PReq];
        int[] GuardaBT = new int[PReq];
        
        /*
         * (Sem Parente:0;Parente:1;Filho:2),PID,PPID 
         * [0,120,-1] Se sem parente            -> PPID = -1
         * [1,203,20] Se parente    -> PID,PPID
         */
        int[][] GuardaRelacaoPPID = new int[PReq][];
        int[] GuardaPID = new int[PReq];
        String[] GuardaProcesso = new String[PReq];

        System.out.printf("%-10s %-15s %-15s %-15s %-15s %-15s %-15s%n", "PID", "Processo X", "Tipo","BT","I/O","FILHOS","ESTADO");


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

            /* 
             * LCG - Linear congruential generator
             * X[n+1] = (a * X[n] + c) % m
            */ 
            int LCG = (int) ((a * X + c) % m);
            int posicaoTipos = LCG % 3;
            int MarcaPPID = LCG % 2;
            int PegaPID = LCG % GuardaPID.length; //Pega PIDs atuais guardados em GuardaPID

            int PID = PIDGen+i;
            
            /* Conversão para positivos */
            int posTotal    = (posicaoTipos<0)?-posicaoTipos:posicaoTipos; //Substituição para Math.abs()
            int posPPID     = (MarcaPPID<0)?-MarcaPPID:MarcaPPID;
            int posPegaPID  = (PegaPID<0)?-PegaPID:PegaPID;

            /* 
             * 1) Define 'Tentativas' como 0
             * 2) Loop verificando se PID é igual PID dentro do loop "for"
             * 3) Incrementa 1 em 'Tentativas'
             * 4) 'Regenera' faz o processo de busca de PIDs
             * 5) 'Positiva' torna o valor de 'Regenera' para um valor positivo
             * 6) Avalia se PID escolhido randomicamente é diferente ao PID atual do "for" loop
             * 7) Guarda PID escolhido em PegaPID se o PID for diferente
             * 8) Mata While loop
             * 9) Se PID continuar sendo igual ao PID atual, continua o loop por 4 iterações
             * 10) Após 4 iterações, define PegaPID = -1 e posPPID = 0 e mata while loop; (Define PID atual como processo próprio).
             */
            int[] RelacaoPPID = new int[3];

            int Tentativas = 0;
            while(GuardaPID[posPegaPID] == PID){
                Tentativas++;
                int Regenera = LCG % GuardaPID.length;
                int Positiva = (Regenera<0)?-Regenera:Regenera;
                if(GuardaPID[Positiva] != PID){
                    PegaPID = Positiva;
                    break;
                }
                if(Tentativas == 4){
                    PegaPID = -1;
                    posPPID = 0;
                    break;
                }
            }
            //-----------------------------------------------------
            
            /* Tipo de mídia sendo usada e suas velocidades */
            int[] MediaInOut = {30, 80, 120};
            GuardaInOut[i] = MediaInOut[posTotal];
            
            
            GuardaPID[i] = PID; //Guarda o PID atual

            /*
             * 1) Guarda tipo de processo (Com filho ou sem)
             * 2) Guarda PID atual
             * 3) Guarda relação PPID
             * 4) Guarda este array em uma matriz
             */
            RelacaoPPID[0] = posPPID;
            RelacaoPPID[1] = PID;
            RelacaoPPID[2] = (RelacaoPPID[0] == 0)?-1:GuardaPID[posPegaPID];
            
            GuardaRelacaoPPID[i] = RelacaoPPID;

            /*
             * Guarda PID, Número do processo, Tipo de mídia, Burst Time, Velocidade de mídia
             */
            GuardaProcesso[i] = String.format("%d:%d:%d:%d:%d", PID,i+1,posTotal,GuardaBT[i],GuardaInOut[i]);   
        
        }

        /*
         * Relacionamento de PID e PPIDs
         * For aborda: linkagem de PID com PPID e Estado de processo inicial (parado) 
         */

        for(int i=0;i<GuardaRelacaoPPID.length;i++){
            int TipoPPID = GuardaRelacaoPPID[i][0];
            int PPID     = GuardaRelacaoPPID[i][2];
            
            
            /* Tipo,PPID
             * [1,0] -> [0,-1]
             */
            if(TipoPPID == 1 && PPID == 0){
                GuardaRelacaoPPID[i][0] = 0;
                GuardaRelacaoPPID[i][2] = -1;
            }
            
            /*
             * 1) Se Tipo = 1 e PPID não é -1, verifica se PID existe
             * 2) Abre for loop
             * 3) Se PPID interno no for igual ao PPID externo e index J não for igual index externo I -> PPID realmente existe
             * 4) Se não existe, torna Tipo e PPID são anulados
             */
            if(TipoPPID == 1 && PPID != -1){
                boolean ppidExiste = false;
                for(int j=0;j<GuardaRelacaoPPID.length;j++){
                    if(GuardaRelacaoPPID[j][1] == PPID && j != i){
                        ppidExiste = true;
                        break;
                    }
                }
                if(!ppidExiste){
                    GuardaRelacaoPPID[i][0] = 0;
                    GuardaRelacaoPPID[i][2] = -1;
                }
            }
             /*
             * Guarda PID, Número do processo, Tipo de mídia, Burst Time, Velocidade de mídia, Tipo de parentesco, PPID, Estado inicial 
             */
            GuardaProcesso[i] = String.format("%s:%s:%s:0",GuardaProcesso[i],GuardaRelacaoPPID[i][0],GuardaRelacaoPPID[i][2]);
        }

        /*
         * Printa na tela informação inicial dos processos (por enquanto)
         */
        for(int i=0;i<GuardaRelacaoPPID.length;i++){  
            String[] Processos = GuardaProcesso[i].split(":");
            String PID  = Processos[0];
            String PROC = Processos[1];
            String TIPO = Processos[2];
            String BRST = Processos[3];
            String IO   = Processos[4];
            String PPID = Processos[6];
            String STAT = Processos[7];
            if(Integer.parseInt(PPID) == -1){
                PPID = "-";
            }
            if(Integer.parseInt(STAT) == 0){
                STAT = "Parado";
            }
            System.out.printf("%-10s %-15s %-15s %-15s %-15s %-15s %-15s%n", PID, String.format("Processo %s",PROC), InOut[Integer.parseInt(TIPO)],String.format("%s ms",BRST),String.format("%s ms",IO),PPID,STAT);
        }
        /* ---------------------------- */
    }
}