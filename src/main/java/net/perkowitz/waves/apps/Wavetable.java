package net.perkowitz.waves.apps;

import com.google.common.collect.Lists;
import net.perkowitz.waves.Waveform;
import net.perkowitz.waves.Wavetable;

import java.io.IOException;


public class Wavetable {

    public static void main(String args[]) throws IOException {

        net.perkowitz.waves.Wavetable wavetable = new net.perkowitz.waves.Wavetable("WT01", 61, 64);
        wavetable.set(0, Waveform.sine());
        wavetable.set(30, Waveform.pulse(0.2));
        wavetable.set(60, Waveform.square());
        wavetable.renderAsDir(".");



    }



}
