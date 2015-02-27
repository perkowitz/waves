package net.perkowitz.waves;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by mikep on 2/18/15
 */
public class Waveform extends Tone {

    private static int defaultSize = 1000; //1000;
    private boolean useInterpolation = true;


    /*** Basic methods ***********************************************/

    public Waveform() {
        samples = new ArrayList<Double>(Collections.nCopies(defaultSize,0d));
    }

    public Waveform(int size) {
        samples = new ArrayList<Double>(Collections.nCopies(size,0d));
    }


    /*** Basic methods ***********************************************/

    // returns value by index with interpolation
    public double get(double index) {

        if (index >= 0 && index < getSize()) {
            int leftIndex = (int)Math.floor(index);
            int rightIndex = (int)Math.ceil(index);

            double leftValue = get(leftIndex);

            if (!useInterpolation) {
                return leftValue;
            } else {
                double rightValue = get(rightIndex);
                double weight = index - leftIndex;
                return leftValue*(1-weight) + rightValue*weight;
            }
        }

        return 0d;
    }

    // returns value by position within waveform (0=start, 1=end)
    public double getByPosition(double position) {
        return get(getSize() * position);
    }


    public Waveform copy() {

        Waveform waveform = new Waveform();
        for (int index = 0; index < getSize(); index++) {
            waveform.samples.set(index,get(index));
        }

        return waveform;
    }


    /*** Process single waveform ***********************************************/

    public Waveform scale(double scalingFactor) {
        return (Waveform)super.scale(scalingFactor);
    }
    public Waveform reduce() {
        return (Waveform)super.reduce();
    }
    public Waveform clip() {
        return (Waveform)super.clip();
    }
    public Waveform normalize() {
        return (Waveform)super.normalize();
    }

    public Waveform phase(int degrees) {
        degrees = (degrees+360) % 360;
        return phase((double)degrees / 360.0);
    }

    public Waveform phase(double position) {

        List<Double> newSamples = Lists.newArrayList();
        int offset = (int)(getSize()*position);
        for (int index = 0; index < getSize(); index++) {
            newSamples.add(get((index + offset) % getSize()));
        }
        samples = newSamples;

        return this;
    }

    public Waveform scaleFrequency(double frequencyMultiplier) {

        List<Double> newSamples = Lists.newArrayList();
        for (int index = 0; index < getSize(); index++) {
            newSamples.add(get((index*frequencyMultiplier) % getSize()));
        }
        samples = newSamples;

        return this;
    }

    public Waveform addHarmonics(double[] weights) {

        for (int harmonic = 1; harmonic < weights.length; harmonic++) {
            Waveform harmonicWaveform = this.copy().scaleFrequency(harmonic);
            harmonicWaveform.scale(weights[harmonic]);
            this.add(harmonicWaveform);
        }

        return this.reduce();
    }

    /*** Combine multiple tones ***********************************************/

    public Waveform add(Waveform waveform) {
        return (Waveform)super.add(waveform);
    }
    public Waveform subtract(Waveform waveform) {
        return (Waveform)super.subtract(waveform);
    }
    public Waveform multiply(Waveform waveform) {
        return (Waveform)super.multiply(waveform);
    }
    public Waveform multiply2(Waveform waveform) {
        return (Waveform)super.multiply2(waveform);
    }

    public Waveform frequencyAdd(Waveform waveform, double frequencyMultiplier) {

        Waveform waveformCopy = waveform.copy();
        waveformCopy.scaleFrequency(frequencyMultiplier);
        add(waveformCopy);

        return this;
    }


    /*** Static utilities ***********************************************/

    public static void setDefaultSize(int defaultSize) {
        Waveform.defaultSize = defaultSize;
    }

    /*** Static builders ***********************************************/

    public static Waveform saw() {

        Waveform waveform = new Waveform();

        double increment = 2 / (double)waveform.getSize();
        for (int index=0; index<waveform.getSize(); index++) {
            waveform.samples.set(index, index*increment - 1);
        }

        return waveform;
    }

    public static Waveform square() {
        return Waveform.pulse(0.5);
    }

    public static Waveform pulse(double pulseWidth) {

        Waveform waveform = new Waveform();

        int split = (int)Math.round(waveform.getSize() * pulseWidth);

        for (int index=0; index<split; index++) {
            waveform.samples.set(index, 1d);
        }
        for (int index=split; index<waveform.getSize(); index++) {
            waveform.samples.set(index, -1d);
        }

        return waveform;
    }

    public static Waveform sine() {

        Waveform waveform = new Waveform();

        for (int index=0; index<waveform.getSize(); index++) {
            double angleInRadians = (double)index/waveform.getSize() * 2*Math.PI;
            waveform.samples.set(index, Math.sin(angleInRadians));
        }

        return waveform;
    }

    public static Waveform random(Long seed) {

        Random random;
        if (seed == null) {
            random = new Random();
        } else {
            random = new Random(seed);
        }

        Waveform waveform = new Waveform();

        for (int index=0; index<waveform.getSize(); index++) {
            waveform.samples.set(index, random.nextDouble()*2 - 1);
        }

        return waveform;
    }


}
