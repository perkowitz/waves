package net.perkowitz.waves.apps;

import net.perkowitz.waves.Wave;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;


public class Cycles {

    public static void main(String args[]) throws IOException {

        double singleStep = Math.pow(2.0, 1.0/12.0);

        for (int note=0; note <=60; note++) {

            int semitonesFromA = note - 9 - 48;
            double pow = Math.pow(singleStep, semitonesFromA);
            double freq = Wave.A440_FREQUENCY * Math.pow(singleStep, semitonesFromA);

            System.out.printf("Note: %d, Freq: %f, Samples: %f, Len: %d\n", note, freq, Wave.DEFAULT_SAMPLE_RATE / freq, Wave.noteToCycleLength(note));

//            System.out.printf("Note: %d, Cycles: %d\n", note, Wave.noteToCycleLength(note));
        }

    }



}
