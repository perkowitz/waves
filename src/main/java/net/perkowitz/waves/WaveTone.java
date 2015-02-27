package net.perkowitz.waves;

import java.io.IOException;


public class WaveTone {

    public static void main(String args[]) throws IOException {

        Waveform.setDefaultSize(1000);
        double[] harmonicWeights = { 1, .5, .25, .125, .0625, 0.03125, 0.015625 };
        Waveform waveform1 = Waveform.sine().addHarmonics(harmonicWeights);
        Waveform waveform2 = Waveform.sine();


        Tone tone1 = Tone.fromWaveform(waveform1, 36, 4, true);
//        Tone tone2 = Tone.fromWaveform(waveform2, 36, 2, true);
//        tone1.add(tone2);
//        tone1.reduce();
//        System.out.println(tone1);

        tone1.toWavFile("whatever.wav");


    }



}
