package net.perkowitz.waves;

import com.google.common.collect.Lists;

import java.io.IOException;


public class AppWavetable {

    public static void main(String args[]) throws IOException {

        Wavetable wavetable = new Wavetable("WT01", 61, 64);
        wavetable.set(0, Waveform.sine());
        wavetable.set(30, Waveform.pulse(0.2));
        wavetable.set(60, Waveform.square());
        wavetable.renderAsDir(".");



    }



}
