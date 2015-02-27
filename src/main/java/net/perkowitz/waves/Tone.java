package net.perkowitz.waves;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by mikep on 2/18/15
 */
public class Tone {

    public static int DEFAULT_SAMPLE_RATE = 44100; //44100;
    public static int DEFAULT_BIT_DEPTH = 15;
    public static int A440_FREQUENCY = 440;                                 // reference note
    public static double SINGLE_STEP_FREQUENCY = Math.pow(2.0, 1.0/12.0);   // 12th root of 2 (scale factor for each semitone)

    protected List<Double> samples;

    /*** Constructor ***********************************************/

    public Tone() {
        samples = Lists.newArrayList();
    }


    /*** Utility ***********************************************/

    public String toString() {

        String output = "";
        for (int index = 0; index < samples.size(); index += 4) {
//            output += samples.get(index) + "\n";
            output += "|";
            int length = (int)(100 * (samples.get(index)+1)/2);
            for (int i=0; i<length; i++) {
                output += "-";
            }
            output += "\n";
        }

        return output;
    }


    public int getSize() {
        return samples.size();
    }

    // returns value by index
    public double get(int index) {
        if (index >= 0 && index < getSize()) {
            return samples.get(index);
        }

        return 0d;
    }


    public Tone copy() {

        Tone tone = new Tone();
        for (int index = 0; index < getSize(); index++) {
            tone.samples.add(get(index));
        }

        return tone;
    }


    private double[] toDoubleArray() {

        double max = Math.pow((long)2,(long)DEFAULT_BIT_DEPTH);

        double[] doubles = new double[samples.size()];
        for (int i=0; i<samples.size(); i++) {
            Double sample = samples.get(i);
            doubles[i] = sample;
        }

        return doubles;
    }


    public void toWavFile(String filename) {
        StdAudio.save(filename, toDoubleArray());
    }


    /*** Process single tone ***********************************************/

    public Tone scale(double scalingFactor) {
        for (int index=0; index<getSize(); index++) {
            samples.set(index, samples.get(index) * scalingFactor);
        }

        return this;
    }

    public Tone reduce() {

        double max = 0d;
        for (int index=0; index<getSize(); index++) {
            double abs = Math.abs(samples.get(index));
            if (abs > max) {
                max = abs;
            }
        }

        return scale(1 / max);
    }

    public Tone clip() {
        for (int index=0; index<getSize(); index++) {
            if (samples.get(index) > 1) {
                samples.set(index, 1d);
            } else if (samples.get(index) < -1) {
                samples.set(index, -1d);
            }
        }

        return this;
    }

    public Tone normalize() {
        // rescale entire wave (bigger or smaller) to fill dynamic range
        return this;
    }


    /*** Combine multiple tones ***********************************************/

    public Tone add(Tone tone) {
        for (int index=0; index<samples.size(); index++) {
            samples.set(index, samples.get(index) + tone.samples.get(index));
        }
        return this;
    }

    public Tone subtract(Tone tone) {
        for (int index=0; index<samples.size(); index++) {
            samples.set(index, samples.get(index) - tone.samples.get(index));
        }
        return this;
    }

    public Tone multiply(Tone tone) {
        for (int index=0; index<samples.size(); index++) {
            samples.set(index, samples.get(index) * tone.samples.get(index));
        }
        return this;
    }

    public Tone multiply2(Tone tone) {
        for (int index=0; index<samples.size(); index++) {
            double absValue = Math.abs(samples.get(index) * tone.samples.get(index));
            double mult = 1;
            if (samples.get(index) < 0) {
                mult = -1;
            }
            samples.set(index, absValue * mult  );
        }
        return this;
    }



    /*** Static utilities ***********************************************/

    public static double noteToCycleLength(int note) {
        int semitonesFromA = (note - 9) - 48;
        double noteFrequency = A440_FREQUENCY * Math.pow(SINGLE_STEP_FREQUENCY,semitonesFromA);
        return (double)DEFAULT_SAMPLE_RATE / noteFrequency;
    }


    /*** Static builders ***********************************************/

    public static Tone fromWaveform(Waveform waveform, int note, double seconds, boolean endAtWaveformCycle) {

        double cycleLength = Tone.noteToCycleLength(note);
        int size = (int)(seconds * DEFAULT_SAMPLE_RATE);
        if (endAtWaveformCycle) {
            size += cycleLength - (size % cycleLength);
        }
        System.out.printf("fromWaveform: cycle=%f, size=%d\n", cycleLength, size);

        Tone tone = new Tone();

        for (int index=0; index<size; index++) {
            double waveformIndex = index % cycleLength;
            tone.samples.add(waveform.getByPosition(waveformIndex / cycleLength));
        }

        return tone;
    }



}
