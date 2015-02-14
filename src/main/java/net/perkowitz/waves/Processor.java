package net.perkowitz.waves;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


public class Processor {

    // JSON keys

    private static final String RAW_FILE_SUFFIX = ".snc";
    private static final String WAV_FILE_SUFFIX = ".wav";

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static int cycleLength;
    private static List<Wave> argWaves;

    public static void main(String args[]) throws IOException {

        if (args.length < 3) {
            System.out.println("Usage: Process <process file> <note> <out file>");
            System.out.println("\nExamples:");
            System.out.println("  Process saw.json 60 saw");
            System.out.println("  Process random.json 48 random");
            System.out.printf("  Process morph.json 42 morph\n", RAW_FILE_SUFFIX, RAW_FILE_SUFFIX);
            return;
        }

        String processFile = args[0];
        Integer note = new Integer(args[1]);
        String outFile = args[2] + "_" + note;

        Process process = Process.parseFromFile(processFile);
        Wave wave = process.apply(note);

        wave.save(outFile + RAW_FILE_SUFFIX);

        StdAudio.save(outFile + WAV_FILE_SUFFIX, wave.toDoubleArray());

    }



}
