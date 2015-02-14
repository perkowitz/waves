package net.perkowitz.waves;

import com.google.common.collect.Lists;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mikep on 2/13/15
 */
public class Process {

    public static final String PROCESS_KEY = "process";
    public static final String INPUTS_KEY = "inputs";
    public static final String PARAMETERS_KEY = "parameters";

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String operation;
    private List<Process> inputs;
    private List<String> parameters;

    public Process(String operation, List<Process> inputs, List<String> parameters) {
        this.operation = operation;
        this.inputs = inputs;
        this.parameters = parameters;
    }

    public Wave apply(int note) {

        List<Wave> inputWaves = Lists.newArrayList();
        for (Process input : inputs) {
            inputWaves.add(input.apply(note));
        }

        return operate(operation, note, inputWaves, parameters);
    }

    public static Wave operate(String operation, int note, List<Wave> inputWaves, List<String> parameters) {

        int cycleLength = Wave.noteToCycleLength(note);

        // sound generation
        if (operation.equals("saw")) {
            return Wave.saw(cycleLength);
        } else if (operation.equals("square")) {
            return Wave.square(cycleLength);
        } else if (operation.equals("pulse")) {
            return Wave.pulse(cycleLength,new Double(parameters.get(0)));
        } else if (operation.equals("sine")) {
            return Wave.sine(cycleLength);
        } else if (operation.equals("random")) {
            return Wave.random(cycleLength);
        } else if (operation.equals("silence")) {
            return Wave.silence(cycleLength);
        } else if (operation.equals("readfile")) {
            return Wave.load(parameters.get(0));

        // adjust wave within headroom
        } else if (operation.equals("normalize")) {
            Wave wave = inputWaves.get(0);
            wave.normalize();
            return wave;
        } else if (operation.equals("clip")) {
            Wave wave = inputWaves.get(0);
            wave.clip();
            return wave;
        } else if (operation.equals("wrap")) {
            Wave wave = inputWaves.get(0);
            wave.wrap();
            return wave;

        // modify single wave
        } else if (operation.equals("pad")) {
            Wave wave = inputWaves.get(0);
            wave.pad(new Long(parameters.get(0)));
            return wave;
        } else if (operation.equals("downsample")) {
            Wave wave = inputWaves.get(0);
            wave.downsample(new Double(parameters.get(0)));
            return wave;
        } else if (operation.equals("sync")) {
            Wave wave = inputWaves.get(0);
            Integer syncCycleLength = Wave.note2Samples(new Integer(parameters.get(0)),Wave.DEFAULT_SAMPLE_RATE);
            wave.sync(syncCycleLength);
            return wave;
        } else if (operation.equals("lpf")) {
            Wave wave = inputWaves.get(0);
            wave.lowpass(new Integer(parameters.get(0)));
            return wave;
        } else if (operation.equals("hpf")) {
            Wave wave = inputWaves.get(0);
            wave.highpass(new Integer(parameters.get(0)));
            return wave;

        // combine two waves
        } else if (operation.equals("add")) {
            Wave wave1 = inputWaves.get(0);
            Wave wave2 = inputWaves.get(1);
            wave1.add(wave2);
            return wave1;
        } else if (operation.equals("subtract")) {
            Wave wave1 = inputWaves.get(0);
            Wave wave2 = inputWaves.get(1);
            wave1.subtract(wave2);
            return wave1;
        } else if (operation.equals("multiply")) {
            Wave wave1 = inputWaves.get(0);
            Wave wave2 = inputWaves.get(1);
            wave1.multiply(wave2);
            return wave1;
        } else if (operation.equals("append")) {
            Wave wave1 = inputWaves.get(0);
            Wave wave2 = inputWaves.get(1);
            wave1.append(wave2);
            return wave1;
        } else if (operation.equals("average")) {
            Wave wave1 = inputWaves.get(0);
            Wave wave2 = inputWaves.get(1);
            return Wave.average(wave1,wave2,new Double(parameters.get(0)));
        } else if (operation.equals("morph")) {
            Wave wave1 = inputWaves.get(0);
            Wave wave2 = inputWaves.get(1);
            return Wave.morph(wave1,wave2,new Integer(parameters.get(0)),cycleLength);
        } else if (operation.equals("morphloop")) {
            Wave wave1 = inputWaves.get(0);
            Wave wave2 = inputWaves.get(1);
            int length = (new Integer(parameters.get(0)))/2;
            Wave morph1 = Wave.morph(wave1,wave2,length,cycleLength);
            Wave morph2 = Wave.morph(wave2,wave1,length,cycleLength);
            morph1.append(morph2);
            return morph1;

        // file handling
        } else if (operation.equals("writefile")) {
            Wave wave = inputWaves.get(0);
            String filename = parameters.get(0);
            try {
                wave.save(filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return wave;

        }

        System.out.println("Process not found: " + operation);

        return null;
    }

    public static Process parse(JsonNode jsonDefinition) {

        System.out.println("Parsing " + jsonDefinition);
        String operation = jsonDefinition.path(PROCESS_KEY).getTextValue().toLowerCase();

        List<String> parameters = Lists.newArrayList();
        Iterator parameterNodes = jsonDefinition.path(PARAMETERS_KEY).getElements();
        while (parameterNodes.hasNext()) {
            parameters.add(((JsonNode)parameterNodes.next()).getTextValue());
        }

        List<Process> inputs = Lists.newArrayList();
        Iterator<JsonNode> inputNodes = jsonDefinition.path(INPUTS_KEY).getElements();
        while (inputNodes.hasNext()) {
            JsonNode thisNode = inputNodes.next();
            Process thisProcess = Process.parse(thisNode);
            inputs.add(thisProcess);
        }

        System.out.println("Finished parsing " + jsonDefinition);
        return new Process(operation, inputs, parameters);
    }

    public static Process parseFromFile(String filename) {

        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line;
            String json = "";
            while ((line = in.readLine()) != null)   {
                json += line.trim() + " ";
            }
            in.close();

            JsonNode node = OBJECT_MAPPER.readTree(json);
            return Process.parse(node);

        } catch (IOException e) {
            System.err.println("Error reading from " + filename);
            return null;
        }
    }


}
