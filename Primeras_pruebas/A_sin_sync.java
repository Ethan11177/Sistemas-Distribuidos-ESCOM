public class A_sin_sync extends Thread{

    static long n;

    public void run() {
        for (int i = 0; i < 100000; i++) { 
            n++;
        }
    }

    public static void main(String[] args) throws Exception{
        try {
            A_sin_sync t1 = new A_sin_sync();
            A_sin_sync t2 = new A_sin_sync();

            t1.start();
            t2.start();
            
            t1.join();
            t2.join();

            System.out.println(n);
            
        } catch (Exception e) {
            //TODO: handle exception
        }
        
    }
}
