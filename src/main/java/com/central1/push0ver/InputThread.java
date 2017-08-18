package com.central1.push0ver;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class InputThread extends Thread {
    private final InputStream in;
    private StringBuilder buf = new StringBuilder(1000);

    InputThread(InputStream in) {
        this.in = in;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                buf.append(line).append(System.lineSeparator());
            }
        } catch (IOException ioe) {

            buf.append("Java Exception: ").append(ioe).append(System.lineSeparator());


        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    // nobody cares
                }
            }
        }
    }

    public String getOutput() {
        return this.buf.toString().trim();
    }
}