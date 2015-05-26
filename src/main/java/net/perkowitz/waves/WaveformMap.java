package net.perkowitz.waves;

import java.util.HashMap;

public class WaveformMap<X,Y> extends HashMap<X,Y> {

    public WaveformMap(Waveform[] waveforms) {
        for (Waveform waveform : waveforms) {
            this.put((X)waveform.getName(), (Y)waveform);
        }
    }

}
