package net.perkowitz.waves;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;

/** Tone
 *
 * Represents an audible tone, with sampling rate, pitch, etc
 *
 * Created by mikep on 2/18/15
 */
public class Tone {

    public static int DEFAULT_SAMPLE_RATE = 44100;
    public static int DEFAULT_BIT_DEPTH = 15;
    public static int A440_FREQUENCY = 440;                                 // reference note
    public static double SINGLE_STEP_FREQUENCY = Math.pow(2.0, 1.0/12.0);   // 12th root of 2 (scale factor for each semitone)

    private static int sampleRate = DEFAULT_SAMPLE_RATE;
    private static int bitDepth = DEFAULT_BIT_DEPTH;

    private static String fileType = "wav";

    protected List<Double> samples;
    protected String name = "tone";


    /*** Constructors ***********************************************/

    public Tone() {
        samples = Lists.newArrayList();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    public int size() {
        return samples.size();
    }

    // returns value by index
    public double get(int index) {
        if (index >= 0 && index < size()) {
            return samples.get(index);
        }

        return 0d;
    }

    private double[] toDoubleArray() {

        double max = Math.pow((long)2,(long)bitDepth);

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

    public void toWavFile(File path) {
        String outputName = path.toString();
        if (path.isDirectory()) {
            outputName = path.toString() + "/" + name + "." + fileType;
        }
        StdAudio.save(outputName, toDoubleArray());
    }



    /*** Process single tone ***********************************************/

    public Tone name(String name) {
        this.name = name;
        return this;
    }

    public Tone copy() {

        Tone tone = new Tone();
        for (int index = 0; index < size(); index++) {
            tone.samples.add(get(index));
        }

        return tone;
    }

    public Tone scale(double scalingFactor) {
        for (int index=0; index< size(); index++) {
            samples.set(index, samples.get(index) * scalingFactor);
        }

        return this;
    }

    public Tone reduce() {

        double max = 0d;
        for (int index=0; index< size(); index++) {
            double abs = Math.abs(samples.get(index));
            if (abs > max) {
                max = abs;
            }
        }

        return scale(1 / max);
    }

    public Tone clip() {
        for (int index=0; index< size(); index++) {
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

        Double max = null;
        Double min = null;
        List<Double> newSamples = Lists.newArrayList();

        for (int index = 0; index < size(); index++) {
            if (max == null || samples.get(index) > max) {
                max = samples.get(index);
            }
            if (min == null || samples.get(index) < min) {
                min = samples.get(index);
            }
        }

        Double scale = 1 / max;
        for (int index = 0; index < size(); index++) {
            newSamples.add(samples.get(index) * scale);
        }

        samples = newSamples;
        return this;
    }

    public Tone smooth(int width) {

        List<Double> newSamples = Lists.newArrayList();
        for (int index = 0; index < size()-width; index++) {
            double total = 0d;
            for (int window = 0; window < width; window++) {
                total += samples.get(index + window);
            }
            newSamples.add(total / width);
        }
        samples = newSamples;

        return this;
    }

    public Tone downsample(double factor) {

        List<Double> newSamples = Lists.newArrayList();
        for (int index = 0; index < size(); index++) {
            int refIndex = new Double(index/factor).intValue();
            refIndex = new Double(refIndex*factor).intValue();
            newSamples.add(samples.get(refIndex));

        }
        samples = newSamples;

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

    public static void setSampleRate(int sampleRate) {
        Tone.sampleRate = sampleRate;
    }

    public static void setBitDepth(int bitDepth) {
        Tone.bitDepth = bitDepth;
    }

    public static double noteToCycleLength(int note) {
        int semitonesFromA = (note - 9) - 48;
        double noteFrequency = A440_FREQUENCY * Math.pow(SINGLE_STEP_FREQUENCY,semitonesFromA);
        return (double)sampleRate / noteFrequency;
    }

    public static int computeToneLength(int note, double seconds, boolean endAtWaveformCycle) {
        double cycleLength = Tone.noteToCycleLength(note);
        int length = (int)(seconds * sampleRate);
        if (endAtWaveformCycle) {
            length += cycleLength - (length % cycleLength);
        }
        return length;
    }

    /*** Static builders ***********************************************/

    public static Tone fromWaveform(Waveform waveform, int note, double seconds, boolean endAtWaveformCycle) {

        double cycleLength = Tone.noteToCycleLength(note);
        int length = computeToneLength(note, seconds, endAtWaveformCycle);

        Tone tone = new Tone();
        tone.name = waveform.getName() + " " + note;
        for (int index=0; index<length; index++) {
            double waveformIndex = index % cycleLength;
            tone.samples.add(waveform.getByPosition(waveformIndex / cycleLength));
        }

        return tone;
    }

    // creates a tone where one audio sample corresponds to one waveform sample
    public static Tone fromWaveformSamples(Waveform waveform) {

        Tone tone = new Tone();
        for (int index=0; index<waveform.size(); index++) {
            tone.samples.add(waveform.get(index));
        }

        return tone;
    }

    public static Tone morph(List<Waveform> waveforms, int note, double seconds, boolean endAtWaveformCycle) {

        double cycleLength = Tone.noteToCycleLength(note);
        int length = computeToneLength(note, seconds, endAtWaveformCycle);

        Tone tone = new Tone();
        for (int waveIndex = 0; waveIndex < waveforms.size()-1; waveIndex++) {
            Waveform currentWave = waveforms.get(waveIndex);
            Waveform nextWave = waveforms.get(waveIndex + 1);

            for (int index=0; index<length; index++) {
                double weight = 1 - ((double)index / length);
                double waveformIndex = index % cycleLength;
                double value = currentWave.getByPosition(waveformIndex / cycleLength) * weight +
                        nextWave.getByPosition(waveformIndex / cycleLength) * (1-weight);
                tone.samples.add(value);
            }

        }

        return tone;
    }

    public static Tone chord(List<Waveform> waveforms, int root, List<Integer> intervals, double seconds) {

        Tone chord = null;

        for (Integer interval : intervals) {
            int note = root + interval;
            Tone tone = Tone.morph(waveforms, note, seconds, false);
            if (chord == null) {
                chord = tone.copy();
            } else {
                chord.add(tone);
            }
        }
        if (chord != null) {
            chord.normalize();
        }

        return chord;
    }

}
