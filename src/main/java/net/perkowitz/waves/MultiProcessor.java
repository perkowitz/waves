package net.perkowitz.waves;

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

        if (args.length < 6) {
            System.out.println("Usage: Process <process file> <low note> <high note> <skip> <outdir> <filename>");
            return;
        }

        String processFile = args[0];
        Integer lowNote = new Integer(args[1]);
        Integer highNote = new Integer(args[2]);
        Integer skip = new Integer(args[3]);
        String outdirString = args[4];
        String filename = args[5];

        File outdir = new File(outdirString);
        if (!outdir.exists()) {
            outdir.mkdirs();
        }

        Process process = Process.parseFromFile(processFile);
        for (int note=lowNote; note <= highNote; note+=skip) {
            System.out.printf("Rendering note: %d\n", note);
            String outFile = outdirString + "/" + filename + "_" + note;
            Wave wave = process.apply(note);
            StdAudio.save(outFile + WAV_FILE_SUFFIX, wave.toDoubleArray());
        }


    }



}
