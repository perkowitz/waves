package net.perkowitz.waves.apps;

import com.google.common.collect.Lists;
import net.perkowitz.waves.*;
import net.perkowitz.waves.Process;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class MultiProcessor {

    // JSON keys

    private static final String RAW_FILE_SUFFIX = ".snc";
    private static final String WAV_FILE_SUFFIX = ".wav";

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static int cycleLength;
    private static List<Wave> argWaves;

    public static void main(String args[]) throws IOException {

        if (args.length < 5) {
            System.out.println("Usage: Process <process file/dir> <audio dir> <low note> <high note> <skip>");
            return;
        }

        File processIn = new File(args[0]);
        String outdirString = args[1];
        Integer lowNote = new Integer(args[2]);
        Integer highNote = new Integer(args[3]);
        Integer skip = new Integer(args[4]);

        File outdir = new File(outdirString);
        if (!outdir.exists()) {
            outdir.mkdirs();
        }

        List<File> inFiles = Lists.newArrayList();
        if (processIn.isDirectory()) {
            for (File file : processIn.listFiles()) {
                inFiles.add(file);
            }
        } else {
            inFiles.add(processIn);
        }

        for (File in : inFiles) {

            String filestem = in.getName();
            if (filestem.endsWith(".json")) {
                filestem = filestem.substring(0,filestem.length()-5);
            }

            String outStem = outdirString + "/" + filestem;

            processFile(in.toString(), outStem, lowNote, highNote, skip);

        }

    }

    private static void processFile(String processFile, String outStem, int lowNote, int highNote, int skip) {

        System.out.printf("Processing %s to %s\n", processFile, outStem);
        net.perkowitz.waves.Process process = Process.parseFromFile(processFile);
        for (int note=lowNote; note <= highNote; note+=skip) {
            System.out.printf("Rendering note: %d\n", note);
            Wave wave = process.apply(note);

            String outFile = outStem + "_" + note + WAV_FILE_SUFFIX;
            StdAudio.save(outFile, wave.toDoubleArray());
        }

    }


}
