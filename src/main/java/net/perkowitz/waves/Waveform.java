package net.perkowitz.waves;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Waveform
 *
 * Represents a single-cycle waveform. No pitch info.
 *
 * Created by mikep on 2/18/15
 */
public class Waveform extends Tone {

    private static int defaultSize = 1000; //1000;
    private boolean useInterpolation = true;
    protected String name = "waveform";


    /*** constructor ***********************************************/

    public Waveform() {
        samples = new ArrayList<Double>(Collections.nCopies(defaultSize,0d));
    }

    public Waveform(int size) {
        samples = new ArrayList<Double>(Collections.nCopies(size,0d));
    }


    /*** Basic methods ***********************************************/

    // returns value by index with interpolation
    public double get(double index) {

        if (index >= 0 && index < size()) {
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
        return get(size() * position);
    }


    public Waveform copy() {

        Waveform waveform = new Waveform();
        for (int index = 0; index < size(); index++) {
            waveform.samples.set(index,get(index));
        }

        return waveform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*** Process single waveform ***********************************************/

    public Waveform name(String name) {
        this.name = name;
        return this;
    }

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
    public Waveform smooth(int width) {
        return (Waveform)super.smooth(width);
    }
    public Waveform downsample(double factor) {
        return (Waveform)super.downsample(factor);
    }

    public Waveform phase(int degrees) {
        degrees = (degrees+360) % 360;
        return phase((double)degrees / 360.0);
    }

    public Waveform phase(double position) {

        List<Double> newSamples = Lists.newArrayList();
        int offset = (int)(size()*position);
        for (int index = 0; index < size(); index++) {
            newSamples.add(get((index + offset) % size()));
        }
        samples = newSamples;

        return this;
    }

    public Waveform scaleFrequency(double frequencyMultiplier) {

        List<Double> newSamples = Lists.newArrayList();
        for (int index = 0; index < size(); index++) {
            newSamples.add(get((index*frequencyMultiplier) % size()));
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


    /*** Combine multiple waves ***********************************************/

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
        waveform.name = "Saw";

        double increment = 2 / (double)waveform.size();
        for (int index=0; index<waveform.size(); index++) {
            waveform.samples.set(index, index*increment - 1);
        }

        return waveform;
    }

    public static Waveform square() {
        Waveform waveform = Waveform.pulse(0.5);
        waveform.name = "Square";
        return waveform;
    }

    public static Waveform pulse(double pulseWidth) {

        Waveform waveform = new Waveform();
        waveform.name = String.format("Pulse%02d", (int)(pulseWidth*100));

        int split = (int)Math.round(waveform.size() * pulseWidth);

        for (int index=0; index<split; index++) {
            waveform.samples.set(index, 1d);
        }
        for (int index=split; index<waveform.size(); index++) {
            waveform.samples.set(index, -1d);
        }

        return waveform;
    }

    public static Waveform sine() {

        Waveform waveform = new Waveform();
        waveform.name = "Sine";

        for (int index=0; index<waveform.size(); index++) {
            double angleInRadians = (double)index/waveform.size() * 2*Math.PI;
            waveform.samples.set(index, Math.sin(angleInRadians));
        }

        return waveform;
    }

    public static Waveform random(Long seed, int smoothing) {

        Random random;
        if (seed == null) {
            random = new Random();
        } else {
            random = new Random(seed);
        }

        Waveform waveform = new Waveform();
        waveform.name = "Rnd";
        if (seed != null) {
            waveform.name += seed;
        }
        if (smoothing > 1) {
            waveform.name += "s" + smoothing;
        }

        for (int index=0; index<waveform.size(); index++) {
            waveform.samples.set(index, random.nextDouble()*2 - 1);
        }

        waveform.smooth(smoothing);

        return waveform;
    }

    public static Waveform mix(Waveform waveform1, Waveform waveform2, double weight1) {

        Waveform waveform = new Waveform();
        for (int index=0; index<waveform1.size(); index++) {
            waveform.samples.add(waveform1.get(index)*weight1 + waveform2.get(index)*(1-weight1));
        }

        return waveform;
    }

}
