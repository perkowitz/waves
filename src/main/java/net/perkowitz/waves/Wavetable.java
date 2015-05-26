package net.perkowitz.waves;

import java.io.File;

/** Wavetable
 *
 * Represents a sequence of waveforms
 * - an empty position in the table should be interpolated from the waveforms on either side
 * - the first and last positions must be defined (so something can be interpolated)
 *
 * Can be rendered multiple ways
 * - as a collection of single-cycle waves (for loading individually into a microwave)
 * - as a single wave file where each waveform appears as one cycle, with the loop point set to one cycle
 * - as a tone that is a morph through the table over time
 *
 * Created by mikep on 4/29/15
 */
public class Wavetable {

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

    public Tone renderAsTone(int note, double seconds) {

        return null;
    }

    public void renderAsDir(String path) {
        // render as individual single-cycle wav files in a named directory

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
        // create a single file with the single-cycle waves appended (crossfade?) and a loop point set
    }



}
