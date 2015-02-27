package net.perkowitz.waves;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;


public class WaveTone {

    public static void main(String args[]) throws IOException {

        int harmonics = 100;
        double[] harmonicWeights = new double[harmonics];
        double weight = 1;
        for (int i=0; i<harmonics; i++) {
            harmonicWeights[i] = weight;
            weight = weight * 0.7;
        }

//        int granularity = 10;
//        List<Waveform> waveforms = Lists.newArrayList();
//        waveforms.add(Waveform.square());
//        waveforms.add(Waveform.random(100l, granularity));
//        waveforms.add(Waveform.random(101l, granularity));
//        waveforms.add(Waveform.random(102l, granularity));
//        waveforms.add(Waveform.random(100l, granularity));
//        Tone.morph(waveforms, 24, 2, true).toWavFile("whatever24.wav");
//        Tone.morph(waveforms, 36, 2, true).toWavFile("whatever36.wav");
//        Tone.morph(waveforms, 48, 2, true).toWavFile("whatever48.wav");

        Tone.morph(Lists.newArrayList(Waveform.square(), Waveform.sine().scaleFrequency(1.1)), 36, 4, true).toWavFile("w36.wav");

    }



}
