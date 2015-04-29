package net.perkowitz.waves.apps;

import net.perkowitz.waves.Wave;

import java.io.IOException;

public class Sonic {

    public static void main(String args[]) throws IOException {

        if (args.length < 2) {
            System.out.println("Usage: waves <wavetype> <semitone offset>");
            return;
        }

        String waveform = args[0];
        Integer semitones = new Integer(args[1]);

        int cycleLength = Wave.note2Samples(semitones, Wave.DEFAULT_SAMPLE_RATE);

//        Wave wave = Wave.waveform(cycleSize,waveform);
//        Wave sync = wave.copy();
//        sync.sync(Wave.note2Samples(semitones+1,Wave.DEFAULT_SAMPLE_RATE));
//        wave.pad(Wave.DEFAULT_SAMPLE_RATE);
//        sync.pad(Wave.DEFAULT_SAMPLE_RATE);
//        wave.append(sync);
//        wave.save("out.snc");

//        Wave wave = Wave.morph(Wave.sine(cycleLength),Wave.saw(cycleLength),4*Wave.DEFAULT_SAMPLE_RATE,cycleLength);
//        //wave.pad(Wave.DEFAULT_SAMPLE_RATE);


        Wave wave = Wave.average(Wave.sine(cycleLength),Wave.saw(cycleLength),1);
        Wave sine = Wave.sine(cycleLength);
        Wave down = sine.copy();
        down.downsample(8.5);
        Wave morph = Wave.morph(sine,down,Wave.DEFAULT_SAMPLE_RATE,cycleLength);
        morph.pad(Wave.DEFAULT_SAMPLE_RATE);
        wave = morph;

        wave.save("out.snc");
    }

}
