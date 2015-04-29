package net.perkowitz.waves;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: mperkowi
 * Date: 4/29/15
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Wavetable {

    public static int DEFAULT_SAMPLE_RATE = 44100;
    public static int DEFAULT_BIT_DEPTH = 8;

    private String name;
    private int tableSize;
    private int samplesPerWave;
    private Waveform[] wavetable;


    public Wavetable(String name, int tableSize, int samplesPerWave) {
        this.name = name;
        this.tableSize = tableSize;
        this.samplesPerWave = samplesPerWave;
        this.wavetable = new Waveform[tableSize];
    }

    public void set(int position, Waveform waveform) {
        if (position >= 0 && position < tableSize) {
            wavetable[position] = waveform;
        }
    }

    public Waveform get(int position) {
        return wavetable[position];
    }

    public void renderAsDir(String path) {
        // render as individual wav files in a named directory

        File dir = new File(path + "/" + name);
        dir.mkdirs();

        String filePrefix = dir + "/" + name + "-";
        for (int position=0; position < tableSize; position++) {
            if (wavetable[position] != null) {
                Tone tone = Tone.fromWaveformSamples(wavetable[position]);
                tone.toWavFile(filePrefix + String.format("%04d",position) + ".wav");    // not sure if this can create 8-bit files
            }
        }

    }

    public void renderAsFile(String path) {
        // create a single file with the waves appended (crossfade?) and a loop point set
    }



}
