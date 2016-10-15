package net.perkowitz.waves;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/** vx8
 *
 * Defining the basic sounds of the VX8 synth (a set of waves for the Akai Z8)
 *
 * Created by mikep on 2/18/15
 */
public class VX8 {

    public static String PREFIX_SEPARATOR = "-";
    public static String NOTE_SEPARATOR = " ";
    public static String WAVE_PREFIX = "W" + PREFIX_SEPARATOR;
    public static String MORPH_PREFIX = "M" + PREFIX_SEPARATOR;
    public static String CHORD_PREFIX = "C" + PREFIX_SEPARATOR;

    private static double waveformLengthSecs = 2.0;

    public static Waveform[] waves = new Waveform[] {
            Waveform.saw().name("Saw"),
            Waveform.sine().name("Sine"),
            Waveform.square().name("Square"),
            Waveform.pulse(0.02).name("Pulse02"),
            Waveform.pulse(0.06).name("Pulse06"),
            Waveform.pulse(0.1).name("Pulse10"),
            Waveform.pulse(0.25).name("Pulse25"),
            Waveform.random(1l,10).name("Rnd1"),
            Waveform.random(2l,10).name("Rnd2"),
            Waveform.random(3l,10).name("Rnd3"),
            Waveform.random(4l,10).name("Rnd4"),
            Waveform.sine().downsample(10).name("Sine10"),
            Waveform.sine().downsample(20).name("Sine20"),
            Waveform.sine().downsample(100).name("Sine100"),
            Waveform.sine().downsample(200).name("Sine200")
    };
    public static WaveformMap<String,Waveform> waveformMap = new WaveformMap(waves);

    public static Waveform[] chordWaves = new Waveform[] {
            Waveform.saw().name("Saw"),
            Waveform.square().name("Square"),
            Waveform.pulse(0.06).name("Pulse06"),
            Waveform.random(1l,10).name("Rnd1"),
            Waveform.sine().downsample(20).name("Sine20")
    };



    public static List<Morph> morphs = Lists.newArrayList();
    static {
        morphs.add(createMorph("SqrSaw", Lists.newArrayList("Square", "Saw"), true));
        morphs.add(createMorph("SqrSine", Lists.newArrayList("Square", "Sine200", "Sine100"), true));
        morphs.add(createMorph("Sine", Lists.newArrayList("Sine", "Sine20"), true));
        morphs.add(createMorph("Sine2", Lists.newArrayList("Sine100", "Sine20"), true));
        morphs.add(createMorph("Rnd", Lists.newArrayList("Rnd1", "Rnd2", "Rnd3", "Rnd4"), true));
        morphs.add(createMorph("Sweep1", Lists.newArrayList("Sine200", "Pulse25", "Sine20",  "Rnd1"), true));
        morphs.add(createMorph("Pulse", Lists.newArrayList("Pulse25", "Pulse10", "Pulse06", "Pulse02"), true));
    }

    public static List<Morph> chordMorphs = Lists.newArrayList();
    static {
        chordMorphs.add(createMorph("SqrSaw", Lists.newArrayList("Square", "Saw"), false));
        chordMorphs.add(createMorph("SqrSine", Lists.newArrayList("Square", "Sine200", "Sine100"), false));
        chordMorphs.add(createMorph("Sine2", Lists.newArrayList("Sine100", "Sine20"), false));
        chordMorphs.add(createMorph("Pulse", Lists.newArrayList("Pulse10", "Pulse02"), false));
        chordMorphs.add(createMorph("Sweep1", Lists.newArrayList("Sine200", "Pulse25", "Sine20",  "Rnd1"), false));
    }

    public static Map<String,Integer[]> chords = Maps.newHashMap();
    static {
        chords.put("Maj", new Integer[] {0, 4, 7});
        chords.put("Min", new Integer[] {0, 3, 7});
        chords.put("Aug", new Integer[] {0, 4, 8});
        chords.put("Dim", new Integer[] {0, 3, 6});
        chords.put("Maj7", new Integer[] {0, 4, 7, 11});
        chords.put("Min7", new Integer[] {0, 3, 7, 10});
        chords.put("Aug7", new Integer[] {0, 4, 8, 10});
        chords.put("Dim7", new Integer[] {0, 3, 6, 9});
        chords.put("Maj6", new Integer[] {0, 4, 7, 9});
        chords.put("Min6", new Integer[] {0, 3, 7, 9});
        chords.put("Sus2", new Integer[] {0, 2, 7});
        chords.put("Sus4", new Integer[] {0, 5, 7});
    }

    public static void createWaves(String path, int[] notes) {

        File dir = new File(path);
        dir.mkdirs();
        for (String name : waveformMap.keySet()) {
            for (int note : notes) {
                Waveform waveform = waveformMap.get(name);
                waveform.normalize();
                Tone tone = Tone.fromWaveform(waveformMap.get(name), note, waveformLengthSecs, true);
                if (notes.length == 1) {
                    tone.setName(WAVE_PREFIX + name);
                } else {
                    tone.setName(WAVE_PREFIX + name + NOTE_SEPARATOR + note);
                }
                tone.toWavFile(dir);
            }
        }
    }

    public static void createMorphs(String path, List<Morph> morphs, int[] notes) {

        File dir = new File(path);
        dir.mkdirs();

        for (Morph morph : morphs) {
            for (int note : notes) {
                Tone tone = morph.toTone(note, waveformLengthSecs);
                tone.setName(MORPH_PREFIX + morph.getName() + NOTE_SEPARATOR + note);
                tone.toWavFile(dir);
            }
        }

    }

    public static void createChords(String path, Map<String,Integer[]> chords, List<Morph> morphs, int[] notes) {

        File dir = new File(path);
        dir.mkdirs();

        for (Waveform wave : chordWaves) {
            for (int note : notes) {
                for (String name : chords.keySet()) {
                    Tone chord = Tone.chord(Lists.<Waveform>newArrayList(wave, wave), note, Lists.newArrayList(chords.get(name)), waveformLengthSecs);
                    chord.setName(CHORD_PREFIX + wave.getName() + PREFIX_SEPARATOR + name + NOTE_SEPARATOR + note);
                    chord.toWavFile(dir);
                }
            }
        }

    }

    public static void createMorphChords(String path, Map<String,Integer[]> chords, List<Morph> morphs, int[] notes) {

        File dir = new File(path);
        dir.mkdirs();

        for (Morph morph : morphs) {
            for (int note : notes) {
                for (String name : chords.keySet()) {
                    Tone chord = Tone.chord(morph.getWaveforms(), note, Lists.newArrayList(chords.get(name)), waveformLengthSecs);
                    chord.setName(CHORD_PREFIX + morph.getName() + PREFIX_SEPARATOR + name + NOTE_SEPARATOR + note);
                    chord.toWavFile(dir);
                }
            }
        }

    }

    public static Morph createMorph(String morphName, List<String> waveNames, boolean loop) {

        List<Waveform> waveforms = Lists.newArrayList();
        for (String waveName : waveNames) {
            waveforms.add(waveformMap.get(waveName));
        }
        if (loop) {
            // TODO: figure out how to put loops in wav files someday
            waveforms.add(waveforms.get(0));  // loop back to the beginning
        }

        return new Morph(morphName, waveforms);
    }

}
