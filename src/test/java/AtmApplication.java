public class AtmApplication {

    public static void withdrawMoney(int amount) throws InterruptedException {
        //processing going on here
        Thread.sleep(2000L);
        System.out.println(String.format("[Application] Successful Withdrawal of [%s] units!",amount));

    }


    public static void main(String[] args) throws InterruptedException {
        withdrawMoney(20000);
        //do something else
        Thread.sleep(200);
    }

}
