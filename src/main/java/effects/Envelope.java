package effects;


public class Envelope extends Effect {


    public Envelope() {
        super();
    }
    double sampleRate= 44100.0;

    public void setInputSampleStream(short[] inputAudioStream) {
        this.inputAudioStream = inputAudioStream;
    }
    @Override
    public synchronized short[] createEffect() {
      /*  try {
            double frequency=1/15;
            int framerate = 44100;
            for(int i =0 ; i < this.inputAudioStream.length; i ++) {
                inputAudioStream[i] *= sin(2 * pi * frequency * t / self.framerate)
                if(checkFlag < Short.MAX_VALUE && checkFlag > Short.MIN_VALUE) {
                    amplitude = (short)checkFlag;
                    this.inputAudioStream[i] =  amplitude;
                    position += 1;
                }
            }*/
            return this.inputAudioStream;
    }

    @Override
    public synchronized short[] getOutputAudioStream() {
        return inputAudioStream;
    }
}
