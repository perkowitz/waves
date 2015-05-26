package net.perkowitz.waves;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;

/** Morph
 *
 *
 * Created by mikep on 5/25/15
 */
public class Morph {

    private String name;
    private List<Waveform> waveforms = Lists.newArrayList();


    /**** constructors *******************************/

    public Morph(String name) {
        this.name = name;
    }

    public Morph(String name, List<Waveform> waveforms) {
        this.name = name;
        this.waveforms = waveforms;
    }

    public Morph(String name, Waveform[] waveforms) {
        this.name = name;
        this.waveforms = Lists.newArrayList(waveforms);
    }


    /******* methods ***************************/

    public Tone toTone(int note, double seconds) {
        Tone tone = Tone.morph(waveforms, note, seconds, true);
        tone.setName(name);
        return tone;
    }

    public void toWavFile(String path, int note, double seconds ) {
        Tone tone = toTone(note, seconds);
        tone.toWavFile(path);
    }

    public String getName() {
        return name;
    }

    public List<Waveform> getWaveforms() {
        return waveforms;
    }

}
