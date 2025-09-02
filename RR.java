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
        int PReq;
        int BTLim = 2000; //Limite de Burst Time de 2 segundos
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
       
        int[] GuardaInOut = new int[PReq];
        int[] GuardaBT = new int[PReq];
        
        /*
         * (Sem Parente:0;Filho:1),PID,PPID 
         * [0,120,-1] Se sem parente            -> PPID = -1
         * [1,203,20] Se parente    -> PID,PPID
         */
        int[][] GuardaRelacaoPPID = new int[PReq][];
        int[] GuardaPID = new int[PReq];
        String[] GuardaProcesso = new String[PReq];

        System.out.printf("%-10s %-15s %-15s %-15s %-15s %-15s %-15s %-15s%n", "PID", "Processo X", "Tipo","BT","I/O","Parentes","Estado","Prioridade");


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
             * 1) Guarda tipo de processo (Com parente ou sem)
             * 2) Guarda PID atual
             * 3) Guarda relação PPID
             * 4) Guarda este array em uma matriz
             */
            RelacaoPPID[0] = posPPID;
            RelacaoPPID[1] = PID;
            RelacaoPPID[2] = (RelacaoPPID[0] == 0)?-1:GuardaPID[posPegaPID];
            
            GuardaRelacaoPPID[i] = RelacaoPPID;

            /*
             * Guarda: PID, Número do processo, Tipo de mídia, Burst Time, Velocidade de mídia, Prioridade (0,1)
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
             * Guarda: PID, Número do processo, Tipo de mídia, Burst Time, Velocidade de mídia, Tipo de parentesco, PPID, Estado inicial 
             */
            GuardaProcesso[i] = String.format("%s:%s:%s:0",GuardaProcesso[i],GuardaRelacaoPPID[i][0],GuardaRelacaoPPID[i][2]);
        }
        
        /* 
        * Esta parte específica explica a definição de prioridade com base no BT do processo.
        * Comentada pois o enúnciado pedia "Tipos de I/O: Disco → fila baixa; Fita magnética → fila alta; Impressora → fila alta;"
        * Mantendo comentada pois eu achei interessante e foi legal de fazer
        *
        * Calculando média de BT para fornecer 3 prioridades
        * ETAPA 1 -> Organiza array de menor para maior
        * Método: Bubble sort
        * 1) Define array OrganizadoBT
        * 2) Copia GuardaBT para OrganizadoBT
        * 3) Abre while loop
        * 4) Abre for loop
        * 5) Se conteúdo de OrganizadoBT maior que o próximo conteúdo de OrganizadoBT -> Temp será igual index atual; Index atual irá ser substituído pelo valor do próximo index; Próximo index será igual Temp
        * 6) Troca ocorreu
        * 7) Inicia while loop
        
           
        int[] OrganizadoBT = new int[GuardaBT.length];
        System.arraycopy(GuardaBT, 0, OrganizadoBT, 0, GuardaBT.length);
        
        boolean troca;
        do {
            troca = false;
            for (int i = 0; i < OrganizadoBT.length - 1; i++) {
                if (OrganizadoBT[i] > OrganizadoBT[i+1]) {
                    
                    int temp = OrganizadoBT[i];
                    OrganizadoBT[i] = OrganizadoBT[i+1];
                    OrganizadoBT[i+1] = temp;
                    troca = true;
                }
            }
        } while (troca);

        /*
         * ETAPA 2 -> Calcula média de todos os valores dentro de OrganizadoBT
         * 1) Define soma
         * 2) Define conjunto
         * Me = (X0+X1+X2...+Xn)/Conjunto
         * 3) Abre loop e soma todos os valores de OrganizadoBT em "Soma"
         * 4) Calcula média na variável "Média"
         * 
         * 5) Abre loop para definir prioridade
         * 6) Importa processos guardados
         * 7) Importa Burst time de Processos
         * 8) Define meio da média ao dividir Média por 2
         * 9) Define valor padrão para prioridade
         * 10)Se burst for menor que Media: Prioridade 1
         * 11)Se burst for maior que Média: Prioridade 3
         * 12)Se burst estiver no meio da Média (MediaMetade): Prioridade 2
         * 13)Guarda prioridade no GuardaProcesso
         
        int Soma=0;
        int Conjunto = OrganizadoBT.length;
        for(int i=0;i<Conjunto;i++){
            Soma+=OrganizadoBT[i];
        }
        int Media = Soma/Conjunto;

        for(int i=0;i<GuardaRelacaoPPID.length;i++){
            String[] Processos = GuardaProcesso[i].split(":");
            int BRST = Integer.parseInt(Processos[3]);
            int MediaMetade = Media/2;
            int prioridade=3;
            if(BRST < Media){
                prioridade=1;
            }
            if(BRST >= Media){
                prioridade=3;
            }
            if(BRST <= MediaMetade && !(BRST < Media)){
                prioridade=2;
            }
            if(BRST >= MediaMetade && !(BRST > Media)){
                prioridade=2;
            }
            GuardaProcesso[i]=String.format("%s:%s",GuardaProcesso[i],prioridade);
        }
        */
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
            String PRIO = "Baixa";
            //{"Disco","Fita magnética","Impressora"};
            //Tipos de I/O: Disco → fila baixa; Fita magnética → fila alta; Impressora → fila alta;
            switch(Integer.parseInt(TIPO)){
                case 0:
                    PRIO="Baixa";
                    break;
                case 1:
                case 2:
                    PRIO="Alta";
                    break;                    
            }
            // 1->Prioridade Alta; 0->Prioridade Baixa : Varreduras
            GuardaProcesso[i]=String.format("%s:%s:%s",GuardaProcesso[i],(Integer.parseInt(TIPO) > 0)?1:0,"0");

            System.out.printf("%-10s %-15s %-15s %-15s %-15s %-15s %-15s %-15s%n", PID, String.format("Processo %s",PROC), InOut[Integer.parseInt(TIPO)],String.format("%s ms",BRST),String.format("%s ms",IO),PPID,STAT,PRIO);
        }
        
        /*
         * Escalonador com pelo menos 3 filas: alta prioridade, baixa prioridade e fila(s) de I/O;
         * For 1) Define tamanho das filas
         * For 2) Define valor das filas (PIDs)
         */
        String[] P1,P2;
        int[] FilaIO = new int[GuardaProcesso.length];
        int FilaIOIDX=0;
        int P1Len=0,P2Len=0;
        for(int i=0;i<GuardaProcesso.length;i++){
            int prioridades = Integer.parseInt(GuardaProcesso[i].split(":")[8]);
            switch(prioridades){
                case 0:
                    P2Len+=1;
                    break;
                case 1:
                    P1Len+=1;
                    break;
            }
        }

        P1 = new String[P1Len];
        P2 = new String[P2Len];
        
        int p1Index = 0;
        int p2Index = 0;
        
        for (int i = 0; i < GuardaProcesso.length; i++) {
            int prioridades = Integer.parseInt(GuardaProcesso[i].split(":")[8]);
            String Processo = GuardaProcesso[i];
            switch (prioridades) {
                case 0:
                    P2[p2Index++] = Processo; 
                    break;
                case 1:
                    P1[p1Index++] = Processo; 
                    break;
            }
        }

        System.out.print("#".repeat(49));
        System.out.print(" LOG DE PROCESSO ");
        System.out.print("#".repeat(52));

        String SalvaProcesso=""; // BTRestate1:BTRestate2:BTRestate3:....
        System.out.printf("%n%-10s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s%n", "PID", "Processo X", "Tipo","BT","I/O","Parentes","Estado","Prioridade","Execução","BT Restante","Varreduras");



        /* 
        >>>>>NESTA FASE O ARRAY QUE GUARDA OS PROCESSOS TEM 9 INDEXES <<<<<
        *
        * Ordem de entrada na fila de prontos: novos processos → fila alta; processos de I/O → depende do tipo; preempção → fila baixa
        * 
        * Processamento de processos com I/O e controle de preempção
        * 1) Abre for loop
        * 2) Extrai e converte valores de Burst Time, I/O Time e contador de varreduras
        * 3) Define status do processo como "Em execução" (valor 1)
        * 4) Simula execução do processo, decrementando BT para cada unidade de I/O time
        * 5) Controla contador de preempção durante a execução
        * 6) Atualiza BT restante se processo não completou execução
        * 7) Finaliza processo (status 2) quando BT chega a zero e adiciona à fila de I/O para output
        * 8) Incrementa contador de varreduras do processo
        * 9) Gera saída formatada com status atualizado e informações do processo
        *
        * O mesmo deve ser feito para processos de prioridade baixa e FilaIO
        * 
        * Após lista de alta prioridade e baixa prioridade forem executadas e BT restante de algum processo for maior que 0, deixe-o esperando
        * Printe quais processos foram terminados (eles estarão listados na FilaIO) 
        * Depois de printar, repita o processo
        *
        * 1 > Prioridade Alta <-----------------|
        * 2 > Prioridade Baixa                  |
        * 3 > FilaIO                            |
        * 4 > (Se BT de fila alta ou baixa > 0) |
        */

        for(int i=0;i<P1.length;i++){
            String[] ProcessosP1 = P1[i].split(":");
            int BT = Integer.parseInt(ProcessosP1[3]);
            int BTVelho = BT;
            int IOTempo = Integer.parseInt(ProcessosP1[4]);
            int Varreduras = Integer.parseInt(ProcessosP1[9]);
            ProcessosP1[6] = "1"; //Em execução
            int Preempt = 0;
            for(int j=0; j<IOTempo; j++){
                Preempt++;
                BT--;
                
                if(j == IOTempo - 1 && BT > 0){
                    ProcessosP1[3] = String.format("%d", BT);
                    SalvaProcesso = String.format("%s:%s", SalvaProcesso, BT);
                }
                if(BT == 0){
                    ProcessosP1[6] = "2";
                    FilaIO[FilaIOIDX++] = Integer.parseInt(ProcessosP1[0]);
                    break;
                }
            }
            ProcessosP1[9] = String.format("%d",Varreduras+1);
            String STATUS="";
            switch(Integer.parseInt(ProcessosP1[6])){
                case 1:
                    STATUS="Em execução";
                    break;
                case 2: 
                    STATUS="Terminou";
                    break;
            }
            System.out.printf("%-10s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s%n", ProcessosP1[0], String.format("Processo %s",ProcessosP1[1]), InOut[Integer.parseInt(ProcessosP1[2])],String.format("%s ms",BTVelho),String.format("%s ms",ProcessosP1[4]),ProcessosP1[5],STATUS,(Integer.parseInt(ProcessosP1[8]) > 0)?"Alta":"Baixa",String.format("%s ms",Preempt),BT,ProcessosP1[9]);
        }

    }
}