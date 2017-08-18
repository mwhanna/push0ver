package com.central1.push0ver;

import java.io.File;
import java.io.IOException;

/**
 * Created by delliot on 2017-05-30.
 */
public class Command {
    private final ProcessBuilder pb;
    private InputThread it;
    private String stdout;
    private int exitCode = Integer.MAX_VALUE;


    public Command(String args) {
        this.pb = new ProcessBuilder( args.split(" ") );
        this.stdout = new String();
    }


    public Command execute() throws IOException {
        pb.directory(new File("."));
        pb.redirectErrorStream( true );
        Process p = pb.start();

        it = new InputThread( p.getInputStream() );
        it.start();

        try {
            exitCode = p.waitFor();
            stdout = it.getOutput();
            p.destroy();

        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
        return this;
    }

    public Command execute(File dir) throws IOException {
        pb.directory( dir );
        pb.redirectErrorStream( true );
        Process p = pb.start();

        it = new InputThread( p.getInputStream() );
        it.start();

        try {
            exitCode = p.waitFor();
            stdout = it.getOutput();

        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
        return this;
    }

    public String getStdout() {
        return stdout != null  ? stdout : null;
    }

    public int getExitCode() {
        return exitCode;
    }

}
