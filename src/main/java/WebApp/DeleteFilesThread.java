package WebApp;

import java.io.File;

public class DeleteFilesThread extends Thread {

    String a,b;

    DeleteFilesThread(String s1, String s2) {
        a = s1;
        b = s2;
    }

    public void run() {

            try {
                sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (a.endsWith(".jar")) {
                File file = new File(a);
                file.delete();
            } else {

                File file = new File(a);
                file.delete();
                file = new File(b);
                file.delete();

        }
    }
}