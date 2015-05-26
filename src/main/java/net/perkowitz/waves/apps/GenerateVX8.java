package net.perkowitz.waves.apps;

import com.google.common.collect.Maps;
import net.perkowitz.waves.VX8;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/** GenerateVX8
 *
 * Creates files to load into the Akai Z8 to turn it into a vx8 synth
 * Creates a bunch of raw synth waveforms
 *
 * Created by mikep on 5/25/15
 */

public class GenerateVX8 {

    private static int[] basicNotes = new int[] {24, 36, 48};

    public static void main(String args[]) throws IOException {

        String rootPath = "audio/vx8";
        File root = new File(rootPath);
        root.mkdirs();

        VX8.createWaves(rootPath + "/waves", basicNotes);
        VX8.createMorphs(rootPath + "/morphs", VX8.morphs, basicNotes);
        VX8.createChords(rootPath + "/chords", VX8.chords, null);

    }

}
