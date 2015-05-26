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

    public static String PREFIX_SEPARATOR = "|";
    public static String NOTE_SEPARATOR = " ";
    public static String WAVE_PREFIX = "W" + PREFIX_SEPARATOR;
    public static String MORPH_PREFIX = "M" + PREFIX_SEPARATOR;
    public static String CHORD_PREFIX = "C" + PREFIX_SEPARATOR;

    private static double waveformLengthSecs = 2.0;

    public static Waveform[] waveforms = new Waveform[] {
            Waveform.saw().name("Saw"),
            Waveform.sine().name("Sine"),
            Waveform.square().name("Square"),
            Waveform.pulse(0.1).name("Pulse10"),
            Waveform.pulse(0.25).name("Pulse25"),
            Waveform.random(1l,10).name("Rnd1"),
            Waveform.random(2l,10).name("Rnd2"),
            Waveform.random(3l,10).name("Rnd3"),
            Waveform.random(4l,10).name("Rnd4"),
            Waveform.sine().downsample(5).name("Sine5"),
            Waveform.sine().downsample(10).name("Sine10"),
            Waveform.sine().downsample(20).name("Sine20")
    };
    public static WaveformMap<String,Waveform> waveformMap = new WaveformMap(waveforms);

    public static List<List<String>> morphs = Lists.newArrayList();
    static {
        morphs.add(Lists.newArrayList("Saw", "Square"));
        morphs.add(Lists.newArrayList("Sine", "Sine20"));
        morphs.add(Lists.newArrayList("Rnd1", "Rnd2", "Rnd3", "Rnd4"));
        morphs.add(Lists.newArrayList("Sine20", "Pulse10", "Sine10","Rnd1"));
    }

    public static Map<String,Integer[]> chords = Maps.newHashMap();
    static {
        chords.put("Major", new Integer[] {0, 4, 7});
        chords.put("Minor", new Integer[] {0, 3, 7});
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
                tone.setName(WAVE_PREFIX + name + NOTE_SEPARATOR + note);
                tone.toWavFile(dir);
            }
        }
    }

    public static void createMorphs(String path, List<List<String>> morphs, int[] notes) {

        File dir = new File(path);
        dir.mkdirs();
        for (List<String> morph : morphs) {
            List<Waveform> waveforms = Lists.newArrayList();
            for (String waveform : morph) {
                waveforms.add(waveformMap.get(waveform));
            }
            waveforms.add(waveforms.get(0));   // loop back to the start

            for (int note : notes) {
                Tone tone = Tone.morph(waveforms, note, waveformLengthSecs, true);
                tone.setName(MORPH_PREFIX + StringUtils.join(morph, "-") + NOTE_SEPARATOR + note);
                tone.toWavFile(dir);
            }
        }
    }

    public static void createChords(String path, Map<String,Integer[]> chords, int[] notes) {

        File dir = new File(path);
        dir.mkdirs();

        List<Waveform> morphList = Lists.newArrayList(Waveform.square(), Waveform.saw());

        int note = 48;
        for (String name : chords.keySet()) {
            Integer[] intervals = chords.get(name);
            Tone chord = Tone.chord(morphList, note, Lists.newArrayList(chords.get(name)), waveformLengthSecs);
            chord.setName(CHORD_PREFIX + name + NOTE_SEPARATOR + note);
            chord.toWavFile(dir);
        }

    }

}
