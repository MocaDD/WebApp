package hello;

import java.io.File;

public class DeleteFilesThread extends Thread {

    String a,b;
    int n;


    DeleteFilesThread(String s1, String s2, int number) {
        a = s1;
        b = s2;
        n = number;
    }

    public void run() {

        if (n == 2) {

            try {
                sleep(80000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            File file = new File(a);
            file.delete();
            file = new File(b);
            file.delete();
        }

        if (n == 1) {
            try {
                sleep(80000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            File file = new File(a);
            file.delete();
        }
    }
}